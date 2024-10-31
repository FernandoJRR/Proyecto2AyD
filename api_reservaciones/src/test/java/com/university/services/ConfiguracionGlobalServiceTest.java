package com.university.services;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.HashSet;

import javax.validation.Validator;

import com.university.models.ConfiguracionGlobal;
import com.university.models.dto.ConfiguracionGlobalDto;
import com.university.repositories.ConfiguracionGlobalRepository;

public class ConfiguracionGlobalServiceTest {

    @InjectMocks
    private ConfiguracionGlobalService configuracionGlobalService;

    @Mock
    private ConfiguracionGlobalRepository configuracionGlobalRepository;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private Validator validator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetConfig_Success() throws Exception {
        // Configurar el mock para retornar una configuración
        ConfiguracionGlobal configuracion = new ConfiguracionGlobal();
        when(configuracionGlobalRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(configuracion));

        // Ejecutar el método
        ConfiguracionGlobal result = configuracionGlobalService.getConfig();

        // Verificar los resultados
        assertNotNull(result);
        verify(configuracionGlobalRepository, times(1)).findFirstByOrderByIdAsc();
    }

    @Test
    public void testGetConfig_NotFound() {
        // Configurar el mock para devolver un Optional vacío
        when(configuracionGlobalRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());

        // Verificar que el método lanza una excepción cuando no se encuentra la configuración
        Exception exception = assertThrows(Exception.class, () -> {
            configuracionGlobalService.getConfig();
        });

        assertEquals("Configuraciones no encontradas", exception.getMessage());
    }

    @Test
    public void testActualizarConfig_Success() throws Exception {
        // Configurar el mock para retornar una configuración existente
        ConfiguracionGlobal configExistente = new ConfiguracionGlobal();
        configExistente.setId(1L);
        configExistente.setImagen(new byte[0]);
        configExistente.setMimeTypeImg("image/png");
        configExistente.setNombre("Configuración existente");

        // Cuando se llame al repositorio, devolver la configuración existente
        when(configuracionGlobalRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(configExistente));

        // Crear una nueva configuración para actualizar
        ConfiguracionGlobal nuevaConfig = new ConfiguracionGlobal();
        nuevaConfig.setNombre("Nueva Configuración");

        // Simular la actualización guardando el nuevo valor
        ConfiguracionGlobal configActualizada = new ConfiguracionGlobal();
        configActualizada.setId(1L);
        configActualizada.setImagen(new byte[0]);
        configActualizada.setMimeTypeImg("image/png");
        configActualizada.setNombre("Nueva Configuración");

        // Cuando se guarde la configuración, devolver la configuración actualizada
        when(configuracionGlobalRepository.save(any(ConfiguracionGlobal.class))).thenReturn(configActualizada);

        // Mockear la validación para que sea exitosa
        when(validator.validate(any())).thenReturn(new HashSet<>());  // No hay errores de validación

        // Ejecutar el método
        ConfiguracionGlobalDto resultado = configuracionGlobalService.actualizarConfig(nuevaConfig);

        // Verificar que el nombre de la nueva configuración sea el esperado
        assertEquals("Nueva Configuración", resultado.getNombre());  // Verificar que el campo "nombre" sea actualizado correctamente
        verify(configuracionGlobalRepository, times(1)).save(any(ConfiguracionGlobal.class));
    }

    @Test
    public void testActualizarConfig_NotFound() {
        // Configurar el mock para devolver un Optional vacío
        when(configuracionGlobalRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());

        // Verificar que el método lanza una excepción cuando no se encuentra la configuración
        Exception exception = assertThrows(Exception.class, () -> {
            configuracionGlobalService.actualizarConfig(new ConfiguracionGlobal());
        });

        assertEquals("Configuraciones no encontradas", exception.getMessage());
    }

    @Test
    public void testActualizarImagen_Success() throws Exception {
        // Configurar el mock para retornar una configuración existente
        ConfiguracionGlobal configExistente = new ConfiguracionGlobal();
        configExistente.setId(1L);
        configExistente.setImagen(new byte[0]);
        configExistente.setMimeTypeImg("image/png");

        when(configuracionGlobalRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(configExistente));
        when(multipartFile.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(multipartFile.getContentType()).thenReturn("image/png");
        when(configuracionGlobalRepository.save(any(ConfiguracionGlobal.class))).thenReturn(configExistente);

        // Ejecutar el método
        ConfiguracionGlobalDto resultado = configuracionGlobalService.actualizarImagen(multipartFile);

        // Verificar los resultados
        assertNotNull(resultado);
        assertArrayEquals(new byte[]{1, 2, 3}, configExistente.getImagen());
        assertEquals("image/png", configExistente.getMimeTypeImg());
        verify(configuracionGlobalRepository, times(1)).save(configExistente);
    }

    @Test
    public void testActualizarImagen_NoFile() {
        // Verificar que el método lanza una excepción cuando el archivo es nulo
        Exception exception = assertThrows(Exception.class, () -> {
            configuracionGlobalService.actualizarImagen(null);
        });

        assertEquals("Imagen vacia.", exception.getMessage());
    }

    @Test
    public void testActualizarImagen_NotFound() {
        // Configurar el mock para devolver un Optional vacío
        when(configuracionGlobalRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());

        // Verificar que el método lanza una excepción cuando no se encuentra la configuración
        Exception exception = assertThrows(Exception.class, () -> {
            configuracionGlobalService.actualizarImagen(multipartFile);
        });

        assertEquals("Configuraciones no encontradas", exception.getMessage());
    }
    @Test
    public void testGetConfigDto_Success() throws Exception {
        // Configurar el mock para retornar una configuración existente
        ConfiguracionGlobal configExistente = new ConfiguracionGlobal();
        configExistente.setId(1L);
        configExistente.setNombre("Configuración existente");
        configExistente.setImagen(new byte[0]);
        configExistente.setMimeTypeImg("image/png");

        // Cuando se llame al repositorio, devolver la configuración existente
        when(configuracionGlobalRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(configExistente));

        // Ejecutar el método
        ConfiguracionGlobalDto resultado = configuracionGlobalService.getConfigDto();

        // Verificar que los valores retornados son correctos
        assertNotNull(resultado);  // Verificar que el resultado no sea nulo
        assertEquals("Configuración existente", resultado.getNombre());  // Verificar que el nombre sea correcto
        assertEquals("http://localhost:8080/api/global_config/public/getImagen", resultado.getImagenString());  // Verificar que la URL de la imagen sea la correcta

        // Verificar que el repositorio fue llamado una vez
        verify(configuracionGlobalRepository, times(1)).findFirstByOrderByIdAsc();
    }

    @Test
    public void testGetConfigDto_NotFound() {
        // Configurar el mock para retornar una configuración vacía
        when(configuracionGlobalRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());

        // Ejecutar el método y verificar que lanza la excepción correcta
        Exception exception = assertThrows(Exception.class, () -> {
            configuracionGlobalService.getConfigDto();
        });

        // Verificar que el mensaje de la excepción sea el esperado
        assertEquals("Configuraciones no encontradas", exception.getMessage());

        // Verificar que el repositorio fue llamado una vez
        verify(configuracionGlobalRepository, times(1)).findFirstByOrderByIdAsc();
    }

}
