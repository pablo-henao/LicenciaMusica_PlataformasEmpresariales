# Bloque 2 — Autorización por rol y dueño (🔴 imprescindible, parte 2 de 5)

## Objetivo de este bloque

Antes de este bloque, `SecurityConfig` solo exigía `authenticated()` en todos los endpoints: **cualquier** usuario logueado, sin importar su rol o si el recurso era suyo, podía editar el beat de otro productor, invitar colaboradores a un beat ajeno, o listar las compras de todos los usuarios de la plataforma. Los DTOs del Bloque 1 ya evitaban que alguien *falsificara* campos (`productor_id`, `estado`, etc.), pero no evitaban que alguien *actuara sobre recursos que no le pertenecen*. Eso es lo que cierra este bloque.

No se tocó frontend ni la lógica de negocio de créditos/checkout — eso sigue en los bloques 3 y 4.

## Cómo se implementó

Se evaluó usar `@PreAuthorize` (requiere `@EnableMethodSecurity` + SpEL para leer el `beatId` del body), pero varias de las reglas necesitan cargar la entidad relacionada de todas formas (por ejemplo, para saber si eres dueño de un `TipoLicencia` hay que cargar su `Beat`). Se optó por checks explícitos dentro de los servicios, en el mismo punto donde ya se resuelve la entidad — más simple de leer, de testear (no necesita contexto de Spring Security en los tests) y consistente con el estilo explícito que ya tiene el proyecto (igual que `ResourceNotFoundException`).

**Archivo nuevo:** [`security/AutorizacionUtil.java`](../backend/src/main/java/music/license/security/AutorizacionUtil.java) — dos métodos estáticos reutilizados en 5 servicios:
- `exigirRol(usuario, rolRequerido)` → lanza `AccessDeniedException` si el rol no coincide.
- `exigirPropietario(propietarioDelRecurso, solicitante, mensaje)` → lanza `AccessDeniedException` si los ids no coinciden.

**`GlobalExceptionHandler`** ahora mapea `AccessDeniedException` → `403 Forbidden` con el mismo formato `ApiError` que ya usan los otros errores.

## Reglas aplicadas, por módulo

| Endpoint | Regla nueva |
|---|---|
| `POST /api/beats` | Solo usuarios con rol `PRODUCTOR` pueden crear beats |
| `PUT /api/beats/{id}`, `DELETE /api/beats/{id}` | Solo el productor dueño del beat |
| `POST /api/licencias` | Solo el productor dueño del `beatId` referenciado |
| `PUT /api/licencias/{id}`, `DELETE /api/licencias/{id}` | Solo el productor dueño del beat de esa licencia |
| `POST /api/colaboradores` | Solo el productor dueño del `beatId` puede invitar colaboradores |
| `DELETE /api/colaboradores/{id}` | Solo el productor dueño del beat |
| `POST /api/acuerdos-creditos` | Solo el productor dueño del `beatId` puede abrir el acuerdo |
| `DELETE /api/acuerdos-creditos/{id}` | Solo el productor dueño del beat |
| `GET /api/compras`, `GET /api/compras/{id}` | Solo devuelve/permite ver las compras del propio usuario autenticado |
| `DELETE /api/compras/{id}` | Solo el comprador dueño de esa compra |

Los servicios ahora reciben el usuario autenticado como parámetro (`Usuario solicitante`), que los controladores obtienen igual que en el Bloque 1 con `@AuthenticationPrincipal Usuario usuario`.

## Cambio de comportamiento importante: `GET /api/compras`

Antes devolvía **todas las compras de todos los usuarios** — cualquiera podía ver quién le compró qué a quién. Ahora devuelve solo las compras del usuario autenticado (usa el nuevo método `CompraRepository.findByCompradorId`). No existe un rol `ADMIN` en el sistema que justificara mantener una vista "de todas", así que esto es simplemente cerrar una fuga de datos, no un rediseño. El Bloque 5 (catálogo/historial) le dará a este endpoint filtros y paginación reales.

## Lo que quedó deliberadamente sin tocar (y por qué)

