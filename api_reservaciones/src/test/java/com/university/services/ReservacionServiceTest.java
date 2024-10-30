package com.university.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.university.enums.DiaSemanaEnum;
import com.university.models.Cancelacion;
import com.university.models.DiaAtencion;
import com.university.models.EstadoReservacion;
import com.university.models.Factura;
import com.university.models.HorarioAtencionServicio;
import com.university.models.HorarioAtencionUsuario;
import com.university.models.MetodoPago;
import com.university.models.Pago;
import com.university.models.Reservacion;
import com.university.models.Servicio;
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

public class ReservacionServiceTest {

    @InjectMocks
    private ReservacionService reservacionService;

    @Mock
    private ReservacionRepository reservacionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ServicioRepository servicioRepository;

    @Mock
    private UnidadRecursoRepository unidadRecursoRepository;

    @Mock
    private EstadoReservacionRepository estadoReservacionRepository;

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private MetodoPagoRepository metodoPagoRepository;

    @Mock
    private DiaAtencionRepository diaAtencionRepository;

    @Mock
    private FacturaRepository facturaRepository;

    @Mock
    private CancelacionRepository cancelacionRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearReservacion_Success() throws Exception {
        String emailUsuario = "cliente@example.com";
        Long idServicio = 1L;
        LocalDate fecha = LocalDate.of(2024, 10, 30); // Definimos una fecha específica
        LocalTime horaInicio = LocalTime.of(10, 0);
        LocalTime horaFinal = LocalTime.of(12, 0);
        String nombreMetodoPago = "Tarjeta";
        String numeroPago = "123456";
        Float montoPago = 100.0f;

        // Mock de usuario encontrado
        Usuario usuario = new Usuario();
        usuario.setEmail(emailUsuario);
        when(usuarioRepository.findByEmail(emailUsuario)).thenReturn(Optional.of(usuario));

        // Mock de servicio encontrado
        Servicio servicio = new Servicio();
        servicio.setId(idServicio);
        servicio.setTrabajadores_simultaneos(1); // Configuración de trabajadores simultáneos
        when(servicioRepository.findById(idServicio)).thenReturn(Optional.of(servicio));

        // Mock del día de atención
        String nombreDiaSemana = DiaSemanaEnum.fromDayOfWeek(fecha.getDayOfWeek()).getNombre();
        DiaAtencion diaAtencion = new DiaAtencion(nombreDiaSemana);
        when(diaAtencionRepository.findOneByNombre(nombreDiaSemana)).thenReturn(Optional.of(diaAtencion));

        // Mock de horario de atención del servicio
        HorarioAtencionServicio horarioAtencion = new HorarioAtencionServicio();
        horarioAtencion.setHoraInicio(horaInicio);
        horarioAtencion.setHoraFinal(horaFinal);
        horarioAtencion.setDiaAtencion(diaAtencion);
        servicio.setHorariosAtencionServicios(Arrays.asList(horarioAtencion));

        // Mock del encargado disponible
        Usuario encargado = new Usuario();
        encargado.setEmail("encargado@example.com");

        HorarioAtencionUsuario horarioEncargado = new HorarioAtencionUsuario();
        horarioEncargado.setHoraInicio(horaInicio);
        horarioEncargado.setHoraFinal(horaFinal);
        horarioEncargado.setDiaAtencion(diaAtencion);
        encargado.setHorariosAtencionUsuario(Arrays.asList(horarioEncargado));

        // Configura el mock para devolver el encargado disponible
        when(usuarioRepository.findUsuariosConPermisosParaServicio(idServicio)).thenReturn(Arrays.asList(encargado));

        // Mock del estado de la reservación
        EstadoReservacion estadoProgramada = new EstadoReservacion();
        estadoProgramada.setNombre("Programada");
        when(estadoReservacionRepository.findOneByNombre("Programada")).thenReturn(Optional.of(estadoProgramada));

        // Mock del método de pago
        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setNombre(nombreMetodoPago);
        when(metodoPagoRepository.findOneByNombre(nombreMetodoPago)).thenReturn(Optional.of(metodoPago));

        // Mock del pago
        Pago pago = new Pago(metodoPago, numeroPago, montoPago);
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        // Mock de reservación
        Reservacion reservacion = new Reservacion(usuario, encargado, servicio, null, horaInicio, horaFinal, fecha, pago, estadoProgramada);
        when(reservacionRepository.save(any(Reservacion.class))).thenReturn(reservacion);

        // Ejecutar el método
        Reservacion result = reservacionService.crearReservacion(emailUsuario, null, idServicio, null, null, null,
                horaInicio, horaFinal, fecha, nombreMetodoPago, numeroPago, montoPago);

        // Verificar resultados
        assertNotNull(result);
        assertEquals(usuario, result.getUsuario());
        assertEquals(servicio, result.getServicio());
        assertEquals(horaInicio, result.getHoraInicio());
        assertEquals(horaFinal, result.getHoraFinal());
        assertEquals(fecha, result.getFecha());
        assertEquals(pago, result.getPago());
        assertEquals(estadoProgramada, result.getEstadoReservacion());

        // Verificar interacciones
        verify(usuarioRepository, times(1)).findByEmail(emailUsuario);
        verify(servicioRepository, times(1)).findById(idServicio);
        verify(estadoReservacionRepository, times(1)).findOneByNombre("Programada");
        verify(pagoRepository, times(1)).save(any(Pago.class));
        verify(reservacionRepository, times(1)).save(any(Reservacion.class));
    }


