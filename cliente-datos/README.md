# Microservicio de datos de clientes

API REST para registrar, consultar, actualizar y eliminar datos de clientes. El proyecto está construido con Java 21, Spring Boot, Spring Data JPA y una base de datos H2 en memoria.

## Requisitos

- JDK 21 instalado y disponible en `PATH` (`java -version`).
- No necesitas instalar Maven: el repositorio incluye Maven Wrapper (`mvnw` / `mvnw.cmd`).

## Levantar el proyecto

Ejecuta los comandos desde la carpeta raíz del proyecto.

### Windows (PowerShell o CMD)

```powershell
.\mvnw.cmd spring-boot:run
```

### Linux o macOS

```bash
./mvnw spring-boot:run
```

Al iniciar, la API queda disponible en `http://localhost:8081`. Para detenerla, usa `Ctrl+C` en la terminal.

También puedes generar el JAR y ejecutarlo:

```bash
./mvnw clean package
java -jar target/cliente-datos-0.0.1-SNAPSHOT.jar
```

En Windows reemplaza `./mvnw` por `./mvnw.cmd`.

## Servicio y endpoints

Este repositorio implementa un único servicio: **clientes**. Todos sus endpoints usan el prefijo `/internal/clientes` y reciben o devuelven JSON.

| Método | Ruta | Descripción | Respuesta esperada |
| --- | --- | --- | --- |
| `POST` | `/internal/clientes` | Registra un cliente | `201 Created` y el cliente creado |
| `GET` | `/internal/clientes/{id}` | Consulta un cliente por ID | `200 OK` y el cliente encontrado |
| `PUT` | `/internal/clientes/{id}` | Reemplaza los datos del cliente | `200 OK` y el cliente actualizado |
| `DELETE` | `/internal/clientes/{id}` | Elimina un cliente | `200 OK` y un mensaje de confirmación |

### Formato del cliente

El `POST` y el `PUT` esperan estos campos:

```json
{
  "nombre": "Ana",
  "apellidoPaterno": "García",
  "apellidoMaterno": "López",
  "fechaNacimiento": "1995-04-23",
  "sexo": "F",
  "correo": "ana.garcia@example.com",
  "telefono": "5512345678"
}
```

Reglas de validación:

- `nombre` y `apellidoPaterno` son obligatorios y aceptan hasta 50 caracteres.
- `apellidoMaterno` es opcional y acepta hasta 50 caracteres. Puede enviarse como `null`.
- `fechaNacimiento` es obligatoria, usa el formato `AAAA-MM-DD` y no puede ser futura.
- `sexo` es obligatorio y debe ser `M` o `F`.
- `correo` es obligatorio y debe tener formato de correo; máximo 100 caracteres.
- `telefono` es obligatorio y debe contener exactamente 10 dígitos.
- No se permiten dos clientes con el mismo nombre y apellidos, ignorando mayúsculas, minúsculas y espacios repetidos o en los extremos.

## Ejemplos de uso

Los ejemplos usan `curl` y presuponen que el servicio está activo en el puerto `8081`.
Los bloques de varias líneas están escritos para Bash (Linux, macOS, Git Bash o WSL). En PowerShell, usa `curl.exe` para evitar el alias `curl` y adapta las continuaciones de línea, o ejecuta cada petición desde una terminal Bash.

### Crear un cliente

```bash
curl -i -X POST http://localhost:8081/internal/clientes \
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

La respuesta incluye el `id` generado; úsalo en las siguientes operaciones. Por ejemplo:

```json
{
  "id": 1,
  "nombre": "Ana",
  "apellidoPaterno": "García",
  "apellidoMaterno": "López",
  "fechaNacimiento": "1995-04-23",
  "sexo": "F",
  "correo": "ana.garcia@example.com",
  "telefono": "5512345678"
}
```

### Consultar por ID

```bash
curl -i http://localhost:8081/internal/clientes/1
```

### Actualizar

El `PUT` recibe el objeto completo, con las mismas reglas que el alta:

```bash
curl -i -X PUT http://localhost:8081/internal/clientes/1 \
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

### Eliminar

```bash
curl -i -X DELETE http://localhost:8081/internal/clientes/1
```

Respuesta:

```json
{
  "id": 1,
  "mensaje": "Cliente eliminado"
}
```

## Base de datos H2

La aplicación usa una base H2 en memoria. Al reiniciar el servicio, los datos se pierden. La consola está disponible en:

```text
http://localhost:8081/h2-console
```

Datos de conexión:

| Campo | Valor |
| --- | --- |
| JDBC URL | `jdbc:h2:mem:clientesdb` |
| User Name | `sa` |
| Password | *(vacío)* |

## Configuración

La configuración predeterminada está en `src/main/resources/application.properties`:

- Puerto HTTP: `8081`
- URL de H2: `jdbc:h2:mem:clientesdb`
- Consola H2: `/h2-console`
- Esquema JPA: se actualiza con `spring.jpa.hibernate.ddl-auto=update`

## Pruebas

Para ejecutar las pruebas automatizadas del proyecto:

```bash
./mvnw test
```

En Windows:

```powershell
.\mvnw.cmd test
```
