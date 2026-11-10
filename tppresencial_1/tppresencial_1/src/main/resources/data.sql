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

INSERT INTO categoria (nombre)
SELECT * FROM (SELECT 'Niños') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nombre = 'Niños');

-- Productos de ejemplo: modelos reales, todos con fotos de estudio sobre
-- fondo blanco para mantener una estética uniforme en la tienda.
-- precio es el importe vigente; precio_original solo se carga para ofertas.
INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'ASICS Gel-Kayano 19', 'Unisex', 'ASICS', 'Running con amortiguación GEL para largas distancias', 109999.0, NULL, 'Azul', 20, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/8/88/ASICS_GEL-Kayano_19.jpg/960px-ASICS_GEL-Kayano_19.jpg',
       (SELECT id FROM categoria WHERE nombre = 'Running')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'ASICS Gel-Kayano 19');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Puma Smash Leather', 'Hombre', 'Puma', 'Zapatilla urbana de cuero blanco y estilo clásico', 74999.0, NULL, 'Blanco', 30, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/3/30/Puma_Smash_Leather.jpg/960px-Puma_Smash_Leather.jpg',
       (SELECT id FROM categoria WHERE nombre = 'Urbanas')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Puma Smash Leather');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Reebok Omni Zone', 'Hombre', 'Reebok', 'Basketball retro con soporte de tobillo y amortiguación', 119999.0, 139999.0, 'Negro', 12, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/1/1e/Reebok_Omni_Zone.JPG/960px-Reebok_Omni_Zone.JPG',
       (SELECT id FROM categoria WHERE nombre = 'Basketball')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Reebok Omni Zone');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Nike Air Max', 'Unisex', 'Nike', 'Silueta urbana con amortiguación Air visible', 89999.0, NULL, 'Gris', 25, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/4/49/Sports_shoes.jpg/960px-Sports_shoes.jpg',
       (SELECT id FROM categoria WHERE nombre = 'Urbanas')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Nike Air Max');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'ASICS Hyper Speed', 'Unisex', 'ASICS', 'Running ultraliviana para competencia', 94999.0, NULL, 'Amarillo', 18, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/a/a7/Running_shoes.jpg/960px-Running_shoes.jpg',
       (SELECT id FROM categoria WHERE nombre = 'Running')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'ASICS Hyper Speed');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Merrell All Out Crush', 'Hombre', 'Merrell', 'Trail running liviana con suela de gran agarre', 97999.0, NULL, 'Verde', 16, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/0/07/Merrell_All_Out_Crush_Light.jpg/960px-Merrell_All_Out_Crush_Light.jpg',
       (SELECT id FROM categoria WHERE nombre = 'Running')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Merrell All Out Crush');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Saucony Fastwitch 9', 'Unisex', 'Saucony', 'Running de competencia con perfil bajo', 102999.0, NULL, 'Negro', 14, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/2/20/Saucony_Fastwitch_9.jpg/960px-Saucony_Fastwitch_9.jpg',
       (SELECT id FROM categoria WHERE nombre = 'Running')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Saucony Fastwitch 9');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Campus BlueWhite', 'Hombre', 'Campus', 'Zapatilla urbana deportiva de diseño liviano', 62999.0, NULL, 'Azul', 28, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/b/b5/Campus_BlueWhite.jpg/960px-Campus_BlueWhite.jpg',
       (SELECT id FROM categoria WHERE nombre = 'Urbanas')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Campus BlueWhite');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Converse Jack Purcell Boat', 'Hombre', 'Converse', 'Estilo náutico de cuero para uso diario', 79999.0, NULL, 'Marrón', 15, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/b/bd/Converse_Jack_Purcell_Boat_Shoes.jpg/960px-Converse_Jack_Purcell_Boat_Shoes.jpg',
       (SELECT id FROM categoria WHERE nombre = 'Urbanas')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Converse Jack Purcell Boat');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'VEJA Oca Low', 'Unisex', 'VEJA', 'Zapatilla de lona con suela de caucho natural', 84999.0, 99999.0, 'Negro', 20, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/a/a1/Oca-low-washed-black-contrast-thread-canvas-sneaker.png/960px-Oca-low-washed-black-contrast-thread-canvas-sneaker.png',
       (SELECT id FROM categoria WHERE nombre = 'Urbanas')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'VEJA Oca Low');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Onitsuka Tiger Delegation EX', 'Unisex', 'Onitsuka Tiger', 'Diseño retro de inspiración deportiva', 91999.0, NULL, 'Bordó', 17, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/6/65/Onitsuka_Tiger_DELEGATION_EX.jpg/960px-Onitsuka_Tiger_DELEGATION_EX.jpg',
       (SELECT id FROM categoria WHERE nombre = 'Urbanas')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Onitsuka Tiger Delegation EX');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Fallen Footwear Classic', 'Hombre', 'Fallen', 'Zapatilla de skate resistente y acolchada', 69999.0, NULL, 'Blanco', 22, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/1/11/Fallen_Footwear_Shoes.jpg/960px-Fallen_Footwear_Shoes.jpg',
       (SELECT id FROM categoria WHERE nombre = 'Urbanas')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Fallen Footwear Classic');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Reebok EasyTone', 'Mujer', 'Reebok', 'Training con suela de propulsión para tonificar', 82999.0, NULL, 'Blanco', 21, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/a/a6/Reebok_EasyTone.jpeg/960px-Reebok_EasyTone.jpeg',
       (SELECT id FROM categoria WHERE nombre = 'Training')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Reebok EasyTone');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Campus Martin', 'Hombre', 'Campus', 'Running de entrenamiento con malla respirable', 71999.0, NULL, 'Gris', 24, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/6/63/Campus_Martin_Running_Shoes.jpg/960px-Campus_Martin_Running_Shoes.jpg',
       (SELECT id FROM categoria WHERE nombre = 'Running')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Campus Martin');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Campus Perry Women', 'Mujer', 'Campus', 'Zapatilla urbana femenina de perfil bajo', 64999.0, NULL, 'Blanco', 26, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/8/8a/Campus_Women%27s_Perry_%28L%29_Running_Shoes.jpg/960px-Campus_Women%27s_Perry_%28L%29_Running_Shoes.jpg',
       (SELECT id FROM categoria WHERE nombre = 'Urbanas')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Campus Perry Women');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Campus Tyson', 'Hombre', 'Campus', 'Zapatilla deportiva de caña alta', 68999.0, NULL, 'Negro', 19, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/f/fe/CampusTyson.jpg/960px-CampusTyson.jpg',
       (SELECT id FROM categoria WHERE nombre = 'Urbanas')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Campus Tyson');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Inov-8 F-Lite 235', 'Unisex', 'Inov-8', 'Training funcional ultraliviana para gimnasio', 99999.0, NULL, 'Negro', 15, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/e/e7/Inov-8_shoes.jpg/960px-Inov-8_shoes.jpg',
       (SELECT id FROM categoria WHERE nombre = 'Training')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Inov-8 F-Lite 235');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Saucony Type A8', 'Unisex', 'Saucony', 'Running de pista ultraliviana', 98999.0, 119999.0, 'Azul', 13, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/a/ab/Saucony_Type_A8.jpg/960px-Saucony_Type_A8.jpg',
       (SELECT id FROM categoria WHERE nombre = 'Running')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Saucony Type A8');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Solecraft Wool Jogger', 'Mujer', 'Solecraft', 'Jogging de lana merino, liviano y transpirable', 87999.0, NULL, 'Negro', 18, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/a/a5/Most_Comfortable_Shoes_for_Jogging_-The_Wool_Joggers.jpg/960px-Most_Comfortable_Shoes_for_Jogging_-The_Wool_Joggers.jpg',
       (SELECT id FROM categoria WHERE nombre = 'Running')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Solecraft Wool Jogger');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Adidas Yeezy 500', 'Unisex', 'Adidas', 'Silueta urbana de perfil alto con suela robusta', 159999.0, NULL, 'Negro', 10, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/f/fc/2023_Adidas_Yeezy_500_%281%29.jpg/960px-2023_Adidas_Yeezy_500_%281%29.jpg',
       (SELECT id FROM categoria WHERE nombre = 'Urbanas')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Adidas Yeezy 500');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Adidas Yeezy Boost 350 V2 Onyx', 'Unisex', 'Adidas', 'Zapatilla urbana con amortiguación Boost', 169999.0, 189999.0, 'Negro', 8, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/e/e7/2023_Adidas_Yeezy_Boost_350_V2_Onyx_%281%29.jpg/960px-2023_Adidas_Yeezy_Boost_350_V2_Onyx_%281%29.jpg',
       (SELECT id FROM categoria WHERE nombre = 'Urbanas')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Adidas Yeezy Boost 350 V2 Onyx');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Crysta Running', 'Unisex', 'Crysta', 'Running liviana con malla transpirable', 74999.0, NULL, 'Gris', 21, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/4/44/CrystaRunning.jpg/960px-CrystaRunning.jpg',
       (SELECT id FROM categoria WHERE nombre = 'Running')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Crysta Running');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Jordan Baby Retro', 'Niños', 'Jordan', 'Zapatilla infantil de básquet inspirada en la Air Jordan, con ajuste seguro y suela de goma', 94999.0, 109999.0, 'Negro', 18, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/d/d9/Baby_Jordan.JPG/960px-Baby_Jordan.JPG',
       (SELECT id FROM categoria WHERE nombre = 'Niños')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Jordan Baby Retro');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Nike Kids Court', 'Niños', 'Nike', 'Zapatilla deportiva infantil de cuero con cierre de cordones y abrojo', 79999.0, NULL, 'Negro', 24, 'https://commons.wikimedia.org/wiki/Special:FilePath/Zwarte_leren_kindersportschoenen%2C_zool_rood_met_witte_rand%2C_rode_merktekentjes%2C_merk_Nike%2C_met_originele_doos_met_klepdeksel%2C_objectnr_76377-1-3.JPG?width=960',
       (SELECT id FROM categoria WHERE nombre = 'Niños')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Nike Kids Court');

INSERT INTO productos (nombre, genero, marca, descripcion, precio, precio_original, color, stock, imagen_url, categoria_id)
SELECT 'Campus Kids Canvas', 'Niños', 'Campus', 'Zapatilla infantil de lona con suela de goma, liviana y flexible', 54999.0, NULL, 'Crema', 30, 'https://commons.wikimedia.org/wiki/Special:FilePath/Cr%C3%A8mekleurige_kindergympies_met_vetersluiting_en_rubber-latex_zool%2C_objectnr_69237-1-2.JPG?width=960',
       (SELECT id FROM categoria WHERE nombre = 'Niños')
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Campus Kids Canvas');

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
