# Entrega — Tuckersoft Branch Engine

## Estado

Se implementaron los cinco checkpoints: seguridad JWT, nodos, partidas, decisiones y notificación asíncrona. El resultado final de los autotests debe copiarse aquí después de ejecutarlos contra PostgreSQL y el SMTP de pruebas.

## Flujo asíncrono

`DecisionService` guarda la partida y la decisión dentro de una transacción y publica `DecisionCommittedEvent`. Tras el commit, `BranchNotificationListener` procesa el evento en un hilo `branch-worker-*`, dentro de una transacción nueva. El listener envía el Informe de Realidad mediante `JavaMailSender` y registra el resultado `SENT` o `FAILED` en `RealityLog`, además de estabilizar la decisión o marcarla con error.

## Pendiente local

Rellenar `equipo.json` con los tres integrantes reales y ejecutar los autotests con PostgreSQL y el servidor SMTP levantados. `.env` se crea desde `.env.example` y no se versiona.
