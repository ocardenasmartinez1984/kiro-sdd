# Implementation Plan

- [x] 1. Inicializar el proyecto Spring Boot con Maven
  - Crear el `pom.xml` con parent `spring-boot-starter-parent` 3.5.x y `java.version` 21.
  - Añadir dependencias: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, `h2` (runtime), `lombok` (optional) y `spring-boot-starter-test` (test).
  - Crear la clase principal `UserCrudApplication` con `@SpringBootApplication` en el paquete `com.example.usercrud`.
  - _Requirements: 7.1, 7.2, 7.3_

- [x] 2. Configurar la aplicación y la base de datos H2
  - Crear `src/main/resources/application.yml` con puerto 8080, datasource H2 en memoria, JPA (`ddl-auto: update`, `show-sql`) y consola H2 habilitada en `/h2-console`.
  - Verificar que la aplicación arranca correctamente.
  - _Requirements: 7.3, 7.4, 7.5_

- [x] 3. Implementar la entidad User
  - Crear la entidad `User` en el paquete `entity` con campos `id`, `nombre`, `email` (único), `activo` (default `true`).
  - Anotar con JPA (`@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`) y Lombok (`@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`).
  - _Requirements: 5.1, 5.2, 5.3_

- [x] 4. Crear los DTOs de entrada y salida
  - Crear `UserRequest` en el paquete `dto` con validaciones (`@NotBlank` en nombre, `@NotBlank` + `@Email` en email, `activo` opcional).
  - Crear `UserResponse` en el paquete `dto` con `id`, `nombre`, `email`, `activo`.
  - _Requirements: 1.2, 1.3, 3.3, 5.2_

- [x] 5. Implementar el repositorio UserRepository
  - Crear la interfaz `UserRepository` extendiendo `JpaRepository<User, Long>`.
  - Añadir los métodos derivados `existsByEmail(String email)` y `findByEmail(String email)`.
  - _Requirements: 1.4, 3.4_

- [x] 6. Crear las excepciones de negocio y la estructura de error
  - Crear `ResourceNotFoundException` (para 404) y `DuplicateEmailException` (para 409) en el paquete `exception`.
  - Crear `ErrorResponse` con `status`, `message`, `timestamp` y `errors` (mapa de campos).
  - _Requirements: 6.1, 6.2, 6.3_

- [x] 7. Implementar el GlobalExceptionHandler
  - Crear `GlobalExceptionHandler` con `@RestControllerAdvice`.
  - Manejar `MethodArgumentNotValidException` → 400 con detalle por campo, `ResourceNotFoundException` → 404, `DuplicateEmailException` → 409 y un fallback `Exception` → 500.
  - Devolver siempre la estructura `ErrorResponse`.
  - _Requirements: 1.2, 1.3, 2.3, 3.2, 3.3, 6.1, 6.2, 6.3_

- [x] 8. Implementar la lógica de negocio en UserService
  - Crear `UserService` transaccional con métodos `create`, `findAll`, `findById`, `update`, `delete`.
  - Implementar mapeo privado `toEntity`/`toResponse`.
  - En `create`: validar email único (`existsByEmail`) lanzando `DuplicateEmailException`; asignar `activo=true` si viene null.
  - En `findById`/`update`/`delete`: lanzar `ResourceNotFoundException` si no existe.
  - En `update`: si el email cambia y ya pertenece a otro usuario, lanzar `DuplicateEmailException`.
  - _Requirements: 1.1, 1.4, 1.5, 2.2, 2.3, 3.1, 3.2, 3.4, 4.1, 4.2, 5.3_

- [x] 9. Implementar los endpoints REST en UserController
  - Crear `UserController` con base path `/api/users`.
  - Implementar POST (201 + Location), GET lista (200), GET por id (200), PUT (200) y DELETE (204).
  - Aplicar `@Valid` en los cuerpos de entrada de POST y PUT.
  - _Requirements: 1.1, 1.2, 1.3, 2.1, 2.2, 2.3, 2.4, 3.1, 3.2, 3.3, 4.1, 4.2_

- [x] 10. Escribir pruebas unitarias del UserService
  - Con JUnit 5 + Mockito, probar: creación exitosa, email duplicado, no encontrado en get/update/delete, actualización con email en conflicto y valor por defecto de `activo`.
  - _Requirements: 1.1, 1.4, 2.2, 2.3, 3.1, 3.2, 3.4, 4.1, 4.2, 5.3_

- [x] 11. Escribir pruebas del UserController y del repositorio
  - Con `@WebMvcTest` + `MockMvc` (service mockeado), verificar códigos HTTP, validación de entrada y estructura de errores para los flujos CRUD.
  - Con `@DataJpaTest` sobre H2, verificar `existsByEmail` y `findByEmail`.
  - _Requirements: 1.2, 1.3, 2.1, 2.2, 2.3, 2.4, 3.1, 3.2, 4.1, 4.2, 6.1, 6.2, 6.3_

