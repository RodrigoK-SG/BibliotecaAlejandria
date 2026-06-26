document.addEventListener('DOMContentLoaded', function() {
    
    // --- LÓGICA MODAL: EDITAR SUCURSAL ---
    const modalEditar = document.getElementById('modalEditarSucursal');
    if (modalEditar) {
        modalEditar.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            
            // Extraer datos guardados en los atributos del botón
            const id = button.getAttribute('data-id');
            const nombre = button.getAttribute('data-nombre');
            const direccion = button.getAttribute('data-direccion');
            const telefono = button.getAttribute('data-telefono');
            const tieneBiblioteca = button.getAttribute('data-biblioteca') === 'true';

            // Asignar a los campos del formulario
            modalEditar.querySelector('#editId').value = id;
            modalEditar.querySelector('#editNombreSede').value = nombre;
            modalEditar.querySelector('#editDireccion').value = direccion;
            modalEditar.querySelector('#editTelefono').value = telefono;
            
            // Título dinámico del modal
            modalEditar.querySelector('#editModalTitle').textContent = 'Editar ' + nombre;
            
            // Configurar Switch
            const switchBtn = modalEditar.querySelector('#editSwitchBiblioteca');
            const labelBtn = modalEditar.querySelector('#editLabelBiblioteca');
            const container = modalEditar.querySelector('#editSwitchContainer');
            
            switchBtn.checked = tieneBiblioteca;
            actualizarSwitchUI(switchBtn.checked, labelBtn, container);
        });

        // Escuchar cambios manuales dentro del switch de edición
        const switchBtn = document.getElementById('editSwitchBiblioteca');
        if (switchBtn) {
            switchBtn.addEventListener('change', function() {
                const labelBtn = document.getElementById('editLabelBiblioteca');
                const container = document.getElementById('editSwitchContainer');
                actualizarSwitchUI(this.checked, labelBtn, container);
            });
        }
    }

    // --- LÓGICA MODAL: NUEVA SUCURSAL (Switch UI) ---
    const switchNuevo = document.getElementById('nuevoSwitchBiblioteca');
    if (switchNuevo) {
        switchNuevo.addEventListener('change', function() {
            const label = document.getElementById('nuevoLabelBiblioteca');
            const container = document.getElementById('nuevoSwitchContainer');
            actualizarSwitchUI(this.checked, label, container);
        });
    }

    // --- LÓGICA DE VALIDACIÓN ANTES DE ELIMINAR / DESACTIVAR ---
    const botonesEliminar = document.querySelectorAll('.btn-eliminar-sucursal');
    botonesEliminar.forEach(boton => {
        boton.addEventListener('click', function() {
            const id = this.getAttribute('data-id');
            const nombre = this.getAttribute('data-nombre');
            const tieneElementos = this.getAttribute('data-tiene-elementos') === 'true';

            if (tieneElementos) {
                // CASO A: Tiene inventario/dependencias -> Mostramos Error de Integridad
                const modalError = new bootstrap.Modal(document.getElementById('modalErrorIntegridad'));
                document.getElementById('errorNombreSede').textContent = nombre;
                modalError.show();
            } else {
                // CASO B: Está vacía -> Procedemos al modal de confirmación normal para desactivar
                const modalDesactivar = new bootstrap.Modal(document.getElementById('modalDesactivarSucursal'));
                document.getElementById('desactivarId').value = id;
                document.getElementById('desactivarNombreSede').textContent = nombre;
                modalDesactivar.show();
            }
        });
    });

    // Función auxiliar para actualizar los textos e interactividad de los Switches
    function actualizarSwitchUI(isChecked, labelElement, containerElement) {
        if (isChecked) {
            labelElement.textContent = 'HABILITADO';
            labelElement.className = 'fw-bold text-success mb-0';
            containerElement.classList.add('active-green');
        } else {
            labelElement.textContent = 'DESACTIVADO';
            labelElement.className = 'fw-bold text-secondary mb-0';
            containerElement.classList.remove('active-green');
        }
    }
});