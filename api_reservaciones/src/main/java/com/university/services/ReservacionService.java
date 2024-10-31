package com.university.services;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.enums.DiaSemanaEnum;
import com.university.models.Cancelacion;
import com.university.models.DiaAtencion;
import com.university.models.EstadoReservacion;
import com.university.models.Factura;
import com.university.models.MetodoPago;
import com.university.models.Pago;
import com.university.models.Reservacion;
import com.university.models.Servicio;
import com.university.models.UnidadRecurso;
import com.university.models.Usuario;
import com.university.repositories.CancelacionRepository;
import com.university.repositories.DiaAtencionRepository;
import com.university.repositories.EstadoReservacionRepository;
import com.university.repositories.FacturaRepository;
import com.university.repositories.MetodoPagoRepository;
import com.university.repositories.PagoRepository;
import com.university.repositories.ReservacionRepository;
import com.university.repositories.ServicioRepository;
import com.university.repositories.UnidadRecursoRepository;
import com.university.repositories.UsuarioRepository;
import com.university.services.jasper.ComprobanteImprimible;

@Service
public class ReservacionService {

    @Autowired
    private ReservacionRepository reservacionRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ServicioRepository servicioRepository;
    @Autowired
    private UnidadRecursoRepository unidadRecursoRepository;
    @Autowired
    private EstadoReservacionRepository estadoReservacionRepository;
    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private MetodoPagoRepository metodoPagoRepository;
    @Autowired
    private DiaAtencionRepository diaAtencionRepository;
    @Autowired
    private FacturaRepository facturaRepository;
    @Autowired
    private CancelacionRepository cancelacionRepository;
    @Autowired
    private ComprobanteImprimible comprobanteImprimible;

    public List<Reservacion> getReservacionesByCliente(Long id_cliente) throws Exception {
        return reservacionRepository.findByUsuario(new Usuario(id_cliente));
    }

    /**
     * Método para crear una reservación
     */
    @Transactional
    public Reservacion crearReservacion(String emailUsuario, Long idEncargado, Long idServicio,
            String nombreUnidadRecurso, String nombreRecurso, Long idPago,
            LocalTime horaInicio, LocalTime horaFinal, LocalDate fecha,
            String nombreMetodoPago, String numeroPago, Float montoPago) throws Exception {

        // Buscar el usuario cliente
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // Buscar el servicio
        Servicio servicio = servicioRepository.findById(idServicio)
                .orElseThrow(() -> new Exception("Servicio no encontrado"));

        validarDisponibilidadServicio(servicio, horaInicio, horaFinal, fecha);
        // Validar la disponibilidad de trabajadores simultáneos
        validarDisponibilidadTrabajadoresSimultaneos(servicio, fecha, horaInicio, horaFinal);

        // Asignar un encargado automáticamente si no se pasa uno
        Usuario encargado = null;
        if (idEncargado == null) {
            encargado = obtenerEncargadoAutomatico(servicio, fecha, horaInicio, horaFinal);
        } else {
            encargado = this.usuarioRepository.findById(idEncargado)
                    .orElseThrow(() -> new Exception("Usuario encargado no encontrado"));
        }
        // Validar la disponibilidad del encargado para la fecha y horas proporcionadas
        validarDisponibilidadEncargado(encargado, horaInicio, horaFinal, fecha);

        // Verificar si la unidad de recurso es necesaria y buscarla
        UnidadRecurso unidadRecurso = null;
        if (nombreRecurso != null) {
            unidadRecurso = unidadRecursoRepository
                    .findByNombreAndRecurso_Nombre(nombreUnidadRecurso, nombreRecurso)
                    .orElseThrow(() -> new Exception("Unidad de recurso no encontrada"));
        }

        // Validar disponibilidad de la unidad de recurso
        validarDisponibilidadUnidadRecurso(unidadRecurso, horaInicio, horaFinal, fecha);

        // Crear el estado de la reservación como "Programada"
        EstadoReservacion estadoProgramada = estadoReservacionRepository
                .findOneByNombre("Programada")
                .orElseThrow(() -> new Exception("Estado de reservación no encontrado"));

        // Crear el pago
        Pago pago = crearPago(nombreMetodoPago, numeroPago, montoPago);

        // Crear la reservación
        Reservacion reservacion = new Reservacion(usuario, encargado, servicio, unidadRecurso,
                horaInicio, horaFinal, fecha, pago, estadoProgramada);

        // Guardar la reservación
        return reservacionRepository.save(reservacion);
    }

