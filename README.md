# Sistema de Gestion de Servicios - Clinica San Rafael

Aplicacion web basica para administrar pacientes, medicos, citas, historial clinico y recetas de una clinica. Esta hecha con Java, Spring Boot, Thymeleaf y MySQL.

El proyecto esta pensado como practica academica: mantiene las funciones principales, pero evita caracteristicas avanzadas para que el codigo sea facil de leer y explicar.

## Funcionalidades

- Inicio con accesos directos a los cinco modulos.
- Registro, edicion, consulta y eliminacion de pacientes.
- Registro, edicion, consulta y eliminacion de medicos.
- Programacion, edicion, consulta y cancelacion de citas.
- Registro y consulta del historial clinico de cada atencion.
- Emision, consulta y anulacion de recetas medicas.
- Impresion de recetas desde el navegador.
- Validaciones basicas con mensajes en espanol.
- Datos de ejemplo cargados automaticamente con Flyway.

No incluye inicio de sesion, roles, dashboard, busquedas avanzadas, filtros ni generacion de PDF.

## Tecnologias

- Java 17 (probado con OpenJDK 24.0.1).
- Spring Boot 3.5.16.
- Spring MVC y Thymeleaf.
- Spring Data JPA e Hibernate.
- MySQL 8.0.
- Flyway para crear las tablas y cargar datos de ejemplo.
- Maven Wrapper con Maven 3.9.16.
- HTML5 y CSS3.

## Requisitos

- Windows con PowerShell 5.1 o superior.
- JDK 17 o superior.
- MySQL Server 8.0 instalado en `C:\Program Files\MySQL\MySQL Server 8.0`.
- Puerto `3307` libre para MySQL.
- Puerto `8080` libre para la aplicacion.

## Inicio rapido

Desde la raiz del proyecto:

```powershell
.\scripts\start-all.cmd
```

El script realiza estas tareas:

1. Detecta un JDK disponible.
2. Genera `config/local.properties` con credenciales aleatorias si no existe.
3. Inicializa y arranca la instancia local de MySQL en el puerto `3307`.
4. Crea la base de datos `clinica_db` y el usuario de aplicacion.
5. Compila la aplicacion si no existe el jar.
6. Arranca Spring Boot y espera a que responda.

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

## Configuracion

La configuracion publica esta en `src/main/resources/application.properties` y no contiene credenciales.

Las credenciales locales se guardan en `config/local.properties`, un archivo ignorado por Git:

```properties
spring.datasource.username=clinica_user
spring.datasource.password=...
db.root.password=...
```

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
  service/          Reglas de negocio
  web/controller/   Controladores MVC

src/main/resources/
  templates/        Vistas Thymeleaf
  static/css/       Hoja de estilos
  db/migration/     Migraciones Flyway

scripts/            Arranque y parada de MySQL y de la aplicacion
config/             Configuracion local ignorada por Git
docs/               Documentacion detallada para principiantes
```

## Base de datos

El esquema se crea automaticamente con Flyway:

- `V1__schema.sql`: tablas, indices y claves foraneas.
- `V2__seed_data.sql`: datos de ejemplo.

Tablas principales:

- `pacientes`
- `medicos`
- `citas`
- `historiales`
- `recetas`
- `receta_items`

## Recetas

Las recetas se pueden consultar en pantalla y luego imprimir con el boton **Imprimir receta**. La impresion usa el navegador y una hoja de estilos especial, por lo que no se genera un archivo PDF desde el servidor.

## Documentacion

La guia detallada, pensada para explicar el proyecto paso a paso, esta en:

```text
docs/DOCUMENTACION.md
```

## Solucion de problemas

- Si MySQL no arranca, revise los archivos de `mysql-logs/`.
- Si la aplicacion no responde, revise `.logs/app.out.log` y `.logs/app.err.log`.
- Si el puerto `3307` u `8080` esta ocupado, detenga el proceso correspondiente.
- Si aparece un error de acceso a la base de datos, revise que `config/local.properties` exista y tenga las credenciales correctas.
