document.addEventListener('DOMContentLoaded', function() {
    
    // 1. Gráfico de Barras: Ventas Físicas vs Online
    const canvasBar = document.getElementById('ventasChart');
    if (canvasBar) {
        const ctxBar = canvasBar.getContext('2d');
        new Chart(ctxBar, {
            type: 'bar',
            data: {
                // CONSUMIMOS LAS VARIABLES INYECTADAS POR THYMELEAF
                labels: window.chartLabels,
                datasets: [
                    {
                        label: 'Ventas Físicas',
                        data: window.chartVentasFisicas, 
                        backgroundColor: '#3b82f6', // Azul
                        borderRadius: 4,
                        barPercentage: 0.6,
                        categoryPercentage: 0.8
                    },
                    {
                        label: 'Ventas Online',
                        data: window.chartVentasOnline,
                        backgroundColor: '#10b981', // Verde
                        borderRadius: 4,
                        barPercentage: 0.6,
                        categoryPercentage: 0.8
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'bottom', labels: { usePointStyle: true, boxWidth: 8, padding: 20 } }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) { return 'S/ ' + value; }
                        },
                        border: { display: false },
                        grid: { color: '#f8f9fa', drawBorder: false }
                    },
                    x: {
                        grid: { display: false },
                        border: { display: false }
                    }
                }
            }
        });
    }

    // 2. Gráfico de Dona: Top 5 Libros Más Alquilados
    const canvasDoughnut = document.getElementById('topLibrosChart');
    if (canvasDoughnut && window.topLibrosLabels && window.topLibrosLabels.length > 0) {
        
        // Formatear labels para incluir el porcentaje/cantidad si se desea, 
        // aquí los usamos directo como vienen de Java
        const ctxDoughnut = canvasDoughnut.getContext('2d');
        new Chart(ctxDoughnut, {
            type: 'doughnut',
            data: {
                labels: window.topLibrosLabels,
                datasets: [{
                    data: window.topLibrosData,
                    // Paleta de colores atractiva para los 5 top libros
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
                        display: true // Habilitado para ver los nombres
                    },
                    tooltip: { enabled: true }
                },
                layout: { padding: 10 }
            }
        });
    }
});