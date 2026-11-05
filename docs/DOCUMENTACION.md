# Documentacion del proyecto

## Sistema de Gestion de Servicios - Clinica San Rafael

Esta guia explica que hace el sistema, como esta organizado y como se puede presentar o mantener. Esta escrita para alguien que esta aprendiendo Spring Boot.

---

## 1. Que hace el sistema

La aplicacion permite administrar los servicios basicos de una clinica desde el navegador:

- Registrar, editar, consultar y eliminar pacientes.
- Registrar, editar, consultar y eliminar medicos.
- Programar, consultar, editar y cancelar citas.
- Registrar el historial clinico de una atencion.
- Emitir, consultar y anular recetas medicas.
- Imprimir una receta usando el navegador.

La aplicacion no tiene inicio de sesion ni roles. Esta pensada para usarse de forma local.

---

## 2. Que se decidio simplificar

Para que el proyecto sea facil de entender se quitaron las funciones que agregaban complejidad innecesaria:

- No hay dashboard con indicadores.
- No hay busquedas ni filtros avanzados.
- No hay generacion de PDF desde el servidor.
- No hay paginas de error personalizadas.
- No hay archivo JavaScript propio.
- Los listados muestran todos los registros.
- Las recetas siempre muestran tres filas de medicamentos en el formulario.

Esto no significa que el sistema tenga errores intencionales. Significa que el alcance es mas pequeno y que cada parte se puede explicar con claridad.

---

## 3. Tecnologias

| Componente | Tecnologia |
| --- | --- |
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.5.16 |
| Web | Spring MVC y Tomcat embebido |
| Vistas | Thymeleaf |
| Persistencia | Spring Data JPA e Hibernate |
| Base de datos | MySQL 8.0 |
| Migraciones | Flyway |
| Build | Maven Wrapper con Maven 3.9.16 |
| Frontend | HTML5 y CSS3 |

El proyecto se compilo y ejecuto con OpenJDK 24.0.1, manteniendo compatibilidad con Java 17.

---

## 4. Arquitectura general

El sistema usa el patron MVC por capas:

```text
Navegador
   |
   v
Controlador (web/controller)
   |
   v
Servicio (service)
   |
   v
Repositorio (repository)
   |
   v
MySQL
```

Ademas, el controlador entrega datos a una plantilla Thymeleaf, que genera el HTML que ve el usuario.

### Responsabilidades de cada capa

| Capa | Responsabilidad |
| --- | --- |
| `domain` | Representa las tablas de la base de datos con clases Java. |
| `repository` | Consulta y guarda datos usando Spring Data JPA. |
| `service` | Contiene las reglas del negocio y lanza errores cuando algo no es valido. |
| `web/controller` | Recibe las peticiones del navegador y decide que pantalla mostrar. |
| `templates` | Contiene el HTML de las pantallas con Thymeleaf. |
| `static/css` | Contiene los estilos visuales. |

---

## 5. Estructura de carpetas

```text
src/main/java/com/clinica/servicios/
  ServiciosApplication.java      Inicia la aplicacion
  domain/                        Entidades y enumeraciones
  repository/                    Repositorios JPA
  service/                       Reglas de negocio y excepciones
  web/controller/                Controladores MVC

src/main/resources/
  application.properties         Configuracion general
  templates/                     Vistas Thymeleaf
    fragments/layout.html        Plantilla base con menu y mensajes
    home.html                    Pantalla de inicio
    pacientes/                   Listado, formulario y detalle
    medicos/                     Listado y formulario
    citas/                       Listado, formulario y detalle
    historial/                   Listado, formulario y detalle
    recetas/                     Listado, formulario y detalle
  static/css/styles.css          Estilos de toda la aplicacion
  db/migration/                  Scripts de Flyway
```

---

## 6. Modelo de datos

### Paciente

Guarda los datos personales del paciente: documento, nombres, apellidos, fecha de nacimiento, sexo, telefono, correo y direccion.

### Medico

Guarda los datos del medico: nombres, apellidos, especialidad, colegiatura, telefono, correo y estado activo.

### Cita

Relaciona un paciente con un medico. Tiene fecha y hora, motivo y estado:

- `PROGRAMADA`: la cita esta pendiente.
- `ATENDIDA`: la cita ya fue atendida.
- `CANCELADA`: la cita fue cancelada.

### HistorialMedico

Guarda la atencion de una cita. Incluye motivo de consulta, diagnostico, tratamiento, observaciones y signos vitales. Una cita puede tener un historial.

### Receta

Se crea a partir de un historial ya registrado. Tiene paciente, medico, historial, fecha de emision y estado:

- `EMITIDA`: la receta esta vigente.
- `ANULADA`: la receta ya no se debe usar.

### RecetaItem

Es cada medicamento dentro de una receta: nombre, presentacion, dosis, frecuencia, duracion, cantidad e indicaciones.

