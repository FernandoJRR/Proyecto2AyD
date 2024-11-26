package com.university.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.university.models.DiaAtencion;
import com.university.models.DuracionServicio;
import com.university.models.HorarioAtencionServicio;
import com.university.models.Servicio;
import com.university.models.request.CreateServicioDto;
import com.university.repositories.DuracionServicioRepository;
import com.university.repositories.HorarioAtencionServicioRepository;
import com.university.repositories.ServicioRepository;

public class ServicioServiceTest {

    @InjectMocks
    private ServicioService servicioService;

    @Mock
    private ServicioRepository servicioRepository;

    @Mock
    private DuracionServicioRepository duracionServicioRepository;

    @Mock
    private HorarioAtencionServicioRepository horarioAtencionServicioRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetServicio_Success() throws Exception {
        Long idServicio = 1L;
        Servicio servicio = new Servicio();
        servicio.setId(idServicio);
        servicio.setNombre("Consulta General");

        when(servicioRepository.findById(idServicio)).thenReturn(Optional.of(servicio));

        Servicio result = servicioService.getServicio(idServicio);

        assertNotNull(result);
        assertEquals(idServicio, result.getId());
        assertEquals("Consulta General", result.getNombre());

        verify(servicioRepository, times(1)).findById(idServicio);
    }

    @Test
    void testGetServicio_NotFound() {
        Long idServicio = 1L;

        when(servicioRepository.findById(idServicio)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            servicioService.getServicio(idServicio);
        });

        assertEquals("Servicio no encontrado.", exception.getMessage());
        verify(servicioRepository, times(1)).findById(idServicio);
    }

    @Test
    void testCreateServicio_Success() throws Exception {
        // Preparación del DTO de entrada
        CreateServicioDto servicioDto = new CreateServicioDto();
        Servicio servicio = new Servicio();
        servicio.setNombre("Terapia Física");

        DuracionServicio duracionServicio = new DuracionServicio();
        duracionServicio.setHoras(1);
        duracionServicio.setMinutos(30);

        HorarioAtencionServicio horario1 = new HorarioAtencionServicio();
        horario1.setHoraInicio(LocalTime.of(9, 0));
        horario1.setHoraFinal(LocalTime.of(12, 0));
        horario1.setDiaAtencion(new DiaAtencion("Lunes"));

        servicioDto.setServicio(servicio);
        servicioDto.setDuracionServicio(duracionServicio);
        servicioDto.setHorariosAtencionServicio(List.of(horario1));

        when(servicioRepository.save(any(Servicio.class))).thenReturn(servicio);
        when(duracionServicioRepository.save(any(DuracionServicio.class))).thenAnswer(i -> i.getArgument(0));
        when(horarioAtencionServicioRepository.save(any(HorarioAtencionServicio.class))).thenAnswer(i -> i.getArgument(0));

        Servicio result = servicioService.createServicio(servicioDto);

        assertNotNull(result);
        assertEquals("Terapia Física", result.getNombre());

        verify(servicioRepository, times(3)).save(servicio);
        verify(duracionServicioRepository, times(1)).save(any(DuracionServicio.class));
        verify(horarioAtencionServicioRepository, times(1)).save(any(HorarioAtencionServicio.class));
    }

    @Test
    void testCreateServicio_MissingDuracionServicio() {
        CreateServicioDto servicioDto = new CreateServicioDto();
        Servicio servicio = new Servicio();
        servicio.setNombre("Consulta Especializada");

        servicioDto.setServicio(servicio);
        servicioDto.setHorariosAtencionServicio(List.of(new HorarioAtencionServicio()));

        Exception exception = assertThrows(Exception.class, () -> {
            servicioService.createServicio(servicioDto);
        });

        assertEquals("Debes asignar una duracion al servicio", exception.getMessage());
    }

    @Test
    void testCreateServicio_NoHorariosAtencion() throws Exception {
        CreateServicioDto servicioDto = new CreateServicioDto();
        Servicio servicio = new Servicio();
        servicio.setNombre("Radiología");

        DuracionServicio duracionServicio = new DuracionServicio();
        duracionServicio.setHoras(0);
        duracionServicio.setMinutos(30);

        servicioDto.setServicio(servicio);
        servicioDto.setDuracionServicio(duracionServicio);
        servicioDto.setHorariosAtencionServicio(Collections.emptyList());

        when(servicioRepository.save(any(Servicio.class))).thenReturn(servicio);
        when(duracionServicioRepository.save(any(DuracionServicio.class))).thenAnswer(i -> i.getArgument(0));

        Servicio result = servicioService.createServicio(servicioDto);

        assertNotNull(result);
        assertEquals("Radiología", result.getNombre());

        verify(servicioRepository, times(3)).save(servicio);
        verify(duracionServicioRepository, times(1)).save(any(DuracionServicio.class));
        verify(horarioAtencionServicioRepository, times(0)).save(any(HorarioAtencionServicio.class));
    }

    @Test
    void testUpdateServicio_Success() throws Exception {
        Long idServicio = 1L;
        Servicio servicioExistente = new Servicio();
        servicioExistente.setId(idServicio);
        servicioExistente.setNombre("Consulta General");

        // Configura el mock para devolver el servicio existente en la búsqueda
        when(servicioRepository.findById(idServicio)).thenReturn(Optional.of(servicioExistente));
        when(servicioRepository.save(servicioExistente)).thenReturn(servicioExistente);

        // Llama al método de actualización
        Servicio result = servicioService.updateServicio(idServicio, servicioExistente);

        assertNotNull(result);
        assertEquals("Consulta General", result.getNombre());

        // Verifica que el servicio se ha guardado correctamente
        verify(servicioRepository, times(1)).findById(idServicio);
        verify(servicioRepository, times(1)).save(servicioExistente);
    }

    @Test
    void testUpdateServicio_NotFound() throws Exception {
        Long idServicio = 1L;
        Servicio servicioActualizado = new Servicio();
        servicioActualizado.setId(idServicio);
        servicioActualizado.setNombre("Consulta Especializada");

        when(servicioRepository.findById(idServicio)).thenReturn(Optional.empty());

        Servicio result = servicioService.updateServicio(idServicio, servicioActualizado);

        assertNull(result);
        verify(servicioRepository, times(1)).findById(idServicio);
        verify(servicioRepository, times(0)).save(any(Servicio.class));
    }

    // Helper para convertir objetos a JSON String
    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetServicios_Success() {
        // Simulación de una lista de servicios
        Servicio servicio1 = new Servicio("Servicio 1");
        Servicio servicio2 = new Servicio("Servicio 2");

        List<Servicio> serviciosMock = Arrays.asList(servicio1, servicio2);

        // Configuración del mock para devolver la lista de servicios
        when(servicioRepository.findAll()).thenReturn(serviciosMock);

        // Llamada al método de servicio
        List<Servicio> result = servicioService.getServicios();

        // Verificaciones
        assertNotNull(result, "La lista de servicios no debe ser nula");
        assertEquals(2, result.size(), "La lista de servicios debe tener 2 elementos");
        assertEquals("Servicio 1", result.get(0).getNombre(), "El primer servicio debe ser 'Servicio 1'");
        assertEquals("Servicio 2", result.get(1).getNombre(), "El segundo servicio debe ser 'Servicio 2'");

        // Verificación de interacción con el repositorio
        verify(servicioRepository).findAll();
    }
}
