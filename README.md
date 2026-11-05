# Sistema de Gestion de Servicios - Clinica San Rafael

Aplicacion web para la gestion de pacientes, medicos, citas, historial clinico y recetas medicas de una clinica. Fue desarrollada con HTML, CSS, Java, Spring Boot y MySQL, e incluye la descarga de recetas en formato PDF.

## Funcionalidades

- Dashboard con indicadores de pacientes, medicos, citas del dia, citas programadas e historiales recientes.
- CRUD de pacientes con busqueda por nombres, apellidos o documento.
- CRUD de medicos con busqueda por nombre, especialidad o colegiatura.
- Agenda de citas con filtros por fecha, estado y medico.
- Registro de atenciones en el historial clinico, incluyendo signos vitales.
- Emision, consulta y anulacion de recetas medicas.
- Descarga de recetas en PDF con datos del paciente, medico, diagnostico y medicamentos.
- Paginas de error personalizadas para respuestas 404, 405 y 500.
- Interfaz responsive con navegacion lateral y formularios adaptables.

## Tecnologias

- Java 17 (probado con OpenJDK 24.0.1).
- Spring Boot 3.5.16.
- Spring MVC y Thymeleaf.
- Spring Data JPA e Hibernate.
- MySQL 8.0.
- Flyway para migraciones de base de datos.
- OpenPDF 2.4.0.
- Maven Wrapper con Maven 3.9.16.
- HTML5, CSS3 y JavaScript vanilla.

## Requisitos

- Windows con PowerShell 5.1 o superior.
- JDK 17 o superior. La aplicacion fue probada con OpenJDK 24.0.1.
- MySQL Server 8.0 instalado en `C:\Program Files\MySQL\MySQL Server 8.0`.
- Puerto `3307` libre para MySQL y puerto `8080` libre para la aplicacion.

## Inicio rapido

Desde la raiz del proyecto:

```powershell
.\scripts\start-all.cmd
```

El script realiza automaticamente estas tareas:

1. Detecta un JDK disponible.
2. Genera `config/local.properties` con credenciales aleatorias si no existe y la base local es nueva.
3. Inicializa la instancia local de MySQL si es la primera ejecucion.
4. Arranca MySQL en el puerto `3307`.
5. Crea la base de datos `clinica_db` y el usuario de aplicacion.
6. Compila la aplicacion si no existe el jar.
7. Arranca Spring Boot y espera a que responda en `http://localhost:8080`.

Luego abra en el navegador:

```text
http://localhost:8080
```

Para detener la aplicacion y MySQL:

```powershell
.\scripts\stop-all.cmd
```

## Ejecucion manual

Si MySQL ya esta iniciado y el jar fue compilado:

```powershell
$env:JAVA_HOME = "C:\Users\User\.jdks\openjdk-24.0.1"
.\mvnw.cmd -DskipTests package
java -jar target\servicios-pacientes-1.0.0.jar
```

Antes de ejecutar el jar debe existir `config/local.properties`. Puede generarlo con `.\scripts\start-all.cmd` o copiarlo desde `config/local.properties.example` y completar los valores.

Tambien puede iniciar solo MySQL con:

```powershell
.\scripts\mysql-start.ps1
```

## Configuracion

La configuracion publica esta en `src/main/resources/application.properties` y no contiene credenciales.

Las credenciales locales se guardan en `config/local.properties`, un archivo ignorado por Git:

```properties
spring.datasource.username=clinica_user
spring.datasource.password=...
db.root.password=...
```

Si el archivo no existe y la base local es nueva, `start-all.cmd` lo genera con contrasenas aleatorias. Para configurarlo manualmente, copie `config/local.properties.example` como `config/local.properties` y complete los valores. No suba ese archivo al repositorio.

Datos de la instancia local:

| Elemento | Valor |
| --- | --- |
| Host | `127.0.0.1` |
| Puerto MySQL | `3307` |
| Base de datos | `clinica_db` |
| Usuario de aplicacion | `clinica_user` |
| Usuario root local | `root` |
| Credenciales | Definidas en `config/local.properties` |

## Estructura del proyecto

```text
src/main/java/com/clinica/servicios/
  domain/           Entidades JPA y enumeraciones
  repository/       Repositorios Spring Data JPA
  service/          Reglas de negocio y generacion de PDF
  web/controller/   Controladores MVC y manejo de errores

src/main/resources/
  templates/        Vistas Thymeleaf
  static/           CSS y JavaScript
  db/migration/     Migraciones Flyway

scripts/            Arranque y parada de MySQL y de la aplicacion
config/             Configuracion local ignorada por Git
docs/               Documentacion tecnica detallada
```

## Base de datos

El esquema se crea automaticamente con Flyway:

- `V1__schema.sql`: tablas, indices, claves foraneas y borrado en cascada de los items de receta.
- `V2__seed_data.sql`: datos de ejemplo de pacientes, medicos, citas, historiales y recetas.

Tablas principales:

- `pacientes`
- `medicos`
- `citas`
- `historiales`
- `recetas`
- `receta_items`

## Recetas en PDF

La descarga esta disponible en:

```text
GET /recetas/{id}/pdf
```

El PDF incluye encabezado de la clinica, codigo de receta, fecha de emision, datos del paciente, datos del medico, diagnostico, tabla de medicamentos, indicaciones generales y espacio para firma y sello.

## Documentacion tecnica

La documentacion detallada de arquitectura, modelo de datos, rutas, reglas de negocio, configuracion y mantenimiento esta en:

```text
docs/DOCUMENTACION.md
```

## Solucion de problemas

- Si MySQL no arranca, revise los archivos de `mysql-logs/`.
- Si la aplicacion no responde, revise `.logs/app.out.log` y `.logs/app.err.log`.
- Si el puerto `3307` o `8080` esta ocupado, detenga el proceso correspondiente o cambie la configuracion.
- Si el directorio de datos de MySQL se corrompe, detenga MySQL y respalde/elimine `mysql-data/` para volver a inicializar la instancia local.
- No use nombres de directorio de datos que empiecen con punto en MySQL 8.0 en Windows; por eso el proyecto usa `mysql-data/` y no `.mysql-data/`.
- Si aparece un error de acceso a la base de datos, revise que `config/local.properties` exista y tenga las credenciales correctas. Si ya existe `mysql-data/` pero falta ese archivo, creelo manualmente a partir de `config/local.properties.example`.
