# dynamicqr-backend

API REST para generación, administración y análisis de códigos QR dinámicos.

El backend expone los servicios que consume el frontend: crear y editar QR, cambiar su destino sin reimprimir el código, registrar escaneos y, más adelante, aplicar estilos visuales (incluido un asistente de diseño con IA local).

## Enfoque del producto

Cada QR no apunta de forma fija a la URL final. Apunta a una **URL corta interna** que el sistema resuelve en tiempo real contra PostgreSQL y redirige al destino configurado. Por eso el destino se puede cambiar sin alterar el código visual ya impreso o distribuido.

La generación visual se hará con **ZXing** (corrección de error **H / 30%**) y se entregará como **SVG** a partir de la matriz de módulos, no como PNG plano. Así el frontend lo escala sin pérdida y el backend puede parametrizar colores, gradientes, forma de módulos, ojos de esquina y logo central.

Cada escaneo se persiste (fecha, hora y contexto disponible: IP, dispositivo, navegador, país, etc.) para métricas por periodo, por código y del sistema.

Un modelo local vía **Ollama + Spring AI** interpretará descripciones en lenguaje natural (por ejemplo: *“QR para campaña navideña, elegante y colores cálidos”*) y devolverá JSON de estilo. La IA no genera imágenes: traduce intención en configuración que se aplica al SVG.

## Stack

| Pieza | Versión / detalle |
|---|---|
| Java | 21 |
| Spring Boot | 3.4.13 |
| Maven | 3.9.16 (wrapper incluido) |
| Empaquetado | WAR para WildFly 41 |
| Base de datos | PostgreSQL, esquema `dynamicqr` |
| Persistencia | Spring Data JPA / Hibernate |

Despliegue actual en **WildFly 41**. El Tomcat embebido va como `provided`; el logging usa el de WildFly (no Logback) y el WAR aísla JPA/CDI del servidor para no chocar con Hibernate de Spring Boot.

## Arquitectura

Capas bajo `src/main/java/dynamicqr`:

```
frontend
    │
    ▼
resource     →  rutas HTTP (/api/...)
    │
    ▼
service      →  CRUD y reglas de negocio
    │
    ▼
repository   →  Spring Data JPA (junto a los services)
    │
    ▼
domain       →  entidades mapeadas a PostgreSQL
```

| Paquete | Rol |
|---|---|
| `domain` | Entidades JPA |
| `service` | Lógica y repositorios |
| `resource` | API REST + CORS |
| `dto` | Contratos JSON (pendiente) |
| `security` | Autenticación (pendiente) |

Los **resources** no sustituyen a los services: son el path HTTP que el frontend llama. Cada servicio de negocio tiene su resource.

CORS está abierto en `CorsConfig` para que el frontend en otro origen (por ejemplo `localhost:5173`) pueda consumir la API.

## Modelo de datos

Esquema PostgreSQL `dynamicqr`. El DDL vive fuera de Hibernate (`ddl-auto=none`).

| Tabla | Entidad | Descripción |
|---|---|---|
| `usuarios` | `Usuario` | Cuentas de administración |
| `qr` | `Qr` | Códigos y su destino (`destino_url`) |
| `escaneos` | `Escaneo` | Cada lectura (IP, user-agent, dispositivo, geo, referrer) |
| `versiones` | `Version` | Historial de cambios de destino/nombre |

Tipos de QR (`QrTipo`): `url`, `texto`, `wifi`, `vcard`, `email`, `telefono`.

Campos de auditoría en todas las tablas: `usuario_creador`, `fecha_creacion`, `usuario_editor`, `fecha_edicion`.

## API actual

Context-root en WildFly: `/dynamicqr-backend`.

Base: `http://localhost:8080/dynamicqr-backend`

| Recurso | Path |
|---|---|
| Usuarios | `/api/usuarios` |
| QR | `/api/qr` |
| Escaneos | `/api/escaneos` |
| Versiones | `/api/versiones` |

En todos:

| Método | Ruta | Acción |
|---|---|---|
| `GET` | `/` | Listar |
| `GET` | `/{id}` | Buscar |
| `POST` | `/` | Crear (`201`) |
| `PUT` | `/{id}` | Actualizar |
| `DELETE` | `/{id}` | Eliminar (`204`) |

Si el id no existe: `404` con `{"error": "..."}`.

El hash de contraseña de usuario se puede enviar al crear/actualizar, pero **no se devuelve** en las respuestas.

## Qué ya está y qué sigue

**Listo**

- Entidades y CRUD de usuarios, QR, escaneos y versiones
- Resources REST y CORS
- WAR desplegable en WildFly 41

**Siguiente** (según el enfoque del producto)

- URL corta interna y redirección pública (el endpoint de resolución debe quedar abierto)
- Generación SVG con ZXing (ECC H)
- Estilos: color, gradiente, módulos, ojos, logo
- Endpoints de estadísticas
- Flyway para migraciones versionadas
- SpringDoc OpenAPI
- Ollama + Spring AI como asistente de estilo
- Autenticación en operaciones de administración

## Requisitos

- JDK 21
- Maven 3.9.16 (o el wrapper `mvnw`)
- PostgreSQL con la base `db_dynamicqr` y el esquema `dynamicqr`
- WildFly 41 (probado con 41.0.1.Final)

## Configuración

`src/main/resources/application.properties`:

```properties
spring.application.name=dynamicqr-backend
server.port=8080

spring.datasource.url=jdbc:postgresql://localhost:5432/db_dynamicqr
spring.datasource.username=postgres
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=none
spring.jpa.open-in-view=false
spring.jpa.properties.hibernate.default_schema=dynamicqr
```

En WildFly ya existe el datasource `java:/db_dynamicqr`. Se puede pasar a JNDI más adelante.

## Compilar y desplegar

```powershell
.\mvnw.cmd -DskipTests package
```

El artefacto queda en `target/dynamicqr-backend.war`. Copiarlo a:

```
C:\Servers\wildfly-41.0.1.Final\standalone\deployments
```

Arranque local (sin WildFly, Tomcat embebido) no es el camino principal: el logging de Logback está excluido a propósito para el servidor de aplicaciones.

## Ejemplo de uso

Listar códigos:

```http
GET /dynamicqr-backend/api/qr
```

Crear un QR de tipo URL:

```http
POST /dynamicqr-backend/api/qr
Content-Type: application/json

{
  "nombre": "Carta del restaurante",
  "tipo": "url",
  "destinoUrl": "https://ejemplo.com/menu",
  "activo": true,
  "usuarioCreador": 1,
  "usuarioEditor": 1
}
```

Registrar un escaneo:

```http
POST /dynamicqr-backend/api/escaneos
Content-Type: application/json

{
  "qrId": 1,
  "userAgent": "Mozilla/5.0 ...",
  "dispositivo": "movil",
  "usuarioCreador": 1,
  "usuarioEditor": 1
}
```
