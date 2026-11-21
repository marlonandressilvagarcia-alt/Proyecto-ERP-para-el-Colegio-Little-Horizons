package com.littlehorizons.erp.test.vista;

import com.littlehorizons.erp.test.controlador.BuscadorEstudiantesController;
import com.littlehorizons.erp.test.modelo.EstudianteDTO;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Vista de consola para el Buscador Avanzado de Estudiantes.
 * Maneja la interacción por menú, validaciones de entrada y presentación de resultados.
 */
public class BuscadorEstudiantesView {

    private static final String LINEA_DOBLE = "===============================================================================";
    private static final String LINEA_SIMPLE = "-------------------------------------------------------------------------------";

    private final BuscadorEstudiantesController controller;
    private final Scanner scanner;

    public BuscadorEstudiantesView() {
        this.controller = new BuscadorEstudiantesController();
        this.scanner = new Scanner(System.in);
    }

    public BuscadorEstudiantesView(BuscadorEstudiantesController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    /**
     * Inicia el ciclo principal del menú del buscador avanzado.
     */
    public void iniciar() {
        boolean salir = false;
        while (!salir) {
            mostrarMenuPrincipal();
            System.out.print("Seleccione una opción: ");
            String entrada = scanner.nextLine().trim();

            switch (entrada) {
                case "1":
                    procesarFiltroPorGrado();
                    break;
                case "2":
                case "3":
                case "4":
                case "5":
                    mostrarMensajeNoImplementado();
                    break;
                case "0":
                    mostrarMensajeSalida();
                    salir = true;
                    break;
                default:
                    System.out.println("\nOpción no válida. Intente nuevamente.");
                    pausar("Presione Enter para continuar...");
                    break;
            }
        }
    }

    /**
     * Muestra el encabezado y las opciones del menú principal del prototipo.
     */
    private void mostrarMenuPrincipal() {
        System.out.println(LINEA_DOBLE);
        System.out.println("            BUSCADOR AVANZADO DE ESTUDIANTES - PROTOTIPO");
        System.out.println(LINEA_DOBLE);
        System.out.println();
        System.out.println("Seleccione el filtro que desea aplicar:");
        System.out.println();
        System.out.println("1. Filtrar por grado académico [FUNCIONAL]");
        System.out.println("2. Filtrar por rango de edad [NO IMPLEMENTADO]");
        System.out.println("3. Filtrar por género [NO IMPLEMENTADO]");
        System.out.println("4. Filtrar por estado de matrícula [NO IMPLEMENTADO]");
        System.out.println("5. Filtrar por rango de promedio académico [NO IMPLEMENTADO]");
        System.out.println("0. Salir");
        System.out.println();
    }

    /**
     * Mensaje estándar para los filtros no implementados.
     */
    private void mostrarMensajeNoImplementado() {
        System.out.println();
        System.out.println("Esta funcionalidad aún no ha sido implementada.");
        System.out.println("Será agregada en futuras versiones del sistema.");
        System.out.println();
        pausar("Presione Enter para continuar...");
    }

    /**
     * Mensaje de despedida al salir del buscador.
     */
    private void mostrarMensajeSalida() {
        System.out.println();
        System.out.println("Saliendo del buscador...");
        System.out.println("Gracias por usar el sistema.");
    }

    /**
     * Gestiona el submenú y la selección del grado académico con sus validaciones.
     */
    private void procesarFiltroPorGrado() {
        while (true) {
            System.out.println();
            System.out.println("--- FILTRAR POR GRADO ACADÉMICO ---");
            System.out.println();
            System.out.println("Grados disponibles en el sistema:");
            Map<Integer, String> grados = controller.getGradosCatalogo();
            for (int i = 1; i <= 12; i++) {
                System.out.println(i + ". " + grados.get(i));
            }
            System.out.println();
            System.out.print("Ingrese el número del grado que desea filtrar (o 0 para cancelar): ");
            String entrada = scanner.nextLine().trim();

            int opcionGrado;
            try {
                opcionGrado = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println();
                System.out.println("Error: Debe ingresar un número válido.");
                pausar("Presione Enter para intentar nuevamente...");
                continue;
            }

            if (opcionGrado == 0) {
                System.out.println();
                System.out.println("Operación cancelada.");
                pausar("Presione Enter para volver al menú principal...");
                return;
            }

            if (opcionGrado < 1 || opcionGrado > 12) {
                System.out.println();
                System.out.println("Error: Debe seleccionar un número entre 1 y 12.");
                pausar("Presione Enter para intentar nuevamente...");
                continue;
            }

            // Grado válido seleccionado
            String nombreGrado = controller.obtenerNombreGrado(opcionGrado);
            System.out.println();
            System.out.println("Procesando búsqueda...");
            System.out.println("Consultando estudiantes matriculados en " + nombreGrado + "...");
            System.out.println();

            List<EstudianteDTO> resultados = controller.filtrarEstudiantesPorGrado(opcionGrado);
            mostrarResultadosBusqueda(resultados, nombreGrado);
            return;
        }
    }

    /**
     * Muestra la lista de estudiantes encontrados o el mensaje correspondiente
     * según el formato exacto requerido.
     */
    private void mostrarResultadosBusqueda(List<EstudianteDTO> resultados, String nombreGrado) {
        System.out.println(LINEA_DOBLE);
        System.out.println("            RESULTADOS DE LA BÚSQUEDA");
        System.out.println(LINEA_DOBLE);
        System.out.println();
        System.out.println("Filtro aplicado: Grado académico = " + nombreGrado);
        System.out.println();

        if (resultados.isEmpty()) {
            System.out.println("No se encontraron estudiantes matriculados en " + nombreGrado + " para el año 2025.");
            System.out.println();
            System.out.println("Posibles razones:");
            System.out.println("  - No hay estudiantes matriculados en ese grado para el año 2025");
            System.out.println("  - Verifique que la base de datos tenga información del año actual");
            System.out.println();
        } else {
            System.out.println("Se encontraron " + resultados.size() + " estudiantes matriculados en " + nombreGrado + " (año 2025):");
            System.out.println();

            for (EstudianteDTO estudiante : resultados) {
                System.out.println(LINEA_SIMPLE);
                System.out.println("ID: " + estudiante.getIdEstudiante());
                System.out.println("Nombre: " + estudiante.getNombreCompleto());
                System.out.println("Documento: " + estudiante.getDocumentoFormateado());
            }
            System.out.println(LINEA_SIMPLE);
            System.out.println();
            System.out.println("Total de resultados: " + resultados.size() + " estudiantes");
            System.out.println();
        }

        System.out.println(LINEA_DOBLE);
        pausar("Presione Enter para volver al menú principal...");
        System.out.println(LINEA_DOBLE);
        System.out.println();
    }

    private void pausar(String mensaje) {
        System.out.println(mensaje);
        scanner.nextLine();
    }
}
