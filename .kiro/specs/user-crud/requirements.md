# Requirements Document

## Introduction

Este documento define los requisitos para un **mantenedor de usuarios** (CRUD) construido con Java 21, Spring Boot 3, Lombok y base de datos H2. La aplicación expone una API REST que permite crear, consultar, actualizar y eliminar usuarios, con validación de datos y persistencia en H2 (en memoria para desarrollo). El objetivo es proveer un servicio backend simple, mantenible y bien probado que sirva como base para gestionar la información de usuarios.

## Requirements

### Requirement 1: Crear usuario

**User Story:** Como administrador del sistema, quiero registrar nuevos usuarios, para poder mantener un catálogo actualizado de las personas que acceden al sistema.

#### Acceptance Criteria

1. WHEN el cliente envía una petición POST a `/api/users` con un cuerpo válido THEN el sistema SHALL crear el usuario y responder con estado HTTP 201 y el usuario creado incluyendo su identificador generado.
2. WHEN el cuerpo de la petición no incluye el campo `nombre` o está vacío THEN el sistema SHALL responder con estado HTTP 400 y un mensaje de error indicando el campo inválido.
3. WHEN el cuerpo de la petición incluye un `email` con formato inválido THEN el sistema SHALL responder con estado HTTP 400 y un mensaje de error de validación.
4. WHEN el cuerpo de la petición incluye un `email` que ya existe en la base de datos THEN el sistema SHALL responder con estado HTTP 409 (Conflict) y un mensaje indicando que el email ya está registrado.
5. WHEN se crea un usuario exitosamente THEN el sistema SHALL persistir el registro en la base de datos H2.

### Requirement 2: Consultar usuarios

**User Story:** Como administrador del sistema, quiero listar y consultar usuarios, para poder revisar la información registrada.

#### Acceptance Criteria

1. WHEN el cliente envía una petición GET a `/api/users` THEN el sistema SHALL responder con estado HTTP 200 y la lista de todos los usuarios registrados.
2. WHEN el cliente envía una petición GET a `/api/users/{id}` con un identificador existente THEN el sistema SHALL responder con estado HTTP 200 y los datos del usuario solicitado.
3. WHEN el cliente envía una petición GET a `/api/users/{id}` con un identificador que no existe THEN el sistema SHALL responder con estado HTTP 404 y un mensaje indicando que el usuario no fue encontrado.
4. WHEN no existen usuarios registrados THEN el sistema SHALL responder con estado HTTP 200 y una lista vacía.

### Requirement 3: Actualizar usuario

**User Story:** Como administrador del sistema, quiero modificar los datos de un usuario existente, para mantener la información al día.

#### Acceptance Criteria

1. WHEN el cliente envía una petición PUT a `/api/users/{id}` con un identificador existente y un cuerpo válido THEN el sistema SHALL actualizar los datos del usuario y responder con estado HTTP 200 y el usuario actualizado.
2. WHEN el cliente envía una petición PUT a `/api/users/{id}` con un identificador que no existe THEN el sistema SHALL responder con estado HTTP 404 y un mensaje indicando que el usuario no fue encontrado.
3. WHEN el cuerpo de la petición de actualización contiene datos inválidos THEN el sistema SHALL responder con estado HTTP 400 y un mensaje de error de validación.
4. WHEN el cliente intenta actualizar el `email` a uno ya usado por otro usuario THEN el sistema SHALL responder con estado HTTP 409 (Conflict).

### Requirement 4: Eliminar usuario

**User Story:** Como administrador del sistema, quiero eliminar usuarios, para retirar del catálogo a quienes ya no deben tener acceso.

#### Acceptance Criteria

1. WHEN el cliente envía una petición DELETE a `/api/users/{id}` con un identificador existente THEN el sistema SHALL eliminar el usuario y responder con estado HTTP 204 (No Content).
2. WHEN el cliente envía una petición DELETE a `/api/users/{id}` con un identificador que no existe THEN el sistema SHALL responder con estado HTTP 404 y un mensaje indicando que el usuario no fue encontrado.

### Requirement 5: Modelo de datos del usuario

