-- =====================================================================
-- data.sql
-- ========
-- Spring Boot ejecuta este script automáticamente al arrancar (una vez
-- que Hibernate ya creó las tablas, gracias a
-- "spring.jpa.defer-datasource-initialization=true" en
-- application.properties). Sirve para precargar datos de ejemplo y poder
-- probar los endpoints de la API inmediatamente, sin tener que cargar
-- categorías y productos a mano.
--
-- Nota: como "spring.jpa.hibernate.ddl-auto=update" no borra datos
-- existentes, estos INSERT se protegen con "WHERE NOT EXISTS" para no
-- duplicar filas cada vez que se reinicia la aplicación.
-- =====================================================================

-- Categorías de ejemplo
INSERT INTO categoria (nombre)
SELECT * FROM (SELECT 'Running') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nombre = 'Running');

INSERT INTO categoria (nombre)
SELECT * FROM (SELECT 'Urbanas') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nombre = 'Urbanas');

INSERT INTO categoria (nombre)
SELECT * FROM (SELECT 'Basketball') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nombre = 'Basketball');

INSERT INTO categoria (nombre)
SELECT * FROM (SELECT 'Fútbol') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nombre = 'Fútbol');

INSERT INTO categoria (nombre)
SELECT * FROM (SELECT 'Training') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nombre = 'Training');

-- Productos de ejemplo, cada uno asociado a una categoría existente mediante subconsulta
INSERT INTO productos (nombre, genero, marca, descripcion, precio, color, stock, imagen_url, categoria_id)
SELECT 'Air Runner X1', 'Unisex', 'Nike', 'Zapatilla de running liviana, ideal para largas distancias', 89999.0, 'Negro', 25, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Running')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Air Runner X1');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, color, stock, imagen_url, categoria_id)
SELECT 'Street Classic', 'Hombre', 'Adidas', 'Zapatilla urbana de estilo retro', 74999.0, 'Blanco', 40, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Urbanas')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Street Classic');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, color, stock, imagen_url, categoria_id)
SELECT 'Jump Pro 23', 'Hombre', 'Puma', 'Zapatilla de basketball con amortiguación reforzada', 109999.0, 'Rojo', 15, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Basketball')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Jump Pro 23');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, color, stock, imagen_url, categoria_id)
SELECT 'City Walk W', 'Mujer', 'Nike', 'Zapatilla urbana cómoda para uso diario', 69999.0, 'Rosa', 30, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Urbanas')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'City Walk W');

-- Catálogo de demostración: variedad de categorías, géneros y precios.
-- precio es el importe vigente; precio_original solo se carga para ofertas.
INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Cloud Pace 2', 'Mujer', 'Asics', 'Running liviana con amortiguación para entrenamientos diarios', 82999.0, NULL, 'Lila', 22, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Running')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Cloud Pace 2');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Trail Ridge', 'Hombre', 'Topper', 'Running de trail con suela de gran agarre', 97999.0, NULL, 'Verde', 18, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Running')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Trail Ridge');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Velocity Knit', 'Unisex', 'New Balance', 'Malla respirable y pisada flexible para correr', 119999.0, 149999.0, 'Azul', 12, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Running')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Velocity Knit');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Retro Court', 'Hombre', 'Reebok', 'Diseño clásico de cuero sintético para todos los días', 79999.0, NULL, 'Blanco', 28, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Urbanas')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Retro Court');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Canvas Street', 'Mujer', 'Vans', 'Zapatilla urbana de lona con estilo relajado', 67999.0, 84999.0, 'Negro', 16, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Urbanas')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Canvas Street');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Platform Move', 'Mujer', 'Fila', 'Silueta urbana con plataforma liviana', 92999.0, NULL, 'Beige', 14, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Urbanas')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Platform Move');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Court Vision', 'Unisex', 'Converse', 'Clásica de lona para combinar con cualquier look', 64999.0, NULL, 'Rojo', 20, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Urbanas')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Court Vision');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Dunk Elevate', 'Mujer', 'Puma', 'Basketball con soporte lateral y amortiguación', 124999.0, 154999.0, 'Blanco', 10, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Basketball')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Dunk Elevate');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Fast Break 4', 'Hombre', 'Adidas', 'Basketball estable para cambios rápidos de dirección', 134999.0, NULL, 'Negro', 9, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Basketball')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Fast Break 4');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Indoor Control', 'Unisex', 'Topper', 'Calzado de fútbol sala con suela de gran control', 74999.0, NULL, 'Azul', 24, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Fútbol')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Indoor Control');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Field Pro FG', 'Hombre', 'Nike', 'Botines para césped natural con ajuste firme', 114999.0, 139999.0, 'Negro', 11, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Fútbol')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Field Pro FG');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Junior Goal', 'Niños', 'Adidas', 'Botines junior cómodos para empezar a jugar', 58999.0, NULL, 'Verde', 18, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Fútbol')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Junior Goal');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Flex Training', 'Mujer', 'Reebok', 'Entrenamiento funcional con base flexible y estable', 89999.0, NULL, 'Coral', 17, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Training')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Flex Training');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Power Lift', 'Hombre', 'Under Armour', 'Training con soporte reforzado para gimnasio', 109999.0, 129999.0, 'Gris', 13, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Training')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Power Lift');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Mini Sprint', 'Niños', 'Puma', 'Zapatilla liviana para correr y jugar', 49999.0, NULL, 'Azul', 25, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Running')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Mini Sprint');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Little Street', 'Niños', 'Converse', 'Lona urbana con cierre cómodo para uso diario', 55999.0, NULL, 'Rosa', 19, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Urbanas')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Little Street');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Playground Mid', 'Niños', 'Fila', 'Zapatilla resistente con soporte para jugar', 63999.0, 74999.0, 'Blanco', 15, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Training')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Playground Mid');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Kids Court Mini', 'Niños', 'Adidas', 'Modelo urbano liviano para todos los días', 52999.0, NULL, 'Negro', 21, NULL,
       (SELECT id FROM categoria WHERE nombre = 'Urbanas')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Kids Court Mini');

