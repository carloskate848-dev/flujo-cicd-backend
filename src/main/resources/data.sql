INSERT INTO tareas (titulo, descripcion, completada, fecha_creacion) VALUES
  ('Configurar repositorio en GitHub', 'Crear el repo y subir el proyecto base', false, CURRENT_TIMESTAMP),
  ('Conectar pipeline de CI', 'Enlazar SonarCloud, CodeQL y Trivy al PR', false, CURRENT_TIMESTAMP),
  ('Revisar hallazgos de seguridad', 'Corregir la inyección SQL detectada en el análisis', false, CURRENT_TIMESTAMP);