**User Story:** Como desarrollador, quiero un modelo de usuario bien definido, para garantizar consistencia en los datos almacenados.

#### Acceptance Criteria

1. THE entidad Usuario SHALL contener los campos: `id` (identificador único autogenerado), `nombre` (obligatorio), `email` (obligatorio y único) y `activo` (booleano, por defecto `true`).
2. THE campo `email` SHALL ser validado con formato de correo electrónico.
3. WHEN se crea un usuario sin especificar el campo `activo` THEN el sistema SHALL asignar el valor `true` por defecto.

### Requirement 6: Manejo de errores consistente

**User Story:** Como consumidor de la API, quiero recibir respuestas de error consistentes, para poder manejar los fallos de manera predecible.

#### Acceptance Criteria

1. WHEN ocurre un error de validación THEN el sistema SHALL responder con un cuerpo JSON que incluya el código de estado, el mensaje y los detalles de los campos inválidos.
2. WHEN ocurre un error de recurso no encontrado THEN el sistema SHALL responder con un cuerpo JSON que incluya el código de estado y un mensaje descriptivo.
3. THE respuestas de error SHALL seguir una estructura uniforme en toda la API.

### Requirement 7: Configuración y ejecución

**User Story:** Como desarrollador, quiero poder ejecutar la aplicación localmente con facilidad, para desarrollar y probar rápidamente.

#### Acceptance Criteria

1. THE aplicación SHALL usar Java 21 y Spring Boot 3.x.
2. THE aplicación SHALL usar Lombok para reducir el código repetitivo (getters, setters, constructores).
3. THE aplicación SHALL usar una base de datos H2 en memoria para el entorno de desarrollo.
4. THE consola web de H2 SHALL estar habilitada en el entorno de desarrollo para inspección de datos.
5. WHEN la aplicación arranca THEN el sistema SHALL exponer la API REST en el puerto configurado (por defecto 8081).

### Requirement 8: Documentación interactiva de la API (Swagger/OpenAPI)

**User Story:** Como consumidor de la API, quiero una documentación interactiva de los endpoints, para explorar y probar la API sin herramientas externas.

#### Acceptance Criteria

1. THE aplicación SHALL exponer una especificación OpenAPI 3 en formato JSON en la ruta `/v3/api-docs`.
2. THE aplicación SHALL exponer una interfaz Swagger UI navegable en la ruta `/swagger-ui.html`.
3. THE documentación SHALL incluir los endpoints CRUD de usuarios con sus métodos HTTP, parámetros, cuerpos de petición y códigos de respuesta.
4. THE documentación SHALL incluir metadatos de la API (título, versión y descripción).
5. WHEN la aplicación arranca THEN el sistema SHALL generar la documentación automáticamente a partir de los controladores y DTOs existentes.

### Requirement 9: Contenerización con Docker

**User Story:** Como desarrollador de operaciones, quiero ejecutar la aplicación en un contenedor Docker, para desplegarla de forma reproducible sin depender del entorno local.

#### Acceptance Criteria

1. THE proyecto SHALL incluir un `Dockerfile` que construya una imagen ejecutable de la aplicación.
2. THE `Dockerfile` SHALL usar una construcción multi-etapa (build con Maven + JDK 21, runtime con JRE/JDK 21) para minimizar el tamaño de la imagen final.
3. WHEN se construye la imagen THEN el sistema SHALL empaquetar el JAR ejecutable generado por Spring Boot.
4. WHEN se ejecuta el contenedor THEN el sistema SHALL exponer la API en el puerto 8081.
5. THE proyecto SHALL incluir un archivo `docker-compose.yml` que permita levantar la aplicación con un solo comando.
6. THE proyecto SHALL incluir un `.dockerignore` que excluya artefactos innecesarios del contexto de build (por ejemplo `target`, archivos de IDE).
7. THE contenedor SHALL ejecutar la aplicación con un usuario no root por buenas prácticas de seguridad.

### Requirement 10: Concurrencia con Virtual Threads

**User Story:** Como desarrollador, quiero que la aplicación procese las peticiones sobre virtual threads de Java 21, para mejorar la escalabilidad ante cargas de I/O sin aumentar el consumo de hilos de plataforma.

