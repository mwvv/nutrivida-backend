# Plan de Pruebas - NutriVida Backend

## Objetivo

Validar el correcto funcionamiento de la API REST de NutriVida (Productos, Usuarios, Ventas y Autenticación) mediante pruebas automatizadas con JUnit y generación de datos ficticios con DataFaker.

---

## Herramientas

- Java 21
- Spring Boot 4.1.2
- JUnit 5 (Jupiter)
- Mockito
- MockMvc (`@WebMvcTest`)
- DataFaker 2.4.2

## Cómo ejecutarlas

```
./mvnw test
```

Resultado de la última ejecución: **105 pruebas, 0 fallos**.

> La prueba `NutrividaBackendApplicationTests` (carga del contexto completo) necesita MySQL encendido, porque arranca la aplicación real con Flyway. Las demás no necesitan base de datos.

## Datos de prueba

La clase `util/DatosFalsos` genera, con DataFaker, productos, usuarios y ventas válidos (DTO, entidades y JSON) distintos en cada ejecución. Cada prueba modifica solo el dato que quiere invalidar. `util/Tokens` genera los tokens JWT de ADMIN y CLIENTE para las pruebas de seguridad.

---

# Casos de Prueba

# CP-01: Lógica de negocio de Producto

## Clase

`ProductoServiceTest` (Mockito) — 8 pruebas

## Objetivo

Verificar que el servicio convierte entidades en respuestas, crea y actualiza copiando los datos del Request, y que eliminar un producto inexistente falla.

## Resultado Esperado

* Listar y buscar devuelven los datos del producto en formato `Response`.
* Buscar o actualizar un id inexistente devuelve vacío y no guarda nada.
* Al crear no se envía un id; el id generado vuelve en la respuesta.
* Eliminar un id inexistente lanza `NoSuchElementException` y no borra.

---

# CP-02: Lógica de negocio de Usuario

## Clase

`UsuarioServiceTest` (Mockito) — 8 pruebas

## Objetivo

Verificar las reglas propias de usuarios: rol por defecto, contraseña hasheada y datos sensibles.

## Resultado Esperado

* Todo usuario nuevo queda con rol `CLIENTE`.
* La contraseña se guarda hasheada con BCrypt, nunca en texto plano.
* La respuesta no contiene el campo `contrasena`.
* Editar un usuario conserva su rol actual (un ADMIN sigue siendo ADMIN).
* Eliminar un id inexistente lanza `NoSuchElementException`.

---

# CP-03: Lógica de negocio de Venta

## Clase

`VentaServiceTest` (Mockito) — 8 pruebas

## Objetivo

Verificar el manejo de la fecha y las operaciones básicas de ventas.

## Resultado Esperado

* Crear una venta sin fecha usa la fecha de hoy; con fecha, respeta la indicada.
* Actualizar sin fecha conserva la fecha original.
* Buscar o actualizar un id inexistente devuelve vacío.
* Eliminar un id inexistente lanza `NoSuchElementException`.

---

# CP-04: Validaciones de los DTO

## Clase

`ValidacionDTOTest` (Bean Validation) — 35 pruebas

## Objetivo

Verificar que las reglas del esquema de la base de datos se validan antes de llegar a ella.

## Resultado Esperado

* **Producto:** tipo solo `Producto` o `Servicio`; precio entre 1 y 9999999 (límites incluidos); nombre de 3 a 60 caracteres; resumen de 10 a 120; campos obligatorios reportados.
* **Usuario:** correo solo `@duoc.cl`, `@profesor.duoc.cl` o `@gmail.com`; contraseña de 4 a 10 caracteres; RUN de 7 a 9; objetivo y tipo de alimentación dentro de la lista permitida; fecha de nacimiento no futura (y opcional); región y comuna obligatorias.
* **Venta:** total mayor o igual a 0; fecha opcional; usuario y total obligatorios.

---

# CP-05: Tokens JWT

## Clase

`JwtServiceTest` — 5 pruebas

## Objetivo

Verificar la generación y validación de los tokens de autenticación.

## Resultado Esperado

* El token contiene el correo y el rol, y tiene fecha de expiración.
* Un token expirado es rechazado.
* Un token firmado con otra clave es rechazado.
* Un token con el payload alterado (rol cambiado a ADMIN) es rechazado.
* Un texto que no es un token es rechazado.

---

# CP-06: API de Productos y reglas de acceso

## Clase

`ProductoControllerTest` (MockMvc + Spring Security) — 14 pruebas

## Resultado Esperado

* `GET` de lista y por id es **público** (200) y trae `_links` HATEOAS (`self` y `todos-los-productos`).
* `GET` de un id inexistente devuelve 404 con mensaje.
* `POST` sin token devuelve 401; con token de CLIENTE devuelve 403; con ADMIN devuelve 201.
* `POST` con datos inválidos devuelve 400 con el detalle por campo.
* `POST` con JSON mal formado devuelve 400; con código duplicado devuelve 409.
* `PUT` de un id inexistente devuelve 404.
* `DELETE` devuelve 204 para ADMIN, 403 para CLIENTE, 404 si no existe y 409 si el producto está relacionado con ventas.

---

# CP-07: API de Usuarios y seguridad JWT

## Clase

`UsuarioControllerTest` (MockMvc + Spring Security) — 10 pruebas

## Resultado Esperado

* Sin token, token inválido o token expirado devuelven 401 en formato JSON.
* Con token de CLIENTE devuelve 403.
* ADMIN lista y consulta usuarios con `_links`, y la respuesta nunca contiene `contrasena`.
* `POST` con correo de dominio no permitido devuelve 400; válido devuelve 201 con rol CLIENTE.
* `DELETE` de un usuario con ventas devuelve 409 (antes era un error 500); válido devuelve 204.

---

# CP-08: API de Ventas

## Clase

`VentaControllerTest` (MockMvc + Spring Security) — 8 pruebas

## Resultado Esperado

* Solo ADMIN accede: sin token 401, CLIENTE 403.
* `GET` devuelve `_links` (`self` y `todas-las-ventas`); un id inexistente devuelve 404.
* `POST` sin fecha es válido y devuelve 201; con total negativo devuelve 400.
* `DELETE` devuelve 204.

---

# CP-09: Autenticación

## Clase

`AuthControllerTest` (MockMvc + Spring Security) — 8 pruebas

## Resultado Esperado

* `POST /auth/login` es público y devuelve el token, el tipo `Bearer` y el rol.
* Credenciales incorrectas devuelven 401; sin datos devuelve 400.
* `POST /auth/register` es público, crea un CLIENTE y no expone la contraseña; datos inválidos devuelven 400 y correo repetido devuelve 409.
* `GET /auth/me` sin token devuelve 401; con token de CLIENTE devuelve sus datos.

---

# CP-10: Arranque de la aplicación

## Clase

`NutrividaBackendApplicationTests` — 1 prueba

## Objetivo

Verificar que el contexto completo de Spring arranca con Flyway, JPA y Security.

## Resultado Esperado

* La aplicación inicia sin errores (requiere MySQL encendido).

---

# Resumen

| Clase | Pruebas |
|---|---|
| ProductoServiceTest | 8 |
| UsuarioServiceTest | 8 |
| VentaServiceTest | 8 |
| ValidacionDTOTest | 35 |
| JwtServiceTest | 5 |
| ProductoControllerTest | 14 |
| UsuarioControllerTest | 10 |
| VentaControllerTest | 8 |
| AuthControllerTest | 8 |
| NutrividaBackendApplicationTests | 1 |
| **Total** | **105** |
