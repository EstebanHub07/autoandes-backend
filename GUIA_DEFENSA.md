# Cómo entender y defender tu backend AutoAndes

## Correspondencia con PharmaBDU1

| Ya conocías en Pharma | Ahora en AutoAndes | Responsabilidad |
|---|---|---|
| ProductoController y ProductoService | ServicioController y ServicioService | Catálogo de servicios del taller |
| Producto relacionado con Categoria | Vehiculo relacionado con Cliente | CRUD con relación ManyToOne |
| Venta y DetalleVenta | OrdenServicio y DetalleOrden | Cabecera y líneas guardadas juntas |
| VentaServiceImpl.registrar | OrdenServiceImpl.registrar | Validar reglas y calcular total |
| PermisosPorRol | PermisosPorRol | Unir permisos de los roles del usuario |
| JwtAuthenticationFilter | JwtAuthenticationFilter | Validar token antes del controlador |
| CrudService | CrudService | Contrato común create/update/read/delete/readAll |

Las entidades están en entity porque lo pide el examen. Los repositorios siguen siendo interfaces JpaRepository.
Los DTO son clases Java con Lombok, como en PharmaBDU1: se accede con `request.getVehiculoId()`. `@Getter` y `@Setter` generan los accesores; `@NoArgsConstructor` y `@AllArgsConstructor` generan los constructores. Request recibe los datos de entrada y Response define los datos de salida.
Una entidad tiene setters porque cambia dentro de una transacción. Un DTO transporta una foto de los datos.

## Recorrido de registrar una orden

1. El cliente envía POST /api/v1/ordenes con vehiculoId, kilometraje y detalles. El token va en Authorization.
2. JwtAuthenticationFilter verifica firma y vencimiento; UsuarioDetailsService obtiene el usuario y sus permisos actuales.
3. SecurityConfig exige ORDEN_REGISTRAR. El JEFE_TALLER recibe 403 incluso usando Postman.
4. OrdenController recibe OrdenRequestDTO. @Valid comprueba campos, tamaño de lista y cantidades.
5. La interfaz OrdenService conecta con OrdenServiceImpl: Spring inyecta la implementación por constructor.
6. OrdenServiceImpl.registrar, con @Transactional, valida duplicados, bloquea y busca el vehículo, consulta su última orden REGISTRADA y compara kilometraje.
7. Obtiene los precios con ServicioRepository. Comprueba que cada servicio esté activo y crea los detalles.
8. Calcula precio × cantidad con BigDecimal, suma subtotales y asigna REGISTRADA, fecha y usuario del token.
9. OrdenServicioRepository.saveAndFlush guarda la cabecera. CascadeType.ALL persiste los detalles.
10. AutoAndesMapper transforma la entidad en OrdenResponseDTO. El controlador responde 201 con JSON.

El controlador no conoce Oracle ni llama al repositorio. El servicio no depende de la pantalla.

## Dónde buscar cada regla

- RN-01: ServicioServiceImpl.guardar y delete. existsByCodigoAndIdNot detecta duplicados en altas y cambios; DetalleOrdenRepository detecta servicios usados. PATCH permite baja lógica.
- RN-02: VehiculoServiceImpl.guardar y delete. Verifica placa, cliente y órdenes relacionadas.
- RN-03: OrdenRequestDTO, DetalleRequestDTO y OrdenServiceImpl.registrar. Repite en el servicio las reglas de cantidad/detalles y verifica servicios únicos y activos.
- RN-04: OrdenServiceImpl.registrar y findFirstByVehiculoIdAndEstadoOrderByFechaDescIdDesc. Mira la última REGISTRADA, no la última anulada.
- RN-05: OrdenServiceImpl.anular. Motivo válido, estado REGISTRADA y misma fecha local America/Lima.

## Preguntas frecuentes de defensa

**¿Por qué el backend calcula el total?**
El cliente puede alterar cualquier JSON. Se consultan precios reales y se guardan como precioUnitario histórico. Un cambio futuro del catálogo no modifica el precio de una orden anterior.

**¿Qué garantiza que no quede media orden?**
@Transactional en el servicio y una excepción RuntimeException que salga del método. Spring hace rollback. La relación cascade guarda hijos con la cabecera; cascade por sí solo no proporciona atomicidad.
La prueba falloEnInsercionDeDetalleRevierteCabecera fuerza un error de base de datos después del INSERT de la cabecera y comprueba que ambas tablas conservan su cantidad original.

**¿Por qué se permite un hueco en OS-000006?**
La secuencia Oracle es concurrente y no se revierte. La transacción puede fallar sin dejar órdenes ni detalles; el número consumido no se reutiliza. Correlativo significa generado secuencialmente; no se promete una numeración fiscal sin huecos.

**¿Cómo funciona el reporte?**
OrdenServicioRepository.ingresos ejecuta JPQL con JOIN, WHERE, SUM y GROUP BY. La base filtra ANULADA y agrega filas. Construye IngresoResponseDTO directamente. No carga todas las órdenes con findAll para sumar en Java.

**¿Por qué DTO y no entidad?**
El contrato expone solo lo necesario. VehiculoResponseDTO incluye clienteId y clienteNombre, sin una entidad anidada. Se evitan ciclos de relaciones, cargas diferidas durante la serialización y exposición de datos internos como contraseñas.

**¿Qué contiene el JWT?**
sub identifica al usuario; authorities contiene permisos; iat es emisión y exp el vencimiento. JwtService firma con una clave secreta. Base64 permite leer el contenido: el token no es cifrado ni debe contener contraseñas.
Cada petición verifica firma/expiración y vuelve a cargar el usuario para respetar cambios de estado o permisos.

**¿401 o 403?**
401: falta autenticación válida, el token fue alterado, venció o el usuario está inactivo.
403: el token es válido y el usuario no tiene el permiso.
La SPA futura deberá cerrar sesión por un 401 ajeno al login y conservarla ante 403.

**¿Dónde están los permisos?**
PermisosPorRol implementa la matriz. ADMIN tiene todos, ASESOR registra órdenes y JEFE_TALLER anula y ve reportes.
SecurityConfig usa hasAuthority. Los controladores nunca comparan nombres de rol.

**¿Cómo se comprueba una contraseña?**
SeedPasswords genera hash BCrypt al inicializar contraseñas vacías. AuthServiceImpl usa PasswordEncoder.matches. No se compara un nuevo hash con el anterior: BCrypt incluye sal aleatoria.
Usuario inexistente, contraseña incorrecta y usuario inactivo reciben exactamente el mismo mensaje.

## Demostración sugerida

1. Arrancar Oracle y la API. Abrir Swagger y hacer login como admin.
2. Crear un servicio nuevo, editarlo, darlo de baja y eliminarlo si no se usó.
3. Crear un vehículo con cliente de la semilla. Provocar placa duplicada y mostrar 409.
4. Intentar kilometraje 44999 para AAA-111: debe responder 409 e indicar 45000.
5. Registrar una orden válida y comprobar cabecera, detalle y suma en SQL Developer.
6. Tomar los conteos de ambas tablas. Enviar una orden cuyo tercer servicio sea SRV-008 y comparar los conteos después del 409.
7. Como asesor, intentar anular y consultar reporte: 403.
8. Como jefe, intentar registrar: 403. Como jefe, anular una orden creada hoy y comprobar el reporte.
9. Enviar token alterado y petición sin token: 401.
10. Ejecutar mvnw.cmd test y explicar una de las pruebas.

Las preguntas de sessionStorage, signals, guards, interceptores y menú corresponden al frontend pendiente.

