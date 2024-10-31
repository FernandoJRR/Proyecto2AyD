package com.university.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.repositories.ReservacionRepository;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ReporteService {

    @Autowired
    private ReservacionRepository reservacionRepository;

    public Long generarReporteReservaciones(LocalDate fechaInicio, LocalDate fechaFin, String estado) throws Exception {
        if (fechaInicio == null || fechaFin == null) {
            throw new Exception("Las fechas de inicio y finalización son obligatorias.");
        }

        return reservacionRepository.countByFechaAndEstado(fechaInicio, fechaFin, estado);
    }

    public long contarReservacionesPorTiempoYCantidad(String tipoTiempo, int cantidad, String estado) throws Exception {
        // Calcular la fecha de inicio en función del tipo de tiempo y la cantidad
        // proporcionada
        LocalDate fechaInicio;
        LocalDate fechaFin = LocalDate.now(); // Fecha actual como límite superior

        switch (tipoTiempo.toLowerCase()) {
            case "semana":
                fechaInicio = fechaFin.minus(cantidad, ChronoUnit.WEEKS);
                break;
            case "mes":
                fechaInicio = fechaFin.minus(cantidad, ChronoUnit.MONTHS);
                break;
            case "anio":
                fechaInicio = fechaFin.minus(cantidad, ChronoUnit.YEARS);
                break;
            default:
                throw new IllegalArgumentException("Tipo de tiempo no válido. Usa 'semana', 'mes' o 'año'.");
        }

        // Si el estado es null, contar todas las reservaciones en el rango temporal
        if (estado == null) {
            return reservacionRepository.countByFechaBetween(fechaInicio, fechaFin);
        } else {
            return reservacionRepository.countByFechaBetweenAndEstadoReservacion_Nombre(fechaInicio, fechaFin, estado);
        }
    }

    public List<Map.Entry<String, Long>> topServiciosSolicitados(LocalDate fechaInicio, LocalDate fechaFin) {
        // Validación de fechas
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
        // Consulta para obtener el conteo de cada servicio entre las fechas
        // especificadas
        List<Object[]> resultados = reservacionRepository.contarServiciosPorFecha(fechaInicio, fechaFin);

        // Mapear los resultados a una lista de entradas con el nombre del servicio y el
        // conteo
        Map<String, Long> conteoServicios = resultados.stream()
                .collect(Collectors.toMap(
                        resultado -> (String) resultado[0], // Nombre del servicio
                        resultado -> (Long) resultado[1] // Conteo de reservaciones
                ));

        // Obtener el top 5 de servicios ordenados por el conteo en orden descendente
        return conteoServicios.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .limit(5)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> obtenerServiciosMasReservados(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Object[]> resultados = reservacionRepository.contarReservasPorServicio(fechaInicio, fechaFin);
        List<Map<String, Object>> reporte = new ArrayList<>();

        for (Object[] resultado : resultados) {
            Map<String, Object> servicio = new HashMap<>();
            servicio.put("nombre", resultado[0]);
            servicio.put("totalReservas", resultado[1]);
            reporte.add(servicio);
        }

        return reporte;
    }

    public List<Map<String, Object>> obtenerTopClientesConMasReservaciones(LocalDate fechaInicio, LocalDate fechaFin, int topN) {
        List<Object[]> resultados = reservacionRepository.contarReservacionesPorCliente(fechaInicio, fechaFin);

        return resultados.stream()
                .limit(topN)
                .map(obj -> {
                    Map<String, Object> clienteData = new HashMap<>();
                    clienteData.put("clienteId", obj[0]);
                    clienteData.put("cantidadReservaciones", obj[1]);
                    return clienteData;
                })
                .collect(Collectors.toList());
    }
}
