# Principio KISS (Keep It Simple)

Prioriza la solución más simple que resuelva el problema real. La simplicidad es una restricción de diseño de primer nivel en este proyecto.

## Reglas

- Resuelve solo el problema pedido. No añadas features, capas de abstracción ni configurabilidad "por si acaso" (YAGNI).
- Prefiere lo directo y legible sobre lo ingenioso. El código se lee muchas más veces de las que se escribe.
- No introduzcas patrones o infraestructura pesados (por ejemplo CQRS, event sourcing, colas de mensajes, cachés distribuidas, frameworks de mapeo) salvo que exista una necesidad concreta y demostrable. Para un CRUD simple, normalmente NO se justifican.
- Menos piezas móviles: menos clases, menos dependencias, menos configuración. Cada elemento nuevo debe ganarse su lugar.
- Antes de añadir una abstracción, pregunta si hay al menos dos usos reales que la necesiten. Si no, no la crees.
- Mantén la arquitectura hexagonal + DDD existente, pero sin inflarla: usa los puertos y adaptadores que ya hay; no multipliques interfaces ni indirecciones innecesarias.
- Si una propiedad de configuración logra el objetivo (como `spring.threads.virtual.enabled`), prefiérela antes que escribir código.
- Los tests de verificación puntual (throwaway) no se dejan en el repositorio: verifican algo una vez y se eliminan.

## Cómo aplicarlo al proponer cambios

- Cuando existan varias opciones, elige la de menor complejidad que cumpla los requisitos y explica brevemente por qué.
- Si una petición implica sobre-ingeniería para el tamaño actual del proyecto, dilo con franqueza y ofrece la alternativa simple.
