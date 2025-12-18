# Andon + OEE en tiempo real

## Descripción

Sistema Andon para monitorear en tiempo (casi) real el estado de una línea de producción
(RUN/STOP) y calcular el OEE (Disponibilidad, Rendimiento y Calidad) a partir de eventos de
producción. Este proyecto está pensado como base para integrarse después con datos reales
de planta y dashboards en tiempo real.

## Problema

Actualmente la información de paros y desempeño de la línea:

- Se registra de forma manual o dispersa.
- No se visualiza en tiempo real.
- El cálculo de OEE llega tarde y no siempre es consistente.

Esto dificulta tomar decisiones rápidas y atacar los principales desperdicios.

## Objetivo

Construir un servicio backend que:

- Reciba eventos de producción y paros (aunque inicialmente sean simulados).
- Calcule métricas básicas de:
  - **Disponibilidad** (Availability)
  - **Rendimiento** (Performance)
  - **Calidad** (Quality)
  - **OEE** (Overall Equipment Effectiveness)
- Exponga estas métricas vía API para ser consumidas por dashboards u otros servicios.

## Stack / Arquitectura

- **Backend:** Java + Spring Boot
- **Build:** Maven
- **Estilo de API:** REST (JSON)
- **Estado actual:** eventos y cálculo de OEE en memoria (sin BD todavía).

A futuro se integrará con:

- BD de series de tiempo (por ejemplo TimescaleDB).
- MQTT / WebSockets para actualización en tiempo real.
- Dashboards (Grafana, front-end React, etc.).

## Estado actual (MVP técnico end-to-end)

MVP técnico implementado:

- Aplicación Spring Boot (`backend/andon-backend`) corriendo en `http://localhost:8080`.
- Cálculo de OEE a partir de una lista de eventos simulados en memoria.

### Endpoints disponibles

#### `GET /health`

Verifica que el servicio esté vivo.

**Ejemplo de respuesta:**

```json
{
  "status": "ok",
  "service": "andon-oee-api",
  "version": "0.1.0"
}
