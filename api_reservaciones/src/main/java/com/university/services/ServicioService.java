package com.university.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.models.DiaAtencion;
import com.university.models.DuracionServicio;
import com.university.models.HorarioAtencionServicio;
import com.university.models.Servicio;
import com.university.models.UnidadRecurso;
import com.university.models.Usuario;
import com.university.models.request.CreateServicioDto;
import com.university.repositories.DiaAtencionRepository;
import com.university.repositories.DuracionServicioRepository;
import com.university.repositories.HorarioAtencionServicioRepository;
import com.university.repositories.ServicioRepository;
import com.university.repositories.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private DiaAtencionRepository diaAtencionRepository;

    @Autowired
    private DuracionServicioRepository duracionServicioRepository;

    @Autowired
    private HorarioAtencionServicioRepository horarioAtencionServicioRepository;

    public List<Servicio> getServicios() {
        return (List<Servicio>) servicioRepository.findAll();
    }

    public Servicio getServicio(Long id) throws Exception {
        Servicio servicio = servicioRepository.findById(id).orElse(null);
        // si el rol no existe lanzamos error
        if (servicio == null) {
            throw new Exception("Servicio no encontrado.");
        }
        return servicio;
    }

    public List<Servicio> getServicioByNegocioId(Long idNegocio) throws Exception {
        List<Servicio> servicios = servicioRepository.findByNegocioId(idNegocio);
        return servicios;
    }

    public List<Usuario> getEncargadosByServicio(Long idServicio) throws Exception {
        List<Usuario> encargados = usuarioRepository.findUsuariosByServicioId(idServicio);
        return encargados;
    }

    public List<UnidadRecurso> getUnidadesRecursoByServicio(Long idServicio) throws Exception {
        List<UnidadRecurso> encargados = servicioRepository.findUnidadesRecursoByServicioId(idServicio);
        return encargados;
    }

    @Transactional
    public Servicio createServicio(CreateServicioDto servicioDto) throws Exception {
        Servicio servicio = servicioDto.getServicio();
        DuracionServicio duracionServicio = servicioDto.getDuracionServicio();
        List<HorarioAtencionServicio> horariosAtencionServicio = servicioDto.getHorariosAtencionServicio();

        if (duracionServicio == null) {
            throw new Exception("Debes asignar una duracion al servicio");
        }

        Servicio servicioCreado = servicioRepository.save(servicio);

        // Se le asigna una duracion al Servicio
        DuracionServicio duracionServicioCreado = new DuracionServicio(
                servicioCreado, duracionServicio.getMinutos(), duracionServicio.getHoras());

        duracionServicioRepository.save(duracionServicioCreado);

        servicioCreado.setDuracionServicio(duracionServicioCreado);
        servicioCreado = servicioRepository.save(servicioCreado);

        List<HorarioAtencionServicio> horariosAtencionServicioCreados = new ArrayList<>();
        // Se crean los horarios de atencion que tendra el usuario
        for (HorarioAtencionServicio horarioAtencionServicio : horariosAtencionServicio) {
            HorarioAtencionServicio horarioAtencionServicioCreado = new HorarioAtencionServicio(
                    horarioAtencionServicio.getHoraInicio(), horarioAtencionServicio.getHoraFinal(),
                    horarioAtencionServicio.getDiaAtencion());

            horarioAtencionServicioCreado.setServicio(servicioCreado);

            horarioAtencionServicioRepository.save(horarioAtencionServicioCreado);
            horariosAtencionServicioCreados.add(horarioAtencionServicioCreado);
        }

        servicioCreado.setHorariosAtencionServicios(horariosAtencionServicioCreados);
        servicioCreado = servicioRepository.save(servicioCreado);

        return servicioCreado;
    }

    @Transactional
    public Servicio updateServicio(Long id, Servicio servicioActualizado) throws Exception {
        // Buscar el servicio existente
        Servicio servicioExistente = servicioRepository.findById(id)
                .orElseThrow(() -> new Exception("Servicio no encontrado"));

        // Validar y actualizar nombre
        if (servicioActualizado.getNombre() != null && !servicioActualizado.getNombre().isBlank()) {
            servicioExistente.setNombre(servicioActualizado.getNombre());
        }

        // Validar y actualizar negocio
        if (servicioActualizado.getNegocio() != null) {
            servicioExistente.setNegocio(servicioActualizado.getNegocio());
        }

        // Validar y actualizar costo
        if (servicioActualizado.getCosto() != null && servicioActualizado.getCosto() >= 0) {
            servicioExistente.setCosto(servicioActualizado.getCosto());
        } else if (servicioActualizado.getCosto() != null) {
            throw new Exception("El costo del servicio no puede ser negativo.");
        }

        // Validar y actualizar días de cancelación
        if (servicioActualizado.getDias_cancelacion() != null && servicioActualizado.getDias_cancelacion() >= 0) {
            servicioExistente.setDias_cancelacion(servicioActualizado.getDias_cancelacion());
        } else if (servicioActualizado.getDias_cancelacion() != null) {
            throw new Exception("Los días de cancelación no pueden ser negativos.");
        }

        // Validar y actualizar porcentaje de reembolso
        if (servicioActualizado.getPorcentaje_reembolso() != null &&
                servicioActualizado.getPorcentaje_reembolso() >= 0 &&
                servicioActualizado.getPorcentaje_reembolso() <= 100) {
            servicioExistente.setPorcentaje_reembolso(servicioActualizado.getPorcentaje_reembolso());
        } else if (servicioActualizado.getPorcentaje_reembolso() != null) {
            throw new Exception("El porcentaje de reembolso debe estar entre 0 y 100.");
        }

        // Validar y actualizar trabajadores simultáneos
        if (servicioActualizado.getTrabajadores_simultaneos() != null &&
                servicioActualizado.getTrabajadores_simultaneos() >= 0) {
            servicioExistente.setTrabajadores_simultaneos(servicioActualizado.getTrabajadores_simultaneos());
        } else if (servicioActualizado.getTrabajadores_simultaneos() != null) {
            throw new Exception("El número de trabajadores simultáneos no puede ser negativo.");
        }

        // Validar y actualizar asignación automática
        servicioExistente.setAsignacion_automatica(servicioActualizado.isAsignacion_automatica());

        // Validar y actualizar duración del servicio
        if (servicioActualizado.getDuracionServicio() != null) {
            DuracionServicio nuevaDuracion = servicioActualizado.getDuracionServicio();
            if (nuevaDuracion.getHoras() < 0 || nuevaDuracion.getMinutos() < 0) {
                throw new Exception("La duración del servicio no puede ser negativa.");
            }
            DuracionServicio duracionExistente = duracionServicioRepository.findById(servicioExistente.getDuracionServicio().getId())
                    .orElseThrow(() -> new Exception("Duración del servicio no encontrada."));
            duracionExistente.setHoras(nuevaDuracion.getHoras());
            duracionExistente.setMinutos(nuevaDuracion.getMinutos());
            duracionServicioRepository.save(duracionExistente);
            servicioExistente.setDuracionServicio(duracionExistente);
        }

          // Validar y actualizar horarios de atención
    if (servicioActualizado.getHorariosAtencionServicios() != null) {
        List<HorarioAtencionServicio> nuevosHorarios = servicioActualizado.getHorariosAtencionServicios();
        List<HorarioAtencionServicio> horariosActuales = servicioExistente.getHorariosAtencionServicios();

        // Crear una lista auxiliar para manejar las actualizaciones
        List<HorarioAtencionServicio> horariosActualizados = new ArrayList<>();

        for (HorarioAtencionServicio nuevoHorario : nuevosHorarios) {
            if (nuevoHorario.getId() == null) {
                // Es un nuevo horario
                nuevoHorario.setServicio(servicioExistente);
                horariosActualizados.add(nuevoHorario);
            } else {
                // Actualizar horario existente
                HorarioAtencionServicio horarioExistente = horariosActuales.stream()
                        .filter(h -> h.getId().equals(nuevoHorario.getId()))
                        .findFirst()
                        .orElseThrow(() -> new Exception("Horario no encontrado con ID: " + nuevoHorario.getId()));

                horarioExistente.setHoraInicio(nuevoHorario.getHoraInicio());
                horarioExistente.setHoraFinal(nuevoHorario.getHoraFinal());
                horarioExistente.setDiaAtencion(nuevoHorario.getDiaAtencion());
                horariosActualizados.add(horarioExistente);
            }
        }

        // Reemplazar la colección con los horarios actualizados
        horariosActuales.clear();
        horariosActuales.addAll(horariosActualizados);
    }

        // Guardar y retornar el servicio actualizado
        return servicioRepository.save(servicioExistente);
    }
}
