# Cliente API

API REST para administrar clientes. La aplicación expone los endpoints públicos y delega las operaciones de persistencia a un servicio de datos externo.

## Requisitos

- Java 21.
- El servicio de datos de clientes disponible en `http://localhost:8081` (o en la dirección configurada en `cliente-datos.base-url`).

El proyecto usa Spring Boot 3.4.13 y Maven. Incluye los wrappers de Maven (`mvnw` y `mvnw.cmd`), por lo que no es necesario instalar Maven por separado.

## Instalación y ejecución

1. Clona el repositorio y entra a la carpeta del proyecto:

   ```bash
   git clone <URL_DEL_REPOSITORIO>
   cd cliente-api
   ```

2. Asegúrate de tener Java 21 y de iniciar el servicio de datos en la dirección configurada. Por defecto, este servicio debe escuchar en `http://localhost:8081` y ofrecer las rutas internas descritas abajo.

3. Inicia la API desde la raíz del proyecto:

   ```bash
   # Linux/macOS
   ./mvnw spring-boot:run

   # Windows (PowerShell o CMD)
   .\mvnw.cmd spring-boot:run
   ```

La API queda disponible en `http://localhost:8080`.

Para generar el artefacto ejecutable:

```bash
# Linux/macOS
./mvnw clean package

# Windows
.\mvnw.cmd clean package
```

Luego puedes ejecutarlo con `java -jar target/cliente-api-0.0.1-SNAPSHOT.jar`.

## Configuración

En `src/main/resources/application.properties`:

| Propiedad | Valor por defecto | Descripción |
|---|---|---|
| `server.port` | `8080` | Puerto HTTP de esta API. |
| `cliente-datos.base-url` | `http://localhost:8081` | URL base del servicio de datos. |
| `cliente-datos.timeout` | `3s` | Tiempo límite de conexión y lectura hacia el servicio de datos. |

Puedes reemplazar estos valores con variables de entorno de Spring Boot, por ejemplo:

```bash
CLIENTE_DATOS_BASE_URL=http://localhost:8090 CLIENTE_DATOS_TIMEOUT=5s ./mvnw spring-boot:run
```

En PowerShell también puedes definirlas antes de iniciar la aplicación:

```powershell
$env:CLIENTE_DATOS_BASE_URL = "http://localhost:8090"
$env:CLIENTE_DATOS_TIMEOUT = "5s"
.\mvnw.cmd spring-boot:run
```

## Servicios y endpoints disponibles

Todos los endpoints públicos tienen el prefijo `/api/v1/clientes` y usan JSON.

| Método | Ruta | Descripción | Respuesta exitosa |
|---|---|---|---|
| `POST` | `/api/v1/clientes` | Crea un cliente. | `201 Created`, cuerpo `{"id": 1}` y encabezado `Location`. |
| `GET` | `/api/v1/clientes/{id}` | Consulta un cliente por ID. | `200 OK` con los datos del cliente. |
| `PUT` | `/api/v1/clientes/{id}` | Actualiza un cliente por ID. | `200 OK` con los datos actualizados. |
| `DELETE` | `/api/v1/clientes/{id}` | Elimina un cliente por ID. | `200 OK` con el ID y un mensaje. |

El ID debe ser mayor que cero. El cuerpo usado para crear y actualizar debe incluir:

| Campo | Reglas |
|---|---|
| `nombre` | Obligatorio, hasta 50 caracteres. |
| `apellidoPaterno` | Obligatorio, hasta 50 caracteres. |
| `apellidoMaterno` | Opcional, hasta 50 caracteres. |
| `fechaNacimiento` | Obligatoria; formato `AAAA-MM-DD` y no puede ser posterior a hoy. |
| `sexo` | Obligatorio; `M` o `F`. |
| `correo` | Obligatorio y con formato de correo válido; máximo 100 caracteres. |
| `telefono` | Obligatorio; exactamente 10 dígitos. |

### Ejemplos

Crear un cliente:

```bash
curl -i -X POST http://localhost:8080/api/v1/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Ana",
    "apellidoPaterno": "Lopez",
    "apellidoMaterno": "Garcia",
    "fechaNacimiento": "1995-04-12",
    "sexo": "F",
    "correo": "ana.lopez@example.com",
    "telefono": "5512345678"
  }'
```

Consultar el cliente con ID `1`:

```bash
curl -i http://localhost:8080/api/v1/clientes/1
```

Respuesta de consulta (ejemplo):

```json
{
  "id": 1,
  "nombre": "Ana",
  "apellidoPaterno": "Lopez",
  "apellidoMaterno": "Garcia",
  "fechaNacimiento": "1995-04-12",
  "sexo": "F",
  "correo": "ana.lopez@example.com",
  "telefono": "5512345678"
}
```

Actualizar el cliente con ID `1`:

```bash
curl -i -X PUT http://localhost:8080/api/v1/clientes/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Ana",
    "apellidoPaterno": "Lopez",
    "apellidoMaterno": "Garcia",
    "fechaNacimiento": "1995-04-12",
    "sexo": "F",
    "correo": "ana.nueva@example.com",
    "telefono": "5512345678"
  }'
```

Eliminar el cliente con ID `1`:

```bash
curl -i -X DELETE http://localhost:8080/api/v1/clientes/1
```

## Integración con el servicio de datos

Esta API realiza llamadas HTTP al servicio de datos usando estas rutas internas:

| Método | Ruta interna |
|---|---|
| `POST` | `/internal/clientes` |
| `GET` | `/internal/clientes/{id}` |
| `PUT` | `/internal/clientes/{id}` |
| `DELETE` | `/internal/clientes/{id}` |

Por ello, el servicio de datos debe estar activo y aceptar los cuerpos y respuestas JSON correspondientes. Si no está disponible o excede el tiempo límite, la API genera un error de servicio no disponible. Las respuestas `400`, `404` y `409` del servicio de datos se traducen a excepciones de datos inválidos, cliente no encontrado y nombre duplicado, respectivamente.
