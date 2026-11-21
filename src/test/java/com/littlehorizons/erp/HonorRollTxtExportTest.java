package com.littlehorizons.erp;

import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.service.ReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HonorRollTxtExportTest {

    @Test
    @DisplayName("Should generate cuadro_de_honor.txt with correct content and structure")
    void testExportHonorRollToTxt() throws IOException {
        ReportService reportService = new ReportService(null, null, null, null, null);

        Student s1 = new Student(1, "TI", "1023456789", "Juan", "Carlos", "Pérez", "González",
                LocalDate.of(2008, 5, 10), "M", "Calle 10", "3001234567", "juan@test.com", "O+",
                "Ninguna", "Ninguna", LocalDate.of(2025, 1, 15), "Activo");
        Student s2 = new Student(2, "TI", "1023456790", "María", "Elena", "López", "Ramírez",
                LocalDate.of(2008, 8, 20), "F", "Calle 20", "3001234568", "maria@test.com", "A+",
                "Ninguna", "Ninguna", LocalDate.of(2025, 1, 15), "Activo");

        List<ReportService.StudentPerformance> topStudents = new ArrayList<>();
        topStudents.add(new ReportService.StudentPerformance(s1, 4.85));
        topStudents.add(new ReportService.StudentPerformance(s2, 4.70));

        File tempFile = File.createTempFile("test_cuadro_de_honor", ".txt");
        tempFile.deleteOnExit();
        String testFilePath = tempFile.getAbsolutePath();
        boolean success = reportService.exportHonorRollToTxt(topStudents, "Final", 2025, testFilePath);

        assertTrue(success, "Export should return true");
        assertTrue(tempFile.exists(), "File should exist on disk");

        String content = Files.readString(tempFile.toPath());
        assertTrue(content.contains("CUADRO DE HONOR - EXCELENCIA ACADÉMICA (Final - 2025)"));
        assertTrue(content.contains("Juan Carlos Pérez González"));
        assertTrue(content.contains("TI 1023456789"));
        assertTrue(content.contains("4.85") || content.contains("4,85"));
        assertTrue(content.contains("María Elena López Ramírez"));
        assertTrue(content.contains("Total de estudiantes destacados: 2"));

        tempFile.delete();
    }
}
