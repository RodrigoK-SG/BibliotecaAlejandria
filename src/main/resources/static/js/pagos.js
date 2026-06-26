document.addEventListener("DOMContentLoaded", function() {

    const radioTarjeta = document.getElementById('pagoTarjeta');
    const radioYape = document.getElementById('pagoYape');
    const panelTarjeta = document.getElementById('panelTarjeta');
    const panelYape = document.getElementById('panelYape');
    const formCheckout = document.getElementById('formCheckout');

    // Despliegue dinámico de los subpaneles de Método de Pago
    function manejarCambioPago() {
        if(panelTarjeta) panelTarjeta.style.display = 'none';
        if(panelYape) panelYape.style.display = 'none';

        if (radioTarjeta && radioTarjeta.checked && panelTarjeta) {
            panelTarjeta.style.display = 'flex';
        } else if (radioYape && radioYape.checked && panelYape) {
            panelYape.style.display = 'flex';
        }
    }

    if(radioTarjeta) radioTarjeta.addEventListener('change', manejarCambioPago);
    if(radioYape) radioYape.addEventListener('change', manejarCambioPago);

    // Validación frontal antes de enviar al servidor
    if(formCheckout) {
        formCheckout.addEventListener('submit', function(e) {
            if (radioTarjeta && radioTarjeta.checked) {
                const num = document.getElementById('tarjetaNum').value.trim();
                const fecha = document.getElementById('tarjetaFecha').value.trim();
                const cvc = document.getElementById('tarjetaCvc').value.trim();
                if (!num || !fecha || !cvc) {
                    e.preventDefault();
                    alert("Complete todos los campos de su tarjeta bancaria.");
                }
            } else if (radioYape && radioYape.checked) {
                const telf = document.getElementById('yapeTelf').value.trim();
                const cod = document.getElementById('yapeCod').value.trim();
                if (!telf || !cod) {
                    e.preventDefault();
                    alert("Ingrese su número de celular y el código de aprobación de Yape.");
                }
            }
        });
    }

    manejarCambioPago(); // Inicializar estado
});