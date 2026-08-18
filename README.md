# flujo-cicd-backend

API REST de tareas (To-Do) construida con **Spring Boot 4 / Java 21**.
Es el complemento en el lado **Java** del flujo de
`Flujo_CI-CD_concepto_2_1.svg`: push a `feature/*` → PR a `preprod` →
cobertura (JaCoCo) → checks de seguridad y calidad → quality gate →
aprobación → merge → build → autenticación OIDC → publicación en S3 →
despliegue vía SSM.

Este proyecto es **funcional** (CRUD real con base de datos) y contiene
**dos fallas intencionales** para que las veas reflejadas por SonarCloud,
CodeQL y Trivy cuando conectes el repo al pipeline. Ver la sección
"Fallas intencionales" más abajo.

## Funcionalidad

API de gestión de tareas, persistida en una base H2 en memoria:

| Método | Endpoint                         | Descripción                              |
|--------|-----------------------------------|-------------------------------------------|
| GET    | `/api/tareas`                     | Lista todas las tareas (o filtra por `?completada=true/false`) |
| GET    | `/api/tareas/{id}`                | Obtiene una tarea por id                  |
| GET    | `/api/tareas/buscar?texto=...`    | Busca tareas por texto en el título       |
| POST   | `/api/tareas`                     | Crea una tarea (`{"titulo": "...", "descripcion": "..."}`) |
| PUT    | `/api/tareas/{id}`                | Actualiza título/descripción              |
| PATCH  | `/api/tareas/{id}/completar?completada=true` | Marca como completada/pendiente |
| DELETE | `/api/tareas/{id}`                | Elimina una tarea                         |

La consola web de H2 queda disponible en `http://localhost:8080/h2-console`
(JDBC URL: `jdbc:h2:mem:tareasdb`, usuario `sa`, sin contraseña).

## Fallas intencionales (para el ejercicio de CI/CD)

1. **Inyección SQL (CWE-89)** — `BusquedaTareaRepositorio.buscarPorTitulo()`
   concatena el parámetro del usuario directamente en una consulta nativa,
   en vez de usar un parámetro ligado. SonarCloud y CodeQL deberían
   marcarla como vulnerabilidad **Blocker/Critical**. Se usa desde
   `GET /api/tareas/buscar?texto=...`.
   - Corrección propuesta (queda como ejercicio): usar
     `query.setParameter("texto", texto)` en vez de concatenar el string.
2. **Dependencia vulnerable** — el `pom.xml` incluye a propósito
   `commons-collections:commons-collections:3.2.1`, una versión con una
   vulnerabilidad de deserialización insegura conocida. Trivy (o el OWASP
   Dependency-Check equivalente) debería marcarla como hallazgo de
   seguridad en la cadena de dependencias (SCA).
   - Corrección propuesta: migrar a `org.apache.commons:commons-collections4`
     en su última versión estable.

La idea es que subas el proyecto tal cual, conectes el pipeline, veas
estos hallazgos aparecer en SonarCloud/Trivy, y luego practiques
corrigiéndolos en un PR nuevo para ver cómo el quality gate pasa a verde.

## Qué incluye

- Spring Boot 4 (Java 21) + Spring Web + Spring Data JPA + Validation + H2.
- Maven Wrapper (`mvnw` / `mvnw.cmd`) — no necesitas instalar Maven aparte.
- **JaCoCo** configurado en `pom.xml`, con un umbral mínimo de cobertura
  (60% de líneas) que falla el build si no se cumple.
- 15 tests unitarios/de integración (JUnit 5 + Mockito + MockMvc), ~86% de
  cobertura sobre el código de negocio.
- Workflows de **GitHub Actions** en `.github/workflows/`:
  - `ci-pr.yml`: PR hacia `preprod` → build, tests + JaCoCo, SonarCloud,
    Trivy (SCA), y un job `quality-gate` que resume el resultado.
  - `codeql.yml`: análisis estático (**SAST**) con CodeQL para Java.
  - `cd-preprod.yml`: push/merge a `preprod` → empaqueta el JAR, se
    autentica en AWS por **OIDC**, lo publica en **S3** y ejecuta
    `deploy/deploy.sh` en el servidor destino vía **SSM**.
- `deploy/deploy.sh`: script placeholder para el servidor de preprod.

## Requisitos (Windows)

- **JDK 21** (Temurin recomendado). El proyecto usa el wrapper de Maven,
  así que **no necesitas instalar Maven por separado**.
