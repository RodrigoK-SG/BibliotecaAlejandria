document.addEventListener('DOMContentLoaded', function() {
    
    // --- LÓGICA MODAL EDITAR LIBRO ---
    const modalEditar = document.getElementById('modalEditarLibro');
    if (modalEditar) {
        modalEditar.addEventListener('show.bs.modal', function (event) {
            
            const button = event.relatedTarget;
            
            // Extraer info (Limpiado: sin variables de alquiler)
            const id = button.getAttribute('data-id');
            const isbn = button.getAttribute('data-isbn');
            const titulo = button.getAttribute('data-titulo');
            const formato = button.getAttribute('data-formato');
            const pVenta = button.getAttribute('data-pventa');
            const editorial = button.getAttribute('data-editorial');
            const activo = button.getAttribute('data-activo') === 'true';

            // Actualizar campos
            modalEditar.querySelector('#editId').value = id;
            modalEditar.querySelector('#editIsbn').value = isbn;
            modalEditar.querySelector('#editTitulo').value = titulo;
            modalEditar.querySelector('#editFormato').value = formato; 
            modalEditar.querySelector('#editPrecioVenta').value = pVenta;
            modalEditar.querySelector('#editEditorial').value = editorial;
            
            // Switch de estado
            const switchEstado = modalEditar.querySelector('#editSwitchEstado');
            const labelEstado = modalEditar.querySelector('#labelEstadoTexto');
            switchEstado.checked = activo;
            labelEstado.textContent = activo ? 'Estado: ACTIVO' : 'Estado: INACTIVO';
            labelEstado.className = activo ? 'form-check-label fw-bold text-success ms-2 mt-2' : 'form-check-label fw-bold text-danger ms-2 mt-2';
        });

        // Evento para cambiar el texto del switch
        const switchBtn = document.getElementById('editSwitchEstado');
        if(switchBtn) {
            switchBtn.addEventListener('change', function() {
                const label = document.getElementById('labelEstadoTexto');
                if(this.checked) {
                    label.textContent = 'Estado: ACTIVO';
                    label.className = 'form-check-label fw-bold text-success ms-2 mt-2';
                } else {
                    label.textContent = 'Estado: INACTIVO';
                    label.className = 'form-check-label fw-bold text-danger ms-2 mt-2';
                }
            });
        }
    }

    // --- LÓGICA MODAL DESACTIVAR LIBRO ---
    const modalDesactivar = document.getElementById('modalDesactivarLibro');
    if (modalDesactivar) {
        modalDesactivar.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const id = button.getAttribute('data-id');
            const titulo = button.getAttribute('data-titulo');

            modalDesactivar.querySelector('#deleteId').value = id;
            modalDesactivar.querySelector('#deleteTitulo').textContent = titulo;
        });
    }
});