    @Transactional
    public Factura completarReservacion(Long idReservacion) throws Exception {
        // Buscar la reservación
        Reservacion reservacion = reservacionRepository.findById(idReservacion)
            .orElseThrow(() -> new EntityNotFoundException("Reservación no encontrada"));

        // Obtener el estado "Completada"
        EstadoReservacion estadoCompletada = estadoReservacionRepository
            .findOneByNombre("Completada")
            .orElseThrow(() -> new Exception("Estado de reservación 'Completada' no encontrado"));

        // Cambiar el estado de la reservación a "Completada"
        reservacion.setEstadoReservacion(estadoCompletada);
        reservacionRepository.save(reservacion);

        // Crear y guardar la factura
        Factura factura = new Factura();
        factura.setUsuario(reservacion.getUsuario());
        factura.setReservacion(reservacion);
        factura.setMonto(reservacion.getPago().getMonto());

        return facturaRepository.save(factura);
    }

    /**
     * Método para cancelar una reservación.
     * Cambia el estado de la reservación a "Cancelada", genera una factura y un registro en la tabla de cancelación.
     * @param idReservacion ID de la reservación a cancelar.
     * @param motivoCancelacion Motivo de la cancelación.
     * @throws Exception Si ocurre algún error al procesar la cancelación.
     */
    @Transactional
    public String cancelarReservacion(Long idReservacion, String motivoCancelacion, LocalDate fechaCancelacion) throws Exception {
        // Buscar la reservación por ID
        Reservacion reservacion = reservacionRepository.findById(idReservacion)
                .orElseThrow(() -> new Exception("Reservación no encontrada"));

        // Validar si la reservación ya está cancelada o completada
        if (!reservacion.getEstadoReservacion().getNombre().equals("Programada")) {
            throw new Exception("La reservación ya fue cancelada o completada.");
        }

        // Obtener el servicio y calcular el monto a reembolsar
        Servicio servicio = reservacion.getServicio();
        Float porcentajeReembolso = servicio.getPorcentaje_reembolso();
        Float montoReembolso = reservacion.getPago().getMonto() * (porcentajeReembolso / 100);

        // Crear y guardar la factura
        Factura factura = new Factura();
        factura.setUsuario(reservacion.getUsuario());
        factura.setReservacion(reservacion);
        factura.setMonto(montoReembolso);
        facturaRepository.save(factura);

        // Crear y guardar el registro de cancelación
        Cancelacion cancelacion = new Cancelacion();
        cancelacion.setReservacion(reservacion);
        cancelacion.setFechaCancelacion(fechaCancelacion);
        cancelacion.setMotivoCancelacion(motivoCancelacion);
        cancelacion.setMontoReembolsado(montoReembolso);
        cancelacionRepository.save(cancelacion);

        // Cambiar el estado de la reservación a "Cancelada"
        EstadoReservacion estadoCancelada = estadoReservacionRepository
                .findOneByNombre("Cancelada")
                .orElseThrow(() -> new Exception("Estado de reservación 'Cancelada' no encontrado."));
        reservacion.setEstadoReservacion(estadoCancelada);
        reservacionRepository.save(reservacion);

        return "Reservación cancelada exitosamente y factura generada.";
    }

    public Reservacion getReservacion(Long id) throws Exception {
        if (id == null || id <= 0) {
            throw new Exception("Id invalido.");
        }

        Optional<Reservacion> reservacionSearch = this.reservacionRepository.findById(id);

        if (reservacionSearch.isEmpty()) {
            throw new Exception("Reservacion no encontrada.");
        }

        return reservacionSearch.get();
    }

    public byte[] getComprobante(Long id) throws Exception {
        Reservacion reservacion = this.getReservacion(id);

        return this.comprobanteImprimible.init(reservacion);
    }

