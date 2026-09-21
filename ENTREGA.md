# Entrega — Tuckersoft Branch Engine

## Estado

Se implementaron y aprobaron los cinco checkpoints. Resultado de la corrida final de Tuckersoft:

- ★1 Seguridad: 65 comprobaciones.
- ★2 Nodos: 37 comprobaciones.
- ★3 Partidas: 40 comprobaciones.
- ★4 Decisiones: 101 comprobaciones.
- ★5 Asincronía: 41 comprobaciones.
- Total: 52 tests, 0 fallos, 0 errores y 0 omitidos.

Además, los cinco tests unitarios de `DecisionService` con Mockito pasan desde la raíz mediante `mvnw.cmd test`.

## Flujo asíncrono

`DecisionService` guarda la partida y la decisión dentro de una transacción y publica `DecisionCommittedEvent`. Tras el commit, `BranchNotificationListener` procesa el evento en un hilo `branch-worker-*`, dentro de una transacción nueva. El listener envía el Informe de Realidad mediante `JavaMailSender` y registra el resultado `SENT` o `FAILED` en `RealityLog`, además de estabilizar la decisión o marcarla con error.

## Trabajo no terminado

No quedaron funcionalidades obligatorias pendientes. El archivo `.env` se crea localmente desde `.env.example` y no se versiona, tal como exige el enunciado.
