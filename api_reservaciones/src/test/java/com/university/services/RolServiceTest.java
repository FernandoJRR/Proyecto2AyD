package com.university.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.university.models.Permiso;
import com.university.models.Rol;
import com.university.models.Servicio;
import com.university.models.request.CreateRolDto;
import com.university.models.request.PermisoRolRequest;
import com.university.models.request.ServicioRolRequest;
import com.university.repositories.RolRepository;

public class RolServiceTest {

    @InjectMocks
    private RolService rolService;

    @Mock
    private RolRepository rolRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa los mocks
    }

    @Test
    void testGetRoles_Success() {
        List<Rol> roles = Arrays.asList(new Rol("Admin"), new Rol("User"));
        when(rolRepository.findAll()).thenReturn(roles);

        List<Rol> result = rolService.getRoles();

        assertEquals(2, result.size());
        verify(rolRepository, times(1)).findAll();
    }

    @Test
    void testGetRol_Success() throws Exception {
        String rolStr = "Admin";
        Rol rol = new Rol(rolStr);
        when(rolRepository.findOneByNombre(rolStr)).thenReturn(Optional.of(rol));

        Rol result = rolService.getRol(rolStr);

        assertNotNull(result);
        assertEquals(rolStr, result.getNombre());
        verify(rolRepository, times(1)).findOneByNombre(rolStr);
    }

    @Test
    void testGetRol_NotFound() {
        String rolStr = "NonExistentRole";
        when(rolRepository.findOneByNombre(rolStr)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            rolService.getRol(rolStr);
        });

        assertEquals("Rol no encontrado.", exception.getMessage());
        verify(rolRepository, times(1)).findOneByNombre(rolStr);
    }

    @Test
    void testCreateRol_Success() throws Exception {
        CreateRolDto rolDto = new CreateRolDto();
        Rol rol = new Rol("Manager");
        rolDto.setRol(rol);

        Permiso permiso = new Permiso("MANAGE_USERS", "RutaMock");
        Servicio servicio = new Servicio("Service1");
        rolDto.setPermisos(Collections.singletonList(permiso));
        rolDto.setServicios(Collections.singletonList(servicio));

        when(rolRepository.save(any(Rol.class))).thenReturn(rol);

        Rol result = rolService.createRol(rolDto);

        assertNotNull(result);
        assertEquals("Manager", result.getNombre());
        assertEquals(1, result.getPermisos().size());
        assertEquals(1, result.getServicios().size());
        verify(rolRepository, times(2)).save(any(Rol.class));
    }

    @Test
    void testCreateRol_NoPermisos() {
        CreateRolDto rolDto = new CreateRolDto();
        Rol rol = new Rol("Manager");
        rolDto.setRol(rol);
        rolDto.setPermisos(Collections.emptyList()); // Initialize permisosRol as empty
        rolDto.setServicios(Collections.singletonList(new Servicio("Service1")));

        Exception exception = assertThrows(Exception.class, () -> {
            rolService.createRol(rolDto);
        });

        assertEquals("Debes asignar permisos al rol", exception.getMessage());
    }

    @Test
    void testCreateRol_NoServicios() {
        CreateRolDto rolDto = new CreateRolDto();
        Rol rol = new Rol("Manager");
        rolDto.setRol(rol);
        rolDto.setPermisos(Collections.singletonList(new Permiso("MANAGE_USERS", "RutaMock")));
        rolDto.setServicios(Collections.emptyList()); // Initialize serviciosRol as empty

        Exception exception = assertThrows(Exception.class, () -> {
            rolService.createRol(rolDto);
        });

        assertEquals("Debes asignar servicios al rol", exception.getMessage());
    }

    @Test
    void testActualizarPermisosRol_Success() throws Exception {
        Long rolId = 1L;
        Rol rol = new Rol("Admin");
        rol.setPermisos(new ArrayList<>());
        PermisoRolRequest rolPermiso = new PermisoRolRequest();
        rolPermiso.setIdRol(rolId);
        rolPermiso.setPermisos(Collections.singletonList(new Permiso("VIEW_DASHBOARD", "RutaMock")));

        when(rolRepository.findById(rolId)).thenReturn(Optional.of(rol));
        when(rolRepository.save(any(Rol.class))).thenReturn(rol);

        Rol result = rolService.actualizarPermisosRol(rolPermiso);

        assertNotNull(result);
        assertEquals(1, result.getPermisos().size());
        verify(rolRepository, times(1)).save(rol);
    }

    @Test
    void testActualizarServiciosRol_Success() throws Exception {
        Long rolId = 1L;
        Rol rol = new Rol("Admin");
        rol.setServicios(new ArrayList<>());
        ServicioRolRequest rolServicio = new ServicioRolRequest();
        rolServicio.setIdRol(rolId);
        rolServicio.setServicios(Collections.singletonList(new Servicio("Service1")));

        when(rolRepository.findById(rolId)).thenReturn(Optional.of(rol));
        when(rolRepository.save(any(Rol.class))).thenReturn(rol);

        Rol result = rolService.actualizarServiciosRol(rolServicio);

        assertNotNull(result);
        assertEquals(1, result.getServicios().size());
        verify(rolRepository, times(1)).save(rol);
    }

    @Test
    void testAgregarPermisoRol_Success() throws Exception {
        Rol rol = new Rol("Admin");
        rol.setId(1L);
        Permiso permiso = new Permiso("MANAGE_USERS", "RutaMock");

        when(rolRepository.existsById(rol.getId())).thenReturn(true);
        when(rolRepository.findById(rol.getId())).thenReturn(Optional.of(rol));
        when(rolRepository.save(any(Rol.class))).thenReturn(rol);

        Rol result = rolService.agregarPermisoRol(rol, permiso);

        assertNotNull(result);
        assertEquals(1, result.getPermisos().size());
        verify(rolRepository, times(1)).save(rol);
    }

    @Test
    void testAgregarServicioRol_Success() throws Exception {
        Rol rol = new Rol("Admin");
        rol.setId(1L);
        Servicio servicio = new Servicio("Service1");

        when(rolRepository.existsById(rol.getId())).thenReturn(true);
        when(rolRepository.findById(rol.getId())).thenReturn(Optional.of(rol));
        when(rolRepository.save(any(Rol.class))).thenReturn(rol);

        Rol result = rolService.agregarServicioRol(rol, servicio);

        assertNotNull(result);
        assertEquals(1, result.getServicios().size());
        verify(rolRepository, times(1)).save(rol);
    }

}
