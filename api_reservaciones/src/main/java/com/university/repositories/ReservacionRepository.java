package com.university.repositories;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.university.models.Reservacion;
import com.university.models.Servicio;
import com.university.models.UnidadRecurso;
import com.university.models.Usuario;

@Repository
public interface ReservacionRepository extends CrudRepository<Reservacion, Long> {

    List<Reservacion> findByUsuario(Usuario usuario);

    @Override
    List<Reservacion> findAll();

    boolean existsByEncargadoAndFechaAndHoraInicioLessThanEqualAndHoraFinalGreaterThanEqual(Usuario encargado,
            LocalDate fecha,
            LocalTime horaFinal, LocalTime horaInicio);

    boolean existsByUnidadRecursoAndFechaAndHoraInicioLessThanEqualAndHoraFinalGreaterThanEqual(
            UnidadRecurso unidadRecurso, LocalDate fecha, LocalTime horaFinal, LocalTime horaInicio);

    boolean existsByEncargadoAndFechaAndHoraInicioLessThanEqualAndHoraFinalGreaterThanEqualAndEstadoReservacion_Nombre(
            Usuario encargado, LocalDate fecha, LocalTime horaFinal, LocalTime horaInicio, String estadoNombre);

    boolean existsByUnidadRecursoAndFechaAndHoraInicioLessThanEqualAndHoraFinalGreaterThanEqualAndEstadoReservacion_Nombre(
            UnidadRecurso unidadRecurso, LocalDate fecha, LocalTime horaFinal, LocalTime horaInicio,
            String estadoNombre);

    int countByServicioAndFechaAndHoraInicioLessThanEqualAndHoraFinalGreaterThanEqualAndEstadoReservacion_Nombre(
            Servicio servicio, LocalDate fecha, LocalTime horaFinal, LocalTime horaInicio,
            String estadoReservacionNombre);

    int countByEncargadoAndEstadoReservacion_Nombre(Usuario encargado, String estadoNombre);

    @Query("SELECT COUNT(r) FROM Reservacion r WHERE r.fecha BETWEEN :fechaInicio AND :fechaFin " +
            "AND (:estado IS NULL OR r.estadoReservacion.nombre = :estado)")
    Long countByFechaAndEstado(@Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("estado") String estado);

    Long countByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    Long countByFechaBetweenAndEstadoReservacion_Nombre(LocalDate fechaInicio, LocalDate fechaFin, String estado);

    @Query("SELECT s.nombre, COUNT(r) FROM Reservacion r JOIN r.servicio s " +
            "WHERE r.fecha >= :fechaInicio AND r.fecha <= :fechaFin " +
            "GROUP BY s.nombre ORDER BY COUNT(r) DESC")
    List<Object[]> contarServiciosPorFecha(@Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT r.servicio.nombre, COUNT(r) as totalReservas " +
            "FROM Reservacion r " +
            "WHERE r.fecha BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY r.servicio.nombre " +
            "ORDER BY totalReservas DESC")
    List<Object[]> contarReservasPorServicio(@Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT r.usuario.id, COUNT(r) FROM Reservacion r " +
            "WHERE r.fecha BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY r.usuario.id " +
            "ORDER BY COUNT(r) DESC")
    List<Object[]> contarReservacionesPorCliente(@Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);
}
