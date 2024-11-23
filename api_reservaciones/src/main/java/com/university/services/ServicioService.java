package com.university.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.models.DuracionServicio;
import com.university.models.HorarioAtencionServicio;
import com.university.models.Servicio;
import com.university.models.UnidadRecurso;
import com.university.models.Usuario;
import com.university.models.request.CreateServicioDto;
import com.university.repositories.DuracionServicioRepository;
import com.university.repositories.HorarioAtencionServicioRepository;
import com.university.repositories.ServicioRepository;
import com.university.repositories.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

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

    public Servicio updateServicio(Long id, Servicio servicio) {
        Servicio servicioExistente = servicioRepository.findById(id).orElse(null);
        if (servicioExistente != null) {
            return servicioRepository.save(servicioExistente);
        }
        return null;
    }
}
