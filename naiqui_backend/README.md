# naiqui_backend — E-commerce de zapatillas (Spring Boot + Spring Data JPA)

Backend REST para un e-commerce de zapatillas, desarrollado con Spring Boot, Spring Data JPA, Lombok y Maven, siguiendo una arquitectura en capas (Controller → Service → Repository → Modelo/DTO).

## Requisitos para correrlo

- JDK 17 o superior
- Maven 3.9+ (o usar el wrapper `./mvnw` incluido, si se agrega)

No hace falta instalar ninguna base de datos: el proyecto viene configurado con **H2** (una base de datos relacional embebida) que se guarda en un archivo local (`./data/tppresencial_1`), así que los datos persisten entre reinicios.

## Cómo ejecutarlo

```bash
cd naiqui_backend
mvn spring-boot:run
```

El proyecto consiste en desarrollar una aplicación web interactiva para una tienda de calzado.
La aplicación contará con un catálogo de calzados con información, imágenes, precios y talles disponibles.
Los usuarios podrán buscar y filtrar productos según diferentes características.
También podrán agregar productos a un carrito, modificar las cantidades y gestionar una lista de deseos (wishlist).


La aplicación arranca en `http://localhost:8080`.

Al arrancar por primera vez, `src/main/resources/data.sql` precarga automáticamente el catálogo y las cuentas de prueba `demo@naiqui.com`, `vendedor@naiqui.com` y `admin@naiqui.com`.

Se puede inspeccionar la base de datos desde el navegador en `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/tppresencial_1`, usuario `sa`, sin contraseña).

### Usar MySQL en lugar de H2 (opcional)

En `src/main/resources/application.properties` están comentadas las líneas necesarias para apuntar a una base MySQL local. Basta con comentar el bloque de H2 y descomentar el bloque de MySQL (el driver ya está en el `pom.xml`).

## Arquitectura del proyecto

```
src/main/java/com/uade/e_commerce/
 ├── model/        Entidades JPA (@Entity): Usuario, Categoria, Producto, Carrito, ItemCarrito, Pedido, ItemPedido
 ├── dto/          DTOs usados en los Controllers (nunca se expone la entidad JPA directamente)
 ├── repository/   Interfaces @Repository que extienden JpaRepository (acceso a datos)
 ├── service/      Clases @Service @Transactional con la lógica de negocio
 ├── controller/   Clases @RestController con los endpoints HTTP (devuelven ResponseEntity)
 └── exception/    Excepciones propias + manejador global de errores (@RestControllerAdvice)
```

## Modelo de dominio y relaciones JPA

- **Usuario** 1 — 1 **Carrito** (`@OneToOne`)
- **Usuario** 1 — N **Pedido** (`@OneToMany` / `@ManyToOne`)
- **Categoria** 1 — N **Producto** (`@OneToMany` / `@ManyToOne`)
- **Carrito** 1 — N **ItemCarrito** N — 1 **Producto**
- **Pedido** 1 — N **ItemPedido** N — 1 **Producto**

## Endpoints principales

### Categorías
| Método | URL | Descripción |
|---|---|---|
| GET | `/api/categorias` | Lista todas las categorías |
| GET | `/api/categorias/{id}` | Obtiene una categoría |
| POST | `/api/categorias` | Crea una categoría |
| PUT | `/api/categorias/{id}` | Actualiza una categoría |
| DELETE | `/api/categorias/{id}` | Elimina una categoría |

### Productos
| Método | URL | Descripción |
|---|---|---|
| GET | `/api/productos` | Lista todos los productos |
| GET | `/api/productos/{id}` | Obtiene un producto |
| GET | `/api/productos/categoria/{categoriaId}` | Productos de una categoría |
| GET | `/api/productos/genero/{genero}` | Productos filtrados por género |
| POST | `/api/productos` | Crea un producto |
| PUT | `/api/productos/{id}` | Actualiza un producto |
| DELETE | `/api/productos/{id}` | Elimina un producto |

### Usuarios
| Método | URL | Descripción |
|---|---|---|
| GET | `/api/usuarios` | Lista todos los usuarios |
| GET | `/api/usuarios/{id}` | Obtiene un usuario |
| POST | `/api/usuarios` | Registra un usuario con rol `USUARIO`, contraseña de al menos 8 caracteres y carrito vacío |
| PUT | `/api/usuarios/{id}` | Actualiza datos de contacto |
| DELETE | `/api/usuarios/{id}` | Elimina un usuario |

El frontend crea la cuenta con `POST /api/usuarios` y luego inicia sesión con `POST /api/login`. Antes de enviar operaciones que modifican datos, solicita un token CSRF a `GET /api/csrf`. El login establece el JWT en una cookie `HttpOnly`; el cliente consulta `GET /api/me` para recuperar la sesión y usa `POST /api/logout` para cerrarla. El JWT no se devuelve en el cuerpo de la respuesta ni se guarda en `localStorage`.

La API acepta cookies del origen frontend configurado en `NAIQUI_CORS_ALLOWED_ORIGINS` (lista de orígenes separados por comas; por defecto `http://localhost:5173`). Para producción con HTTPS, configurar `NAIQUI_AUTH_COOKIE_SECURE=true`. Si el frontend y el backend están en sitios distintos, configurar además `NAIQUI_AUTH_COOKIE_SAME_SITE=None`; en desarrollo local se mantiene `Lax`.

### Carrito
| Método | URL | Descripción |
|---|---|---|
| GET | `/api/usuarios/{usuarioId}/carrito` | Carrito actual del usuario |
| POST | `/api/usuarios/{usuarioId}/carrito/items` | Agrega un producto al carrito |
| DELETE | `/api/usuarios/{usuarioId}/carrito/items/{itemId}` | Quita un producto del carrito |
| DELETE | `/api/usuarios/{usuarioId}/carrito` | Vacía el carrito |

### Pedidos
| Método | URL | Descripción |
|---|---|---|
| GET | `/api/pedidos` | Lista todos los pedidos |
| GET | `/api/pedidos/{id}` | Obtiene un pedido |
| GET | `/api/usuarios/{usuarioId}/pedidos` | Historial de pedidos de un usuario |
| POST | `/api/usuarios/{usuarioId}/pedidos` | Confirma la compra (checkout) a partir del carrito actual |
| PUT | `/api/pedidos/{id}/estado` | Actualiza el estado del pedido (body: `{"estado": "ENVIADO"}`) |
| PUT | `/api/pedidos/{id}/cancelar` | Cancela un pedido pendiente y repone el stock |

## Ejemplo de flujo de compra completo

1. `POST /api/usuarios` → registrar usuario
2. `POST /api/usuarios/{usuarioId}/carrito/items` → agregar productos al carrito (body: `{"productoId":1,"talle":42,"cantidad":1}`)
3. `GET /api/usuarios/{usuarioId}/carrito` → revisar el carrito
4. `POST /api/usuarios/{usuarioId}/pedidos` → confirmar la compra (descuenta stock y crea el Pedido)
5. `GET /api/usuarios/{usuarioId}/pedidos` → ver el historial de compras
