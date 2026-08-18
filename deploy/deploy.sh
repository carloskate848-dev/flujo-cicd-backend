#!/usr/bin/env bash
# Se ejecuta en el servidor de preprod vía AWS SSM (paso "Ejecutar script (SSM)"
# del diagrama). Ajusta bucket, rutas y nombre del servicio a tu entorno real.
set -euo pipefail

BUCKET="__S3_DEPLOY_BUCKET__"
DEST="/opt/flujo-cicd-backend"
SERVICE="flujo-cicd-backend" # servicio systemd que corre el JAR

echo "==> Descargando JAR desde s3://${BUCKET}/flujo-cicd-backend"
sudo mkdir -p "${DEST}"
aws s3 cp "s3://${BUCKET}/flujo-cicd-backend/flujo-cicd-backend.jar" "${DEST}/app.jar"

echo "==> Reiniciando servicio"
sudo systemctl restart "${SERVICE}"

echo "==> Despliegue completado"