    /**
     * Método para crear un pago con el método de pago, número y monto
     */
    private Pago crearPago(String nombreMetodoPago, String numero, Float monto) throws Exception {
        // Buscar el método de pago
        MetodoPago metodoPago = metodoPagoRepository.findOneByNombre(nombreMetodoPago)
                .orElseThrow(() -> new Exception("Método de pago no encontrado"));

        // Crear y guardar el pago
        Pago pago = new Pago(metodoPago, numero, monto);
        return pagoRepository.save(pago);
    }

    /**
     * Validar la disponibilidad de trabajadores simultáneos en el servicio.
     *
     * @param servicio   El servicio para el cual se hace la reservación.
     * @param fecha      La fecha de la reservación.
     * @param horaInicio Hora de inicio de la reservación.
     * @param horaFinal  Hora final de la reservación.
     * @throws Exception Si la cantidad de trabajadores simultáneos alcanza el
     *                   límite permitido.
     */
    private void validarDisponibilidadTrabajadoresSimultaneos(Servicio servicio, LocalDate fecha, LocalTime horaInicio,
            LocalTime horaFinal) throws Exception {
        // Contar las reservaciones programadas para el mismo servicio, fecha y horario
        int reservacionesSimultaneas = reservacionRepository
                .countByServicioAndFechaAndHoraInicioLessThanEqualAndHoraFinalGreaterThanEqualAndEstadoReservacion_Nombre(
                        servicio, fecha, horaFinal, horaInicio, "Programada");

        if (reservacionesSimultaneas >= servicio.getTrabajadores_simultaneos()) {
            throw new Exception("No hay trabajadores disponibles para este servicio en el horario seleccionado.");
        }
    }

    /**
     * Validar la disponibilidad del encargado.
     *
     * @param encargado  El usuario encargado.
     * @param horaInicio Hora de inicio de la reservación.
     * @param horaFinal  Hora de final de la reservación.
     * @param fecha      Fecha de la reservación.
     * @throws Exception Si el encargado no está disponible o tiene otra cita
     *                   traslapada.
     */
    private void validarDisponibilidadEncargado(Usuario encargado, LocalTime horaInicio, LocalTime horaFinal,
            LocalDate fecha) throws Exception {
        DiaAtencion diaAtencion = obtenerDiaAtencion(fecha); // Obtener el día de la semana

        // Verificar si el encargado está disponible en ese día y horario
        boolean disponible = encargado.getHorariosAtencionUsuario().stream()
                .anyMatch(horario -> horario.getDiaAtencion().equals(diaAtencion) &&
                        !horaInicio.isBefore(horario.getHoraInicio()) &&
                        !horaFinal.isAfter(horario.getHoraFinal()));

        if (!disponible) {
            throw new Exception("El encargado no está disponible en el horario seleccionado.");
        }

        // Verificar si hay otra reservación que se traslape
        boolean traslape = reservacionRepository
                .existsByEncargadoAndFechaAndHoraInicioLessThanEqualAndHoraFinalGreaterThanEqualAndEstadoReservacion_Nombre(
                        encargado, fecha, horaFinal, horaInicio, "Programada");

        if (traslape) {
            throw new Exception("El encargado ya tiene una reservación en el horario seleccionado.");
        }
    }

    /**
     * Validar la disponibilidad del servicio en la fecha y horas especificadas.
     *
     * @param servicio   El servicio para el cual se hace la reservación.
     * @param horaInicio Hora de inicio de la reservación.
     * @param horaFinal  Hora final de la reservación.
     * @param fecha      Fecha de la reservación.
     * @throws Exception Si el servicio no está disponible en el horario
     *                   seleccionado.
     */
    private void validarDisponibilidadServicio(Servicio servicio, LocalTime horaInicio, LocalTime horaFinal,
            LocalDate fecha) throws Exception {
        DiaAtencion diaAtencion = obtenerDiaAtencion(fecha); // Obtener el día de la semana

        // Verificar si el servicio está disponible en ese día y horario
        boolean disponible = servicio.getHorariosAtencionServicios().stream()
                .anyMatch(horario -> horario.getDiaAtencion().equals(diaAtencion) &&
                        !horaInicio.isBefore(horario.getHoraInicio()) &&
                        !horaFinal.isAfter(horario.getHoraFinal()));

        if (!disponible) {
            throw new Exception("El servicio no está disponible en el horario seleccionado.");
        }
    }

