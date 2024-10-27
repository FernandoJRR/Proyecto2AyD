package com.university.repositories;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.university.models.Reservacion;
import com.university.models.Servicio;
import com.university.models.UnidadRecurso;
import com.university.models.Usuario;

@Repository
public interface ReservacionRepository extends CrudRepository<Reservacion, Long> {

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
}
