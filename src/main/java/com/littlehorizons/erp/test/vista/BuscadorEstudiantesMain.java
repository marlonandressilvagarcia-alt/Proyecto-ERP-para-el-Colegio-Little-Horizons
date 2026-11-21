package com.littlehorizons.erp.test.vista;

/**
 * Punto de entrada directo e independiente para la evaluación del Buscador Avanzado de Estudiantes.
 * Ejecuta el prototipo sin requerir autenticación ni navegación por menús previos.
 */
public class BuscadorEstudiantesMain {

    public static void main(String[] args) {
        mostrarMenuBuscador();
    }

    private static void mostrarMenuBuscador() {
        BuscadorEstudiantesView vista = new BuscadorEstudiantesView();
        vista.iniciar();
    }
}
