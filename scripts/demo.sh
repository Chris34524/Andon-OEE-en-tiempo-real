#!/usr/bin/env bash
set -e

BASE="http://localhost:8080"

echo "Health"
curl -s "$BASE/api/health"; echo

echo "Seed"
curl -s -X POST "$BASE/api/events/seed"; echo

echo "Create STOP (real)"
curl -s -X POST "$BASE/api/events" -H "Content-Type: application/json" \
  -d '{"station":"STATION-01","eventType":"STOP","quantity":0,"note":"STOP real"}'; echo

echo "List events"
curl -s "$BASE/api/events?station=STATION-01&page=0&size=10"; echo

FROM="$(date -u -d '24 hours ago' +%Y-%m-%dT%H:%M:%SZ)"
TO="$(date -u +%Y-%m-%dT%H:%M:%SZ)"

echo "OEE range"
curl -s "$BASE/api/oee?station=STATION-01&from=$FROM&to=$TO"; echo

echo "Station state"
curl -s "$BASE/api/stations/STATION-01/state?stopThresholdMinutes=10"; echo
