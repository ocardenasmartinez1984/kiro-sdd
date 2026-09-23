```
  _   _                  ____                _
 | | | |___  ___ _ __   / ___|_ __ _   _  __| |
 | | | / __|/ _ \ '__| | |   | '__| | | |/ _` |
 | |_| \__ \  __/ |    | |___| |  | |_| | (_| |
  \___/|___/\___|_|     \____|_|   \__,_|\__,_|

        Mantenedor de Usuarios · REST API
```

# User CRUD

API REST para el mantenimiento de usuarios (crear, consultar, actualizar y eliminar), construida con **Java 21**, **Spring Boot 3.5**, **Lombok** y base de datos **H2** en memoria.

---

## Stack tecnológico

| Componente     | Tecnología                          | Versión              |
|----------------|-------------------------------------|----------------------|
| Lenguaje       | Java                                | 21 (LTS)             |
| Framework      | Spring Boot                         | 3.5.x                |
| Persistencia   | Spring Data JPA (Hibernate)         | Gestionada por Boot  |
| Base de datos  | H2 (en memoria)                     | Gestionada por Boot  |
| Boilerplate    | Lombok                              | Gestionada por Boot  |
| Validación     | Jakarta Bean Validation             | Gestionada por Boot  |
| Documentación  | springdoc-openapi (Swagger UI)      | 2.8.x                |
| Contenedores   | Docker + Docker Compose             | multi-etapa          |
| Build          | Maven                               | 3.9+                 |

---

## Arquitectura

Arquitectura **hexagonal (puertos y adaptadores) + DDD**. El dominio queda en el centro, sin dependencias de framework; los adaptadores (web y persistencia) se conectan a través de puertos.

```
        Cliente HTTP
             │  JSON
             ▼
 ┌───────────────────────────┐
 │  Adaptador de entrada web  │  UserController + DTOs + UserWebMapper
 └─────────────┬─────────────┘
               │ invoca
               ▼
 ┌───────────────────────────┐
 │   Puertos de entrada       │  Create/Get/Update/DeleteUserUseCase
 └─────────────┬─────────────┘
               │ implementados por
               ▼
 ┌───────────────────────────┐        ┌──────────────────────┐
 │  UserApplicationService    │───────▶│   Dominio (puro)     │
 │  (@Transactional)          │        │  User, UserId, Email │
 └─────────────┬─────────────┘        └──────────────────────┘
               │ usa
               ▼
 ┌───────────────────────────┐
 │  Puerto de salida          │  UserRepositoryPort
 └─────────────┬─────────────┘
               │ implementado por
               ▼
 ┌───────────────────────────┐
 │ Adaptador de persistencia  │  UserPersistenceAdapter + UserJpaEntity
 └─────────────┬─────────────┘
               │ SQL
               ▼
          ┌─────────┐
          │   H2    │
          └─────────┘
```

Regla de dependencias hacia el centro: el dominio no depende de nada externo; la aplicación depende solo del dominio; la infraestructura depende de aplicación y dominio.

### Estructura de paquetes

```
com.example.usercrud
├── UserCrudApplication.java
├── domain                          # núcleo puro, sin framework
│   ├── model
│   │   ├── User.java               (aggregate root)
│   │   ├── UserId.java             (value object)
│   │   └── Email.java              (value object, valida formato)
│   └── exception
│       ├── InvalidEmailException.java
│       ├── UserNotFoundException.java
│       └── DuplicateEmailException.java
├── application
│   ├── port
│   │   ├── in                      # puertos de entrada (use cases)
│   │   │   ├── CreateUserUseCase.java
│   │   │   ├── GetUserUseCase.java
│   │   │   ├── UpdateUserUseCase.java
│   │   │   ├── DeleteUserUseCase.java
│   │   │   └── CreateUserCommand.java
│   │   └── out                     # puerto de salida
│   │       └── UserRepositoryPort.java
│   └── service
│       └── UserApplicationService.java
└── infrastructure
    ├── adapter
    │   ├── in.web                  # UserController, dto, UserWebMapper, GlobalExceptionHandler
    │   └── out.persistence         # UserJpaEntity, SpringDataUserRepository,
    │                               # UserPersistenceAdapter, UserPersistenceMapper
    └── config
        └── OpenApiConfig.java
```

---

## Requisitos previos

Para ejecución local:
- JDK 21 o superior
- Maven 3.9+

Para ejecución con contenedores:
- Docker y Docker Compose

---

## Cómo ejecutar

```bash
# Compilar
mvn clean compile

# Ejecutar la aplicación
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

---

## Ejecutar con Docker

El proyecto incluye un `Dockerfile` multi-etapa (build con Maven + JDK 21, runtime con JRE 21, usuario no root) y un `docker-compose.yml`.

```bash
# Opción A: Docker Compose (construye y levanta)
docker compose up --build

# Opción B: build y run manual
docker build -t user-crud:latest .
docker run --rm -p 8080:8080 user-crud:latest
```

La API queda disponible en `http://localhost:8080`. Para detener con Compose: `docker compose down`.

La base de datos H2 es en memoria y vive dentro del contenedor, por lo que los datos se pierden al reiniciarlo (comportamiento esperado para este proyecto de ejemplo).

### Documentación de la API (Swagger)

Con la aplicación en marcha:

| Recurso            | URL                                          |
|--------------------|----------------------------------------------|
| Swagger UI         | `http://localhost:8080/swagger-ui.html`      |
| OpenAPI 3 (JSON)   | `http://localhost:8080/v3/api-docs`          |

### Consola H2

Con la aplicación en marcha, abre `http://localhost:8080/h2-console` y conéctate con:

| Campo    | Valor                              |
|----------|------------------------------------|
| JDBC URL | `jdbc:h2:mem:usersdb`              |
| Usuario  | `sa`                               |
| Password | _(vacío)_                          |

---

## Modelo de datos

| Campo    | Tipo      | Restricciones                    |
|----------|-----------|----------------------------------|
| `id`     | `Long`    | PK, autogenerado                 |
| `nombre` | `String`  | obligatorio                      |
| `email`  | `String`  | obligatorio y único              |
| `activo` | `boolean` | por defecto `true`               |

---

## Endpoints

Base path: `/api/users`

| Método   | Ruta              | Descripción             | Éxito | Errores       |
|----------|-------------------|-------------------------|-------|---------------|
| `POST`   | `/api/users`      | Crear usuario           | 201   | 400, 409      |
| `GET`    | `/api/users`      | Listar usuarios         | 200   | —             |
| `GET`    | `/api/users/{id}` | Obtener usuario por id  | 200   | 404           |
| `PUT`    | `/api/users/{id}` | Actualizar usuario      | 200   | 400, 404, 409 |
| `DELETE` | `/api/users/{id}` | Eliminar usuario        | 204   | 404           |

### Ejemplos

**Crear usuario**

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ada Lovelace","email":"ada@example.com","activo":true}'
```

Respuesta `201 Created`:

```json
{
  "id": 1,
  "nombre": "Ada Lovelace",
  "email": "ada@example.com",
  "activo": true
}
```

**Listar usuarios**

```bash
curl http://localhost:8080/api/users
```

**Obtener por id**

```bash
curl http://localhost:8080/api/users/1
```

**Actualizar**

```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ada L.","email":"ada@example.com","activo":false}'
```

**Eliminar**

```bash
curl -X DELETE http://localhost:8080/api/users/1
```

---

## Manejo de errores

Todas las respuestas de error comparten una estructura uniforme:

```json
{
  "status": 400,
  "message": "Error de validación",
  "timestamp": "2026-09-23T10:15:30",
  "errors": {
    "email": "El formato del email es inválido",
    "nombre": "El nombre es obligatorio"
  }
}
```

| Situación                 | Estado HTTP |
|---------------------------|-------------|
| Datos de entrada inválidos| 400         |
| Usuario no encontrado     | 404         |
| Email duplicado           | 409         |
| Error inesperado          | 500         |

---

## Pruebas

```bash
mvn test
```

---

## Licencia

Proyecto de ejemplo con fines educativos.
