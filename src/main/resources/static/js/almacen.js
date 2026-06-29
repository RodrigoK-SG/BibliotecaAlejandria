document.addEventListener('DOMContentLoaded', function() {
    
    // 1. LÓGICA DE CANTIDAD
    const btnRestar = document.getElementById('btnRestar');
    const btnSumar = document.getElementById('btnSumar');
    const inputCantidad = document.getElementById('inputCantidad');

    if (btnSumar && btnRestar && inputCantidad) {
        btnSumar.addEventListener('click', () => {
            let actual = parseInt(inputCantidad.value) || 0;
            inputCantidad.value = actual + 1;
        });

        btnRestar.addEventListener('click', () => {
            let actual = parseInt(inputCantidad.value) || 0;
            if(actual > 1) inputCantidad.value = actual - 1;
        });
    }

    // 2. LÓGICA DE MOSTRAR/OCULTAR MODO LIBRO NUEVO VS EXISTENTE
    const radioExistente = document.getElementById('radioExistente');
    const radioNuevo = document.getElementById('radioNuevo');
    const zonaLibroExistente = document.getElementById('zonaLibroExistente');
    const zonaLibroNuevo = document.getElementById('zonaLibroNuevo');
    const selectLibro = document.getElementById('selectLibro');
    
    // Todos los inputs manuales
    const inputNuevoIsbn = document.getElementById('inputNuevoIsbn');
    const inputTitulo = document.getElementById('apiTitulo');
    const inputEditorial = document.getElementById('apiEditorial');
    const inputPaginas = document.getElementById('apiPaginas');
    const inputPrecio = document.getElementById('apiPrecio');

    if (radioExistente && radioNuevo) {
        radioExistente.addEventListener('change', () => {
            zonaLibroExistente.style.display = 'block';
            zonaLibroNuevo.style.display = 'none';
            
            selectLibro.setAttribute('required', 'required');
            inputNuevoIsbn.removeAttribute('required');
            inputTitulo.removeAttribute('required');
            inputEditorial.removeAttribute('required');
            inputPaginas.removeAttribute('required');
            inputPrecio.removeAttribute('required');
        });

        radioNuevo.addEventListener('change', () => {
            zonaLibroExistente.style.display = 'none';
            zonaLibroNuevo.style.display = 'block';
            
            selectLibro.removeAttribute('required');
            inputNuevoIsbn.setAttribute('required', 'required');
            inputTitulo.setAttribute('required', 'required');
            inputEditorial.setAttribute('required', 'required');
            inputPaginas.setAttribute('required', 'required');
            inputPrecio.setAttribute('required', 'required');
        });
    }

    // 3. LÓGICA API OPEN LIBRARY
    const btnBuscarApi = document.getElementById('btnBuscarApi');
    
    const buscarLibroEnApi = async () => {
        const isbn = inputNuevoIsbn.value.trim();
        if(!isbn) { alert("Escriba un ISBN para buscar."); return; }

        btnBuscarApi.innerHTML = '<span class="spinner-border spinner-border-sm"></span>';
        btnBuscarApi.disabled = true;

        try {
            const response = await fetch(`https://openlibrary.org/api/books?bibkeys=ISBN:${isbn}&jscmd=data&format=json`);
            if (!response.ok) throw new Error("Error en API");

            const data = await response.json();
            const libroData = data[`ISBN:${isbn}`];

            if (libroData) {
                inputTitulo.value = libroData.title || '';
                inputPaginas.value = libroData.number_of_pages || '';
                
                const nombreEditorial = (libroData.publishers && libroData.publishers.length > 0) ? libroData.publishers[0].name : "";
                inputEditorial.value = nombreEditorial;

                if (libroData.cover) {
                    document.getElementById('apiImagen').value = libroData.cover.large || libroData.cover.medium;
                } else {
                    document.getElementById('apiImagen').value = ""; // Se generará en Java
                }
            } else {
                alert("Libro no encontrado en la API. Por favor, llene los datos manualmente.");
            }
        } catch (error) {
            alert("Error de conexión. Llene los datos manualmente.");
        } finally {
            btnBuscarApi.innerHTML = '<i class="bi bi-search"></i> API';
            btnBuscarApi.disabled = false;
        }
    };

    if (btnBuscarApi) btnBuscarApi.addEventListener('click', buscarLibroEnApi);
    if (inputNuevoIsbn) {
        inputNuevoIsbn.addEventListener('keydown', (e) => { 
            if (e.key === 'Enter') { e.preventDefault(); buscarLibroEnApi(); } 
        });
    }
});