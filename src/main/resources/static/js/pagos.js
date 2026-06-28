let bloqueando = false;
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

// ===============================
// SINCRONIZAR CARRITO (BADGE + TOTALES)
// ===============================
function syncCarrito() {
    fetch('/tienda/carrito/cantidad')
        .then(res => res.json())
        .then(total => {
            const badge = document.getElementById('cart-badge');
            if (badge) badge.innerText = total;
        });

    recalcularTotales();
}

// ===============================
// MODIFICAR CANTIDAD (+ o -)
async function modificarCantidad(libroId, cambio) {
    if (bloqueando) return;
    bloqueando = true;

    const spanQty = document.getElementById('qty-' + libroId);
    let cantidadActual = parseInt(spanQty.innerText);
    let nuevaCantidad = cantidadActual + cambio;

    // Validación local: si llega a 0 o menos, eliminar
    if (nuevaCantidad <= 0) {
        bloqueando = false;
        eliminarItem(libroId);
        return;
    }

    try {
        const response = await fetch(`/tienda/carrito/actualizar-cantidad?libroId=${libroId}&nuevaCantidad=${nuevaCantidad}`, {
            method: 'POST'
        });

        if (response.ok) {
            // 1. ACTUALIZAR UI INMEDIATAMENTE
            spanQty.innerText = nuevaCantidad; 
            
            // 2. SINCRONIZAR
            syncCarrito();
        } else {
            alert("Error al actualizar la cantidad.");
        }
    } catch (error) {
        console.error("Error en la petición:", error);
    } finally {
        bloqueando = false;
    }
}


// ===============================
// ELIMINAR ITEM
// ===============================
function eliminarItem(libroId) {

    if (bloqueando) return;
    bloqueando = true;

    fetch(`/tienda/carrito/eliminar-item?libroId=${libroId}`, {
        method: 'POST'
    })
    .then(r => {
        if (r.ok) {
            const fila = document.getElementById('item-row-' + libroId);
            if (fila) fila.remove();

            syncCarrito();
        }
    })
    .finally(() => {
        bloqueando = false;
    });
}

// ===============================
// RECALCULAR TOTALES
// ===============================
function recalcularTotales() {

    const contenedor = document.getElementById('contenedorItemsResumen');
    const filas = contenedor.querySelectorAll('[id^="item-row-"]');

    let nuevoSubtotal = 0;

    filas.forEach(fila => {

        const libroId = fila.id.replace('item-row-', '');

        const cantidad = parseInt(
            document.getElementById('qty-' + libroId).innerText
        ) || 0;

        const precioUnitario = parseFloat(
            fila.querySelector('.item-precio-unitario')
                .getAttribute('data-precio')
        ) || 0;

        nuevoSubtotal += precioUnitario * cantidad;
    });

    document.getElementById('resumenSubtotal').innerText =
        'S/ ' + nuevoSubtotal.toFixed(2);

    const envio = 5.99;
    const total = nuevoSubtotal + envio;

    const totalNode = document.getElementById('resumenTotal');

    if (nuevoSubtotal === 0) {
        totalNode.innerText = 'S/ 0.00';
        document.querySelector('button[type="submit"]').disabled = true;
    } else {
        totalNode.innerText = 'S/ ' + total.toFixed(2);
        document.querySelector('button[type="submit"]').disabled = false;
    }
}

// ===============================
// AGREGAR AL CARRITO
// ===============================
function agregarAlCarrito(libroId) {

    if (bloqueando) return;
    bloqueando = true;

    fetch('/tienda/carrito/agregar', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({
            libroId: libroId,
            cantidad: 1
        })
    })
    .then(r => {
        if (r.ok) syncCarrito();
    })
    .finally(() => {
        bloqueando = false;
    });
}


// ===============================
// ACTUALIZAR SOLO BADGE (OPCIONAL)
// ===============================
function actualizarCarritoCount() {
    fetch('/tienda/carrito/cantidad')
        .then(res => res.json())
        .then(total => {
            const badge = document.getElementById('cart-badge');
            if (badge) badge.innerText = total;
        });
}