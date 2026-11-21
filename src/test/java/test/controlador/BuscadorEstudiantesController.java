package test.controlador;

import com.littlehorizons.erp.config.DatabaseConfig;
import test.modelo.EstudianteDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador para la búsqueda avanzada de estudiantes aplicando filtros con Java Stream API.
 */
public class BuscadorEstudiantesController {

    private final DatabaseConfig dbConfig;
    private static final Map<Integer, String> GRADOS_CATALOGO;

    static {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "Preescolar");
        map.put(2, "Primero");
        map.put(3, "Segundo");
        map.put(4, "Tercero");
        map.put(5, "Cuarto");
        map.put(6, "Quinto");
        map.put(7, "Sexto");
        map.put(8, "Séptimo");
        map.put(9, "Octavo");
        map.put(10, "Noveno");
        map.put(11, "Décimo");
        map.put(12, "Once");
        GRADOS_CATALOGO = Collections.unmodifiableMap(map);
    }

    public BuscadorEstudiantesController() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    /**
     * Retorna el catálogo oficial de grados académicos del sistema (1 a 12).
     */
    public Map<Integer, String> getGradosCatalogo() {
        return GRADOS_CATALOGO;
    }

    /**
     * Obtiene el nombre del grado a partir de su ID/número.
     */
    public String obtenerNombreGrado(int idGrado) {
        return GRADOS_CATALOGO.getOrDefault(idGrado, "Grado " + idGrado);
    }

    /**
     * Consulta todos los estudiantes con matrícula activa en el año 2025
     * desde las tablas relacionadas: estudiantes, matriculas y grados.
     *
     * @return Lista de EstudianteDTO
     */
    public List<EstudianteDTO> consultarEstudiantesMatriculados2025() {
        List<EstudianteDTO> lista = new ArrayList<>();
        String sql = "SELECT " +
                "    e.id_estudiante, " +
                "    e.tipo_documento, " +
                "    e.numero_documento, " +
                "    e.primer_nombre, " +
                "    e.segundo_nombre, " +
                "    e.primer_apellido, " +
                "    e.segundo_apellido, " +
                "    m.id_grado, " +
                "    g.nombre_grado, " +
                "    m.año_academico, " +
                "    m.estado_matricula " +
                "FROM estudiantes e " +
                "INNER JOIN matriculas m ON e.id_estudiante = m.id_estudiante " +
                "INNER JOIN grados g ON m.id_grado = g.id_grado " +
                "WHERE m.año_academico = 2025 " +
                "  AND LOWER(m.estado_matricula) = 'activa' " +
                "ORDER BY e.id_estudiante ASC";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                EstudianteDTO dto = new EstudianteDTO(
                        rs.getInt("id_estudiante"),
                        rs.getString("tipo_documento"),
                        rs.getString("numero_documento"),
                        rs.getString("primer_nombre"),
                        rs.getString("segundo_nombre"),
                        rs.getString("primer_apellido"),
                        rs.getString("segundo_apellido"),
                        rs.getInt("id_grado"),
                        rs.getString("nombre_grado"),
                        rs.getInt("año_academico"),
                        rs.getString("estado_matricula")
                );
                lista.add(dto);
            }
        } catch (SQLException e) {
            System.err.println("Advertencia al consultar base de datos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Filtra los estudiantes por grado académico utilizando Java Stream API.
     * CUMPLE CON LA RESTRICCIÓN TECNOLÓGICA OBLIGATORIA:
     * - stream()
     * - filter() con expresión lambda
     * - collect(Collectors.toList())
     *
     * @param idGrado Número/ID del grado (1 a 12)
     * @return Lista de estudiantes filtrados por el grado seleccionado
     */
    public List<EstudianteDTO> filtrarEstudiantesPorGrado(int idGrado) {
        List<EstudianteDTO> todosLosEstudiantes = consultarEstudiantesMatriculados2025();

        return todosLosEstudiantes.stream()
                .filter(estudiante -> estudiante.getIdGrado() == idGrado)
                .collect(Collectors.toList());
    }
}
