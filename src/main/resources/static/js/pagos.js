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
// ===============================
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
            // 🟢 AQUÍ CAPTURAMOS EL MENSAJE DEL BACKEND SI SUPERA EL STOCK
            const mensajeError = await response.text();
            alert(mensajeError);
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
    
    // --- ESTE ES EL SEGURO DE VIDA ---
    // Si no encuentra el contenedor (ej: estamos en el catálogo y no en el carrito), 
    // aborta la función y evita el error 'Cannot read properties of null'.
    if (!contenedor) {
        return; 
    }
    // ---------------------------------

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
    const botonSubmit = document.querySelector('button[type="submit"]');

    if (nuevoSubtotal === 0) {
        totalNode.innerText = 'S/ 0.00';
        if(botonSubmit) botonSubmit.disabled = true;
    } else {
        totalNode.innerText = 'S/ ' + total.toFixed(2);
        if(botonSubmit) botonSubmit.disabled = false;
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
    .then(async r => {
        if (r.ok) {
            syncCarrito();
            // Opcional: alert("Libro agregado al carrito");
        } else {
            // 🟢 AQUÍ CAPTURAMOS EL MENSAJE DEL BACKEND SI NO HAY STOCK
            const mensajeError = await r.text();
            alert(mensajeError); 
        }
    })
    .catch(error => {
        console.error("Error al agregar:", error);
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

// ==========================================================================
// VALIDACIONES DEL FORMULARIO DE CHECKOUT
// ==========================================================================

document.addEventListener("DOMContentLoaded", function () {
    const formCheckout = document.getElementById("formCheckout");

    if (formCheckout) {
        formCheckout.addEventListener("submit", function (e) {
            // 1. Limpiamos errores previos antes de volver a validar
            limpiarErrores();
            
            let esValido = true;

            // ---------------------------------------------------------
            // VALIDACIÓN 1: Teléfono Personal (9 dígitos, empieza con 9)
            // ---------------------------------------------------------
            const inputTelefono = document.querySelector('input[name="telefono"]');
            if (inputTelefono) {
                const regexTelefono = /^9\d{8}$/;
                if (!regexTelefono.test(inputTelefono.value.trim())) {
                    mostrarError(inputTelefono, "El teléfono debe tener 9 dígitos y empezar con 9.");
                    esValido = false;
                }
            }

            // ---------------------------------------------------------
            // VALIDACIÓN 2: Métodos de Pago
            // ---------------------------------------------------------
            const metodoPagoSeleccionado = document.querySelector('input[name="metodoPago"]:checked').value;

            if (metodoPagoSeleccionado === "TARJETA_CREDITO") {
                const tarjetaNum = document.getElementById("tarjetaNum");
                const tarjetaFecha = document.getElementById("tarjetaFecha");
                const tarjetaCvc = document.getElementById("tarjetaCvc");

                // Validar Algoritmo de Luhn para la tarjeta
                if (!validarLuhn(tarjetaNum.value)) {
                    mostrarError(tarjetaNum, "Número de tarjeta inválido.");
                    esValido = false;
                }

                // Validar Fecha (Formato MM/AA básico)
                const regexFecha = /^(0[1-9]|1[0-2])\/\d{2}$/;
                if (!regexFecha.test(tarjetaFecha.value.trim())) {
                    mostrarError(tarjetaFecha, "Usa el formato MM/AA válido.");
                    esValido = false;
                }

                // Validar CVC (3 o 4 dígitos)
                const regexCvc = /^\d{3,4}$/;
                if (!regexCvc.test(tarjetaCvc.value.trim())) {
                    mostrarError(tarjetaCvc, "CVC inválido.");
                    esValido = false;
                }

            } else if (metodoPagoSeleccionado === "YAPE") {
                const yapeTelf = document.getElementById("yapeTelf");
                const yapeCod = document.getElementById("yapeCod");

                // Validar Celular Yape (9 dígitos, empieza con 9)
                const regexTelefono = /^9\d{8}$/;
                if (!regexTelefono.test(yapeTelf.value.trim())) {
                    mostrarError(yapeTelf, "Debe tener 9 dígitos y empezar con 9.");
                    esValido = false;
                }

                // Validar Código de Aprobación (Exactamente 3 dígitos, según pediste)
                const regexCodigo = /^\d{3}$/;
                if (!regexCodigo.test(yapeCod.value.trim())) {
                    mostrarError(yapeCod, "El código debe ser de 3 dígitos numéricos.");
                    esValido = false;
                }
            }

            // Si alguna validación falló, detenemos el envío del formulario
            if (!esValido) {
                e.preventDefault();
                
                // Opcional: Hacer un scroll suave hacia arriba para que el usuario vea el error
                window.scrollTo({ top: 0, behavior: 'smooth' });
            }
        });
    }
});

// ==========================================================================
// FUNCIONES AUXILIARES PARA VALIDACIÓN
// ==========================================================================

// Función que aplica el Algoritmo de Luhn (Fórmula matemática estándar para tarjetas reales)
function validarLuhn(numero) {
    // Quitar espacios y guiones
    let numLimpio = numero.replace(/[\s-]/g, "");
    
    // Verificar que solo sean números y tenga una longitud normal de tarjeta (13 a 19 dígitos)
    if (!/^\d{13,19}$/.test(numLimpio)) return false;

    let suma = 0;
    let esPar = false;

    // Recorremos los dígitos de derecha a izquierda
    for (let i = numLimpio.length - 1; i >= 0; i--) {
        let digito = parseInt(numLimpio.charAt(i), 10);

        if (esPar) {
            digito *= 2;
            if (digito > 9) digito -= 9;
        }

        suma += digito;
        esPar = !esPar;
    }

    // Si el módulo 10 de la suma es 0, es una tarjeta válida
    return (suma % 10) === 0;
}

// Función para inyectar el error visual usando Bootstrap
function mostrarError(inputElement, mensaje) {
    inputElement.classList.add("is-invalid");
    
    // Crear el elemento de texto rojo
    let divError = document.createElement("div");
    divError.className = "invalid-feedback small fw-bold";
    divError.innerText = mensaje;
    
    // Insertarlo justo debajo del input
    inputElement.parentNode.insertBefore(divError, inputElement.nextSibling);
}

// Función para limpiar todos los mensajes de error antes de volver a intentar
function limpiarErrores() {
    document.querySelectorAll(".is-invalid").forEach(el => el.classList.remove("is-invalid"));
    document.querySelectorAll(".invalid-feedback").forEach(el => el.remove());
}