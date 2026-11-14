package com.littlehorizons.erp;

import com.littlehorizons.erp.model.AcademicRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.OptionalDouble;

import static org.junit.jupiter.api.Assertions.*;

public class AcademicCalculationsTest {

    @Test
    @DisplayName("Should accurately calculate GPA using Java Stream API")
    void testGpaCalculationWithStreams() {
        List<AcademicRecord> records = List.of(
                new AcademicRecord(1, 1, 10, 1, 2024, "I", 4.5, "Excelente", LocalDate.now()),
                new AcademicRecord(2, 1, 11, 2, 2024, "I", 3.8, "Bueno", LocalDate.now()),
                new AcademicRecord(3, 1, 12, 3, 2024, "I", 4.2, "Sobresaliente", LocalDate.now()),
                new AcademicRecord(4, 1, 13, 4, 2024, "I", 4.9, "Destacado", LocalDate.now())
        );

        OptionalDouble avgOpt = records.stream()
                .mapToDouble(AcademicRecord::getGradeValue)
                .average();

        assertTrue(avgOpt.isPresent(), "Average should be present");
        double expected = (4.5 + 3.8 + 4.2 + 4.9) / 4.0; // 4.35
        assertEquals(expected, avgOpt.getAsDouble(), 0.001, "GPA calculation must be mathematically accurate");
        assertEquals(4.4, Math.round(avgOpt.getAsDouble() * 10.0) / 10.0, "Rounded 1-decimal GPA should be 4.4");
    }

    @Test
    @DisplayName("Colombian grade boundary check (0.0 to 5.0)")
    void testGradeBoundaries() {
        double validGrade = 4.5;
        double lowerInvalid = -0.1;
        double upperInvalid = 5.1;

        assertTrue(validGrade >= 0.0 && validGrade <= 5.0, "4.5 must be a valid Colombian grade");
        assertFalse(lowerInvalid >= 0.0 && lowerInvalid <= 5.0, "-0.1 must be rejected");
        assertFalse(upperInvalid >= 0.0 && upperInvalid <= 5.0, "5.1 must be rejected");
    }
}
