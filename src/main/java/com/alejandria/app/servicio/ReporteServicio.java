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

        // 1. Ingresos Totales (Suma general sin importar canal)
        Double ingresos = pedidoRepositorio.sumarIngresosPorFecha(inicioMes, finMes);
        reporte.put("ingresosTotales", ingresos != null ? ingresos : 0.0);
        
        reporte.put("librosVendidos", detallePedidoRepositorio.sumarLibrosVendidosPorFecha(inicioMes, finMes));
        
        String nombreMes = fechaSeleccionada.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "ES")));
        reporte.put("mesActualTexto", nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1));
        reporte.put("mesActualInput", fechaSeleccionada.format(DateTimeFormatter.ofPattern("yyyy-MM")));

        // 2. Gráfico de Dona: Top 5 Libros
        List<Object[]> top5Raw = detallePedidoRepositorio.obtenerTopLibrosVendidos(inicioMes, finMes, PageRequest.of(0, 5));
        List<String> topLabels = new ArrayList<>();
        List<Long> topData = new ArrayList<>();
        
        for (Object[] fila : top5Raw) {
            topLabels.add((String) fila[0]);
            topData.add(((Number) fila[1]).longValue());
        }
        reporte.put("topLibrosLabels", topLabels);
        reporte.put("topLibrosData", topData);

        // 3. Gráfico de Barras: Histórico de 5 meses
        List<String> chartLabels = new ArrayList<>();
        List<Double> chartFisico = new ArrayList<>();
        List<Double> chartOnline = new ArrayList<>();

        for (int i = 4; i >= 0; i--) {
            LocalDate mesIteracion = fechaSeleccionada.minusMonths(i);
            LocalDateTime inicioIter = mesIteracion.withDayOfMonth(1).atStartOfDay();
            LocalDateTime finIter = mesIteracion.withDayOfMonth(mesIteracion.lengthOfMonth()).atTime(LocalTime.MAX);
            
            chartLabels.add(mesIteracion.format(DateTimeFormatter.ofPattern("MMM", new Locale("es", "ES"))).toUpperCase());
            
            // Calculamos todo como venta Online y enviamos 0 a las ventas Físicas
            Double totalMes = pedidoRepositorio.sumarIngresosPorFecha(inicioIter, finIter);
            
            chartOnline.add(totalMes != null ? totalMes : 0.0);
            chartFisico.add(0.0);
        }

        reporte.put("chartLabels", chartLabels);
        reporte.put("chartVentasFisicas", chartFisico); 
        reporte.put("chartVentasOnline", chartOnline);

        return reporte;
    }
}