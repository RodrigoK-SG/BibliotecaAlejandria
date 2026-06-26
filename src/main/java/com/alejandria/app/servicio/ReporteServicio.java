package com.alejandria.app.servicio;

import com.alejandria.app.modelo.enums.CanalVenta;
import com.alejandria.app.repositorio.PedidoRepositorio;
import com.alejandria.app.repositorio.DetallePedidoRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

        reporte.put("ingresosTotales", pedidoRepositorio.sumarIngresosPorFecha(inicioMes, finMes));
        reporte.put("librosVendidos", detallePedidoRepositorio.sumarLibrosVendidosPorFecha(inicioMes, finMes));
        
        String nombreMes = fechaSeleccionada.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "ES")));
        reporte.put("mesActualTexto", nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1));
        reporte.put("mesActualInput", fechaSeleccionada.format(DateTimeFormatter.ofPattern("yyyy-MM")));

        List<Object[]> top5Raw = detallePedidoRepositorio.obtenerTopLibrosVendidos(inicioMes, finMes, PageRequest.of(0, 5));
        List<String> topLabels = new ArrayList<>();
        List<Long> topData = new ArrayList<>();
        
        for (Object[] fila : top5Raw) {
            topLabels.add((String) fila[0]);
            topData.add(((Number) fila[1]).longValue());
        }
        reporte.put("topLibrosLabels", topLabels);
        reporte.put("topLibrosData", topData);

        List<String> chartLabels = new ArrayList<>();
        List<BigDecimal> chartFisico = new ArrayList<>();
        List<BigDecimal> chartOnline = new ArrayList<>();

        for (int i = 4; i >= 0; i--) {
            LocalDate mesIteracion = fechaSeleccionada.minusMonths(i);
            LocalDateTime inicioIter = mesIteracion.withDayOfMonth(1).atStartOfDay();
            LocalDateTime finIter = mesIteracion.withDayOfMonth(mesIteracion.lengthOfMonth()).atTime(LocalTime.MAX);
            
            chartLabels.add(mesIteracion.format(DateTimeFormatter.ofPattern("MMM", new Locale("es", "ES"))).toUpperCase());
            
            chartFisico.add(pedidoRepositorio.sumarIngresosPorCanalYFecha(CanalVenta.valueOf("FISICO"), inicioIter, finIter));
            chartOnline.add(pedidoRepositorio.sumarIngresosPorCanalYFecha(CanalVenta.valueOf("ONLINE"), inicioIter, finIter));
        }

        reporte.put("chartLabels", chartLabels);
        reporte.put("chartFisicas", chartFisico);
        reporte.put("chartOnline", chartOnline);

        return reporte;
    }
}