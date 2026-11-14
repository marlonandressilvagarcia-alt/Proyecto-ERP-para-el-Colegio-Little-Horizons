# Sistema ERP Educativo - Colegio Internacional "Little Horizons"

[![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Apache%20Maven-3.9.9-blue.svg)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-00758F.svg)](https://www.mysql.com/)
[![Architecture](https://img.shields.io/badge/Architecture-MVC%20%2B%20DAO%20%2B%20Service-brightgreen.svg)]()
[![Invoicing](https://img.shields.io/badge/Facturaci%C3%B3n-Electr%C3%B3nica%20TXT-success.svg)]()

---

## 1. Título del Proyecto
**Sistema de Planificación de Recursos Empresariales (ERP) para el Colegio Internacional "Little Horizons"**

---

## 2. Descripción

### Resumen del propósito del sistema
El **ERP Colegio Little Horizons** es una solución informática integral diseñada para centralizar, automatizar y optimizar la administración académica, operativa y financiera de una institución educativa en crecimiento (desde preescolar hasta grado 11°). Diseñado bajo estándares de ingeniería de software robustos, este sistema sustituye los registros manuales dispersos en hojas de cálculo y libros físicos por una base de datos relacional normalizada y una interfaz de consola profesional construida bajo el patrón Modelo-Vista-Controlador (MVC).

### Problemas que resuelve
1. **Pérdida y dispersión de información académica:** Centraliza las calificaciones y registros históricos bajo restricciones de integridad referencial, eliminando inconsistencias y pérdida de notas.
2. **Desorganización extracurricular:** Administra clubes escolares y eventos institucionales con validación en tiempo real de cupos máximos y registro de roles y podios competitivos.
3. **Ineficiencia y riesgos en facturación:** Automatiza la emisión de **Facturas Electrónicas en formato TXT**, calculando el Código Único de Factura Electrónica (CUFE) simulado mediante SHA-256, generando representaciones gráficas de códigos QR DIAN y detallando conceptos exentos de IVA conforme a la normatividad colombiana (Art. 476 E.T.).
4. **Pérdida de inventario y recursos:** Implementa un sistema de control de préstamos diferenciado para estudiantes y profesores, gestionando préstamos de biblioteca, laboratorio y cómputo con detección automática de préstamos vencidos.
5. **Falta de trazabilidad en asistencia:** Permite el seguimiento diario de asistencias y genera alertas tempranas ante ausentismo escolar reiterado (más de 3 inasistencias injustificadas).

### Funcionalidades principales
- **Gestión de Estudiantes y Acudientes:** Registro completo, filiación familiar (hermanos con acudiente común), designación de responsable financiero y matrículas anuales por grado.
- **Gestión Académica:** Registro de calificaciones colombianas (rango 0.0 a 5.0 con precisión decimal), cálculo automatizado de promedios ponderados y GPA mediante **Java Stream API y Lambdas**, e historial de notas.
- **Directorio de Profesores y Carga Docente:** Catálogo de profesores con especialidades, asignaciones de materias por grupo/año lectivo.
- **Actividades Extracurriculares:** Gestión de clubes con validación de capacidad máxima, calendario de eventos y premiación por posiciones.
- **Facturación y Finanzas:** Registro de pagos con numeración de recibo consecutiva, detección de cartera morosa y **generación de facturas electrónicas en archivos de texto plano (.txt)** con CUFE y QR simulado.
- **Gestión de Recursos (Biblioteca y Laboratorio):** Inventario con estados dinámicos (*Disponible*, *Prestado*, *En Mantenimiento*) y control de devoluciones.
- **Reportes y Estadísticas:** Cuadros de honor con ordenamiento funcional, métricas de recaudo por método de pago y auditoría de vencimientos.

---

## 3. Autores
- **Equipo de Desarrollo Little Horizons ERP**
- **Institución:** Colegio Internacional Little Horizons S.A.S.
- **Correo Electrónico de Contacto:** `desarrollo@littlehorizons.edu.co` / `contacto@littlehorizons.edu.co`
- **Ubicación:** Bogotá D.C., Colombia

---

## 4. Tecnologías Utilizadas
- **Lenguaje de Programación:** Java Standard Edition (SE) 21 LTS
- **Herramienta de Construcción y Gestión de Dependencias:** Apache Maven 3.9+
- **Motor de Base de Datos:** MySQL 8.0 o superior (compatible con MySQL 8.4 LTS)
- **Conector JDBC:** MySQL Connector/J 8.4.0 (`com.mysql:mysql-connector-j`)
- **Framework de Pruebas Unitarias:** JUnit 5 Jupiter 5.10.2 (`org.junit.jupiter:junit-jupiter`)
- **IDE Recomendado:** IntelliJ IDEA (Community o Ultimate), Eclipse IDE, o Visual Studio Code

---

## 5. Requerimientos Previos
Para desplegar y ejecutar este proyecto se requiere contar con el siguiente entorno:
1. **Java Development Kit (JDK):** Versión 21 LTS instalada y configurada en la variable de entorno `JAVA_HOME`.
2. **Apache Maven:** Versión 3.8.0 o superior (o utilizar el comando Maven integrado en su IDE).
3. **Servidor de Base de Datos MySQL:** **MySQL 8.0 o superior (REQUERIDO)**.
   > **Nota Importante:** El script de base de datos requiere MySQL 8.0+ para la correcta interpretación de los `CHECK constraints` (como la restricción `CHECK (valor_nota >= 0.0 AND valor_nota <= 5.0)`), el cotejamiento `utf8mb4_unicode_ci` y los índices parciales.

---

## 6. Configuración de la Base de Datos

### 6.1. Creación de la base de datos
Abra su cliente de MySQL preferido (MySQL Workbench, DBeaver, o línea de comandos) y ejecute el script completo ubicado en la carpeta `docs/database.sql`:

```bash
mysql -u root -p < docs/database.sql
```

El script creará automáticamente la base de datos `erp_little_horizons`, todas las 22 tablas normalizadas con integridad referencial y poblará la base de datos con **más de 30 registros coherentes y realistas por cada tabla principal**.

### 6.2. Configuración de credenciales
El sistema administra la conexión a través del archivo de propiedades `src/main/resources/db.properties`. Puede editar los parámetros de conexión según su servidor:

```properties
# Configuración de Conexión a Base de Datos
db.url=jdbc:mysql://localhost:3306/erp_little_horizons?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota&characterEncoding=UTF-8
db.user=root
db.password=
db.driver=com.mysql.cj.jdbc.Driver

# Datos Institucionales para Facturación Electrónica DIAN
school.name=Colegio Internacional Little Horizons S.A.S.
school.nit=901.458.789-3
school.address=Calle 127 # 15-45, Bogotá, Colombia
school.phone=(+57) 601 745 8900
school.email=contacto@littlehorizons.edu.co
school.dian.resolution=18760000001
```

### 6.3. Instalación del driver JDBC
El driver oficial `mysql-connector-j` versión 8.4.0 se encuentra declarado en el archivo `pom.xml`, por lo cual Maven se encarga de su resolución y vinculación al classpath de manera automática durante la compilación.

---

## 7. Instalación y Ejecución

### 7.1. Clonar el repositorio
```bash
git clone https://github.com/usuario/Proyecto-ERP-para-el-Colegio-Little-Horizons-.git
cd Proyecto-ERP-para-el-Colegio-Little-Horizons-
```

### 7.2. Abrir el proyecto en el IDE
- **En IntelliJ IDEA:** Seleccionar `File` > `Open...` y escoger la carpeta raíz del proyecto. El IDE reconocerá el `pom.xml` como proyecto Maven.
- **En Eclipse:** Seleccionar `File` > `Import...` > `Existing Maven Projects`.

### 7.3. Compilar y ejecutar pruebas unitarias
Desde la terminal en la raíz del proyecto, ejecute:
```bash
mvn clean test
```
Esto compilará todas las clases y correrá la suite de pruebas unitarias verificando la generación del CUFE, el formateo de facturas TXT y los cálculos de promedios con Streams.

### 7.4. Ejecutar la aplicación
Para iniciar el sistema de consola interactivo:
```bash
mvn compile exec:java -Dexec.mainClass="com.littlehorizons.erp.Main"
```
O directamente ejecutando la clase `com.littlehorizons.erp.Main` con botón derecho > *Run 'Main'* en su IDE.

---

## 8. Estructura del Proyecto

El código fuente sigue estrictamente la arquitectura por capas **Modelo-Vista-Controlador (MVC)** complementada con los patrones **Data Access Object (DAO)** y **Service Layer**, garantizando alta cohesión y bajo acoplamiento. Todo el código fuente (nombres de clases, interfaces, métodos, variables y documentación interna) está escrito completamente en **inglés**.

```
Proyecto-ERP-para-el-Colegio-Little-Horizons-/
├── pom.xml                                   # Configuración de dependencias Maven
├── README.md                                 # Documentación técnica completa
├── error_log.txt                             # Registro de logs del sistema
├── docs/
│   └── database.sql                          # DDL completo y DML con >30 registros
├── invoices/                                 # Directorio donde se generan las facturas TXT
├── logs/
│   └── error_log.txt                         # Bitácora de errores y eventos
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/littlehorizons/erp/
    │   │       ├── Main.java                 # Punto de entrada de la aplicación
    │   │       ├── config/
    │   │       │   └── DatabaseConfig.java   # Patrón Singleton para conexión JDBC
    │   │       ├── controller/               # Capa Controladora (Coordinación MVC)
    │   │       │   ├── AcademicController.java
    │   │       │   ├── ExtracurricularController.java
    │   │       │   ├── FinancialController.java
    │   │       │   ├── MainController.java
    │   │       │   ├── ReportController.java
    │   │       │   ├── ResourceController.java
    │   │       │   ├── StudentController.java
    │   │       │   └── TeacherController.java
    │   │       ├── dao/                      # Interfaces de Acceso a Datos
    │   │       │   ├── AcademicRecordDAO.java
    │   │       │   ├── AttendanceDAO.java
    │   │       │   ├── ClubDAO.java
    │   │       │   ├── EventDAO.java
    │   │       │   ├── GuardianDAO.java
    │   │       │   ├── InvoiceDAO.java
    │   │       │   ├── PaymentDAO.java
    │   │       │   ├── ResourceDAO.java
    │   │       │   ├── ResourceLoanDAO.java
    │   │       │   ├── StudentDAO.java
    │   │       │   ├── SubjectDAO.java
    │   │       │   ├── TeacherDAO.java
    │   │       │   └── impl/                 # Implementaciones JDBC con PreparedStatement
    │   │       ├── model/                    # Entidades del Dominio (POJOs)
    │   │       │   ├── AcademicRecord.java
    │   │       │   ├── Attendance.java
    │   │       │   ├── Club.java
    │   │       │   ├── ClubMembership.java
    │   │       │   ├── Enrollment.java
    │   │       │   ├── EventParticipant.java
    │   │       │   ├── Grade.java
    │   │       │   ├── Guardian.java
    │   │       │   ├── Invoice.java
    │   │       │   ├── Payment.java
    │   │       │   ├── PaymentConcept.java
    │   │       │   ├── ResourceCategory.java
    │   │       │   ├── SchoolEvent.java
    │   │       │   ├── SchoolResource.java
    │   │       │   ├── Student.java
    │   │       │   ├── StudentGuardian.java
    │   │       │   ├── StudentLoan.java
    │   │       │   ├── Subject.java
    │   │       │   ├── SubjectEnrollment.java
    │   │       │   ├── Teacher.java
    │   │       │   ├── TeacherAssignment.java
    │   │       │   └── TeacherLoan.java
    │   │       ├── service/                  # Capa de Lógica de Negocio y Streams
    │   │       │   ├── AcademicService.java
    │   │       │   ├── ExtracurricularService.java
    │   │       │   ├── FinancialService.java
    │   │       │   ├── ReportService.java
    │   │       │   ├── ResourceService.java
    │   │       │   └── StudentService.java
    │   │       ├── util/                     # Utilidades Transversales
    │   │       │   ├── ConsoleFormatter.java
    │   │       │   ├── CufeGenerator.java
    │   │       │   ├── InputValidator.java
    │   │       │   ├── LoggerUtil.java
    │   │       │   └── TextInvoiceGenerator.java # Generador de Facturas TXT
    │   │       └── view/                     # Capa de Vista de Consola
    │   │           ├── AcademicView.java
    │   │           ├── ExtracurricularView.java
    │   │           ├── FinancialView.java
    │   │           ├── MainMenuView.java
    │   │           ├── ReportView.java
    │   │           ├── ResourceView.java
    │   │           ├── StudentView.java
    │   │           └── TeacherView.java
    │   └── resources/
    │       └── db.properties                 # Archivo de configuración externa
    └── test/
        └── java/
            └── com/littlehorizons/erp/       # Pruebas Unitarias con JUnit 5
                ├── AcademicCalculationsTest.java
                ├── CufeGeneratorTest.java
                └── TextInvoiceGeneratorTest.java
```

---

## 9. Principios y Patrones de Diseño Aplicados

### 9.1. Principios SOLID
1. **Single Responsibility Principle (SRP):**  
   Cada clase tiene una única responsabilidad bien definida:
   - `TextInvoiceGenerator`: Se enfoca exclusivamente en la estructuración visual y escritura del archivo `.txt` de la factura electrónica.
   - `CufeGenerator`: Exclusivamente calcula el hash SHA-256 de conformidad técnica.
   - `InputValidator`: Centraliza la validación de tipos, rangos y fechas de entrada por teclado.
   - `DatabaseConfig`: Administra el ciclo de vida y la configuración de la conexión JDBC.

2. **Open/Closed Principle (OCP):**  
   Las interfaces DAO (`StudentDAO`, `PaymentDAO`, `ResourceLoanDAO`) definen contratos abiertos a extensión mediante nuevas implementaciones (por ejemplo, `PaymentDAOMongoImpl` o `PaymentDAOHibernateImpl`) sin necesidad de modificar el código de los servicios existentes.

3. **Liskov Substitution Principle (LSP):**  
   Todas las implementaciones de DAO pueden ser sustituidas en los constructores de los servicios sin alterar el comportamiento esperado del sistema ni romper invariantes.

4. **Interface Segregation Principle (ISP):**  
   Se diseñaron interfaces específicas y granulares (`AttendanceDAO`, `ClubDAO`, `EventDAO`) en lugar de una interfaz de repositorio monolítica, obligando a los módulos a conocer únicamente los métodos que les competen.

5. **Dependency Inversion Principle (DIP):**  
   La capa de servicios (`StudentService`, `AcademicService`) depende de abstracciones (interfaces DAO) y no directamente de implementaciones concretas de base de datos, facilitando pruebas unitarias mediante *mocking*.

### 9.2. Patrones de Diseño
- **Singleton:** Aplicado en `DatabaseConfig` para asegurar una única instancia de configuración y gestión centralizada del driver JDBC y credenciales durante toda la ejecución.
- **Data Access Object (DAO):** Desacopla la lógica de negocio de las sentencias SQL relacionales. Todas las operaciones de inserción, actualización, eliminación y consulta se encapsulan en las clases `*DAOImpl`.
- **Service Layer (Fachada de Negocio):** Coordina transacciones y valida reglas del dominio (como no permitir que un estudiante ingrese a un club si el cupo está agotado o verificar que una nota esté entre 0.0 y 5.0).
- **Try-With-Resources (Gestión Eficiente de Recursos):** Aplicado en la totalidad de las consultas JDBC en los DAOs (`Connection`, `PreparedStatement`, `ResultSet`), garantizando el cierre automático de descriptores y evitando fugas de memoria o *connection leaks*.
- **Functional Programming & Stream API:** Uso de expresiones Lambda y operadores funcionales (`.stream()`, `.mapToDouble()`, `.average()`, `.collect(Collectors.groupingBy(...))`) para el cálculo de estadísticas académicas y financieras con alto rendimiento.

---

## 10. Funcionalidades Principales

### Módulo 1: Gestión de Estudiantes y Acudientes
- Registro con validación de tipo de documento (TI, CC, CE, Pasaporte) y número único.
- Historial médico escolar (alergias, tipo de sangre, condiciones de salud).
- Relación de acudientes con identificación del acudiente principal y responsable financiero.
- Asignación de matrículas por grado académico (Preescolar a Once) y año escolar.

### Módulo 2: Gestión Académica y Asistencia
- Asignación de materias a docentes por sección (A, B, C).
- Inscripción de estudiantes en materias por período lectivo (I, II, III, IV).
- Calificaciones con rango 0.0 - 5.0 y cálculo de promedios:
  - Promedio de un estudiante en una materia específica.
  - Promedio general del estudiante en el período (GPA).
  - Promedio general del curso en la materia.
- Historial académico histórico acumulado.
- Control de asistencia diaria (Presente, Ausente, Tardanza, Excusa Médica) y reporte de ausencias críticas.

### Módulo 3: Actividades Extracurriculares
- Catálogo de 30 clubes escolares con docente supervisor y horario de reunión.
- Inscripción de estudiantes con **validación estricta de cupo máximo**.
- Registro de retiro justificado de clubes.
- Calendario de eventos institucionales clasificados por tipo (Académico, Cultural, Deportivo, Científico).
- Inscripción de participantes y registro de podios (1er Lugar, 2do Lugar, etc.).

### Módulo 4: Gestión Financiera y Facturación Electrónica (TXT)
- Catálogo de conceptos de cobro (Matrícula, Pensión, Transporte, etc.).
- Registro de pagos con consecutivo de recibo de caja (`REC-YYYY-XXXXX`).
- **Generación automática de Factura Electrónica en formato TXT:**
  - Estructura visual con cabecera institucional y datos del cliente.
  - Desglose de ítems, subtotal exento de IVA según régimen educativo colombiano y total general.
  - Cálculo de CUFE simulado con cifrado SHA-256.
  - Representación de código QR DIAN en formato ASCII.
  - Almacenamiento automático en la carpeta `invoices/factura_FAC-LH-XXXXXX.txt`.
- Consulta de pagos por estudiante o acudiente.
- Detección de estudiantes morosos.

### Módulo 5: Gestión de Recursos (Biblioteca y Laboratorios)
- Inventario codificado de libros, equipos de laboratorio, tecnología y material deportivo.
- Préstamos independientes para estudiantes y profesores.
- Validación de disponibilidad: solo permite prestar recursos en estado *Disponible*.
- Actualización automática de estado a *Prestado* al entregar y a *Disponible* al devolver.
- Identificación de préstamos vencidos que requieren restitución.

---

## 11. Manejo de Errores y Logs

### Sistema de logging implementado
El sistema cuenta con la utilidad `LoggerUtil` que implementa un canal de registro seguro para subprocesos (*thread-safe*). Registra eventos informativos (`INFO`), advertencias (`WARN`) y excepciones críticas (`ERROR`) con marca de tiempo precisa (`yyyy-MM-dd HH:mm:ss`).

### Ubicación del archivo de logs
- `logs/error_log.txt` (en el directorio de logs del sistema)
- `error_log.txt` (en la raíz del proyecto para auditoría inmediata)

### Estrategia de manejo de excepciones
- **Validación preventiva de datos:** Todos los formularios de consola controlan `NumberFormatException`, fechas inválidas o campos vacíos mediante bucles interactivos que no interrumpen la ejecución del programa.
- **SQL Injections prevenidas:** Se utiliza `PreparedStatement` con paso de parámetros tipados en el 100% de las consultas SQL.
- **Resiliencia ante fallos de conexión:** Si la base de datos no está disponible al iniciar, la aplicación lo detecta mediante `DatabaseConfig.testConnection()`, notifica amablemente al usuario en el banner de bienvenida y registra el diagnóstico en el archivo de log sin generar caídas catastróficas.

---

## 12. Demostración Visual de Factura Electrónica TXT

A continuación se muestra un ejemplo real de la factura generada automáticamente en el archivo `invoices/factura_FAC-LH-000001.txt`:

```text
================================================================================
                    COLEGIO INTERNACIONAL LITTLE HORIZONS S.A.S.
                              NIT: 901.458.789-3
              Dirección: Calle 127 # 15-45, Bogotá, Colombia
                 Tel: (+57) 601 745 8900 | Email: contacto@littlehorizons.edu.co
================================================================================
                   FACTURA ELECTRÓNICA DE VENTA (TXT)
Factura No.:       FAC-LH-000001                  Fecha Emisión: 2024-02-15
Resolución DIAN:   No. 18760000001 de Habilitación para Factura Electrónica
Prefijo Autorizado: FAC-LH  |  Rango: 000001 al 100000
--------------------------------------------------------------------------------
DATOS DEL ADQUIRIENTE (ACUDIENTE / RESPONSABLE):
Nombre:            Carlos Eduardo Gómez Trujillo
Identificación:    CC/Doc 52123401
Dirección:         Calle 134 # 19-45, Bogotá
Estudiante:        Santiago Andrés Gómez Martínez
--------------------------------------------------------------------------------
DETALLE DE CONCEPTOS FACTURADOS:
ÍTEM   DESCRIPCIÓN DEL CONCEPTO                   CANT   VR. UNIT     SUBTOTAL  
--------------------------------------------------------------------------------
01     Pensión Mensual Febrero 2024               1      $450000.00   $450000.00
--------------------------------------------------------------------------------
                  SUBTOTAL EXENTO DE IVA (ART. 476 E.T.): $  450,000.00
                                                IVA (0%): $        0.00
                                     VALOR TOTAL A PAGAR: $  450,000.00
================================================================================
INFORMACIÓN TRANSACCIONAL:
Método de Pago:    Transferencia             Recibo de Caja: REC-2024-0002
--------------------------------------------------------------------------------
CÓDIGO ÚNICO DE FACTURA ELECTRÓNICA (CUFE):
4dc90174bd6b9ce36ed031c5a8eacec8392a4d590a60d8e6b30918afae298f97
--------------------------------------------------------------------------------
REPRESENTACIÓN GRÁFICA DEL CÓDIGO QR DE VALIDACIÓN DIAN:
+------------------------------------------------------------------------------+
| [QR CODE SIMULADO - LECTURA DIAN]                                            |
| NumFac:FAC-LH-000001  | FecFac:2024-02-15 | NitFac:901.458.789-3             |
| DocAdq:52123401       | ValFac:450000.00  | ValIva:0.00                      |
| CUFE: 4dc90174bd6b9ce36ed031c5a8eacec8392a4d590a60d8e6b30918afae298f97       |
| https://catalogo-vpfe.dian.gov.co/document/searchqr?documentkey=4dc90174bd6b...|
+------------------------------------------------------------------------------+
Esta factura electrónica es un título valor simulado según las disposiciones
legales vigentes del Decreto 2242 y la Resolución 000042 de la DIAN.
Generado exitosamente por: Little Horizons ERP System (Módulo de Facturación)
================================================================================
```

---

## 13. Problemas Conocidos y Limitaciones
- **Interfaz Gráfica:** Actualmente el sistema funciona bajo terminal interactiva ANSI/CLI estándar de alto contraste.
- **Entorno de Red:** Para ejecutar el proyecto en un entorno distribuido fuera de localhost, se debe modificar la URL del host en `src/main/resources/db.properties`.

---

## 14. Futuras Mejoras
1. Implementación de una interfaz gráfica de usuario moderna con JavaFX o interfaz Web con Spring Boot.
2. Integración directa mediante API REST con el Web Service oficial de validación previa de la DIAN.
3. Generación y firma digital XML con estándar UBL 2.1.
4. Módulo de pasarela de pagos en línea (PSE, Wompi, PayU).

---

## 15. Licencia
Este proyecto ha sido desarrollado bajo fines académicos para el Colegio Internacional "Little Horizons". Todos los derechos reservados © 2024-2026.

---

## 16. Contacto
Para inquietudes, soporte técnico o solicitudes de información sobre el sistema:
- **Colegio Internacional Little Horizons S.A.S.**
- **Dirección:** Calle 127 # 15-45, Bogotá D.C., Colombia
- **PBX:** (+57) 601 745 8900
- **Correo Institucional:** `contacto@littlehorizons.edu.co`
- **Repositorio Oficial:** `https://github.com/usuario/Proyecto-ERP-para-el-Colegio-Little-Horizons-`
