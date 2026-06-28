USE BD_BIBLIOTECA_ALEJANDRIA;


INSERT INTO LIBRO (ISBN, TITULO, SLUG, IMAGEN_PORTADA, SINOPSIS, PAGINAS, FORMATO, PRECIO_VENTA_ACTUAL, EDITORIAL_ID) VALUES
-- Bloque 1: Novelas y Clásicos (Editorial 1 y 2, Categoría General)
('9788432240010', 'Crónica de una muerte anunciada', 'cronica-de-una-muerte', 'https://covers.openlibrary.org/b/isbn/9788432240010-L.jpg', 'Novela corta de Gabriel García Márquez.', 128, 'TAPA_BLANDA', 45.00, 1),
('9788420440027', 'Conversación en La Catedral', 'conversacion-en-la-catedral', 'https://covers.openlibrary.org/b/isbn/9788420440027-L.jpg', 'Obra cumbre de Mario Vargas Llosa.', 608, 'TAPA_BLANDA', 89.90, 1),
('9780141180038', 'Rebelión en la granja', 'rebelion-en-la-granja', 'https://covers.openlibrary.org/b/isbn/9780141180038-L.jpg', 'Fábula distópica clásica.', 112, 'DE_BOLSILLO', 29.90, 2),
('9788497590045', 'Fahrenheit 451', 'fahrenheit-451', 'https://covers.openlibrary.org/b/isbn/9788497590045-L.jpg', 'Historia de un futuro donde los libros se queman.', 192, 'DE_BOLSILLO', 35.00, 6),
('9788433900052', 'Un mundo feliz', 'un-mundo-feliz', 'https://covers.openlibrary.org/b/isbn/9788433900052-L.jpg', 'La distopía perfecta de Aldous Huxley.', 256, 'TAPA_BLANDA', 49.00, 4),
('9788499080066', 'El retrato de Dorian Gray', 'el-retrato-de-dorian-gray', 'https://covers.openlibrary.org/b/isbn/9788499080066-L.jpg', 'El clásico filosófico y estético de Oscar Wilde.', 288, 'DE_BOLSILLO', 32.50, 6),
('9788467000073', 'Crimen y castigo', 'crimen-y-castigo', 'https://covers.openlibrary.org/b/isbn/9788467000073-L.jpg', 'La gran obra psicológica de Fiódor Dostoyevski.', 672, 'TAPA_DURA', 75.00, 1),
('9788497930081', 'La metamorfosis', 'la-metamorfosis', 'https://covers.openlibrary.org/b/isbn/9788497930081-L.jpg', 'El impactante relato de Franz Kafka.', 96, 'DE_BOLSILLO', 19.90, 6),
('9788423300095', 'Nada', 'nada-laforet', 'https://covers.openlibrary.org/b/isbn/9788423300095-L.jpg', 'Premio Nadal de Carmen Laforet.', 304, 'TAPA_BLANDA', 42.00, 1),
('9788432200102', 'La colmena', 'la-colmena', 'https://covers.openlibrary.org/b/isbn/9788432200102-L.jpg', 'Retrato de la posguerra por Camilo José Cela.', 352, 'TAPA_BLANDA', 46.00, 1),

