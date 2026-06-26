DROP DATABASE IF EXISTS BD_BIBLIOTECA_ALEJANDRIA;
CREATE DATABASE BD_BIBLIOTECA_ALEJANDRIA
CHARACTER SET utf8mb4
COLLATE utf8mb4_0900_ai_ci;
USE BD_BIBLIOTECA_ALEJANDRIA;

-- =======================================================
-- MÓDULO 1: ACCESOS Y SEGURIDAD (Sin Sucursales)
-- =======================================================
CREATE TABLE ROL (
    ID                      INT AUTO_INCREMENT PRIMARY KEY,
    NOMBRE                  VARCHAR(50) NOT NULL UNIQUE,
    DESCRIPCION             VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE USUARIO (
    ID                      INT AUTO_INCREMENT PRIMARY KEY,
    NOMBRE_COMPLETO         VARCHAR(150) NOT NULL,
    EMAIL                   VARCHAR(100) NOT NULL UNIQUE,
    PASSWORD_HASH           VARCHAR(255) NOT NULL,
    ACTIVO                  BOOLEAN DEFAULT TRUE,
    CREADO_EN               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ACTUALIZADO_EN          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE USUARIO_ROL (
    USUARIO_ID              INT NOT NULL,
    ROL_ID                  INT NOT NULL,
    PRIMARY KEY (USUARIO_ID, ROL_ID),
    CONSTRAINT FK_UR_USUARIO FOREIGN KEY (USUARIO_ID) REFERENCES USUARIO(ID) ON DELETE CASCADE,
    CONSTRAINT FK_UR_ROL FOREIGN KEY (ROL_ID) REFERENCES ROL(ID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- =======================================================
-- MÓDULO 2: CATÁLOGO DE PRODUCTOS (Altamente Normalizado)
-- =======================================================
CREATE TABLE CATEGORIA (
    ID                      INT AUTO_INCREMENT PRIMARY KEY,
    NOMBRE                  VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE EDITORIAL (
    ID                      INT AUTO_INCREMENT PRIMARY KEY,
    NOMBRE                  VARCHAR(100) NOT NULL UNIQUE,
    PAIS                    VARCHAR(100)
) ENGINE=InnoDB;

CREATE TABLE AUTOR (
    ID                      INT AUTO_INCREMENT PRIMARY KEY,
    NOMBRE                  VARCHAR(150) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE LIBRO (
    ID                      INT AUTO_INCREMENT PRIMARY KEY,
    ISBN                    VARCHAR(20) NOT NULL UNIQUE,
    TITULO                  VARCHAR(255) NOT NULL,
    SLUG                    VARCHAR(255) UNIQUE,
    IMAGEN_PORTADA          VARCHAR(255),
    SINOPSIS                TEXT,
    PAGINAS                 INT NOT NULL,
    FORMATO                 ENUM('TAPA_BLANDA', 'TAPA_DURA', 'DE_BOLSILLO') NOT NULL DEFAULT 'TAPA_BLANDA',
    PRECIO_VENTA_ACTUAL     DECIMAL(10,2) NOT NULL,
    EDITORIAL_ID            INT NOT NULL,
    ACTIVO                  BOOLEAN DEFAULT TRUE,
    CREADO_EN               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ACTUALIZADO_EN          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT CHK_LIBRO_PAGINAS CHECK (PAGINAS > 0),
    CONSTRAINT CHK_LIBRO_PRECIO_VENTA CHECK (PRECIO_VENTA_ACTUAL >= 0),
    CONSTRAINT FK_LIBRO_EDITORIAL FOREIGN KEY (EDITORIAL_ID) REFERENCES EDITORIAL(ID),
    INDEX IDX_LIBRO_TITULO (TITULO),
    INDEX IDX_LIBRO_ISBN (ISBN)
) ENGINE=InnoDB;

CREATE TABLE LIBRO_AUTOR (
    LIBRO_ID                INT NOT NULL,
    AUTOR_ID                INT NOT NULL,
    PRIMARY KEY (LIBRO_ID, AUTOR_ID),
    CONSTRAINT FK_LA_LIBRO FOREIGN KEY (LIBRO_ID) REFERENCES LIBRO(ID) ON DELETE CASCADE,
    CONSTRAINT FK_LA_AUTOR FOREIGN KEY (AUTOR_ID) REFERENCES AUTOR(ID) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE LIBRO_CATEGORIA (
    LIBRO_ID                INT NOT NULL,
    CATEGORIA_ID            INT NOT NULL,
    PRIMARY KEY (LIBRO_ID, CATEGORIA_ID),
    CONSTRAINT FK_LC_LIBRO FOREIGN KEY (LIBRO_ID) REFERENCES LIBRO(ID) ON DELETE CASCADE,
    CONSTRAINT FK_LC_CATEGORIA FOREIGN KEY (CATEGORIA_ID) REFERENCES CATEGORIA(ID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- =======================================================
-- MÓDULO 3: CLIENTES (B2C Simplificado)
-- =======================================================
CREATE TABLE CLIENTE (
    ID                      INT AUTO_INCREMENT PRIMARY KEY,
    USUARIO_ID              INT NOT NULL UNIQUE, 
    TIPO_DOCUMENTO          ENUM('DNI', 'CE', 'PASAPORTE') NOT NULL,
    NUMERO_DOCUMENTO        VARCHAR(20) NOT NULL UNIQUE,
    TELEFONO                VARCHAR(20),
    CREADO_EN               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ACTUALIZADO_EN          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT FK_CLIENTE_USUARIO FOREIGN KEY (USUARIO_ID) REFERENCES USUARIO(ID) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE DIRECCION_ENVIO (
    ID                      INT AUTO_INCREMENT PRIMARY KEY,
    CLIENTE_ID              INT NOT NULL,
    ETIQUETA                VARCHAR(50) NOT NULL,
    DIRECCION_COMPLETA      VARCHAR(255) NOT NULL,
    CIUDAD                  VARCHAR(100) NOT NULL,
    CODIGO_POSTAL           VARCHAR(20),
    CONSTRAINT FK_DIR_CLIENTE FOREIGN KEY (CLIENTE_ID) REFERENCES CLIENTE(ID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- =======================================================
-- MÓDULO 4: LOGÍSTICA Y CONTROL DE STOCK (Tienda Única)
-- =======================================================
CREATE TABLE INVENTARIO_VENTA (
    LIBRO_ID                INT PRIMARY KEY,
    CANTIDAD_DISPONIBLE     INT NOT NULL DEFAULT 0,
    CANTIDAD_RESERVADA      INT NOT NULL DEFAULT 0,
    STOCK_MINIMO            INT DEFAULT 5,
    STOCK_MAXIMO            INT DEFAULT 1000,
    
    CONSTRAINT CHK_INV_DISPONIBLE CHECK (CANTIDAD_DISPONIBLE >= 0),
    CONSTRAINT CHK_INV_RESERVADA CHECK (CANTIDAD_RESERVADA >= 0 AND CANTIDAD_RESERVADA <= CANTIDAD_DISPONIBLE),
    CONSTRAINT FK_INV_LIBRO FOREIGN KEY (LIBRO_ID) REFERENCES LIBRO(ID) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE MOVIMIENTOS_INVENTARIO (
    ID                      INT AUTO_INCREMENT PRIMARY KEY,
    USUARIO_ID              INT NULL, -- Operador de almacén
    LIBRO_ID                INT NOT NULL,
    TIPO_MOVIMIENTO         ENUM('INGRESO_PROVEEDOR', 'VENTA_ONLINE', 'DEVOLUCION', 'MERMA', 'AJUSTE_MANUAL') NOT NULL,
    CANTIDAD                INT NOT NULL,
    DESCRIPCION             VARCHAR(255),
    FECHA_MOVIMIENTO        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT CHK_MOV_CANTIDAD CHECK (CANTIDAD > 0),
    CONSTRAINT FK_MOV_USUARIO FOREIGN KEY (USUARIO_ID) REFERENCES USUARIO(ID),
    CONSTRAINT FK_MOV_LIBRO FOREIGN KEY (LIBRO_ID) REFERENCES LIBRO(ID)
) ENGINE=InnoDB;

-- =======================================================
-- MÓDULO 5: PROCESO DE VENTAS E-COMMERCE
-- =======================================================
CREATE TABLE CARRITO (
    ID                      INT AUTO_INCREMENT PRIMARY KEY,
    CLIENTE_ID              INT NOT NULL UNIQUE,
    CREADO_EN               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ACTUALIZADO_EN          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT FK_CARRITO_CLIENTE FOREIGN KEY (CLIENTE_ID) REFERENCES CLIENTE(ID) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE CARRITO_DETALLE (
    CARRITO_ID              INT NOT NULL,
    LIBRO_ID                INT NOT NULL,
    CANTIDAD                INT NOT NULL DEFAULT 1,
    PRIMARY KEY (CARRITO_ID, LIBRO_ID),
    
    CONSTRAINT CHK_CARRITO_CANTIDAD CHECK (CANTIDAD > 0),
    CONSTRAINT FK_CD_CARRITO FOREIGN KEY (CARRITO_ID) REFERENCES CARRITO(ID) ON DELETE CASCADE,
    CONSTRAINT FK_CD_LIBRO FOREIGN KEY (LIBRO_ID) REFERENCES LIBRO(ID) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE PEDIDO (
    ID                      INT AUTO_INCREMENT PRIMARY KEY,
    CLIENTE_ID              INT NOT NULL,
    DIRECCION_ENVIO_ID      INT NULL,
    ESTADO_ACTUAL           ENUM('PENDIENTE_PAGO', 'PAGADO', 'ENVIADO', 'ENTREGADO', 'CANCELADO') DEFAULT 'PENDIENTE_PAGO',
    TOTAL                   DECIMAL(12,2) NOT NULL,
    FECHA_PEDIDO            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CREADO_EN               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ACTUALIZADO_EN          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT CHK_PEDIDO_TOTAL CHECK (TOTAL >= 0),
    CONSTRAINT FK_PEDIDO_CLIENTE FOREIGN KEY (CLIENTE_ID) REFERENCES CLIENTE(ID),
    CONSTRAINT FK_PEDIDO_DIRECCION FOREIGN KEY (DIRECCION_ENVIO_ID) REFERENCES DIRECCION_ENVIO(ID)
) ENGINE=InnoDB;

CREATE TABLE DETALLE_PEDIDO (
    ID                      INT AUTO_INCREMENT PRIMARY KEY,
    PEDIDO_ID               INT NOT NULL,
    LIBRO_ID                INT NOT NULL,
    CANTIDAD                INT NOT NULL,
    PRECIO_UNITARIO         DECIMAL(10,2) NOT NULL,
    SUBTOTAL                DECIMAL(12,2) NOT NULL,
    
    CONSTRAINT UK_PEDIDO_LIBRO UNIQUE (PEDIDO_ID, LIBRO_ID),
    CONSTRAINT CHK_DP_CANTIDAD CHECK (CANTIDAD > 0),
    CONSTRAINT CHK_DP_PRECIO CHECK (PRECIO_UNITARIO >= 0),
    CONSTRAINT CHK_DP_SUBTOTAL CHECK (SUBTOTAL >= 0),
    CONSTRAINT FK_DP_PEDIDO FOREIGN KEY (PEDIDO_ID) REFERENCES PEDIDO(ID) ON DELETE CASCADE,
    CONSTRAINT FK_DP_LIBRO FOREIGN KEY (LIBRO_ID) REFERENCES LIBRO(ID)
) ENGINE=InnoDB;

CREATE TABLE PAGO (
    ID                      INT AUTO_INCREMENT PRIMARY KEY,
    PEDIDO_ID               INT NOT NULL,
    METODO_PAGO             ENUM('EFECTIVO', 'TARJETA_CREDITO', 'TARJETA_DEBITO', 'TRANSFERENCIA', 'YAPE', 'PLIN') NOT NULL,
    ESTADO_PAGO             ENUM('PENDIENTE', 'APROBADO', 'RECHAZADO', 'REEMBOLSADO') DEFAULT 'PENDIENTE',
    MONTO                   DECIMAL(10,2) NOT NULL,
    REFERENCIA_PAGO         VARCHAR(100),
    FECHA_PAGO              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT CHK_PAGO_MONTO CHECK (MONTO >= 0),
    CONSTRAINT FK_PAGO_PEDIDO FOREIGN KEY (PEDIDO_ID) REFERENCES PEDIDO(ID) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE COMPROBANTE_FISCAL (
    ID                      INT AUTO_INCREMENT PRIMARY KEY,
    PEDIDO_ID               INT NOT NULL,
    TIPO_COMPROBANTE        ENUM('BOLETA', 'FACTURA') NOT NULL,
    SERIE                   VARCHAR(10) NOT NULL,
    NUMERO                  VARCHAR(20) NOT NULL,
    MONTO_IMPUESTO          DECIMAL(10,2) NOT NULL,
    MONTO_TOTAL             DECIMAL(10,2) NOT NULL,
    FECHA_EMISION           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT UK_COMPROBANTE UNIQUE (SERIE, NUMERO),
    CONSTRAINT CHK_COMP_TOTAL CHECK (MONTO_TOTAL >= 0),
    CONSTRAINT FK_COMP_PEDIDO FOREIGN KEY (PEDIDO_ID) REFERENCES PEDIDO(ID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- DATA SET: INSERTS DE PRUEBA (DATA COHERENTE)
-- =========================================================

INSERT INTO ROL (NOMBRE, DESCRIPCION) VALUES 
('ADMINISTRADOR', 'CONTROL TOTAL'), 
('ALMACENERO', 'GESTION DE INVENTARIO'), 
('CLIENTE_WEB', 'CLIENTE DEL PORTAL');

INSERT INTO USUARIO
(NOMBRE_COMPLETO,EMAIL,PASSWORD_HASH)
VALUES
('Administrador',
'admin@alejandria.com',
'$2a$10$S2vE3fgmYApEz5isbGn5HecfO1Np7UyfdtNhuWeaqwnfyXiPcB7w.'),
('Juan Almacén',
'almacen@alejandria.com',
'$2a$10$S2vE3fgmYApEz5isbGn5HecfO1Np7UyfdtNhuWeaqwnfyXiPcB7w.'),
('Usuario Web',
'web@alejandria.com',
'$2a$10$S2vE3fgmYApEz5isbGn5HecfO1Np7UyfdtNhuWeaqwnfyXiPcB7w.');

INSERT INTO USUARIO_ROL VALUES
(1,1),
(2,2),
(3,3);

INSERT INTO EDITORIAL (NOMBRE,PAIS) VALUES
('Planeta','España'),
('Penguin Random House','España'),
('Alfaguara','España'),
('Anagrama','España'),
('Minotauro','España'),
('Debolsillo','España'),
('Salamandra','España'),
('Roca Editorial','España');

INSERT INTO CATEGORIA (NOMBRE) VALUES
('Novela'),
('Fantasía'),
('Ciencia Ficción'),
('Terror'),
('Historia'),
('Romance'),
('Autoayuda'),
('Negocios'),
('Programación'),
('Clásicos');

INSERT INTO AUTOR (NOMBRE) VALUES
('Gabriel García Márquez'),
('Mario Vargas Llosa'),
('George Orwell'),
('J.R.R. Tolkien'),
('J.K. Rowling'),
('Stephen King'),
('Dan Brown'),
('Yuval Noah Harari'),
('Robert C. Martin'),
('Miguel de Cervantes'),
('Antoine de Saint-Exupéry'),
('Paulo Coelho'),
('Frank Herbert'),
('George R. R. Martin'),
('Sun Tzu'),
('Dale Carnegie'),
('Napoleon Hill'),
('Isaac Asimov'),
('Jane Austen'),
('Harper Lee');

INSERT INTO LIBRO
(ISBN,TITULO,SLUG,IMAGEN_PORTADA,SINOPSIS,PAGINAS,FORMATO,PRECIO_VENTA_ACTUAL,EDITORIAL_ID)
VALUES

('9780307474728',
'Cien años de soledad',
'cien-anos-de-soledad',
'https://covers.openlibrary.org/b/isbn/9780307474728-L.jpg',
'Obra maestra del realismo mágico.',
496,
'TAPA_BLANDA',
79.90,
2),

('9788420471839',
'La ciudad y los perros',
'la-ciudad-y-los-perros',
'https://covers.openlibrary.org/b/isbn/9788420471839-L.jpg',
'Novela de Mario Vargas Llosa.',
448,
'TAPA_BLANDA',
69.90,
1),

('9780451524935',
'1984',
'1984',
'https://covers.openlibrary.org/b/isbn/9780451524935-L.jpg',
'Distopía clásica.',
328,
'TAPA_BLANDA',
49.90,
2),

('9780618640157',
'El Señor de los Anillos',
'el-senor-de-los-anillos',
'https://covers.openlibrary.org/b/isbn/9780618640157-L.jpg',
'La épica aventura de la Tierra Media.',
1216,
'TAPA_DURA',
149.90,
5),

('9788478884957',
'Harry Potter y la piedra filosofal',
'harry-potter-1',
'https://covers.openlibrary.org/b/isbn/9788478884957-L.jpg',
'Inicio de la saga.',
320,
'TAPA_BLANDA',
69.90,
7),

('9780307743657',
'El resplandor',
'el-resplandor',
'https://covers.openlibrary.org/b/isbn/9780307743657-L.jpg',
'Clásico del terror.',
688,
'TAPA_BLANDA',
65.90,
2),

('9780307474278',
'El código Da Vinci',
'el-codigo-da-vinci',
'https://covers.openlibrary.org/b/isbn/9780307474278-L.jpg',
'Thriller de misterio.',
592,
'TAPA_BLANDA',
64.90,
2),

('9780062316097',
'Sapiens',
'sapiens',
'https://covers.openlibrary.org/b/isbn/9780062316097-L.jpg',
'Historia de la humanidad.',
512,
'TAPA_BLANDA',
84.90,
3),

('9780132350884',
'Clean Code',
'clean-code',
'https://covers.openlibrary.org/b/isbn/9780132350884-L.jpg',
'Buenas prácticas de programación.',
464,
'TAPA_BLANDA',
179.90,
2),

('9788424119475',
'Don Quijote de la Mancha',
'don-quijote',
'https://covers.openlibrary.org/b/isbn/9788424119475-L.jpg',
'Clásico español.',
1376,
'TAPA_DURA',
99.90,
1),

('9780156012195',
'El Principito',
'el-principito',
'https://covers.openlibrary.org/b/isbn/9780156012195-L.jpg',
'Obra universal.',
96,
'TAPA_BLANDA',
39.90,
2),

('9780061122415',
'El alquimista',
'el-alquimista',
'https://covers.openlibrary.org/b/isbn/9780061122415-L.jpg',
'Novela inspiradora.',
208,
'TAPA_BLANDA',
49.90,
2),

('9780441172719',
'Dune',
'dune',
'https://covers.openlibrary.org/b/isbn/9780441172719-L.jpg',
'Ciencia ficción clásica.',
688,
'TAPA_BLANDA',
89.90,
5),

('9780553593716',
'Juego de Tronos',
'juego-de-tronos',
'https://covers.openlibrary.org/b/isbn/9780553593716-L.jpg',
'Primera novela de Canción de hielo y fuego.',
848,
'TAPA_BLANDA',
89.90,
5),

('9781599869773',
'El arte de la guerra',
'arte-de-la-guerra',
'https://covers.openlibrary.org/b/isbn/9781599869773-L.jpg',
'Estrategia militar.',
273,
'TAPA_BLANDA',
35.90,
2),

('9780671027032',
'Cómo ganar amigos e influir sobre las personas',
'como-ganar-amigos',
'https://covers.openlibrary.org/b/isbn/9780671027032-L.jpg',
'Desarrollo personal.',
320,
'TAPA_BLANDA',
55.90,
2),

('9781585424337',
'Piense y hágase rico',
'piense-y-hagase-rico',
'https://covers.openlibrary.org/b/isbn/9781585424337-L.jpg',
'Finanzas personales.',
320,
'TAPA_BLANDA',
54.90,
2),

('9780553293357',
'Fundación',
'fundacion',
'https://covers.openlibrary.org/b/isbn/9780553293357-L.jpg',
'Saga clásica de ciencia ficción.',
296,
'TAPA_BLANDA',
59.90,
5),

('9780141439518',
'Orgullo y prejuicio',
'orgullo-y-prejuicio',
'https://covers.openlibrary.org/b/isbn/9780141439518-L.jpg',
'Novela romántica.',
480,
'TAPA_BLANDA',
49.90,
2),

('9780061120084',
'Matar un ruiseñor',
'matar-un-ruisenor',
'https://covers.openlibrary.org/b/isbn/9780061120084-L.jpg',
'Clásico de la literatura.',
336,
'TAPA_BLANDA',
55.90,
2);

INSERT INTO INVENTARIO_VENTA
(LIBRO_ID,CANTIDAD_DISPONIBLE,CANTIDAD_RESERVADA,STOCK_MINIMO,STOCK_MAXIMO)
VALUES
(1,45,2,5,200),
(2,32,1,5,200),
(3,58,3,5,200),
(4,20,0,5,100),
(5,40,4,5,150),
(6,18,1,5,100),
(7,37,2,5,150),
(8,29,0,5,120),
(9,15,1,5,80),
(10,24,0,5,100),
(11,51,3,5,200),
(12,43,2,5,150),
(13,22,0,5,100),
(14,27,1,5,120),
(15,48,2,5,200),
(16,36,1,5,150),
(17,31,0,5,120),
(18,26,1,5,120),
(19,39,2,5,180),
(20,34,1,5,150);

INSERT INTO MOVIMIENTOS_INVENTARIO
(USUARIO_ID,LIBRO_ID,TIPO_MOVIMIENTO,CANTIDAD,DESCRIPCION)
VALUES

(2,1,'INGRESO_PROVEEDOR',20,'Reposición de stock'),
(2,1,'VENTA_ONLINE',5,'Venta realizada'),

(2,2,'INGRESO_PROVEEDOR',15,'Compra a editorial'),
(2,2,'VENTA_ONLINE',3,'Pedido web'),

(2,3,'VENTA_ONLINE',6,'Venta online'),
(2,3,'DEVOLUCION',2,'Cliente devolvió ejemplares'),

(2,4,'INGRESO_PROVEEDOR',10,'Nueva edición'),
(2,4,'VENTA_ONLINE',4,'Venta'),

(2,5,'VENTA_ONLINE',8,'Venta'),
(2,5,'DEVOLUCION',1,'Libro devuelto'),

(2,6,'MERMA',2,'Libro deteriorado'),
(2,6,'INGRESO_PROVEEDOR',8,'Reposición'),

(2,7,'VENTA_ONLINE',5,'Venta'),
(2,7,'INGRESO_PROVEEDOR',12,'Reposición'),

(2,8,'VENTA_ONLINE',4,'Venta'),
(2,8,'DEVOLUCION',1,'Devolución'),

(2,9,'INGRESO_PROVEEDOR',5,'Compra'),
(2,9,'VENTA_ONLINE',2,'Venta'),

(2,10,'VENTA_ONLINE',3,'Venta'),
(2,10,'MERMA',1,'Libro dañado'),

(2,11,'INGRESO_PROVEEDOR',15,'Reposición'),
(2,11,'VENTA_ONLINE',6,'Venta'),

(2,12,'VENTA_ONLINE',5,'Venta'),
(2,12,'DEVOLUCION',2,'Cliente devolvió'),

(2,13,'INGRESO_PROVEEDOR',10,'Compra'),
(2,13,'VENTA_ONLINE',4,'Venta'),

(2,14,'VENTA_ONLINE',3,'Venta'),
(2,14,'MERMA',1,'Libro maltratado'),

(2,15,'INGRESO_PROVEEDOR',20,'Reposición'),
(2,15,'VENTA_ONLINE',10,'Venta'),

(2,16,'VENTA_ONLINE',5,'Venta'),
(2,16,'DEVOLUCION',1,'Devolución'),

(2,17,'INGRESO_PROVEEDOR',8,'Compra'),
(2,17,'VENTA_ONLINE',4,'Venta'),

(2,18,'VENTA_ONLINE',2,'Venta'),
(2,18,'MERMA',1,'Libro dañado'),

(2,19,'INGRESO_PROVEEDOR',12,'Reposición'),
(2,19,'VENTA_ONLINE',5,'Venta'),

(2,20,'VENTA_ONLINE',4,'Venta'),
(2,20,'DEVOLUCION',1,'Cliente devolvió');

INSERT INTO USUARIO (NOMBRE_COMPLETO,EMAIL,PASSWORD_HASH) VALUES
('Carlos Pérez','carlos@gmail.com','$2a$10$S2vE3fgmYApEz5isbGn5HecfO1Np7UyfdtNhuWeaqwnfyXiPcB7w.'),
('Ana Torres','ana@gmail.com','$2a$10$S2vE3fgmYApEz5isbGn5HecfO1Np7UyfdtNhuWeaqwnfyXiPcB7w.'),
('Luis Rojas','luis@gmail.com','$2a$10$S2vE3fgmYApEz5isbGn5HecfO1Np7UyfdtNhuWeaqwnfyXiPcB7w.'),
('María Flores','maria@gmail.com','$2a$10$S2vE3fgmYApEz5isbGn5HecfO1Np7UyfdtNhuWeaqwnfyXiPcB7w.'),
('Pedro Díaz','pedro@gmail.com','$2a$10$S2vE3fgmYApEz5isbGn5HecfO1Np7UyfdtNhuWeaqwnfyXiPcB7w.');

INSERT INTO USUARIO_ROL VALUES
(4,3),
(5,3),
(6,3),
(7,3),
(8,3);

INSERT INTO CLIENTE
(USUARIO_ID,TIPO_DOCUMENTO,NUMERO_DOCUMENTO,TELEFONO)
VALUES
(3,'DNI','71234567','999111111'),
(4,'DNI','71234568','999111112'),
(5,'DNI','71234569','999111113'),
(6,'DNI','71234570','999111114'),
(7,'DNI','71234571','999111115'),
(8,'DNI','71234572','999111116');

INSERT INTO DIRECCION_ENVIO
(CLIENTE_ID,ETIQUETA,DIRECCION_COMPLETA,CIUDAD,CODIGO_POSTAL)
VALUES
(1,'Casa','Av. Primavera 101','Lima','15001'),
(2,'Casa','Av. Javier Prado 200','Lima','15002'),
(3,'Casa','Av. Canadá 500','Lima','15003'),
(4,'Casa','Av. Brasil 1200','Lima','15004'),
(5,'Casa','Av. Arequipa 800','Lima','15005'),
(6,'Casa','Av. Colonial 950','Lima','15006');

INSERT INTO PEDIDO
(CLIENTE_ID,DIRECCION_ENVIO_ID,ESTADO_ACTUAL,TOTAL,FECHA_PEDIDO)
VALUES

(1,1,'ENTREGADO',159.80,'2026-01-12'),
(2,2,'ENTREGADO',279.70,'2026-01-28'),

(3,3,'ENTREGADO',119.80,'2026-02-08'),
(4,4,'ENTREGADO',369.60,'2026-02-20'),

(5,5,'ENTREGADO',249.50,'2026-03-04'),
(6,6,'ENTREGADO',429.30,'2026-03-25'),

(2,2,'ENTREGADO',319.60,'2026-04-09'),
(1,1,'ENTREGADO',459.50,'2026-04-22'),

(4,4,'ENTREGADO',199.80,'2026-05-10'),
(3,3,'ENTREGADO',589.70,'2026-05-26'),

(6,6,'ENTREGADO',299.60,'2026-06-08'),
(5,5,'ENTREGADO',499.40,'2026-06-24');

INSERT INTO DETALLE_PEDIDO
(PEDIDO_ID,LIBRO_ID,CANTIDAD,PRECIO_UNITARIO,SUBTOTAL)
VALUES

(1,1,2,79.90,159.80),

(2,4,1,149.90,149.90),
(2,9,1,179.90,179.90),

(3,3,2,49.90,99.80),
(3,11,1,39.90,39.90),

(4,9,2,179.90,359.80),
(4,11,1,9.80,9.80),

(5,13,2,89.90,179.80),
(5,16,1,69.70,69.70),

(6,4,2,149.90,299.80),
(6,15,2,64.75,129.50),

(7,8,2,84.90,169.80),
(7,20,3,49.93,149.80),

(8,9,1,179.90,179.90),
(8,4,1,149.90,149.90),
(8,11,2,64.85,129.70),

(9,5,2,69.90,139.80),
(9,3,1,60.00,60.00),

(10,4,2,149.90,299.80),
(10,9,1,179.90,179.90),
(10,1,1,110.00,110.00),

(11,13,2,89.90,179.80),
(11,18,2,59.90,119.80),

(12,9,2,179.90,359.80),
(12,3,2,69.80,139.60);

INSERT INTO PAGO
(PEDIDO_ID,METODO_PAGO,ESTADO_PAGO,MONTO,REFERENCIA_PAGO)
SELECT
ID,
ELT((ID MOD 4)+1,'YAPE','PLIN','TARJETA_CREDITO','TRANSFERENCIA'),
'APROBADO',
TOTAL,
CONCAT('PAGO-',ID)
FROM PEDIDO;

INSERT INTO COMPROBANTE_FISCAL
(PEDIDO_ID,TIPO_COMPROBANTE,SERIE,NUMERO,MONTO_IMPUESTO,MONTO_TOTAL)
SELECT
ID,
'BOLETA',
'B001',
LPAD(ID,8,'0'),
ROUND(TOTAL*0.18,2),
TOTAL
FROM PEDIDO;