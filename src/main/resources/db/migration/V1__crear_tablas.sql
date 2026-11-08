-- NutriVida - esquema inicial (MySQL 8)
-- Los largos y rangos replican las validaciones de src/utils/validaciones.js

CREATE TABLE productos (
                           id BIGINT NOT NULL AUTO_INCREMENT,
                           codigo VARCHAR(60) NOT NULL,                 -- slug usado en /producto?id=...
                           nombre VARCHAR(60) NOT NULL,                 -- 3 a 60 caracteres
                           tipo VARCHAR(10) NOT NULL,                   -- 'Producto' o 'Servicio'
                           precio INT NOT NULL,                         -- CLP entero, 1 a 9999999
                           unidad VARCHAR(30) NOT NULL,                 -- envase, pack, plan, sesion...
                           resumen VARCHAR(120) NOT NULL,               -- 10 a 120 caracteres
                           descripcion TEXT NOT NULL,
                           PRIMARY KEY (id),
                           UNIQUE KEY uk_productos_codigo (codigo),
                           CONSTRAINT ck_productos_tipo CHECK (tipo IN ('Producto', 'Servicio')),
                           CONSTRAINT ck_productos_precio CHECK (precio BETWEEN 1 AND 9999999)
);

CREATE TABLE producto_imagenes (
                                   id BIGINT NOT NULL AUTO_INCREMENT,
                                   producto_id BIGINT NOT NULL,
                                   archivo VARCHAR(150) NOT NULL,
                                   texto VARCHAR(150) NOT NULL,                 -- texto alternativo
                                   orden INT NOT NULL,
                                   PRIMARY KEY (id),
                                   UNIQUE KEY uk_imagen_orden (producto_id, orden),
                                   CONSTRAINT fk_imagenes_producto FOREIGN KEY (producto_id) REFERENCES productos (id) ON DELETE CASCADE
);

CREATE TABLE producto_beneficios (
                                     id BIGINT NOT NULL AUTO_INCREMENT,
                                     producto_id BIGINT NOT NULL,
                                     texto VARCHAR(200) NOT NULL,
                                     orden INT NOT NULL,
                                     PRIMARY KEY (id),
                                     UNIQUE KEY uk_beneficio_orden (producto_id, orden),
                                     CONSTRAINT fk_beneficios_producto FOREIGN KEY (producto_id) REFERENCES productos (id) ON DELETE CASCADE
);

CREATE TABLE producto_ficha (
                                id BIGINT NOT NULL AUTO_INCREMENT,
                                producto_id BIGINT NOT NULL,
                                etiqueta VARCHAR(60) NOT NULL,
                                valor VARCHAR(150) NOT NULL,
                                orden INT NOT NULL,
                                PRIMARY KEY (id),
                                UNIQUE KEY uk_ficha_orden (producto_id, orden),
                                CONSTRAINT fk_ficha_producto FOREIGN KEY (producto_id) REFERENCES productos (id) ON DELETE CASCADE
);

CREATE TABLE regiones (
                          id INT NOT NULL AUTO_INCREMENT,
                          nombre VARCHAR(50) NOT NULL,
                          PRIMARY KEY (id),
                          UNIQUE KEY uk_regiones_nombre (nombre)
);

CREATE TABLE comunas (
                         id INT NOT NULL AUTO_INCREMENT,
                         region_id INT NOT NULL,
                         nombre VARCHAR(50) NOT NULL,
                         PRIMARY KEY (id),
                         UNIQUE KEY uk_comunas_region_nombre (region_id, nombre),
                         CONSTRAINT fk_comunas_region FOREIGN KEY (region_id) REFERENCES regiones (id)
);

CREATE TABLE usuarios (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          run VARCHAR(9) NOT NULL,                     -- sin puntos ni guion, 7 a 9 caracteres
                          nombre VARCHAR(50) NOT NULL,
                          apellidos VARCHAR(100) NOT NULL,
                          correo VARCHAR(100) NOT NULL,                -- @duoc.cl, @profesor.duoc.cl o @gmail.com
                          contrasena VARCHAR(10) NOT NULL,             -- 4 a 10 caracteres
                          fecha_nacimiento DATE,                       -- opcional en el registro
                          region_id INT NOT NULL,
                          comuna_id INT NOT NULL,
                          objetivo VARCHAR(30) NOT NULL,
                          tipo_alimentacion VARCHAR(15) NOT NULL,
                          restricciones VARCHAR(200),                  -- opcional
                          rol VARCHAR(10) NOT NULL DEFAULT 'CLIENTE',
                          PRIMARY KEY (id),
                          UNIQUE KEY uk_usuarios_run (run),
                          UNIQUE KEY uk_usuarios_correo (correo),
                          CONSTRAINT ck_usuarios_objetivo CHECK (objetivo IN (
                                                                              'Bajar de peso', 'Ganar masa muscular', 'Mantener mi peso', 'Alimentarme más saludable')),
                          CONSTRAINT ck_usuarios_alimentacion CHECK (tipo_alimentacion IN (
                                                                                           'Omnívora', 'Vegetariana', 'Vegana', 'Otra')),
                          CONSTRAINT fk_usuarios_region FOREIGN KEY (region_id) REFERENCES regiones (id),
                          CONSTRAINT fk_usuarios_comuna FOREIGN KEY (comuna_id) REFERENCES comunas (id),
                          CONSTRAINT ck_usuarios_rol CHECK (rol IN ('ADMIN', 'CLIENTE'))
);

CREATE TABLE ventas (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        fecha DATE NOT NULL,
                        usuario_id BIGINT NOT NULL,
                        total INT NOT NULL,
                        PRIMARY KEY (id),
                        CONSTRAINT fk_ventas_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
                        CONSTRAINT ck_ventas_total CHECK (total >= 0)
);

CREATE TABLE venta_detalle (
                               id BIGINT NOT NULL AUTO_INCREMENT,
                               venta_id BIGINT NOT NULL,
                               producto_id BIGINT NOT NULL,
                               cantidad INT NOT NULL,                       -- tope de 20 por compra en linea
                               precio_unitario INT NOT NULL,                -- precio congelado al momento de la venta
                               subtotal INT NOT NULL,
                               PRIMARY KEY (id),
                               UNIQUE KEY uk_detalle_producto (venta_id, producto_id),
                               CONSTRAINT fk_detalle_venta FOREIGN KEY (venta_id) REFERENCES ventas (id) ON DELETE CASCADE,
                               CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES productos (id),
                               CONSTRAINT ck_detalle_cantidad CHECK (cantidad BETWEEN 1 AND 20)
);