- [Visual Studio Code](https://code.visualstudio.com/) con la extensión
  **Extension Pack for Java** (`vscjava.vscode-java-pack`), o IntelliJ IDEA.
- [Git para Windows](https://git-scm.com/download/win).
- Una cuenta de GitHub.

### Instalar el JDK 21 en Windows

```powershell
winget install --id EclipseAdoptium.Temurin.21.JDK
```

Cierra y vuelve a abrir la terminal, y verifica:

```powershell
java -version
```

Debe mostrar `21.x.x`. Si tienes varias versiones de Java instaladas y
`java -version` muestra otra, configura `JAVA_HOME` apuntando a la
instalación de Temurin 21 (Panel de control → Variables de entorno, o):

```powershell
setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot"
```

(cierra y reabre la terminal después de `setx`).

## Cómo correrlo en Windows

Desde PowerShell, dentro de la carpeta del proyecto:

```powershell
# Compilar, correr tests y generar el reporte de cobertura JaCoCo
.\mvnw.cmd clean verify

# Levantar la aplicación
.\mvnw.cmd spring-boot:run
```

La API queda disponible en `http://localhost:8080`. Pruébala, por ejemplo
con PowerShell:

```powershell
Invoke-RestMethod http://localhost:8080/api/tareas
```

o con `curl` si lo tienes instalado (Windows 10/11 ya lo trae):

```powershell
curl http://localhost:8080/api/tareas
```

El reporte de cobertura queda en
`target\site\jacoco\index.html` — ábrelo con doble clic para verlo en el navegador.

Para abrir el proyecto en VS Code:

```powershell
code .
```

## Cómo subirlo a GitHub

```powershell
git init
git config user.name "Tu Nombre"
git config user.email "tu-email@ejemplo.com"
git add .
git commit -m "chore: proyecto Spring Boot base con flujo CI/CD"

# Crea el repo vacío en GitHub (por la web o con GitHub CLI)
gh repo create flujo-cicd-backend --private --source=. --remote=origin

git branch -M main
git push -u origin main

git checkout -b preprod
git push -u origin preprod
```

Trabajo diario siguiendo el flujo del diagrama:

```powershell
git checkout -b feature/mi-cambio
# ...trabajo, commits...
git push -u origin feature/mi-cambio
# Abrir Pull Request feature/mi-cambio → preprod desde GitHub
```

## Configuración pendiente en GitHub (para el flujo completo)

1. **Branch protection en `preprod`** (Settings → Branches): exigir el
   status check `Quality gate` y al menos 1 aprobación de code review.
2. **Secretos y variables** (Settings → Secrets and variables → Actions):
   - `SONAR_TOKEN` (secret): token del proyecto en [SonarCloud](https://sonarcloud.io).
   - `SONAR_PROJECT_KEY` / `SONAR_ORGANIZATION` (variables): datos del
     proyecto creado en SonarCloud.
   - `AWS_ROLE_ARN` (secret): ARN del IAM Role que GitHub Actions asumirá vía OIDC.
   - `AWS_REGION` (variable): región de AWS, p. ej. `us-east-1`.
   - `S3_DEPLOY_BUCKET` (variable): bucket destino de los artefactos.
3. **AWS IAM**: Identity Provider OIDC de GitHub Actions + Role con
   permisos de `s3:PutObject` y `ssm:SendCommand`.
4. Reemplazar `__S3_DEPLOY_BUCKET__` en `deploy/deploy.sh` por el bucket real.

## Estructura relevante

```
src/main/java/com/flujocicd/backend/
  modelo/            Tarea (entidad JPA)
  repositorio/        TareaRepositorio, BusquedaTareaRepositorio (⚠ SQLi intencional)
  servicio/           TareaServicio (lógica de negocio)
  controlador/        TareaControlador (endpoints REST)
  dto/                TareaRequest, TareaRespuesta
  excepcion/          Manejo de errores
src/test/java/...      Tests unitarios y de integración
.github/workflows/
  ci-pr.yml           PR → preprod: build, tests+JaCoCo, Sonar, Trivy, quality gate
  codeql.yml          SAST
  cd-preprod.yml      merge a preprod: build, OIDC, S3, SSM
deploy/deploy.sh       script que corre en el servidor vía SSM
pom.xml                incluye JaCoCo y la dependencia vulnerable intencional
```