-- Bloque 2: Fantasía y Ciencia Ficción (Editorial 5 y 7)
('9788445000114', 'El Hobbit', 'el-hobbit', 'https://covers.openlibrary.org/b/isbn/9788445000114-L.jpg', 'Aventura previa a la Comunidad del Anillo.', 312, 'TAPA_BLANDA', 59.00, 5),
('9788445000121', 'El Silmarillion', 'el-silmarillion', 'https://covers.openlibrary.org/b/isbn/9788445000121-L.jpg', 'La mitología de la Tierra Media.', 448, 'TAPA_DURA', 85.00, 5),
('9788478880133', 'Harry Potter y la cámara secreta', 'harry-potter-2', 'https://covers.openlibrary.org/b/isbn/9788478880133-L.jpg', 'Segundo año en Hogwarts.', 288, 'TAPA_BLANDA', 69.90, 7),
('9788478880140', 'Harry Potter y el prisionero de Azkaban', 'harry-potter-3', 'https://covers.openlibrary.org/b/isbn/9788478880140-L.jpg', 'Llega Sirius Black.', 384, 'TAPA_BLANDA', 72.00, 7),
('9788478880157', 'Harry Potter y el cáliz de fuego', 'harry-potter-4', 'https://covers.openlibrary.org/b/isbn/9788478880157-L.jpg', 'El Torneo de los Tres Magos.', 672, 'TAPA_BLANDA', 89.90, 7),
('9788478880164', 'Harry Potter y la Orden del Fénix', 'harry-potter-5', 'https://covers.openlibrary.org/b/isbn/9788478880164-L.jpg', 'La resistencia contra Voldemort.', 896, 'TAPA_BLANDA', 99.00, 7),
('9788478880171', 'Harry Potter y el misterio del príncipe', 'harry-potter-6', 'https://covers.openlibrary.org/b/isbn/9788478880171-L.jpg', 'Preparativos para la guerra final.', 608, 'TAPA_BLANDA', 85.00, 7),
('9788478880188', 'Harry Potter y las Reliquias de la Muerte', 'harry-potter-7', 'https://covers.openlibrary.org/b/isbn/9788478880188-L.jpg', 'El desenlace de la saga.', 704, 'TAPA_BLANDA', 95.00, 7),
('9788445000190', 'Hijos de Dune', 'hijos-de-dune', 'https://covers.openlibrary.org/b/isbn/9788445000190-L.jpg', 'Tercera entrega de la saga de Arrakis.', 496, 'TAPA_BLANDA', 79.00, 5),
('9788445000206', 'Dios emperador de Dune', 'dios-emperador-de-dune', 'https://covers.openlibrary.org/b/isbn/9788445000206-L.jpg', 'Continuación miles de años después en Dune.', 544, 'TAPA_BLANDA', 79.00, 5),

-- Bloque 3: Terror y Suspenso (Stephen King y afines)
('9788497930210', 'It (Eso)', 'it-eso', 'https://covers.openlibrary.org/b/isbn/9788497930210-L.jpg', 'El terror acecha al club de los perdedores.', 1504, 'TAPA_DURA', 120.00, 6),
('9788497930227', 'Misery', 'misery-king', 'https://covers.openlibrary.org/b/isbn/9788497930227-L.jpg', 'Un escritor atrapado por su fan número uno.', 352, 'DE_BOLSILLO', 38.00, 6),
('9788497930234', 'Carrie', 'carrie-king', 'https://covers.openlibrary.org/b/isbn/9788497930234-L.jpg', 'La primera y famosa novela de Stephen King.', 256, 'DE_BOLSILLO', 34.00, 6),
('9788497930241', 'Cementerio de animales', 'cementerio-de-animales', 'https://covers.openlibrary.org/b/isbn/9788497930241-L.jpg', 'A veces, la muerte es mejor.', 448, 'DE_BOLSILLO', 39.90, 6),
('9788401300251', 'Ángeles y demonios', 'angeles-y-demonios', 'https://covers.openlibrary.org/b/isbn/9788401300251-L.jpg', 'Robert Langdon contra los Illuminati.', 608, 'TAPA_BLANDA', 65.00, 2),
('9788401300268', 'Inferno', 'inferno-dan-brown', 'https://covers.openlibrary.org/b/isbn/9788401300268-L.jpg', 'Misterios basados en la obra de Dante.', 544, 'TAPA_BLANDA', 68.00, 2),
('9788499080275', 'Drácula', 'dracula-stoker', 'https://covers.openlibrary.org/b/isbn/9788499080275-L.jpg', 'La obra maestra de Bram Stoker.', 512, 'DE_BOLSILLO', 29.90, 6),
('9788499080282', 'Frankenstein', 'frankenstein-shelley', 'https://covers.openlibrary.org/b/isbn/9788499080282-L.jpg', 'El moderno Prometeo de Mary Shelley.', 320, 'DE_BOLSILLO', 28.00, 6),
('9788420400299', 'El gato negro y otros relatos', 'el-gato-negro-poe', 'https://covers.openlibrary.org/b/isbn/9788420400299-L.jpg', 'Los mejores cuentos góticos de Edgar Allan Poe.', 224, 'TAPA_BLANDA', 35.00, 3),
('9788497930302', 'La llamada de Cthulhu', 'la-llamada-de-cthulhu', 'https://covers.openlibrary.org/b/isbn/9788497930302-L.jpg', 'Terror cósmico creado por H.P. Lovecraft.', 160, 'DE_BOLSILLO', 24.90, 6),

