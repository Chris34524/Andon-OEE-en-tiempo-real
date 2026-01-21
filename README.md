# Andon + OEE en tiempo real (Spring Boot + PostgreSQL/Timescale + Docker)

Backend MVP para capturar eventos de Andon (RUN/STOP/GOOD_PART/BAD_PART) y calcular OEE por rango de tiempo y estación.  
Incluye endpoint de estado actual de estación (Andon color) y script de demo reproducible.

---

## Stack
- Java 17
- Spring Boot (Web + Data JPA)
- PostgreSQL / TimescaleDB (Docker)
- Maven Wrapper

---

## Estructura del repo

```
Andon-OEE-en-tiempo-real/
├─ docker-compose.yml
├─ README.md
├─ scripts/
│  └─ demo.sh
└─ backend/
   └─ andon-backend/
      ├─ pom.xml
      └─ src/main/java/com/ldr/andon/...
```

---

## Requisitos
- Docker + Docker Compose
- Java 17
- Git Bash (Windows) o Bash (Linux/Mac)

---

## Cómo correr (local)

### 1) Levantar Base de Datos
Desde la raíz del repo:

```bash
docker compose up -d
docker ps
```

> La BD expone: `localhost:5433` (mapeo 5433 -> 5432)

### 2) Levantar Backend
En otra terminal:

```bash
cd backend/andon-backend
./mvnw spring-boot:run
```

Backend en: `http://localhost:8080`

---

## Demo reproducible (1 comando)
Con el backend y la BD arriba, desde la raíz del repo:

```bash
chmod +x scripts/demo.sh
./scripts/demo.sh
```

---

## Endpoints principales

### Health
```bash
curl http://localhost:8080/api/health
```

### Seed (datos demo)
```bash
curl -X POST http://localhost:8080/api/events/seed
```

### Crear evento real
```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{"station":"STATION-01","eventType":"STOP","quantity":0,"note":"STOP real"}'
```

### Cerrar evento (RUN/STOP)
```bash
curl -X PATCH http://localhost:8080/api/events/{id}/close \
  -H "Content-Type: application/json" \
  -d '{"note":"close event"}'
```

### Listar eventos (filtros + paginación)
```bash
curl "http://localhost:8080/api/events?station=STATION-01&page=0&size=50"
```

Opcional: rango por ISO-8601
```bash
curl "http://localhost:8080/api/events?station=STATION-01&from=2026-01-20T00:00:00Z&to=2026-01-21T00:00:00Z&page=0&size=50"
```

### OEE por rango
```bash
curl "http://localhost:8080/api/oee?station=STATION-01&from=2026-01-20T00:00:00Z&to=2026-01-21T00:00:00Z"
```

**Respuesta (ejemplo):**
```json
{
  "availability": 0.25,
  "performance": 1.0,
  "quality": 0.95,
  "oee": 0.24
}
```

### Estado de estación (Andon color)
```bash
curl "http://localhost:8080/api/stations/STATION-01/state?stopThresholdMinutes=10"
```

---

## Notas de MVP
- `GOOD_PART` y `BAD_PART` se guardan como eventos instantáneos (se cierran al mismo timestamp).
- `RUN` y `STOP` pueden permanecer abiertos (status `OPEN`) hasta cerrarse con el endpoint `close`.
- El cálculo de OEE usa:
  - Availability = runtime / plannedTime (plannedTime = duración del rango)
  - Performance = 1.0 (placeholder en MVP; listo para evolucionar a ideal cycle time)
  - Quality = good / (good + bad)

---

## Próximos upgrades (post MVP)
- Performance real con Ideal Cycle Time / target rate
- Métricas adicionales: scrap rate, FPY, conteos por estación
- Hypertable Timescale + índices adicionales
- Postman collection + CI básico