### Relaciones principales

```text
Paciente 1 --- N Cita N --- 1 Medico
Cita     1 --- 1 HistorialMedico
HistorialMedico 1 --- N Receta
Receta   1 --- N RecetaItem
```

---

## 7. Rutas de la aplicacion

### Inicio

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| GET | `/` | Pantalla de inicio con accesos a los modulos |

### Pacientes

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| GET | `/pacientes` | Lista de pacientes |
| GET | `/pacientes/nuevo` | Formulario de registro |
| POST | `/pacientes` | Guarda un paciente nuevo |
| GET | `/pacientes/{id}` | Ficha del paciente |
| GET | `/pacientes/{id}/editar` | Formulario de edicion |
| POST | `/pacientes/{id}` | Actualiza el paciente |
| POST | `/pacientes/{id}/eliminar` | Elimina el paciente |

### Medicos

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| GET | `/medicos` | Lista de medicos |
| GET | `/medicos/nuevo` | Formulario de registro |
| POST | `/medicos` | Guarda un medico nuevo |
| GET | `/medicos/{id}/editar` | Formulario de edicion |
| POST | `/medicos/{id}` | Actualiza el medico |
| POST | `/medicos/{id}/eliminar` | Elimina el medico |

### Citas

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| GET | `/citas` | Lista de citas |
| GET | `/citas/nueva` | Formulario de registro |
| POST | `/citas` | Guarda una cita nueva |
| GET | `/citas/{id}` | Detalle de la cita |
| GET | `/citas/{id}/editar` | Formulario de edicion |
| POST | `/citas/{id}` | Actualiza la cita |
| POST | `/citas/{id}/cancelar` | Cambia el estado a cancelada |

### Historial clinico

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| GET | `/historial` | Lista de historiales |
| GET | `/historial/nuevo` | Formulario de registro |
| POST | `/historial` | Guarda un historial nuevo |
| GET | `/historial/{id}` | Detalle del historial |
| GET | `/historial/{id}/editar` | Formulario de edicion |
| POST | `/historial/{id}` | Actualiza el historial |

### Recetas

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| GET | `/recetas` | Lista de recetas |
| GET | `/recetas/nueva` | Formulario de emision |
| POST | `/recetas` | Emite una receta nueva |
| GET | `/recetas/{id}` | Detalle e impresion de la receta |
| POST | `/recetas/{id}/anular` | Cambia el estado a anulada |

---

## 8. Flujo de una peticion

Ejemplo: el usuario abre la lista de pacientes.

1. El navegador pide `GET /pacientes`.
2. `PacienteController.listar()` recibe la peticion.
3. El controlador llama a `pacienteService.listar()`.
4. El servicio llama a `pacienteRepository.findAll()`.
5. El repositorio consulta la tabla `pacientes` en MySQL.
6. El controlador guarda la lista en el modelo.
7. Thymeleaf muestra `pacientes/list.html` con los datos.

Ejemplo: el usuario guarda un paciente nuevo.

1. El navegador envia `POST /pacientes` con los datos del formulario.
2. El controlador valida el objeto con `@Valid`.
3. Si hay errores, vuelve a mostrar el formulario.
4. Si todo esta correcto, el servicio guarda y se redirige a `/pacientes`.
5. Se muestra un mensaje de exito.

---

## 9. Reglas de negocio

### Pacientes

- El documento es obligatorio y no se puede repetir.
- Los nombres y apellidos son obligatorios.
- No se puede eliminar un paciente si tiene citas, historiales o recetas.

### Medicos

- La colegiatura es obligatoria y no se puede repetir.
- No se puede eliminar un medico si tiene citas, historiales o recetas.

### Citas

- El paciente y el medico son obligatorios.
- La fecha y hora son obligatorias.
- No se puede programar una cita en el mismo horario para el mismo medico.
- No se puede programar una cita en el mismo horario para el mismo paciente.
- Una cita atendida no se puede cancelar.

### Historial clinico

- Debe estar asociado a un paciente y a un medico.
- Si se crea desde una cita, se toman los datos de esa cita.
- No se puede registrar dos veces el historial de la misma cita.
- Al guardar el historial, la cita pasa a estado `ATENDIDA`.

### Recetas

- Debe estar asociada a un historial existente.
- La receta toma el paciente y el medico del historial.
- Debe tener al menos un medicamento con nombre.
- Una receta anulada no se puede volver a anular.

---

## 10. Validaciones y mensajes

Las validaciones de campos usan anotaciones de Jakarta Validation:

- `@NotBlank`: el campo no puede estar vacio.
- `@NotNull`: el campo no puede ser nulo.
- `@Size`: limita la cantidad de caracteres.
- `@Email`: valida el formato del correo.

Los formularios muestran los errores debajo de cada campo.

Cuando una regla del negocio falla, el servicio lanza `BusinessException`. El controlador captura esa excepcion y muestra el mensaje en el formulario o como aviso.