-- Bloque 4: Historia, Ensayo y Autoayuda
('9780062400311', 'Homo Deus', 'homo-deus', 'https://covers.openlibrary.org/b/isbn/9780062400311-L.jpg', 'Una breve historia del mañana por Harari.', 448, 'TAPA_BLANDA', 84.90, 3),
('9788449300322', '21 lecciones para el siglo XXI', '21-lecciones', 'https://covers.openlibrary.org/b/isbn/9788449300322-L.jpg', 'Visiones del presente tecnológico y político.', 400, 'TAPA_BLANDA', 79.00, 3),
('9788417000331', 'El monje que vendió su Ferrari', 'el-monje-ferrari', 'https://covers.openlibrary.org/b/isbn/9788417000331-L.jpg', 'Una fábula espiritual de Robin Sharma.', 224, 'TAPA_BLANDA', 49.90, 2),
('9788496100342', 'El poder del ahora', 'el-poder-del-now', 'https://covers.openlibrary.org/b/isbn/9788496100342-L.jpg', 'Una guía hacia la iluminación espiritual.', 256, 'TAPA_BLANDA', 52.00, 1),
('9788479530353', 'Los 7 hábitos de la gente altamente efectiva', 'los-7-habitos', 'https://covers.openlibrary.org/b/isbn/9788479530353-L.jpg', 'Liderazgo y efectividad por Stephen Covey.', 464, 'TAPA_BLANDA', 69.00, 1),
('9788492450367', 'Padre rico, padre pobre', 'padre-rico-padre-pobre', 'https://covers.openlibrary.org/b/isbn/9788492450367-L.jpg', 'Finanzas e inversiones de Robert Kiyosaki.', 288, 'TAPA_BLANDA', 59.90, 2),
('9788415110372', 'El hombre en busca de sentido', 'el-hombre-busca-sentido', 'https://covers.openlibrary.org/b/isbn/9788415110372-L.jpg', 'Psicología y supervivencia en campos de concentración.', 160, 'TAPA_BLANDA', 45.00, 4),
('9788496980383', 'El secreto', 'el-secreto-rhonda', 'https://covers.openlibrary.org/b/isbn/9788496980383-L.jpg', 'La ley de la atracción por Rhonda Byrne.', 224, 'TAPA_DURA', 65.00, 1),
('9788499080390', 'Moby Dick', 'moby-dick', 'https://covers.openlibrary.org/b/isbn/9788499080390-L.jpg', 'La obsesión del Capitán Ahab con la ballena.', 640, 'DE_BOLSILLO', 39.00, 6),
('9788499080406', 'La Odisea', 'la-odisea-homero', 'https://covers.openlibrary.org/b/isbn/9788499080406-L.jpg', 'El accidentado regreso de Odiseo a Ítaca.', 384, 'DE_BOLSILLO', 29.90, 6),

