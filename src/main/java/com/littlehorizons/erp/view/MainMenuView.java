package com.littlehorizons.erp.view;

import com.littlehorizons.erp.util.ConsoleFormatter;
import com.littlehorizons.erp.util.InputValidator;

/**
 * Main application console view.
 */
public class MainMenuView {

    public void displayWelcomeBanner(boolean dbConnected) {
        System.out.println("╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                COLEGIO INTERNACIONAL LITTLE HORIZONS             ║");
        System.out.println("║             ERP EDUCATIVO - SISTEMA DE GESTIÓN INTEGRAL          ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════╝");
        if (dbConnected) {
            System.out.println("  [Estado de Conexión]: MySQL 8.0 Conectado exitosamente.");
        } else {
            System.out.println("  [AVISO]: Base de datos no disponible localmente en este momento.");
            System.out.println("  Para inicializar la BD, ejecute el script 'docs/database.sql' en MySQL 8.0.");
        }
        System.out.println();
    }

    public int displayMainMenu() {
        System.out.println("╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                         MENÚ PRINCIPAL                           ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════╝");
        System.out.println("  1. Gestión de Estudiantes y Acudientes");
        System.out.println("  2. Gestión Académica (Notas, Promedios y Asistencia)");
        System.out.println("  3. Directorio de Profesores y Carga Académica");
        System.out.println("  4. Actividades Extracurriculares (Clubes y Eventos)");
        System.out.println("  5. Gestión Financiera y Facturación Electrónica (TXT)");
        System.out.println("  6. Gestión de Recursos (Biblioteca y Laboratorios)");
        System.out.println("  7. Reportes y Estadísticas Generales");
        System.out.println("  8. Salir del Sistema");
        System.out.println();
        return InputValidator.readInt("Seleccione una opción (1-8): ", 1, 8);
    }
}
