document.addEventListener('DOMContentLoaded', function() {
    
    // =================================================================
    // 1. NUEVO GRÁFICO: Evolución de Ingresos (Línea de tendencia)
    // =================================================================
    const canvasEvolucion = document.getElementById('evolucionIngresosChart');
    
    if (canvasEvolucion && window.evolucionLabels && window.evolucionLabels.length > 0) {
        const ctxEvolucion = canvasEvolucion.getContext('2d');
        
        // Creamos el degradado (gradient) para que se vea premium
        let gradient = ctxEvolucion.createLinearGradient(0, 0, 0, 300);
        gradient.addColorStop(0, 'rgba(59, 130, 246, 0.4)'); // Azul semi-transparente arriba
        gradient.addColorStop(1, 'rgba(59, 130, 246, 0.0)'); // Transparente abajo

        new Chart(ctxEvolucion, {
            type: 'line',
            data: {
                labels: window.evolucionLabels,
                datasets: [{
                    label: 'Ingresos Totales',
                    data: window.evolucionData,
                    borderColor: '#3b82f6', 
                    backgroundColor: gradient,
                    borderWidth: 3,
                    pointBackgroundColor: '#ffffff',
                    pointBorderColor: '#3b82f6',
                    pointBorderWidth: 2,
                    pointRadius: 4,
                    pointHoverRadius: 6,
                    fill: true, // Activa el fondo con degradado
                    tension: 0.4 // Hace la línea curva en lugar de recta
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false // Ocultamos la leyenda
                    },
                    tooltip: {
                        backgroundColor: 'rgba(17, 24, 39, 0.9)',
                        padding: 12,
                        titleFont: { size: 13 },
                        bodyFont: { size: 14, weight: 'bold' },
                        callbacks: {
                            label: function(context) {
                                return ' S/ ' + context.parsed.y.toFixed(2);
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        border: { display: false },
                        grid: {
                            color: '#f3f4f6', 
                            drawBorder: false
                        },
                        ticks: {
                            color: '#6b7280',
                            font: { size: 11 },
                            callback: function(value) {
                                if (value >= 1000) return 'S/ ' + (value / 1000) + 'k';
                                return 'S/ ' + value;
                            }
                        }
                    },
                    x: {
                        grid: {
                            display: false, 
                            drawBorder: false
                        },
                        ticks: {
                            color: '#6b7280',
                            font: { size: 12, weight: '500' }
                        }
                    }
                }
            }
        });
    }

    // =================================================================
    // 2. GRÁFICO DE DONA: Top 5 Libros Más Vendidos (Se queda igual)
    // =================================================================
    const canvasDoughnut = document.getElementById('topLibrosChart');
    
    if (canvasDoughnut && window.topLibrosLabels && window.topLibrosLabels.length > 0) {
        
        const ctxDoughnut = canvasDoughnut.getContext('2d');
        new Chart(ctxDoughnut, {
            type: 'doughnut',
            data: {
                labels: window.topLibrosLabels,
                datasets: [{
                    data: window.topLibrosData,
                    backgroundColor: ['#475569', '#3b82f6', '#10b981', '#f59e0b', '#ef4444'],
                    borderWidth: 4,
                    borderColor: '#ffffff',
                    hoverOffset: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: '70%', 
                plugins: {
                    legend: {
                        position: 'right',
                        display: true 
                    },
                    tooltip: { enabled: true }
                },
                layout: { padding: 10 }
            }
        });
    }
});