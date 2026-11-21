package com.littlehorizons.erp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import test.modelo.EstudianteDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class StudentSearchFilterTest {

    @Test
    @DisplayName("Should correctly format full name without double spaces when middle name or second last name is null")
    void testNombreCompletoSinEspaciosDobles() {
        // Estudiante con todos los nombres y apellidos
        EstudianteDTO e1 = new EstudianteDTO(1, "TI", "1023456789", "Juan", "Carlos", "Pérez", "González", 11, "Décimo", 2025, "Activa");
        assertEquals("Juan Carlos Pérez González", e1.getNombreCompleto());

        // Estudiante sin segundo nombre
        EstudianteDTO e2 = new EstudianteDTO(2, "TI", "1023456790", "María", null, "López", "Ramírez", 11, "Décimo", 2025, "Activa");
        assertEquals("María López Ramírez", e2.getNombreCompleto());

        // Estudiante sin segundo apellido
        EstudianteDTO e3 = new EstudianteDTO(3, "TI", "1023456791", "Carlos", "Gómez", "Silva", null, 11, "Décimo", 2025, "Activa");
        assertEquals("Carlos Gómez Silva", e3.getNombreCompleto());

        // Estudiante solo con primer nombre y primer apellido
        EstudianteDTO e4 = new EstudianteDTO(4, "TI", "1023456792", "Ana", null, "Torres", null, 11, "Décimo", 2025, "Activa");
        assertEquals("Ana Torres", e4.getNombreCompleto());
    }

    @Test
    @DisplayName("Should filter students by grade using Java Stream API")
    void testFilterByGradeWithStreamApi() {
        List<EstudianteDTO> estudiantes = new ArrayList<>();
        estudiantes.add(new EstudianteDTO(1, "TI", "1023456789", "Juan", "Carlos", "Pérez", "González", 11, "Décimo", 2025, "Activa"));
        estudiantes.add(new EstudianteDTO(2, "TI", "1023456790", "María", "Elena", "López", "Ramírez", 11, "Décimo", 2025, "Activa"));
        estudiantes.add(new EstudianteDTO(3, "TI", "1023456791", "Carlos", "", "Gómez", "Silva", 11, "Décimo", 2025, "Activa"));
        estudiantes.add(new EstudianteDTO(4, "TI", "1023456792", "Laura", "", "Castro", "Mora", 12, "Once", 2025, "Activa"));
        estudiantes.add(new EstudianteDTO(5, "TI", "1023456793", "Pedro", "", "Ruiz", "Vargas", 10, "Noveno", 2025, "Activa"));

        // Filtrar por grado Décimo (idGrado = 11) usando Stream API
        List<EstudianteDTO> filtradosDecimo = estudiantes.stream()
                .filter(e -> e.getIdGrado() == 11)
                .collect(Collectors.toList());

        assertEquals(3, filtradosDecimo.size(), "Deberían encontrarse 3 estudiantes en grado Décimo");
        assertEquals("Juan Carlos Pérez González", filtradosDecimo.get(0).getNombreCompleto());
        assertEquals("María Elena López Ramírez", filtradosDecimo.get(1).getNombreCompleto());
        assertEquals("Carlos Gómez Silva", filtradosDecimo.get(2).getNombreCompleto());

        // Filtrar por grado Once (idGrado = 12)
        List<EstudianteDTO> filtradosOnce = estudiantes.stream()
                .filter(e -> e.getIdGrado() == 12)
                .collect(Collectors.toList());

        assertEquals(1, filtradosOnce.size(), "Debería encontrarse 1 estudiante en grado Once");
        assertEquals("Laura Castro Mora", filtradosOnce.get(0).getNombreCompleto());

        // Filtrar por grado sin estudiantes (ej: Quinto, idGrado = 6)
        List<EstudianteDTO> filtradosQuinto = estudiantes.stream()
                .filter(e -> e.getIdGrado() == 6)
                .collect(Collectors.toList());

        assertTrue(filtradosQuinto.isEmpty(), "No deberían encontrarse estudiantes para grado Quinto");
    }
}