- [x] 12. Prueba end-to-end del flujo CRUD y verificación final
  - Con `@SpringBootTest` + `MockMvc`, recorrer crear → listar → obtener → actualizar → eliminar contra H2 en memoria.
  - Ejecutar `mvn test` y confirmar que la build y todas las pruebas pasan.
  - _Requirements: 1.1, 1.5, 2.1, 2.2, 3.1, 4.1, 7.5_

- [x] 13. Integrar Swagger/OpenAPI (springdoc)
  - Añadir la dependencia `org.springdoc:springdoc-openapi-starter-webmvc-ui` 2.8.x al `pom.xml`.
  - Crear una clase de configuración `OpenApiConfig` con un bean `OpenAPI` que defina título, versión y descripción de la API.
  - Verificar que la app arranca y que `/v3/api-docs` y `/swagger-ui.html` responden correctamente.
  - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5_

- [x] 14. Refactorizar a arquitectura hexagonal + DDD
  - Reorganizar el código a arquitectura hexagonal (puertos y adaptadores) + DDD bajo `com.example.usercrud`, manteniendo el MISMO comportamiento funcional, los MISMOS endpoints (`/api/users`), códigos HTTP, validación y manejo de errores.
  - Crear la capa de dominio en `domain.model`: aggregate root `User` (POJO puro, sin JPA/Spring), value objects `UserId` (envuelve el `Long` id) y `Email` (valida el formato en el constructor); y en `domain.exception` la excepción de dominio `InvalidEmailException` para email inválido.
  - Crear los puertos de entrada en `application.port.in`: `CreateUserUseCase`, `GetUserUseCase`, `UpdateUserUseCase`, `DeleteUserUseCase`.
  - Crear el puerto de salida en `application.port.out`: `UserRepositoryPort`, expresado en términos del dominio (`User`, `UserId`, `Email`).
  - Crear `UserApplicationService` en `application.service` que implemente los puertos de entrada, dependa solo del dominio y de `UserRepositoryPort`, sea transaccional y conserve la lógica: unicidad de email (`DuplicateEmailException`), no encontrado (`ResourceNotFoundException`), conflicto de email en actualización y valor por defecto de `activo`.
  - Crear el adaptador de entrada web en `infrastructure.adapter.in.web`: `UserController` (dependiendo de los use cases), DTOs `UserRequest`/`UserResponse`/`ErrorResponse` en `.dto`, `UserWebMapper` en `mapper` (dominio↔DTO) y `GlobalExceptionHandler` que traduzca excepciones de aplicación y de dominio a `ErrorResponse`.
  - Crear el adaptador de salida de persistencia en `infrastructure.adapter.out.persistence`: `UserJpaEntity` (entidad JPA separada del dominio), `SpringDataUserRepository` (`JpaRepository<UserJpaEntity, Long>` con `existsByEmail`/`findByEmail`), `UserPersistenceAdapter` (implementa `UserRepositoryPort`) y `UserPersistenceMapper` (dominio↔entidad JPA).
  - Mover `OpenApiConfig` a `infrastructure.config`.
  - Eliminar las clases antiguas de la arquitectura por capas (paquetes `controller`, `service`, `repository`, `entity`, `dto`, `exception`) una vez migrada su funcionalidad a la nueva estructura.
  - Migrar y actualizar todas las pruebas a la nueva estructura: pruebas de dominio (value objects y aggregate como POJO), pruebas del `UserApplicationService` con mock de `UserRepositoryPort`, pruebas del adaptador web (`@WebMvcTest` con use cases mockeados), pruebas del adaptador de persistencia (`@DataJpaTest`) y la prueba end-to-end (`@SpringBootTest`). Ejecutar `mvn test` y confirmar que la build y todas las pruebas pasan.
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 2.1, 2.2, 2.3, 2.4, 3.1, 3.2, 3.3, 3.4, 4.1, 4.2, 5.1, 5.2, 5.3, 6.1, 6.2, 6.3, 7.1, 7.2, 7.3, 7.4, 7.5, 8.1, 8.2, 8.3, 8.4, 8.5_

- [ ] 15. Contenerizar la aplicación con Docker
  - Crear un `Dockerfile` multi-etapa (build con Maven + JDK 21, runtime con JRE 21) que empaquete el JAR ejecutable y ejecute como usuario no root, exponiendo el puerto 8080.
  - Crear un `.dockerignore` que excluya `target/`, `.git`, `.kiro` y archivos de IDE.
  - Crear un `docker-compose.yml` con un servicio `app` que mapee `8080:8080`.
  - Verificar que la imagen se construye y el contenedor arranca la API en el puerto 8080.
  - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5, 9.6, 9.7_
