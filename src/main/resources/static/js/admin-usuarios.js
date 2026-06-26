document.addEventListener('DOMContentLoaded', function() {
    
    // --- LÓGICA MODAL EDITAR USUARIO ---
    const modalEditar = document.getElementById('modalEditarUsuario');
    if (modalEditar) {
        modalEditar.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            
            // Extraer datos
            const id = button.getAttribute('data-id');
            const nombre = button.getAttribute('data-nombre');
            const email = button.getAttribute('data-email');
            const rol = button.getAttribute('data-rol');
            const activo = button.getAttribute('data-activo') === 'true';

            // Llenar formulario
            modalEditar.querySelector('#editId').value = id;
            modalEditar.querySelector('#editNombre').value = nombre;
            modalEditar.querySelector('#editEmail').value = email;
            
            // Seleccionar el rol en el dropdown
            const selectRol = modalEditar.querySelector('#editRol');
            for(let i = 0; i < selectRol.options.length; i++) {
                if(selectRol.options[i].value === rol) {
                    selectRol.selectedIndex = i;
                    break;
                }
            }
            
            // Configurar switch de estado
            const switchEstado = modalEditar.querySelector('#editSwitchEstado');
            const labelEstado = modalEditar.querySelector('#editLabelEstado');
            switchEstado.checked = activo;
            actualizarLabelEstado(labelEstado, activo);
        });

        // Evento cambio de estado manual
        const switchBtn = document.getElementById('editSwitchEstado');
        if(switchBtn) {
            switchBtn.addEventListener('change', function() {
                const label = document.getElementById('editLabelEstado');
                actualizarLabelEstado(label, this.checked);
            });
        }
    }

    // --- LÓGICA MODAL NUEVO USUARIO (Switch) ---
    const switchNuevo = document.getElementById('nuevoSwitchEstado');
    if(switchNuevo) {
        switchNuevo.addEventListener('change', function() {
            const label = document.getElementById('nuevoLabelEstado');
            actualizarLabelEstado(label, this.checked);
        });
    }

    // --- LÓGICA MODAL DESACTIVAR USUARIO ---
    const modalDesactivar = document.getElementById('modalDesactivarUsuario');
    if (modalDesactivar) {
        modalDesactivar.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const id = button.getAttribute('data-id');
            const nombre = button.getAttribute('data-nombre');

            modalDesactivar.querySelector('#deleteId').value = id;
            modalDesactivar.querySelector('#deleteNombre').textContent = nombre;
        });
    }

    // Función auxiliar para los colores del texto del Switch
    function actualizarLabelEstado(label, isActivo) {
        if(isActivo) {
            label.textContent = 'ACTIVO';
            label.className = 'fw-bold text-success mb-0';
        } else {
            label.textContent = 'INACTIVO';
            label.className = 'fw-bold text-danger mb-0';
        }
    }
});