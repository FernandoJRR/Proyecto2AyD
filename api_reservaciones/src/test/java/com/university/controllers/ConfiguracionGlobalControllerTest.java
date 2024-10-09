package com.university.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
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
}