-- Alinear también los productos de ejemplo existentes con su categoría deportiva
-- si ya se habían cargado con la antigua categoría "Niños".
UPDATE productos
SET categoria_id = (SELECT id FROM categoria WHERE nombre = 'Running')
WHERE nombre = 'Mini Sprint';

UPDATE productos
SET categoria_id = (SELECT id FROM categoria WHERE nombre = 'Urbanas')
WHERE nombre IN ('Little Street', 'Kids Court Mini');

UPDATE productos
SET categoria_id = (SELECT id FROM categoria WHERE nombre = 'Training')
WHERE nombre = 'Playground Mid';

DELETE FROM categoria
WHERE nombre = 'Niños'
  AND NOT EXISTS (SELECT 1 FROM productos WHERE productos.categoria_id = categoria.id);

-- Usuarios de ejemplo para probar los tres roles desde el inicio del programa
INSERT INTO usuarios (nombre, email, password, rol, direccion, telefono)
SELECT 'Usuario Demo', 'demo@tppresencial.com', '1234', 'USUARIO', 'Av. Siempre Viva 742', '11-5555-5555'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'demo@tppresencial.com');

INSERT INTO usuarios (nombre, email, password, rol, direccion, telefono)
SELECT 'Vendedor Demo', 'vendedor@tppresencial.com', '1234', 'VENDEDOR', 'San Martín 456', '11-6666-6666'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'vendedor@tppresencial.com');

INSERT INTO usuarios (nombre, email, password, rol, direccion, telefono)
SELECT 'Admin Demo', 'admin@tppresencial.com', '1234', 'ADMIN', 'Corrientes 789', '11-7777-7777'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'admin@tppresencial.com');

-- Carrito vacío asociado al usuario de ejemplo (relación 1 a 1 Usuario-Carrito)
INSERT INTO carrito (usuario_id, fecha_creacion)
SELECT (SELECT id FROM usuarios WHERE email = 'demo@tppresencial.com'), CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM carrito WHERE usuario_id = (SELECT id FROM usuarios WHERE email = 'demo@tppresencial.com'));

-- Un carrito adicional para el vendedor de ejemplo si se quiere probar flujo vendedor
INSERT INTO carrito (usuario_id, fecha_creacion)
SELECT (SELECT id FROM usuarios WHERE email = 'vendedor@tppresencial.com'), CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM carrito WHERE usuario_id = (SELECT id FROM usuarios WHERE email = 'vendedor@tppresencial.com'));
