# AutoAndes Backend — Villaizan

Backend de las preguntas 1, 2 y 3 del examen LP2, versión A.
Basado en la arquitectura de [PharmaBDU1](https://github.com/EstebanHub07/PharmaBDU1), referencia 5270f05.
Implementación del dominio AutoAndes con Spring Boot 4.0.7, Java 21+, JPA, Oracle, Flyway, Spring Security y JWT.

## Qué se conserva de tu proyecto

- Controller → interfaz de servicio → implementación → repository → base de datos.
- `service/generic/CrudService`, `service/service` y `service/impl`.
- Request y Response separados, mapeo de entidades y manejador global de errores.
- Filtro JWT, permisos centralizados por rol, API sin sesiones y BCrypt.
- Maven Wrapper para ejecutar sin instalar Maven.

El examen pide `entity` en lugar de `model`. Los DTO se expresan como records de Java (campos inmutables y constructor automático); las entidades tienen campos privados y getters/setters explícitos. No necesitas Lombok para compilar.
La conversión está centralizada en `AutoAndesMapper`.

## 1. Configurar Oracle

Usa un esquema **vacío y dedicado a AutoAndes**. No uses el esquema de farmacia.
El usuario debe poder crear tablas y secuencias y tener cuota de almacenamiento.
La creación del usuario Oracle la realiza el administrador; la aplicación crea sus tablas mediante Flyway.

En `src/main/resources/application.yaml` cambia los valores genéricos:

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:oracle:thin:@//localhost:1521/FREEPDB1}
    username: ${DB_USER:autoandes}
    password: ${DB_PASSWORD:CAMBIAR_PASSWORD}
```

En la URL: `localhost` = host; `1521` = puerto; `FREEPDB1` = nombre del servicio.
También puedes establecer DB_URL, DB_USER y DB_PASSWORD en las variables de entorno del IDE.

## 2. Ejecutar

En PowerShell, ubicado en esta carpeta, con JAVA_HOME apuntando al JDK:

```powershell
.\iniciar.ps1
```

El script genera una clave JWT temporal para desarrollo si no existe JWT_SECRET y ejecuta Maven.
Si usas el IDE o `.\mvnw.cmd spring-boot:run` directamente, configura JWT_SECRET: una clave aleatoria de al menos 32 bytes codificada en Base64.
Una clave distinta invalida los tokens anteriores. No publiques claves reales.

Para producir una clave desde PowerShell:

```powershell
$jwtBytes = New-Object byte[] 48
$jwtRandom = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$jwtRandom.GetBytes($jwtBytes)
$env:JWT_SECRET = [Convert]::ToBase64String($jwtBytes)
$jwtRandom.Dispose()
.\mvnw.cmd spring-boot:run
```

API: http://localhost:8080/api/v1
Swagger: http://localhost:8080/swagger-ui.html

Flyway ejecuta `V1__esquema_y_semilla.sql` una vez. No modifiques una migración ya aplicada: agrega una V2.
Hibernate usa `ddl-auto: validate` y no crea ni borra tablas.
El nombre usa **dos guiones bajos**, como requiere Flyway; el anexo incluye una variante de nombre inconsistente.

## 3. Usuarios de práctica

| Usuario | Contraseña inicial | Rol | Estado |
|---|---|---|---|
| admin | Auto2026* | ADMIN | Activo |
| asesor | Auto2026* | ASESOR | Activo |
| jefe | Auto2026* | JEFE_TALLER | Activo |
| inactivo | Auto2026* | ASESOR | Inactivo |

SeedPasswords cifra con BCrypt únicamente las contraseñas vacías al primer arranque. Puedes cambiar la contraseña semilla con SEED_PASSWORD **antes** de inicializar.
Las siguientes ejecuciones no reemplazan los hash existentes.

Login: POST /api/v1/auth/login

```json
{"username":"admin","password":"Auto2026*"}
```

Copia el campo token en Authorize de Swagger. El esquema Bearer agrega el prefijo.
En Postman usa el encabezado `Authorization: Bearer <token>`.

## 4. Contrato

Todas las rutas de esta tabla tienen prefijo /api/v1.
Solo login, Swagger y OPTIONS son públicos.

| Método | Ruta | Permiso |
|---|---|---|
| POST | /auth/login | Público |
| GET | /auth/me | Autenticado |
| GET | /clientes | VEHICULO_LEER |
| GET | /servicios y /servicios/{id} | SERVICIO_LEER |
| POST, PUT, DELETE | /servicios, /servicios/{id} | SERVICIO_ESCRIBIR |
| PATCH | /servicios/{id}/estado | SERVICIO_ESCRIBIR |
| GET | /vehiculos?clienteId=1 y /vehiculos/{id} | VEHICULO_LEER |
| POST, PUT, DELETE | /vehiculos, /vehiculos/{id} | VEHICULO_ESCRIBIR |
| POST | /ordenes | ORDEN_REGISTRAR |
| GET | /ordenes y /ordenes/{id} | ORDEN_LEER |
| PATCH | /ordenes/{id}/anular | ORDEN_ANULAR |
| GET | /reportes/ingresos-por-servicio | REPORTE_VER |

POST de alta responde 201; consultas y PUT/PATCH 200; DELETE 204.
Errores: 400 validación, 401 autenticación, 403 permiso, 404 inexistente, 409 negocio/integridad y 500 inesperado.
Formato: timestamp, status, error, mensaje, ruta; campos solo en validación de campos.

Servicio:
```json
{"codigo":"SRV-009","nombre":"Revisión general","precio":120.00,"duracionMin":60}
```

Vehículo:
```json
{"placa":"XYZ-123","marca":"Toyota","modelo":"Yaris","anio":2022,"clienteId":1}
```

Orden:
```json
{"vehiculoId":1,"kilometraje":50000,"detalles":[{"servicioId":1,"cantidad":2},{"servicioId":2,"cantidad":1}]}
```

Cambio de estado: `{"activo":false}`.
Anulación: `{"motivo":"El cliente canceló el servicio"}`.
Filtros: `/ordenes?placa=AAA-111&estado=REGISTRADA&desde=2026-10-01&hasta=2026-10-31`.
Reporte: `/reportes/ingresos-por-servicio?desde=2026-10-01&hasta=2026-10-31`.
Fechas ISO; hasta incluye el día completo. Sin filtros se consultan todos los registros.
CORS permite http://localhost:4200, Authorization y PATCH.

## 5. Pruebas

```powershell
.\mvnw.cmd test
```

14 pruebas automatizadas pasaron durante la preparación. Usan H2 temporal en modo Oracle, la migración real y MockMvc con la cadena de seguridad completa.
No modifican Oracle. Prueban CRUD, reglas, reporte, credenciales, permisos, expiración y firma JWT, CORS y Swagger.
Una prueba introduce temporalmente una restricción en la base de pruebas para provocar el fallo al insertar un detalle y confirmar que tampoco persiste su cabecera.

**Pendiente de verificar en tu entorno:** conexión Oracle, validación del esquema con el dialecto Oracle, ejecución de la colección y capturas reales.
H2 verifica la lógica, pero no reemplaza esa evidencia.

Importa `requests/AutoAndes.postman_collection.json`. La colección inicia sesión para cada petición protegida usando el rol indicado.
Ejecuta las carpetas en orden sobre la semilla del proyecto. Crea/edita/elimina un servicio de prueba y registra/anula una orden.
La carpeta P2 asume que el kilometraje previo de AAA-111 no supera 50000; ajusta el dato si ya realizaste otras pruebas.
`requests/verificar-oracle.sql` sirve para comprobar cabecera, detalle, total y ausencia de datos parciales en SQL Developer.

## 6. Semilla y decisiones

El anexo contiene el DDL de cinco tablas, pero omite los INSERT y el DDL de seguridad. Se completaron según sus requisitos:
6 clientes, 8 servicios (SRV-008 inactivo), 8 vehículos, 3 órdenes REGISTRADAS y 1 ANULADA; AAA-111 tiene kilometraje previo 45000.
Las órdenes semilla son del 28/10/2026. Para demostrar anulación crea una orden nueva el día de la prueba.
La secuencia de números empieza en 5. Evita números duplicados concurrentes; puede dejar huecos si una transacción falla, comportamiento normal de una secuencia.
No se usa MAX(numero)+1.
El bloqueo del vehículo serializa dos registros concurrentes para comprobar el último kilometraje confirmado.

## 7. Estado de la entrega

Este directorio contiene el backend. El frontend Angular y el PDF con capturas del sistema completo todavía no están hechos.
No hay publicación en GitHub ni se ha colocado la etiqueta final del examen.
Rama de trabajo local: examen-a-villaizan; existen main y develop.
El primer commit agrupa la base inicial; los cambios y pruebas posteriores quedan registrados. Revisa con tu docente la exigencia de historial por pregunta; no presentes este historial como si documentara etapas que no ocurrieron.
Completa la demostración sobre Oracle antes de fusionar y etiquetar. Consulta GUIA_DEFENSA.md para explicar el código.

