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
5. WHEN la aplicación arranca THEN el sistema SHALL exponer la API REST en el puerto configurado (por defecto 8080).

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
4. WHEN se ejecuta el contenedor THEN el sistema SHALL exponer la API en el puerto 8080.
5. THE proyecto SHALL incluir un archivo `docker-compose.yml` que permita levantar la aplicación con un solo comando.
6. THE proyecto SHALL incluir un `.dockerignore` que excluya artefactos innecesarios del contexto de build (por ejemplo `target`, archivos de IDE).
7. THE contenedor SHALL ejecutar la aplicación con un usuario no root por buenas prácticas de seguridad.
