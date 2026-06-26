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

	// 2. LÓGICA DE MOSTRAR/OCULTAR
	    const radioExistente = document.getElementById('radioExistente');
	    const radioNuevo = document.getElementById('radioNuevo');
	    const zonaLibroExistente = document.getElementById('zonaLibroExistente');
	    const zonaLibroNuevo = document.getElementById('zonaLibroNuevo');
	    const selectLibro = document.getElementById('selectLibro');
	    const inputNuevoIsbn = document.getElementById('inputNuevoIsbn');
	    
	    // Capturamos los campos que acabamos de modificar
	    const inputPaginas = document.getElementById('apiPaginas');
	    const inputPrecio = document.getElementById('apiPrecio');
	    const selectFormato = document.getElementById('apiFormato');

	    if (radioExistente && radioNuevo) {
	        radioExistente.addEventListener('change', () => {
	            zonaLibroExistente.style.display = 'block';
	            zonaLibroNuevo.style.display = 'none';
	            
	            // El libro de la lista ES obligatorio
	            selectLibro.setAttribute('required', 'required');
	            
	            // Los campos del nuevo libro NO son obligatorios (porque están ocultos)
	            inputNuevoIsbn.removeAttribute('required');
	            if(inputPaginas) inputPaginas.removeAttribute('required');
	            if(inputPrecio) inputPrecio.removeAttribute('required');
	            if(selectFormato) selectFormato.removeAttribute('required');
	        });

	        radioNuevo.addEventListener('change', () => {
	            zonaLibroExistente.style.display = 'none';
	            zonaLibroNuevo.style.display = 'block';
	            
	            // El libro de la lista NO es obligatorio
	            selectLibro.removeAttribute('required');
	            
	            // Los campos del nuevo libro SÍ son obligatorios
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
        if(!isbn) { alert("Escriba un ISBN."); return; }

        btnBuscarApi.innerHTML = '<span class="spinner-border spinner-border-sm"></span>...';
        btnBuscarApi.disabled = true;

        try {
            const response = await fetch(`https://openlibrary.org/api/books?bibkeys=ISBN:${isbn}&jscmd=data&format=json`);
            if (!response.ok) throw new Error("Error");

            const data = await response.json();
            const libroData = data[`ISBN:${isbn}`];

            document.getElementById('cardDatosApi').classList.remove('d-none');

            if (libroData) {
                document.getElementById('apiTitulo').value = libroData.title || '';
                document.getElementById('apiPaginas').value = libroData.number_of_pages || 100;
                
                // Editorial: Nueva lógica
                const nombreEditorial = (libroData.publishers && libroData.publishers.length > 0) ? libroData.publishers[0].name : "";
                document.getElementById('apiEditorial').value = nombreEditorial;

                if (libroData.cover) {
                    document.getElementById('apiImgPortada').src = libroData.cover.medium;
                    document.getElementById('apiImgPortada').classList.remove('d-none');
                    document.getElementById('apiImagen').value = libroData.cover.medium;
                }
            } else {
                alert("Libro no encontrado.");
            }
        } catch (error) {
            alert("Error al conectar con la API.");
        } finally {
            btnBuscarApi.innerHTML = '<i class="bi bi-search"></i> Buscar en API';
            btnBuscarApi.disabled = false;
        }
    };

    if (btnBuscarApi) btnBuscarApi.addEventListener('click', buscarLibroEnApi);
    if (inputNuevoIsbn) {
        inputNuevoIsbn.addEventListener('keydown', (e) => { if (e.key === 'Enter') { e.preventDefault(); buscarLibroEnApi(); } });
    }
});