Cuando se busca un registro que no existe, se lanza `ResourceNotFoundException`, que responde con estado HTTP 404.

---

## 11. Base de datos y Flyway

Flyway ejecuta los scripts de `src/main/resources/db/migration` al iniciar la aplicacion.

| Script | Contenido |
| --- | --- |
| `V1__schema.sql` | Crea las tablas, indices y relaciones |
| `V2__seed_data.sql` | Carga pacientes, medicos, citas, historiales y recetas de ejemplo |

Las tablas principales son:

- `pacientes`
- `medicos`
- `citas`
- `historiales`
- `recetas`
- `receta_items`

La aplicacion usa `spring.jpa.hibernate.ddl-auto=none`, por lo que Hibernate no crea ni modifica tablas. Flyway es el unico encargado de la estructura de la base de datos.

---

## 12. Configuracion local

La configuracion publica esta en `src/main/resources/application.properties`:

```properties
server.port=8080
spring.datasource.url=jdbc:mysql://127.0.0.1:3307/clinica_db
spring.config.import=optional:file:./config/local.properties
```

Las credenciales no se suben a Git. Se guardan en `config/local.properties`:

```properties
spring.datasource.username=clinica_user
spring.datasource.password=...
db.root.password=...
```

El script `start-all.cmd` crea ese archivo automaticamente si no existe.

---

## 13. Como ejecutar el proyecto

### Opcion automatica

```powershell
.\scripts\start-all.cmd
```

Abre `http://localhost:8080`.

Para detener todo:

```powershell
.\scripts\stop-all.cmd
```

### Opcion manual

```powershell
$env:JAVA_HOME = "C:\Users\User\.jdks\openjdk-24.0.1"
.\mvnw.cmd -DskipTests package
java -jar target\servicios-pacientes-1.0.0.jar
```

---

## 14. Como explicar el proyecto en una presentacion

Una forma sencilla de presentarlo es seguir este orden:

1. **Problema:** una clinica necesita organizar pacientes, medicos, citas, historiales y recetas.
2. **Solucion:** una aplicacion web local con Spring Boot y MySQL.
3. **Arquitectura:** el navegador llama a un controlador, el controlador usa un servicio, el servicio usa un repositorio y el repositorio guarda en MySQL.
4. **Pantallas:** inicio, pacientes, medicos, citas, historial y recetas.
5. **Validaciones:** los formularios revisan campos obligatorios y el servicio revisa reglas como documentos repetidos o citas cruzadas.
6. **Base de datos:** Flyway crea las tablas y carga datos de ejemplo.
7. **Impresion:** la receta se muestra en HTML y se imprime con el navegador.
8. **Alcance:** es una version basica y educativa, sin login ni funciones avanzadas.

---

## 15. Glosario

| Termino | Significado |
| --- | --- |
| Entidad | Clase Java que representa una tabla. |
| Repositorio | Interfaz que permite consultar y guardar entidades. |
| Servicio | Clase con las reglas del negocio. |
| Controlador | Clase que atiende las rutas web. |
| Modelo | Datos que el controlador envia a la plantilla. |
| Plantilla | Archivo HTML con Thymeleaf. |
| Migracion | Script SQL que Flyway ejecuta para actualizar la base. |
| DTO | Objeto usado para transportar datos; en este proyecto no se usan. |

---

## 16. Mantenimiento

- Para cambiar un texto de la interfaz, revise las plantillas en `src/main/resources/templates`.
- Para cambiar colores o tamanos, revise `src/main/resources/static/css/styles.css`.
- Para cambiar una regla del negocio, revise la clase correspondiente en `service`.
- Para agregar una ruta, agregue un metodo en el controlador y una pantalla en `templates`.
- Para cambiar la base de datos, cree una nueva migracion `V3__...sql`; no modifique una migracion que ya se ejecuto.
- Para agregar un campo, actualice la entidad, la migracion, el formulario y las validaciones.

---

## 17. Problemas frecuentes

| Problema | Posible solucion |
| --- | --- |
| MySQL no arranca | Revise `mysql-logs/`. |
| La aplicacion no responde | Revise `.logs/app.out.log` y `.logs/app.err.log`. |
| Error de acceso a MySQL | Revise `config/local.properties`. |
| El puerto 8080 esta ocupado | Detenga el proceso que lo usa o cambie `server.port`. |
| El puerto 3307 esta ocupado | Detenga el proceso que lo usa. |
| Flyway reporta una migracion fallida | Revise el script y el estado de la tabla `flyway_schema_history`. |

---

## 18. Resumen

El proyecto es una aplicacion MVC sencilla con cinco modulos. Cada modulo tiene entidad, repositorio, servicio, controlador y plantillas. La base de datos se crea con Flyway y la interfaz usa Thymeleaf y CSS. La version actual prioriza claridad sobre funciones avanzadas.
