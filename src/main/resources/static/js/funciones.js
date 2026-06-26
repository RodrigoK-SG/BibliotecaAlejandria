document.addEventListener("DOMContentLoaded", function () {

    // ==========================================================================
    // 1. MÓDULO: LOGIN (Mostrar / Ocultar Contraseña)
    // ==========================================================================
    const togglePassword = document.getElementById("togglePassword");
    const passwordInput = document.getElementById("password");

    if (togglePassword && passwordInput) {
        togglePassword.addEventListener("click", function () {
            const isPassword = passwordInput.getAttribute("type") === "password";
            passwordInput.setAttribute("type", isPassword ? "text" : "password");
            
            const icon = togglePassword.querySelector("i");
            icon.classList.toggle("bi-eye");
            icon.classList.toggle("bi-eye-slash");
        });
    }

    // ==========================================================================
    // 2. MÓDULO: CHECKOUT (Lógica Visual de Entrega)
    // ==========================================================================
    const radioRecojo = document.getElementById("entregaRecojo");
    const radioDelivery = document.getElementById("entregaDelivery");
    const sectionDireccion = document.getElementById("sectionDireccionEnvio");

    if (radioRecojo && radioDelivery && sectionDireccion) {
        const toggleDireccion = () => {
            // Si es Delivery, quitamos 'd-none', si es Recojo, lo agregamos
            sectionDireccion.classList.toggle("d-none", radioRecojo.checked);
        };

        radioRecojo.addEventListener("change", toggleDireccion);
        radioDelivery.addEventListener("change", toggleDireccion);
    }

    // ==========================================================================
    // 3. MÓDULO: CATÁLOGO / FILTROS
    // ==========================================================================
    const checkboxes = document.querySelectorAll(".filter-checkbox");
    const filterForm = document.getElementById("filter-form");
    
    if (checkboxes.length > 0 && filterForm) {
        checkboxes.forEach(cb => {
            cb.addEventListener("change", () => filterForm.submit());
        });
    }

    // ==========================================================================
    // 4. MÓDULO: MI PERFIL (Navegación de Tabs)
    // ==========================================================================
    const tabs = document.querySelectorAll("#profileTabs .list-group-item");
    const contents = document.querySelectorAll(".nav-tab-content");

    tabs.forEach(tab => {
        tab.addEventListener("click", function() {
            tabs.forEach(t => t.classList.remove("active"));
            contents.forEach(c => c.classList.remove("active"));

            this.classList.add("active");
            const target = this.getAttribute("data-tab");
            const element = document.getElementById(target);
            if(element) element.classList.add("active");
        });
    });

    // Manejo del hash para redirección desde footer al perfil
    const rutas = { '#pedidos-tab': 'tab-pedidos', '#prestamos-tab': 'tab-prestamos', '#multas-tab': 'tab-multas' };
    const hash = window.location.hash;
    if (hash && rutas[hash]) {
        const botonObjetivo = document.querySelector(`button[data-tab="${rutas[hash]}"]`);
        if (botonObjetivo) botonObjetivo.click();
    }

    // ==========================================================================
    // 5. MÓDULO: RESEÑAS (Votación interactiva)
    // ==========================================================================
    const estrellas = document.querySelectorAll(".estrella-voto");
    const inputCalificacion = document.getElementById("calificacionInput");

    estrellas.forEach((estrella) => {
        estrella.addEventListener("mouseover", () => {
            const valor = parseInt(estrella.getAttribute("data-value"));
            estrellas.forEach((e, i) => e.classList.toggle("hovered", i < valor));
        });

        estrella.addEventListener("mouseout", () => {
            estrellas.forEach(e => e.classList.remove("hovered"));
        });

        estrella.addEventListener("click", () => {
            const valor = parseInt(estrella.getAttribute("data-value"));
            if(inputCalificacion) inputCalificacion.value = valor;
            estrellas.forEach((e, i) => e.classList.toggle("selected", i < valor));
        });
    });
});

