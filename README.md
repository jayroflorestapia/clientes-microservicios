# Microservicios de clientes

Repositorio con dos aplicaciones Spring Boot: `cliente-api`, que expone la API pública, y `cliente-datos`, que valida y persiste los clientes en H2. La API pública delega las operaciones al servicio de datos por HTTP.

## Requisitos

- JDK 21. Ambos `pom.xml` declaran Java 21; verifica la instalación con `java -version`.
- Maven Wrapper 3.3.4 incluido en cada proyecto; ambos wrappers descargan Maven 3.10.1, por lo que no hace falta instalar Maven manualmente. En Windows usa `mvnw.cmd`; en Linux/macOS usa `./mvnw`.
- Los proyectos usan versiones de Spring Boot definidas en sus `pom.xml`: `cliente-api` 3.4.13 y `cliente-datos` 4.1.2.

Ejecuta los comandos desde la raíz del repositorio, salvo que se indique `cd` a una carpeta.

## Ejecutar los servicios

Inicia primero `cliente-datos` en una terminal:

```powershell
cd cliente-datos
.\mvnw.cmd spring-boot:run
```

En Linux/macOS, el comando equivalente es `./mvnw spring-boot:run`. El servicio de datos queda en `http://localhost:8081`.

Después, en otra terminal, inicia `cliente-api`:

```powershell
cd cliente-api
.\mvnw.cmd spring-boot:run
```

En Linux/macOS usa `./mvnw spring-boot:run`. La API pública queda en `http://localhost:8080` y por defecto llama a `cliente-datos` en `http://localhost:8081`. Ambos procesos deben permanecer activos. Los datos se guardan en H2 en memoria y se pierden al reiniciar `cliente-datos`.

La dirección del servicio de datos puede cambiarse con `CLIENTE_DATOS_BASE_URL`; el tiempo límite se configura con `CLIENTE_DATOS_TIMEOUT` (por defecto, `3s`).

## Ejecutar las pruebas

Ejecuta las pruebas de cada proyecto desde su carpeta:

```powershell
cd cliente-datos
.\mvnw.cmd test
```

```powershell
cd cliente-api
.\mvnw.cmd test
```

En Linux/macOS, cambia `mvnw.cmd` por `./mvnw` en cada carpeta.

## API pública: ejemplos curl

Los siguientes ejemplos usan Bash (Linux/macOS, Git Bash o WSL). En PowerShell, invoca `curl.exe` y adapta las continuaciones de línea. `X-Correlation-Id` es opcional; si se omite, `cliente-api` genera uno. El identificador se devuelve en la respuesta y se propaga al servicio de datos.

### Crear un cliente

```bash
curl -i -X POST http://localhost:8080/api/v1/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Ana",
    "apellidoPaterno": "García",
    "apellidoMaterno": "López",
    "fechaNacimiento": "1995-04-23",
    "sexo": "F",
    "correo": "ana.garcia@example.com",
    "telefono": "5512345678"
  }'
```

Una creación responde `201 Created`, incluye `Location: /api/v1/clientes/{id}` y un cuerpo como `{"id":1}`.

### Consultar, actualizar y eliminar

```bash
curl -i http://localhost:8080/api/v1/clientes/1
```

```bash
curl -i -X PUT http://localhost:8080/api/v1/clientes/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Ana",
    "apellidoPaterno": "García",
    "apellidoMaterno": "López",
    "fechaNacimiento": "1995-04-23",
    "sexo": "F",
    "correo": "ana.nueva@example.com",
    "telefono": "5587654321"
  }'
```

```bash
curl -i -X DELETE http://localhost:8080/api/v1/clientes/1
```

La consulta y actualización responden el cliente completo. La eliminación responde, por ejemplo, `{"id":1,"mensaje":"Cliente eliminado"}`. Para enviar un identificador de correlación explícito, agrega `-H "X-Correlation-Id: demo-123"` a cualquiera de las llamadas.

## Contrato del servicio interno `cliente-datos`

La API pública usa JSON y llama al servicio interno en `http://localhost:8081` con estas rutas:

