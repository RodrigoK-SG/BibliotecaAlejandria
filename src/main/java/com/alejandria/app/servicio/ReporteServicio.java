package com.alejandria.app.servicio;

import com.alejandria.app.repositorio.PedidoRepositorio;
import com.alejandria.app.repositorio.DetallePedidoRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReporteServicio {

    private final PedidoRepositorio pedidoRepositorio;
    private final DetallePedidoRepositorio detallePedidoRepositorio;

    public Map<String, Object> generarReporteMensual(String anioMes) {
        Map<String, Object> reporte = new HashMap<>();
        
        LocalDate fechaSeleccionada = (anioMes != null && !anioMes.isEmpty()) 
                ? LocalDate.parse(anioMes + "-01") 
                : LocalDate.now().withDayOfMonth(1);
                
        LocalDateTime inicioMes = fechaSeleccionada.atStartOfDay();
        LocalDateTime finMes = fechaSeleccionada.withDayOfMonth(fechaSeleccionada.lengthOfMonth()).atTime(LocalTime.MAX);

        // ==========================================
        // 1. KPIs Generales (Mes actual seleccionado)
        // ==========================================
        Double ingresos = pedidoRepositorio.sumarIngresosPorFecha(inicioMes, finMes);
        reporte.put("ingresosTotales", ingresos != null ? ingresos : 0.0);
        
        reporte.put("librosVendidos", detallePedidoRepositorio.sumarLibrosVendidosPorFecha(inicioMes, finMes));
        
        String nombreMes = fechaSeleccionada.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "ES")));
        reporte.put("mesActualTexto", nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1));
        reporte.put("mesActualInput", fechaSeleccionada.format(DateTimeFormatter.ofPattern("yyyy-MM")));

        // ==========================================
        // 2. Gráfico de Dona: Top 5 Libros
        // ==========================================
        List<Object[]> top5Raw = detallePedidoRepositorio.obtenerTopLibrosVendidos(inicioMes, finMes, PageRequest.of(0, 5));
        List<String> topLabels = new ArrayList<>();
        List<Long> topData = new ArrayList<>();
        
        for (Object[] fila : top5Raw) {
            topLabels.add((String) fila[0]);
            topData.add(((Number) fila[1]).longValue());
        }
        reporte.put("topLibrosLabels", topLabels);
        reporte.put("topLibrosData", topData);

        // ==========================================
        // 3. NUEVO GRÁFICO: Evolución de Ingresos (Últimos 6 meses)
        // ==========================================
        List<String> evolucionLabels = new ArrayList<>();
        List<Double> evolucionData = new ArrayList<>();

        // Iteramos desde 5 hasta 0. Ej: Si estamos en Junio (0), retrocede hasta Enero (5). Total = 6 meses.
        for (int i = 5; i >= 0; i--) {
            LocalDate mesIteracion = fechaSeleccionada.minusMonths(i);
            LocalDateTime inicioIter = mesIteracion.withDayOfMonth(1).atStartOfDay();
            LocalDateTime finIter = mesIteracion.withDayOfMonth(mesIteracion.lengthOfMonth()).atTime(LocalTime.MAX);
            
            // Agregamos el nombre del mes (Ej: "ENE", "FEB") a la lista de etiquetas
            evolucionLabels.add(mesIteracion.format(DateTimeFormatter.ofPattern("MMM", new Locale("es", "ES"))).toUpperCase());
            
            // Calculamos los ingresos de ese mes y los agregamos a la lista de datos
            Double totalMes = pedidoRepositorio.sumarIngresosPorFecha(inicioIter, finIter);
            evolucionData.add(totalMes != null ? totalMes : 0.0);
        }

        // Enviamos las nuevas listas a Thymeleaf
        reporte.put("evolucionLabels", evolucionLabels);
        reporte.put("evolucionData", evolucionData);

        return reporte;
    }
}