    /**
     * Validar la disponibilidad de la unidad de recurso.
     *
     * @param unidadRecurso La unidad de recurso a verificar.
     * @param horaInicio    Hora de inicio de la reservación.
     * @param horaFinal     Hora de final de la reservación.
     * @param fecha         Fecha de la reservación.
     * @throws Exception Si la unidad de recurso ya está reservada en el horario
     *                   seleccionado.
     */
    private void validarDisponibilidadUnidadRecurso(UnidadRecurso unidadRecurso, LocalTime horaInicio,
            LocalTime horaFinal, LocalDate fecha) throws Exception {
        // Verificar si existe una reservación que traslape en horario para la misma
        // unidad de recurso
        boolean traslape = reservacionRepository
                .existsByUnidadRecursoAndFechaAndHoraInicioLessThanEqualAndHoraFinalGreaterThanEqualAndEstadoReservacion_Nombre(
                        unidadRecurso, fecha, horaFinal, horaInicio, "Programada");

        if (traslape) {
            throw new Exception("La unidad de recurso ya está reservada en el horario seleccionado.");
        }
    }

    /**
     * Obtener el día de la semana basado en la fecha proporcionada.
     *
     * @param fecha Fecha de la reservación.
     * @return El objeto DiaAtencion que corresponde al día de la semana.
     */
    private DiaAtencion obtenerDiaAtencion(LocalDate fecha) throws Exception {
        DayOfWeek diaSemana = fecha.getDayOfWeek();
        return diaAtencionRepository.findOneByNombre(DiaSemanaEnum.fromDayOfWeek(diaSemana).getNombre())
                .orElseThrow(() -> new Exception("Día de atención no encontrado."));
    }

    /**
     * Método para obtener automáticamente un encargado para un servicio.
     * Balancea la carga asignando al usuario con menos asignaciones activas y que
     * tenga permisos para el servicio.
     *
     * @param servicio   El servicio a reservar.
     * @param fecha      Fecha de la reservación.
     * @param horaInicio Hora de inicio de la reservación.
     * @param horaFinal  Hora de final de la reservación.
     * @return El usuario encargado disponible.
     * @throws Exception Si no hay encargados disponibles.
     */
    private Usuario obtenerEncargadoAutomatico(Servicio servicio, LocalDate fecha, LocalTime horaInicio,
            LocalTime horaFinal) throws Exception {
        List<Usuario> encargadosDisponibles = usuarioRepository.findUsuariosConPermisosParaServicio(servicio.getId());

        DiaAtencion diaAtencion;
        diaAtencion = obtenerDiaAtencion(fecha); // Obtener el día de atención fuera del lambda

        // Filtrar usuarios por disponibilidad en el horario y fecha solicitados
        encargadosDisponibles = encargadosDisponibles.stream()
                .filter(encargado -> {
                    // Verificar disponibilidad del encargado en su horario de atención
                    boolean disponibleEnHorario = encargado.getHorariosAtencionUsuario().stream()
                            .anyMatch(horario -> horario.getDiaAtencion().equals(diaAtencion) &&
                                    !horaInicio.isBefore(horario.getHoraInicio()) &&
                                    !horaFinal.isAfter(horario.getHoraFinal()));
                    if (!disponibleEnHorario)
                        return false;

                    // Verificar que no tenga una reservación traslapada en este horario
                    return !reservacionRepository
                            .existsByEncargadoAndFechaAndHoraInicioLessThanEqualAndHoraFinalGreaterThanEqualAndEstadoReservacion_Nombre(
                                    encargado, fecha, horaFinal, horaInicio, "Programada");
                })
                .collect(Collectors.toList());

        if (encargadosDisponibles.isEmpty()) {
            throw new Exception("No hay encargados disponibles para el servicio en el horario seleccionado.");
        }

        // Balancear la carga seleccionando al encargado con menos asignaciones activas
        return encargadosDisponibles.stream()
                .min(Comparator.comparingInt(encargado -> reservacionRepository
                        .countByEncargadoAndEstadoReservacion_Nombre(encargado, "Programada")))
                .orElseThrow(() -> new Exception("Error al balancear la carga de encargados."));
    }

}