-- Bloque 5: Libros del 41 al 60 (Expansión variada)
('9788420600411', 'El túnel', 'el-tunel-sabato', 'https://covers.openlibrary.org/b/isbn/9788420600411-L.jpg', 'La genial obra existencialista de Ernesto Sabato.', 160, 'TAPA_BLANDA', 36.00, 4),
('9788433900428', 'Seda', 'seda-baricco', 'https://covers.openlibrary.org/b/isbn/9788433900428-L.jpg', 'Fábula poética de Alessandro Baricco.', 128, 'TAPA_BLANDA', 39.00, 4),
('9788433900435', 'Tokio blues', 'tokio-blues', 'https://covers.openlibrary.org/b/isbn/9788433900435-L.jpg', 'La nostálgica obra de Haruki Murakami.', 384, 'TAPA_BLANDA', 69.00, 4),
('9788466300449', 'Crónicas Marcianas', 'cronicas-marcianas', 'https://covers.openlibrary.org/b/isbn/9788466300449-L.jpg', 'Relatos de la colonización de Marte de Ray Bradbury.', 352, 'DE_BOLSILLO', 34.90, 6),
('9788497590456', 'El lobo estepario', 'el-lobo-estepario', 'https://covers.openlibrary.org/b/isbn/9788497590456-L.jpg', 'El laberinto interno de Hermann Hesse.', 272, 'DE_BOLSILLO', 32.00, 6),
('9788497590463', 'Siddhartha', 'siddhartha-hesse', 'https://covers.openlibrary.org/b/isbn/9788497590463-L.jpg', 'La senda de la sabiduría espiritual.', 176, 'DE_BOLSILLO', 28.00, 6),
('9788433900473', 'Ficciones', 'ficciones-borges', 'https://covers.openlibrary.org/b/isbn/9788433900473-L.jpg', 'Los legendarios cuentos de Jorge Luis Borges.', 224, 'TAPA_BLANDA', 55.00, 4),
('9788433900480', 'El Aleph', 'el-aleph-borges', 'https://covers.openlibrary.org/b/isbn/9788433900480-L.jpg', 'El infinito concentrado en un punto.', 208, 'TAPA_BLANDA', 55.00, 4),
('9788437600490', 'Rayuela', 'rayuela-cortazar', 'https://covers.openlibrary.org/b/isbn/9788437600490-L.jpg', 'La contra-novela de Julio Cortázar.', 600, 'TAPA_BLANDA', 75.00, 3),
('9788466300500', 'Bestiario', 'bestiario-cortazar', 'https://covers.openlibrary.org/b/isbn/9788466300500-L.jpg', 'Primer libro de relatos de Cortázar.', 168, 'DE_BOLSILLO', 29.00, 6),
('9788420400512', 'La tía Julia y el escribidor', 'la-tia-julia', 'https://covers.openlibrary.org/b/isbn/9788420400512-L.jpg', 'Novela semi-autobiográfica de Vargas Llosa.', 448, 'TAPA_BLANDA', 65.00, 3),
('9788420400529', 'La fiesta del Chivo', 'la-fiesta-del-chivo', 'https://covers.openlibrary.org/b/isbn/9788420400529-L.jpg', 'Análisis literario sobre la dictadura de Trujillo.', 544, 'TAPA_BLANDA', 72.00, 3),
('9788432200539', 'El coronel no tiene quien le escriba', 'el-coronel', 'https://covers.openlibrary.org/b/isbn/9788432200539-L.jpg', 'Espera trágica e inquebrantable de justicia.', 112, 'TAPA_BLANDA', 39.00, 1),
('9788432200546', 'Del amor y otros demonios', 'del-amor-demonios', 'https://covers.openlibrary.org/b/isbn/9788432200546-L.jpg', 'Romance trágico en la época colonial.', 208, 'TAPA_BLANDA', 45.00, 1),
('9788423300557', 'La sombra del viento', 'la-sombra-del-viento', 'https://covers.openlibrary.org/b/isbn/9788423300557-L.jpg', 'Misterios en el Cementerio de los Libros Olvidados.', 576, 'TAPA_BLANDA', 69.90, 1),
('9788423300564', 'El juego del ángel', 'el-juego-del-angel', 'https://covers.openlibrary.org/b/isbn/9788423300564-L.jpg', 'Intrigas y pactos literarios oscuros en Barcelona.', 672, 'TAPA_BLANDA', 69.90, 1),
('9788423300571', 'El prisionero del cielo', 'el-prisionero-del-cielo', 'https://covers.openlibrary.org/b/isbn/9788423300571-L.jpg', 'Tercera parte de la inolvidable saga de Zafón.', 384, 'TAPA_BLANDA', 65.00, 1),
('9788423300588', 'El laberinto de los espíritus', 'laberinto-espiritu', 'https://covers.openlibrary.org/b/isbn/9788423300588-L.jpg', 'Espectacular cierre del universo de Zafón.', 928, 'TAPA_DURA', 99.00, 1),
('9788497590593', 'Ensayo sobre la ceguera', 'ensayo-ceguera', 'https://covers.openlibrary.org/b/isbn/9788497590593-L.jpg', 'La desgarradora epidemia blanca de José Saramago.', 384, 'DE_BOLSILLO', 45.00, 6),
('9788497590609', 'Las intermitencias de la muerte', 'intermitencias-muerte', 'https://covers.openlibrary.org/b/isbn/9788497590609-L.jpg', '¿Qué pasa cuando la muerte decide dejar de matar?', 272, 'DE_BOLSILLO', 39.00, 6),

