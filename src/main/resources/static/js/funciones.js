document.addEventListener("DOMContentLoaded", function () {

    // ==========================================================================
    // 1. MÓDULO: LOGIN (Mostrar / Ocultar Contraseña - Formulario Inicio)
    // ==========================================================================
    setupPasswordToggle("togglePassword", "password");

    // ==========================================================================
    // 2. MÓDULO: REGISTRO (Mostrar / Ocultar Contraseña - Formulario Registro)
    // ==========================================================================
    setupPasswordToggle("toggleRegPassword", "reg-password");

    // Función auxiliar para no repetir código de toggle
    function setupPasswordToggle(toggleId, inputId) {
        const toggleBtn = document.getElementById(toggleId);
        const inputField = document.getElementById(inputId);

        if (toggleBtn && inputField) {
            toggleBtn.addEventListener("click", function () {
                const isPassword = inputField.getAttribute("type") === "password";
                inputField.setAttribute("type", isPassword ? "text" : "password");
                
                const icon = toggleBtn.querySelector("i");
                icon.classList.toggle("bi-eye");
                icon.classList.toggle("bi-eye-slash");
            });
        }
    }

    // ==========================================================================
    // 3. MÓDULO: MI PERFIL (Navegación de Tabs)
    // ==========================================================================
    const tabs = document.querySelectorAll("#profileTabs .list-group-item");
    const contents = document.querySelectorAll(".nav-tab-content");

    if(tabs.length > 0) {
        tabs.forEach(tab => {
            tab.addEventListener("click", function() {
                // Remover clases activas
                tabs.forEach(t => t.classList.remove("active"));
                contents.forEach(c => c.classList.remove("active", "d-none"));
                contents.forEach(c => c.classList.add("d-none"));

                // Añadir activa al seleccionado
                this.classList.add("active");
                const target = this.getAttribute("data-tab");
                const element = document.getElementById(target);
                if(element) {
                    element.classList.remove("d-none");
                    element.classList.add("active");
                }
            });
        });

        // Manejo del hash para redirección desde footer al perfil
        const rutas = { '#pedidos-tab': 'tab-pedidos', '#ajustes-tab': 'tab-ajustes' };
        const hash = window.location.hash;
        if (hash && rutas[hash]) {
            const botonObjetivo = document.querySelector(`button[data-tab="${rutas[hash]}"]`);
            if (botonObjetivo) botonObjetivo.click();
        }
    }
});

// ==========================================================================
    // AÑADIR AL CARRITO SIN RECARGAR LA PÁGINA (AJAX)
    // ==========================================================================
    const formsCarrito = document.querySelectorAll('form[action="/tienda/carrito/agregar"]');
    
    formsCarrito.forEach(form => {
        form.addEventListener('submit', function(e) {
            e.preventDefault(); // Evita que la página te lleve a otra pantalla
            
            const formData = new FormData(this);
            const botonSubmit = this.querySelector('button[type="submit"]');
            const textoOriginal = botonSubmit.innerHTML;
            
            // Efecto visual de carga
            botonSubmit.innerHTML = '<i class="bi bi-hourglass-split"></i> Añadiendo...';
            botonSubmit.disabled = true;

            fetch(this.action, {
                method: 'POST',
                body: formData
            })
            .then(response => {
                if(response.ok) {
                    // Si el servidor responde OK, actualizamos el numerito rojo
                    const badge = document.getElementById('cart-badge');
                    if(badge) {
                        const cantidadAñadida = parseInt(formData.get('cantidad')) || 1;
                        const cantidadActual = parseInt(badge.innerText) || 0;
                        badge.innerText = cantidadActual + cantidadAñadida;
                    }
                    
                    // Efecto visual de éxito
                    botonSubmit.innerHTML = '<i class="bi bi-check-lg"></i> ¡Añadido!';
                    botonSubmit.classList.replace('btn-dark', 'btn-success'); // Si usas un color específico, cámbialo
                    
                    setTimeout(() => {
                        botonSubmit.innerHTML = textoOriginal;
                        botonSubmit.disabled = false;
                        botonSubmit.classList.replace('btn-success', 'btn-dark');
                    }, 2000);
                } else {
                    alert("Hubo un problema al añadir el libro al carrito.");
                    botonSubmit.innerHTML = textoOriginal;
                    botonSubmit.disabled = false;
                }
            })
            .catch(error => {
                console.error("Error:", error);
                botonSubmit.innerHTML = textoOriginal;
                botonSubmit.disabled = false;
            });
        });
    });
	
	
	
	
	
	
	
	
	
	
	
	// ==========================================================================
	// PAGINACIÓN PARA EL CATÁLOGO
	// ==========================================================================
	document.addEventListener("DOMContentLoaded", function () {
	    const librosPorPagina = 30; // Puedes cambiar esto al número que desees
	    const grilla = document.getElementById("grilla-libros");
	    const paginacionContenedor = document.getElementById("paginacion-libros");

	    if (grilla && paginacionContenedor) {
	        const libros = grilla.querySelectorAll(":scope > .col");
	        const totalPaginas = Math.ceil(libros.length / librosPorPagina);

	        // Si hay menos libros que el límite, no dibujamos paginación
	        if (totalPaginas <= 1) return;

	        function mostrarPagina(pagina) {
	            // Ocultar todos los libros
	            libros.forEach((libro, index) => {
	                libro.style.display = "none";
	                // Mostrar solo los que pertenecen a la página actual
	                if (index >= (pagina - 1) * librosPorPagina && index < pagina * librosPorPagina) {
	                    libro.style.display = ""; // Restaura el display original
	                }
	            });
	            renderizarPaginacion(pagina);
	        }

	        function renderizarPaginacion(paginaActual) {
	            let html = '';
	            
	            // Botón Anterior
	            html += `<li class="page-item ${paginaActual === 1 ? 'disabled' : ''}">
	                        <a class="page-link text-dark" href="#" data-page="${paginaActual - 1}">Anterior</a>
	                     </li>`;

	            // Números de página
	            for (let i = 1; i <= totalPaginas; i++) {
	                if (i === paginaActual) {
	                    html += `<li class="page-item active">
	                                <a class="page-link text-white fw-bold" href="#" style="background-color: #c86b66; border-color: #c86b66;" data-page="${i}">${i}</a>
	                             </li>`;
	                } else {
	                    html += `<li class="page-item">
	                                <a class="page-link text-dark" href="#" data-page="${i}">${i}</a>
	                             </li>`;
	                }
	            }

	            // Botón Siguiente
	            html += `<li class="page-item ${paginaActual === totalPaginas ? 'disabled' : ''}">
	                        <a class="page-link text-dark" href="#" data-page="${paginaActual + 1}">Siguiente</a>
	                     </li>`;

	            paginacionContenedor.innerHTML = html;

	            // Darle funcionalidad a los botones
	            paginacionContenedor.querySelectorAll(".page-link").forEach(boton => {
	                boton.addEventListener("click", function(e) {
	                    e.preventDefault();
	                    const paginaDestino = parseInt(this.getAttribute("data-page"));
	                    if (paginaDestino >= 1 && paginaDestino <= totalPaginas) {
	                        mostrarPagina(paginaDestino);
	                        // Subir suavemente al inicio del catálogo
	                        window.scrollTo({ top: grilla.offsetTop - 80, behavior: 'smooth' });
	                    }
	                });
	            });
	        }

	        // Iniciar en la página 1
	        mostrarPagina(1);
	    }
	});