#### Acceptance Criteria

1. THE aplicación SHALL habilitar los virtual threads de Java 21 (Project Loom) para el manejo de peticiones HTTP.
2. THE configuración SHALL activarse mediante la propiedad `spring.threads.virtual.enabled=true` en `application.yml`.
3. WHEN llega una petición HTTP THEN el servidor (Tomcat) SHALL atenderla sobre un virtual thread en lugar de un hilo de plataforma del pool tradicional.
4. THE cambio SHALL preservar el comportamiento funcional existente de todos los endpoints (mismos códigos HTTP y respuestas).

### Requirement 11: Despliegue en Kubernetes (minikube)

**User Story:** Como desarrollador, quiero desplegar la aplicación en un clúster minikube local, para probarla en un entorno Kubernetes sin infraestructura remota.

#### Acceptance Criteria

1. THE proyecto SHALL incluir manifests de Kubernetes (Deployment y Service) para desplegar la aplicación.
2. THE Deployment SHALL usar la imagen Docker de la aplicación y exponer el contenedor en el puerto 8081.
3. THE Service SHALL exponer la aplicación dentro del clúster y permitir el acceso local (tipo `NodePort`).
4. THE Deployment SHALL definir probes de liveness y readiness usando los endpoints de Spring Boot Actuator (`/actuator/health/liveness` y `/actuator/health/readiness`).
5. THE Deployment SHALL declarar límites y solicitudes de recursos (CPU y memoria) razonables.
6. WHEN se aplican los manifests en minikube THEN el pod SHALL arrancar y quedar en estado `Ready`, y la API SHALL responder a través del Service.

### Requirement 12: Observabilidad con Spring Boot Actuator

**User Story:** Como operador, quiero endpoints de salud y estado de la aplicación, para monitorearla y para que las probes de Kubernetes puedan comprobar su disponibilidad.

#### Acceptance Criteria

1. THE aplicación SHALL incluir Spring Boot Actuator.
2. THE aplicación SHALL exponer el endpoint `/actuator/health` con el estado general de la aplicación.
3. THE aplicación SHALL exponer las probes de liveness (`/actuator/health/liveness`) y readiness (`/actuator/health/readiness`).
4. WHEN la aplicación está operativa THEN el endpoint de salud SHALL responder con estado HTTP 200 y `status: UP`.
### Requirement 13: Pipeline de CI/CD con Jenkins

**User Story:** Como desarrollador, quiero un pipeline de Jenkins que compile, pruebe, contenerice y despliegue la aplicación en minikube, para automatizar el ciclo de integración y despliegue de forma reproducible.

#### Acceptance Criteria

1. THE proyecto SHALL incluir un `Jenkinsfile` (pipeline declarativo) en la raíz del repositorio.
2. THE pipeline SHALL incluir una etapa de checkout que clone el código fuente desde el repositorio de GitHub (`https://github.com/ocardenasmartinez1984/kiro-sdd.git`, rama `main`).
3. THE pipeline SHALL incluir una etapa de compilación que construya el proyecto con Maven (por ejemplo `mvn -B clean compile`).
4. THE pipeline SHALL incluir una etapa que ejecute las pruebas unitarias (por ejemplo `mvn -B test`) y SHALL publicar los resultados de las pruebas.
5. WHEN las pruebas unitarias fallan THEN el pipeline SHALL marcar la ejecución como fallida y no continuar con las etapas de contenerización y despliegue.
6. THE pipeline SHALL incluir una etapa de contenerización que construya la imagen Docker de la aplicación reutilizando el `Dockerfile` existente.
7. THE pipeline SHALL incluir una etapa de despliegue que aplique los manifests de Kubernetes (`kubectl apply -f k8s/`) sobre el clúster minikube.
8. THE etapa de despliegue SHALL asegurar que la imagen construida esté disponible para minikube (por ejemplo, construyendo con el daemon Docker de minikube o cargando la imagen con `minikube image load`).
9. WHEN el despliegue se completa THEN el pipeline SHALL verificar que el rollout del Deployment finalice correctamente (por ejemplo `kubectl rollout status`).