-- Bloque 6: Libros del 61 al 85 (Cierre masivo)
('9788433900616', 'La velocidad de la luz', 'velocidad-luz', 'https://covers.openlibrary.org/b/isbn/9788433900616-L.jpg', 'Excelente novela de Javier Cercas.', 304, 'TAPA_BLANDA', 48.00, 4),
('9788433900623', 'Soldados de Salamina', 'soldados-salamina', 'https://covers.openlibrary.org/b/isbn/9788433900623-L.jpg', 'Fascinante relato sobre la Guerra Civil Española.', 216, 'TAPA_BLANDA', 45.00, 4),
('9788498380637', 'Catedrales', 'catedrales-pineiro', 'https://covers.openlibrary.org/b/isbn/9788498380637-L.jpg', 'Un crudo relato negro y de secretos de Claudia Piñeiro.', 336, 'TAPA_BLANDA', 52.00, 7),
('9788432200645', 'Terra Alta', 'terra-alta', 'https://covers.openlibrary.org/b/isbn/9788432200645-L.jpg', 'Novela ganadora del Premio Planeta por Javier Cercas.', 384, 'TAPA_BLANDA', 62.00, 1),
('9788420400652', 'Línea de fuego', 'linea-de-fuego', 'https://covers.openlibrary.org/b/isbn/9788420400652-L.jpg', 'Pérez-Reverte en la batalla del Ebro.', 688, 'TAPA_DURA', 89.00, 3),
('9788420400669', 'El club Dumas', 'el-club-dumas', 'https://covers.openlibrary.org/b/isbn/9788420400669-L.jpg', 'Orgía de suspenso, libros antiguos y ocultismo.', 480, 'TAPA_BLANDA', 59.00, 3),
('9788420400676', 'La carta esférica', 'la-carta-esferica', 'https://covers.openlibrary.org/b/isbn/9788420400676-L.jpg', 'Búsqueda de barcos hundidos en el Mediterráneo.', 496, 'TAPA_BLANDA', 59.00, 3),
('9788401000682', 'La templanza', 'la-templanza', 'https://covers.openlibrary.org/b/isbn/9788401000682-L.jpg', 'Drama de época y negocios de María Dueñas.', 544, 'TAPA_BLANDA', 68.00, 2),
('9788401000699', 'El tiempo entre costuras', 'tiempo-entre-costuras', 'https://covers.openlibrary.org/b/isbn/9788401000699-L.jpg', 'La mítica historia de Sira Quiroga en el espionaje.', 640, 'TAPA_BLANDA', 75.00, 2),
('9788423300705', 'Patria', 'patria-aramburu', 'https://covers.openlibrary.org/b/isbn/9788423300705-L.jpg', 'El impacto del terrorismo de ETA en la sociedad vasca.', 648, 'TAPA_BLANDA', 79.90, 1),
('9788466300712', 'Los pilares de la Tierra', 'pilares-de-la-tierra', 'https://covers.openlibrary.org/b/isbn/9788466300712-L.jpg', 'Obra maestra sobre la construcción de catedrales medievales.', 1040, 'DE_BOLSILLO', 69.00, 6),
('9788466300729', 'Un mundo sin fin', 'un-mundo-sin-fin', 'https://covers.openlibrary.org/b/isbn/9788466300729-L.jpg', 'La esperada continuación de Los pilares de la Tierra.', 1184, 'DE_BOLSILLO', 69.00, 6),
('9788466300736', 'El invierno del mundo', 'invierno-del-mundo', 'https://covers.openlibrary.org/b/isbn/9788466300736-L.jpg', 'Segunda parte de la célebre Trilogía Century de Ken Follett.', 960, 'DE_BOLSILLO', 65.00, 6),
('9788425300741', 'La catedral del mar', 'catedral-del-mar', 'https://covers.openlibrary.org/b/isbn/9788425300741-L.jpg', 'La Barcelona del siglo XIV de Ildefonso Falcones.', 672, 'TAPA_BLANDA', 59.00, 2),
('9788425300758', 'Los herederos de la tierra', 'herederos-tierra', 'https://covers.openlibrary.org/b/isbn/9788425300758-L.jpg', 'Secuela directa de La catedral del mar.', 912, 'TAPA_DURA', 89.00, 2),
('9788496800762', 'El psicoanalista', 'el-psicoanalista', 'https://covers.openlibrary.org/b/isbn/9788496800762-L.jpg', 'El genial juego de suspenso psicológico de John Katzenbach.', 528, 'TAPA_BLANDA', 65.00, 8),
('9788496800779', 'La historia del loco', 'historia-del-loco', 'https://covers.openlibrary.org/b/isbn/9788496800779-L.jpg', 'Intrigas dentro de un hospital psiquiátrico abandonado.', 560, 'TAPA_BLANDA', 62.00, 8),
('9788496800786', 'Jaque al psicoanalista', 'jaque-al-psicoanalista', 'https://covers.openlibrary.org/b/isbn/9788496800786-L.jpg', 'La emocionante secuela de la obra de Katzenbach.', 448, 'TAPA_BLANDA', 65.00, 8),
('9788498380792', 'El niño con el pijama de rayas', 'nino-pijama-rayas', 'https://covers.openlibrary.org/b/isbn/9788498380792-L.jpg', 'Fábula conmovedora de John Boyne.', 224, 'TAPA_BLANDA', 39.00, 7),
('9788423300801', 'Mar de fuego', 'mar-de-fuego', 'https://covers.openlibrary.org/b/isbn/9788423300801-L.jpg', 'Aventura marítima épica por Chufo Lloréns.', 864, 'TAPA_BLANDA', 69.00, 1),
('9788433900818', 'El imperio de los signos', 'imperio-signos', 'https://covers.openlibrary.org/b/isbn/9788433900818-L.jpg', 'Ensayo semiótico clásico por Roland Barthes.', 192, 'TAPA_BLANDA', 46.00, 4),
('9788497930824', 'El cartero siempre llama dos veces', 'cartero-llama-dos', 'https://covers.openlibrary.org/b/isbn/9788497930824-L.jpg', 'Novela negra clásica estadounidense por James M. Cain.', 160, 'DE_BOLSILLO', 25.00, 6),
('9788497930831', 'El largo adiós', 'el-largo-adios', 'https://covers.openlibrary.org/b/isbn/9788497930831-L.jpg', 'El detective Philip Marlowe por Raymond Chandler.', 416, 'DE_BOLSILLO', 35.00, 6),
('9788497930848', 'El sueño eterno', 'el-sueno-eterno', 'https://covers.openlibrary.org/b/isbn/9788497930848-L.jpg', 'Obra cumbre de la novela policíaca.', 256, 'DE_BOLSILLO', 32.00, 6),
('9788420600854', 'El extranjero', 'el-extranjero-camus', 'https://covers.openlibrary.org/b/isbn/9788420600854-L.jpg', 'El absurdo de Albert Camus llevado al límite.', 128, 'TAPA_BLANDA', 35.00, 4);

INSERT INTO LIBRO_CATEGORIA (LIBRO_ID, CATEGORIA_ID)
SELECT ID, (ID MOD 10) + 1 FROM LIBRO WHERE ID > 20;

INSERT INTO LIBRO_AUTOR (LIBRO_ID, AUTOR_ID)
SELECT ID, (ID MOD 20) + 1 FROM LIBRO WHERE ID > 20;

INSERT INTO INVENTARIO_VENTA (LIBRO_ID, CANTIDAD_DISPONIBLE, CANTIDAD_RESERVADA, STOCK_MINIMO, STOCK_MAXIMO)
SELECT ID, 30, 0, 5, 500 FROM LIBRO WHERE ID > 20;