| Método | Ruta | Resultado |
| --- | --- | --- |
| `POST` | `/internal/clientes` | `201 Created` y representación del cliente creado. |
| `GET` | `/internal/clientes/{id}` | `200 OK` y representación del cliente. |
| `PUT` | `/internal/clientes/{id}` | `200 OK` y representación actualizada; reemplaza todos los campos. |
| `DELETE` | `/internal/clientes/{id}` | `200 OK` con `id` y `mensaje`; elimina físicamente el registro. |

El cuerpo de alta y actualización contiene `nombre`, `apellidoPaterno`, `apellidoMaterno`, `fechaNacimiento` (`yyyy-MM-dd`), `sexo` (`M` o `F`), `correo` y `telefono`. El ID solo se toma de la ruta. Nombre y apellido paterno son obligatorios (máximo 50 caracteres); el materno es opcional (máximo 50); fecha de nacimiento es obligatoria y no futura; correo es obligatorio (máximo 100); teléfono debe contener diez dígitos.

El servicio interno devuelve `400` para validación o JSON inválido, `404` si no existe el ID, `409` si ya existe ese nombre completo y `500` para errores inesperados. Los errores usan los campos `status`, `mensaje`, `detalles`, `timestamp`, `path` y `correlationId`. La API pública convierte el duplicado interno `409` a `400`, de acuerdo con el contrato público; conserva `400` y `404`, y responde `503` ante problemas de conexión o timeout con el servicio de datos. Los errores inesperados se responden sin exponer trazas.

### Interpretaciones de ambigüedades

- Para detectar nombres duplicados, se quitan espacios al inicio y al final, se colapsan secuencias de espacios en blanco (incluidos tabuladores y saltos de línea) a un espacio y se compara sin distinguir mayúsculas/minúsculas. La comparación usa `Locale.ROOT`. La representación persistida y devuelta conserva el texto original; la normalización se aplica a la clave de unicidad.
- `apellidoMaterno` puede omitirse o enviarse como `null`. Para la comparación de duplicados, ambos se interpretan como vacío; una cadena compuesta solo por espacios también normaliza a vacío. Como el campo es opcional, no se aplica `@NotBlank` a este valor. El resto de campos requeridos sí rechazan valores vacíos o de solo espacios.
- El contrato público marca el listado paginado como opcional. Esta implementación no incluye ese endpoint.
- En una actualización se envía el objeto completo y el identificador se toma de la ruta; no es una actualización parcial.

## Preguntas de diseño

1. **¿Por qué separar `cliente-api` y `cliente-datos`?** La API pública se ocupa del contrato HTTP externo, validación y traducción de errores; el servicio de datos concentra las operaciones de persistencia. La comunicación HTTP hace explícito ese límite y permite configurar la dirección del servicio de datos.
2. **¿Cómo se evita registrar dos veces al mismo cliente?** `cliente-datos` genera una clave de nombre completo normalizada, consulta duplicados y además la protege con una restricción única en la base. Así la regla se aplica también ante solicitudes concurrentes.
3. **¿Cómo se interpretan espacios y apellido materno opcional?** Los espacios externos se recortan y las secuencias internas se reducen a un espacio para comparar nombres; mayúsculas y minúsculas se ignoran. Materno ausente, `null` o solo espacios equivale a vacío únicamente para la detección de duplicados.
4. **¿Cómo se siguen las solicitudes y se manejan fallos entre servicios?** `cliente-api` acepta o genera `X-Correlation-Id`, lo devuelve y lo propaga en la llamada interna. Un error de conexión o timeout se transforma en `503`; errores de validación y de cliente inexistente se traducen al contrato público, sin propagar detalles internos.
5. **¿Cuánto tiempo tomó el desarrollo?** [Pendiente: completar con el tiempo real que tomó el desarrollo y qué actividades incluye.]

## Documentación por proyecto

- [README de cliente-api](cliente-api/README.md)
- [README de cliente-datos](cliente-datos/README.md)
- Contrato público OpenAPI 2.0: [`contrato-cliente-api (1).yaml`](contrato-cliente-api%20(1).yaml)
