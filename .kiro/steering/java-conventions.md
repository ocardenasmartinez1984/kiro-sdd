# Convenciones de código Java

## Inferencia de tipos con `var`

- Usar `var` para variables locales en lugar de declaraciones con tipo explícito siempre que el tipo sea evidente por el lado derecho de la asignación.
- Aplica a variables locales dentro de métodos (por ejemplo, resultados de llamadas a servicios, builders, colecciones instanciadas).
- No usar `var` cuando perjudique la legibilidad o cuando el tipo no sea claro a partir de la inicialización.
- No aplicar `var` a campos de clase, parámetros de método ni tipos de retorno (Java no lo permite en esos contextos).

Ejemplo:

```java
// Preferido
var response = userService.create(request);
var errors = new HashMap<String, String>();

// Evitar
UserResponse response = userService.create(request);
Map<String, String> errors = new HashMap<>();
```
