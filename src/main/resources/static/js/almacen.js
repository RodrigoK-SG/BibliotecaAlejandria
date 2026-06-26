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
    const inputNuevoIsbn = document.getElementById('inputNuevoIsbn');
    
    // Capturamos los campos del nuevo formulario
    const inputPaginas = document.getElementById('apiPaginas');
    const inputPrecio = document.getElementById('apiPrecio');
    const selectFormato = document.getElementById('apiFormato');

    if (radioExistente && radioNuevo) {
        radioExistente.addEventListener('change', () => {
            zonaLibroExistente.style.display = 'block';
            zonaLibroNuevo.style.display = 'none';
            
            selectLibro.setAttribute('required', 'required');
            inputNuevoIsbn.removeAttribute('required');
            if(inputPaginas) inputPaginas.removeAttribute('required');
            if(inputPrecio) inputPrecio.removeAttribute('required');
            if(selectFormato) selectFormato.removeAttribute('required');
        });

        radioNuevo.addEventListener('change', () => {
            zonaLibroExistente.style.display = 'none';
            zonaLibroNuevo.style.display = 'block';
            
            selectLibro.removeAttribute('required');
            inputNuevoIsbn.setAttribute('required', 'required');
            if(inputPaginas) inputPaginas.setAttribute('required', 'required');
            if(inputPrecio) inputPrecio.setAttribute('required', 'required');
            if(selectFormato) selectFormato.setAttribute('required', 'required');
        });
    }

    // 3. LÓGICA API OPEN LIBRARY
    const btnBuscarApi = document.getElementById('btnBuscarApi');
    
    const buscarLibroEnApi = async () => {
        const isbn = inputNuevoIsbn.value.trim();
        if(!isbn) { alert("Escriba un ISBN para buscar."); return; }

        btnBuscarApi.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Buscando...';
        btnBuscarApi.disabled = true;

        try {
            const response = await fetch(`https://openlibrary.org/api/books?bibkeys=ISBN:${isbn}&jscmd=data&format=json`);
            if (!response.ok) throw new Error("Error en la respuesta de la API");

            const data = await response.json();
            const libroData = data[`ISBN:${isbn}`];

            document.getElementById('cardDatosApi').classList.remove('d-none');

            if (libroData) {
                document.getElementById('apiTitulo').value = libroData.title || '';
                document.getElementById('apiPaginas').value = libroData.number_of_pages || 100;
                
                // Extraer el nombre de la editorial de la API
                const nombreEditorial = (libroData.publishers && libroData.publishers.length > 0) ? libroData.publishers[0].name : "Editorial Desconocida";
                document.getElementById('apiEditorial').value = nombreEditorial;

                if (libroData.cover) {
                    document.getElementById('apiImgPortada').src = libroData.cover.medium;
                    document.getElementById('apiImgPortada').classList.remove('d-none');
                    document.getElementById('apiImagen').value = libroData.cover.medium;
                }
            } else {
                alert("El libro no se encontró en la base de datos pública. Puede llenar los datos manualmente.");
                document.getElementById('apiTitulo').readOnly = false; // Permite escribir manual si no existe en la API
            }
        } catch (error) {
            alert("Error al conectar con la API de OpenLibrary.");
            document.getElementById('apiTitulo').readOnly = false; // Habilita campo manual si hay error
        } finally {
            btnBuscarApi.innerHTML = '<i class="bi bi-search"></i> Buscar en API';
            btnBuscarApi.disabled = false;
        }
    };

    if (btnBuscarApi) btnBuscarApi.addEventListener('click', buscarLibroEnApi);
    if (inputNuevoIsbn) {
        inputNuevoIsbn.addEventListener('keydown', (e) => { 
            if (e.key === 'Enter') { 
                e.preventDefault(); 
                buscarLibroEnApi(); 
            } 
        });
    }
});