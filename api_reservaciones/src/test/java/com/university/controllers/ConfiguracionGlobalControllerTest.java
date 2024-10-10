package com.university.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.models.ConfiguracionGlobal;
import com.university.models.dto.ConfiguracionGlobalDto;
import com.university.services.ConfiguracionGlobalService;
import com.university.transformers.ApiBaseTransformer;

@SpringBootTest
@AutoConfigureMockMvc
public class ConfiguracionGlobalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ConfiguracionGlobalService configuracionGlobalService;

    @InjectMocks
    private ConfiguracionGlobalController configuracionGlobalController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(configuracionGlobalController).build();
    }

    @Test
    void testGetConfig_Success() throws Exception {
        // Simular un objeto ConfiguracionGlobalDto válido
        ConfiguracionGlobalDto configuracionGlobalDto = new ConfiguracionGlobalDto();
        configuracionGlobalDto.setNombre("Configuración General");
        configuracionGlobalDto.setImagenString("http://localhost:8080/api/global_config/public/getImagen");

        // Simular la respuesta del servicio
        when(configuracionGlobalService.getConfigDto()).thenReturn(configuracionGlobalDto);

        // Realizar la solicitud y verificar la respuesta esperada
        MvcResult result = mockMvc.perform(get("/api/global_config/public/getConfig")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // Verifica el estado de la respuesta HTTP
                .andReturn();

        // Convertir la respuesta de String a un objeto JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        // Verificar los valores en el objeto JSON deserializado
        assertEquals(200, response.getCode());  // Verificar el campo 'code'
        assertEquals("OK", response.getMessage());  // Verificar el mensaje
        ConfiguracionGlobalDto data = objectMapper.convertValue(response.getData(), ConfiguracionGlobalDto.class);
        assertEquals("Configuración General", data.getNombre());  // Verificar el campo 'nombre'

        // Verificar que el servicio fue llamado una vez
        verify(configuracionGlobalService, times(1)).getConfigDto();
    }


    @Test
    void testActualizarConfiguracion_Success() throws Exception {
        // Simular un objeto ConfiguracionGlobalDto válido
        ConfiguracionGlobalDto configuracionGlobalDto = new ConfiguracionGlobalDto();
        configuracionGlobalDto.setNombre("Configuración Actualizada");

        // Simular la respuesta del servicio
        when(configuracionGlobalService.actualizarConfig(any(ConfiguracionGlobal.class))).thenReturn(configuracionGlobalDto);

        // Realizar la solicitud y verificar la respuesta esperada
        MvcResult result = mockMvc.perform(patch("/api/global_config/private/actualizarConfiguracion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\": \"Configuración Actualizada\"}")
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())  // Verifica el estado de la respuesta HTTP
                .andReturn();

        // Convertir la respuesta de String a un objeto JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        // Verificar los valores en el objeto JSON deserializado
        assertEquals(200, response.getCode());  // Verificar el campo 'code'
        assertEquals("OK", response.getMessage());  // Verificar el mensaje
        ConfiguracionGlobalDto data = objectMapper.convertValue(response.getData(), ConfiguracionGlobalDto.class);
        assertEquals("Configuración Actualizada", data.getNombre());  // Verificar el campo 'nombre'

        // Verificar que el servicio fue llamado una vez
        verify(configuracionGlobalService, times(1)).actualizarConfig(any(ConfiguracionGlobal.class));
    }

    @Test
    void testActualizarConfiguracion_Failure() throws Exception {
        // Simular un caso en el que el servicio arroje una excepción
        when(configuracionGlobalService.actualizarConfig(any(ConfiguracionGlobal.class))).thenThrow(new Exception("Error al actualizar configuración"));

        // Realizar la solicitud de actualización y verificar la respuesta de error
        mockMvc.perform(patch("/api/global_config/private/actualizarConfiguracion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\": \"Configuración Actualizada\"}")
                .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest())  // Verifica que el estado de la respuesta sea 400 BAD REQUEST
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Error al actualizar configuración"));

        // Verificar que el servicio fue llamado una vez
        verify(configuracionGlobalService, times(1)).actualizarConfig(any(ConfiguracionGlobal.class));
    }


    @Test
    void testGetImagen_Success() throws Exception {
        // Simula una imagen en formato byte[]
        byte[] imagen = "fake-image-content".getBytes();
        ConfiguracionGlobal configGlobal = new ConfiguracionGlobal();
        configGlobal.setImagen(imagen);
        configGlobal.setMimeTypeImg("image/png");

        // Simula la respuesta del servicio
        when(configuracionGlobalService.getConfig()).thenReturn(configGlobal);

        // Realiza la solicitud GET para obtener la imagen
        MvcResult result = mockMvc.perform(get("/api/global_config/public/getImagen"))
                .andExpect(status().isOk())  // Verifica que el estado de la respuesta sea 200 OK
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "image/png"))  // Verifica el header Content-Type
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=image.png"))  // Verifica el header Content-Disposition
                .andReturn();

        // Verifica el contenido binario de la respuesta
        byte[] responseBytes = result.getResponse().getContentAsByteArray();
        assertEquals(imagen.length, responseBytes.length);  // Verifica que el contenido tenga el tamaño correcto

        // Verifica que el servicio fue llamado correctamente
        verify(configuracionGlobalService, times(1)).getConfig();
    }

    @Test
    void testGetImagen_Failure() throws Exception {
        // Simula una excepción en el servicio
        when(configuracionGlobalService.getConfig()).thenThrow(new Exception("Error interno del servidor"));

        // Realiza la solicitud GET y verifica que se maneje el error
        mockMvc.perform(get("/api/global_config/public/getImagen"))
                .andExpect(status().isInternalServerError())  // Verifica que el estado sea 500
                .andExpect(content().string("Error interno del servidor."));  // Verifica el mensaje de error

        // Verifica que el servicio fue llamado correctamente
        verify(configuracionGlobalService, times(1)).getConfig();
    }


    @Test
    void testActualizarImagen_Success() throws Exception {
        // Simula un archivo de imagen subido
        MockMultipartFile file = new MockMultipartFile("file", "imagen.png", MediaType.IMAGE_PNG_VALUE, "fake-image-content".getBytes());

        ConfiguracionGlobalDto configGlobalDto = new ConfiguracionGlobalDto();
        configGlobalDto.setNombre("Nueva Configuración");

        // Simula la respuesta del servicio
        when(configuracionGlobalService.actualizarImagen(any(MultipartFile.class))).thenReturn(configGlobalDto);

        // Realiza la solicitud POST para actualizar la imagen
        mockMvc.perform(multipart("/api/global_config/private/actualizarImagen")
                .file(file))
                .andExpect(status().isOk())  // Verifica que el estado de la respuesta sea 200 OK
                .andExpect(jsonPath("$.code").value(200))  // Verifica que el código de la respuesta sea 200
                .andExpect(jsonPath("$.message").value("OK"))  // Verifica el mensaje de la respuesta
                .andExpect(jsonPath("$.data.nombre").value("Nueva Configuración"))  // Verifica que los datos retornados sean correctos
                .andReturn();

        // Verifica que el servicio fue llamado correctamente
        verify(configuracionGlobalService, times(1)).actualizarImagen(any(MultipartFile.class));
    }

    @Test
    void testActualizarImagen_Failure() throws Exception {
        // Simula un archivo vacío
        MockMultipartFile file = new MockMultipartFile("file", "imagen.png", MediaType.IMAGE_PNG_VALUE, new byte[0]);

        // Simula una excepción en el servicio
        when(configuracionGlobalService.actualizarImagen(any(MultipartFile.class))).thenThrow(new Exception("Error al actualizar imagen"));

        // Realiza la solicitud POST y verifica que se maneje el error
        mockMvc.perform(multipart("/api/global_config/private/actualizarImagen")
                .file(file))
                .andExpect(status().isBadRequest())  // Verifica que el estado sea 400
                .andExpect(jsonPath("$.message").value("Error"))  // Verifica que el mensaje de error sea 'Error'
                .andExpect(jsonPath("$.error").value("Error al actualizar imagen"));  // Verifica que el mensaje de error sea correcto

        // Verifica que el servicio fue llamado correctamente
        verify(configuracionGlobalService, times(1)).actualizarImagen(any(MultipartFile.class));
    }
}