    @Test
    void testCrearReservacion_UsuarioNoEncontrado() {
        String emailUsuario = "noexistente@example.com";

        when(usuarioRepository.findByEmail(emailUsuario)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            reservacionService.crearReservacion(emailUsuario, null, 1L, null, null, null, LocalTime.now(),
                    LocalTime.now().plusHours(1), LocalDate.now(), "Tarjeta", "123", 100.0f);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).findByEmail(emailUsuario);
    }

    @Test
    void testCrearReservacion_ServicioNoEncontrado() {
        String emailUsuario = "cliente@example.com";
        Long idServicio = 1L;

        Usuario usuario = new Usuario();
        usuario.setEmail(emailUsuario);
        when(usuarioRepository.findByEmail(emailUsuario)).thenReturn(Optional.of(usuario));
        when(servicioRepository.findById(idServicio)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            reservacionService.crearReservacion(emailUsuario, null, idServicio, null, null, null, LocalTime.now(),
                    LocalTime.now().plusHours(1), LocalDate.now(), "Tarjeta", "123", 100.0f);
        });

        assertEquals("Servicio no encontrado", exception.getMessage());
        verify(servicioRepository, times(1)).findById(idServicio);
    }

    @Test
    void testCrearReservacion_EncargadoNoDisponible() {
        String emailUsuario = "cliente@example.com";
        Long idEncargado = 2L;

        // Definir las fechas y horas como String y convertirlas
        String fechaString = "2024-10-30"; // Formato: yyyy-MM-dd
        String horaInicioString = "09:00"; // Formato: HH:mm
        String horaFinalString = "10:00";  // Formato: HH:mm

        LocalDate fecha = LocalDate.parse(fechaString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalTime horaInicio = LocalTime.parse(horaInicioString, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime horaFinal = LocalTime.parse(horaFinalString, DateTimeFormatter.ofPattern("HH:mm"));

        String nombreDiaSemana = DiaSemanaEnum.fromDayOfWeek(fecha.getDayOfWeek()).getNombre();
        DiaAtencion diaAtencion = new DiaAtencion(nombreDiaSemana);

        Usuario usuario = new Usuario();
        usuario.setEmail(emailUsuario);

        Servicio servicio = new Servicio();
        servicio.setId(1L);
        servicio.setTrabajadores_simultaneos(1);

        // Configurar el horario de atención del servicio
        HorarioAtencionServicio horarioAtencion = new HorarioAtencionServicio();
        horarioAtencion.setHoraInicio(horaInicio);
        horarioAtencion.setHoraFinal(horaFinal);
        horarioAtencion.setDiaAtencion(diaAtencion);
        servicio.setHorariosAtencionServicios(Arrays.asList(horarioAtencion));

        // Configurar los mocks
        when(usuarioRepository.findByEmail(emailUsuario)).thenReturn(Optional.of(usuario));
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio));
        when(usuarioRepository.findById(idEncargado)).thenReturn(Optional.empty());
        when(diaAtencionRepository.findOneByNombre(nombreDiaSemana)).thenReturn(Optional.of(diaAtencion));

        // Ejecutar y verificar el resultado
        Exception exception = assertThrows(Exception.class, () -> {
            reservacionService.crearReservacion(emailUsuario, idEncargado, 1L, null, null, null, horaInicio,
                    horaFinal, fecha, "Tarjeta", "123", 100.0f);
        });

        assertEquals("Usuario encargado no encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(idEncargado);
    }

    @Test
    void testCrearReservacion_EstadoReservacionNoEncontrado() {
        String emailUsuario = "cliente@example.com";
        Long idServicio = 1L;
        String fechaString = "2024-10-30";
        String horaInicioString = "09:00";
        String horaFinalString = "10:00";

        // Parseo de fecha y horas
        LocalDate fecha = LocalDate.parse(fechaString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalTime horaInicio = LocalTime.parse(horaInicioString, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime horaFinal = LocalTime.parse(horaFinalString, DateTimeFormatter.ofPattern("HH:mm"));

        // Mock del usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(emailUsuario);

        // Mock del servicio
        Servicio servicio = new Servicio();
        servicio.setId(idServicio);
        servicio.setTrabajadores_simultaneos(1);

        // Configuración del día de atención
        String nombreDiaSemana = DiaSemanaEnum.fromDayOfWeek(fecha.getDayOfWeek()).getNombre();
        DiaAtencion diaAtencion = new DiaAtencion(nombreDiaSemana);
        when(diaAtencionRepository.findOneByNombre(nombreDiaSemana)).thenReturn(Optional.of(diaAtencion));

        // Mock del horario de atención del servicio
        HorarioAtencionServicio horarioAtencion = new HorarioAtencionServicio();
        horarioAtencion.setHoraInicio(horaInicio);
        horarioAtencion.setHoraFinal(horaFinal);
        horarioAtencion.setDiaAtencion(diaAtencion);
        servicio.setHorariosAtencionServicios(Arrays.asList(horarioAtencion));

        // Mock del encargado disponible
        Usuario encargado = new Usuario();
        encargado.setEmail("encargado@example.com");

        HorarioAtencionUsuario horarioEncargado = new HorarioAtencionUsuario();
        horarioEncargado.setHoraInicio(horaInicio);
        horarioEncargado.setHoraFinal(horaFinal);
        horarioEncargado.setDiaAtencion(diaAtencion);
        encargado.setHorariosAtencionUsuario(Arrays.asList(horarioEncargado));

        when(usuarioRepository.findUsuariosConPermisosParaServicio(idServicio)).thenReturn(Arrays.asList(encargado));

        // Mock para el estado de reservación no encontrado
        when(usuarioRepository.findByEmail(emailUsuario)).thenReturn(Optional.of(usuario));
        when(servicioRepository.findById(idServicio)).thenReturn(Optional.of(servicio));
        when(estadoReservacionRepository.findOneByNombre("Programada")).thenReturn(Optional.empty());

        // Ejecutar y verificar el resultado
        Exception exception = assertThrows(Exception.class, () -> {
            reservacionService.crearReservacion(emailUsuario, null, idServicio, null, null, null, horaInicio,
                    horaFinal, fecha, "Tarjeta", "123456", 100.0f);
        });

        assertEquals("Estado de reservación no encontrado", exception.getMessage());
        verify(estadoReservacionRepository, times(1)).findOneByNombre("Programada");
    }


    @Test
    void testCrearReservacion_MetodoPagoNoEncontrado() {
        String emailUsuario = "cliente@example.com";
        Long idServicio = 1L;
        String nombreMetodoPago = "Desconocido";
        String fechaString = "2024-10-30";
        String horaInicioString = "10:00";
        String horaFinalString = "12:00";

        LocalDate fecha = LocalDate.parse(fechaString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalTime horaInicio = LocalTime.parse(horaInicioString, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime horaFinal = LocalTime.parse(horaFinalString, DateTimeFormatter.ofPattern("HH:mm"));

        Usuario usuario = new Usuario();
        usuario.setEmail(emailUsuario);

        Servicio servicio = new Servicio();
        servicio.setId(idServicio);
        servicio.setTrabajadores_simultaneos(1);

        String nombreDiaSemana = DiaSemanaEnum.fromDayOfWeek(fecha.getDayOfWeek()).getNombre();
        DiaAtencion diaAtencion = new DiaAtencion(nombreDiaSemana);
        when(diaAtencionRepository.findOneByNombre(nombreDiaSemana)).thenReturn(Optional.of(diaAtencion));

        // Mock de horario de atención del servicio
        HorarioAtencionServicio horarioAtencion = new HorarioAtencionServicio();
        horarioAtencion.setHoraInicio(horaInicio);
        horarioAtencion.setHoraFinal(horaFinal);
        horarioAtencion.setDiaAtencion(diaAtencion);
        servicio.setHorariosAtencionServicios(Arrays.asList(horarioAtencion));

        // Mock del encargado disponible
        Usuario encargado = new Usuario();
        encargado.setEmail("encargado@example.com");

        HorarioAtencionUsuario horarioEncargado = new HorarioAtencionUsuario();
        horarioEncargado.setHoraInicio(horaInicio);
        horarioEncargado.setHoraFinal(horaFinal);
        horarioEncargado.setDiaAtencion(diaAtencion);
        encargado.setHorariosAtencionUsuario(Arrays.asList(horarioEncargado));

        when(usuarioRepository.findUsuariosConPermisosParaServicio(idServicio)).thenReturn(Arrays.asList(encargado));

        EstadoReservacion estadoProgramada = new EstadoReservacion();
        estadoProgramada.setNombre("Programada");

        when(usuarioRepository.findByEmail(emailUsuario)).thenReturn(Optional.of(usuario));
        when(servicioRepository.findById(idServicio)).thenReturn(Optional.of(servicio));
        when(estadoReservacionRepository.findOneByNombre("Programada")).thenReturn(Optional.of(estadoProgramada));
        when(metodoPagoRepository.findOneByNombre(nombreMetodoPago)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            reservacionService.crearReservacion(emailUsuario, null, idServicio, null, null, null, horaInicio,
                    horaFinal, fecha, nombreMetodoPago, "123", 100.0f);
        });

        assertEquals("Método de pago no encontrado", exception.getMessage());
        verify(metodoPagoRepository, times(1)).findOneByNombre(nombreMetodoPago);
    }

    @Test
    void testCancelarReservacion_Success() throws Exception {
        Long idReservacion = 1L;
        String motivoCancelacion = "Cambio de planes";
        String fechaCancelacionString = "2024-10-30";

        LocalDate fechaCancelacion = LocalDate.parse(fechaCancelacionString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Mock de reservación encontrada con estado "Programada"
        Usuario usuario = new Usuario();
        usuario.setEmail("cliente@example.com");

        Servicio servicio = new Servicio();
        servicio.setPorcentaje_reembolso(50.0f);

        Pago pago = new Pago();
        pago.setMonto(200.0f);

        EstadoReservacion estadoProgramada = new EstadoReservacion();
        estadoProgramada.setNombre("Programada");

        Reservacion reservacion = new Reservacion();
        reservacion.setId(idReservacion);
        reservacion.setUsuario(usuario);
        reservacion.setServicio(servicio);
        reservacion.setPago(pago);
        reservacion.setEstadoReservacion(estadoProgramada);

        when(reservacionRepository.findById(idReservacion)).thenReturn(Optional.of(reservacion));

        // Mock del estado de reservación "Cancelada"
        EstadoReservacion estadoCancelada = new EstadoReservacion();
        estadoCancelada.setNombre("Cancelada");
        when(estadoReservacionRepository.findOneByNombre("Cancelada")).thenReturn(Optional.of(estadoCancelada));

        // Mock para guardar la factura y cancelación
        when(facturaRepository.save(any(Factura.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cancelacionRepository.save(any(Cancelacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Ejecutar el método
        String resultado = reservacionService.cancelarReservacion(idReservacion, motivoCancelacion, fechaCancelacion);

        // Verificar los resultados
        assertEquals("Reservación cancelada exitosamente y factura generada.", resultado);
        verify(facturaRepository, times(1)).save(any(Factura.class));
        verify(cancelacionRepository, times(1)).save(any(Cancelacion.class));
        verify(reservacionRepository, times(1)).save(reservacion);
        assertEquals(estadoCancelada, reservacion.getEstadoReservacion());
    }

    @Test
    void testCancelarReservacion_ReservacionNoEncontrada() {
        Long idReservacion = 1L;
        String motivoCancelacion = "Cambio de planes";
        String fechaCancelacionString = "2024-10-30";
        LocalDate fechaCancelacion = LocalDate.parse(fechaCancelacionString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Mock para simular que la reservación no se encuentra
        when(reservacionRepository.findById(idReservacion)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            reservacionService.cancelarReservacion(idReservacion, motivoCancelacion, fechaCancelacion);
        });

        assertEquals("Reservación no encontrada", exception.getMessage());
        verify(reservacionRepository, times(1)).findById(idReservacion);
    }

    @Test
    void testCancelarReservacion_ReservacionYaCanceladaOCompletada() {
        Long idReservacion = 1L;
        String motivoCancelacion = "Cambio de planes";
        String fechaCancelacionString = "2024-10-30";
        LocalDate fechaCancelacion = LocalDate.parse(fechaCancelacionString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Mock de una reservación en estado diferente de "Programada"
        EstadoReservacion estadoCompletada = new EstadoReservacion();
        estadoCompletada.setNombre("Completada");

        Reservacion reservacion = new Reservacion();
        reservacion.setEstadoReservacion(estadoCompletada);

        when(reservacionRepository.findById(idReservacion)).thenReturn(Optional.of(reservacion));

        Exception exception = assertThrows(Exception.class, () -> {
            reservacionService.cancelarReservacion(idReservacion, motivoCancelacion, fechaCancelacion);
        });

        assertEquals("La reservación ya fue cancelada o completada.", exception.getMessage());
        verify(reservacionRepository, times(1)).findById(idReservacion);
    }

    @Test
    void testCompletarReservacion_Success() throws Exception {
        Long idReservacion = 1L;

        // Mock de reservación encontrada en estado "Programada"
        Usuario usuario = new Usuario();
        usuario.setEmail("cliente@example.com");

        Pago pago = new Pago();
        pago.setMonto(150.0f);

        EstadoReservacion estadoProgramada = new EstadoReservacion();
        estadoProgramada.setNombre("Programada");

        EstadoReservacion estadoCompletada = new EstadoReservacion();
        estadoCompletada.setNombre("Completada");

        Reservacion reservacion = new Reservacion();
        reservacion.setId(idReservacion);
        reservacion.setUsuario(usuario);
        reservacion.setPago(pago);
        reservacion.setEstadoReservacion(estadoProgramada);

        when(reservacionRepository.findById(idReservacion)).thenReturn(Optional.of(reservacion));
        when(estadoReservacionRepository.findOneByNombre("Completada")).thenReturn(Optional.of(estadoCompletada));
        when(facturaRepository.save(any(Factura.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Ejecutar el método
        Factura factura = reservacionService.completarReservacion(idReservacion);

        // Verificar resultados
        assertNotNull(factura);
        assertEquals(usuario, factura.getUsuario());
        assertEquals(reservacion, factura.getReservacion());
        assertEquals(pago.getMonto(), factura.getMonto());
        assertEquals(estadoCompletada, reservacion.getEstadoReservacion());

        // Verificar interacciones
        verify(reservacionRepository, times(1)).findById(idReservacion);
        verify(estadoReservacionRepository, times(1)).findOneByNombre("Completada");
        verify(facturaRepository, times(1)).save(any(Factura.class));
        verify(reservacionRepository, times(1)).save(reservacion);
    }

    @Test
    void testCompletarReservacion_ReservacionNoEncontrada() {
        Long idReservacion = 1L;

        // Mock para simular que la reservación no se encuentra
        when(reservacionRepository.findById(idReservacion)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            reservacionService.completarReservacion(idReservacion);
        });

        assertEquals("Reservación no encontrada", exception.getMessage());
        verify(reservacionRepository, times(1)).findById(idReservacion);
    }

    @Test
    void testCompletarReservacion_EstadoCompletadaNoEncontrado() {
        Long idReservacion = 1L;

        // Mock de reservación encontrada en estado "Programada"
        Usuario usuario = new Usuario();
        usuario.setEmail("cliente@example.com");

        Pago pago = new Pago();
        pago.setMonto(150.0f);

        EstadoReservacion estadoProgramada = new EstadoReservacion();
        estadoProgramada.setNombre("Programada");

        Reservacion reservacion = new Reservacion();
        reservacion.setId(idReservacion);
        reservacion.setUsuario(usuario);
        reservacion.setPago(pago);
        reservacion.setEstadoReservacion(estadoProgramada);

        when(reservacionRepository.findById(idReservacion)).thenReturn(Optional.of(reservacion));
        when(estadoReservacionRepository.findOneByNombre("Completada")).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            reservacionService.completarReservacion(idReservacion);
        });

        assertEquals("Estado de reservación 'Completada' no encontrado", exception.getMessage());
        verify(estadoReservacionRepository, times(1)).findOneByNombre("Completada");
    }

}