1. **`GET /api/beats`, `GET /api/beats/{id}`, `GET /api/licencias`, `GET /api/colaboradores`, `GET /api/acuerdos-creditos`** siguen abiertos a cualquier usuario autenticado, sin filtrar por dueño. Es intencional: el catálogo de beats y sus licencias tiene que poder verse (así es como un comprador descubre qué comprar); restringir la lectura de `ColaboradorBeat`/`AcuerdoCreditos` a solo el productor y los colaboradores invitados tiene más sentido hacerlo junto con el flujo de aceptar/rechazar del Bloque 3, donde de todas formas hay que decidir qué puede ver un colaborador invitado de su propia invitación.
2. **`GET /api/usuarios` y `GET /api/usuarios/{id}`** siguen sin restricción — cualquier usuario autenticado puede listar nombre/email/rol de todos los usuarios. Es una fuga de datos menor (expone emails), pero no la toqué en este bloque porque no hay todavía una forma alternativa de que un productor busque a un colaborador por email para invitarlo (`ColaboradorBeatRequest` pide `usuarioId`, no email). Cerrar esto sin dar una alternativa rompería una funcionalidad que ni siquiera existe en el frontend todavía. **Te lo dejo marcado explícitamente para que decidas**: ¿lo cerramos ya (aceptando que invitar colaboradores se vuelve más difícil hasta que haya un endpoint de búsqueda) o lo dejamos así hasta diseñar ese flujo en el Bloque 3?
3. **`DELETE /api/compras/{id}`** sigue existiendo y ahora solo lo puede usar el dueño, pero no cambié si *debería* poder borrarse una compra en absoluto — una compra es un registro de una transacción, borrarla una vez pagada no debería ser posible. Esa decisión de ciclo de vida (`PENDIENTE → COMPLETADA`, y si `COMPLETADA` es inmutable) es del Bloque 4 (checkout), no de autorización.

## Archivos tocados — resumen completo

**Nuevo:**
- `security/AutorizacionUtil.java`

**Modificados:**
- `exception/GlobalExceptionHandler.java` (maneja `AccessDeniedException`)
- `service/BeatService.java`, `service/TipoLicenciaService.java`, `service/ColaboradorBeatService.java`, `service/AcuerdoCreditosService.java`, `service/CompraService.java`
- `controller/BeatController.java`, `controller/TipoLicenciaController.java`, `controller/ColaboradorBeatController.java`, `controller/AcuerdoCreditosController.java`, `controller/CompraController.java`
- `repository/CompraRepository.java` (nuevo método `findByCompradorId`)
- Todos los tests de servicio existentes (firmas nuevas) + casos nuevos de "usuario sin permiso → `AccessDeniedException`" en cada uno

## Verificación hecha

```bash
./gradlew compileJava compileTestJava   # BUILD SUCCESSFUL
DB_PASSWORD=<tu password> ./gradlew test   # 46 tests, 45 pasan
```

Sigue fallando solo `LicenseApplicationTests.contextLoads()` por el mismo motivo del Bloque 1 (necesita tu Postgres local con las credenciales correctas) — no relacionado con este bloque.

## Qué falta (próximos bloques)

3. **Bloque 3 — Lógica de créditos**: aceptar/rechazar por colaborador, validar que el split sume 100% antes de publicar, bloqueo del acuerdo y reapertura al modificar. Aquí también resolvemos el punto 1 de arriba (qué puede ver un colaborador invitado).
4. **Bloque 4 — Checkout + PDF**: transición `PENDIENTE → COMPLETADA`, generación del contrato, y decidir si una compra completada puede eliminarse.
5. **Bloque 5 — Catálogo con filtros + historial de compras** con paginación real.

## Sugerencia de mensaje de commit

```
feat(backend): autorizacion por rol y dueño en beats, licencias, colaboradores, acuerdos y compras

- nuevo AutorizacionUtil con exigirRol/exigirPropietario, usado en los 5 servicios
  que antes dejaban que cualquier usuario autenticado editara/borrara recursos ajenos
- GlobalExceptionHandler mapea AccessDeniedException a 403
- GET /api/compras y GET /api/compras/{id} ahora estan acotados al propio comprador
  (antes exponian las compras de todos los usuarios)
- tests actualizados con casos de "usuario sin permiso" para cada regla nueva
```
