package test.modelo;

/**
 * Data Transfer Object (DTO) que representa a un estudiante con su matrícula y grado
 * para el buscador avanzado.
 */
public class EstudianteDTO {

    private int idEstudiante;
    private String tipoDocumento;
    private String numeroDocumento;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private int idGrado;
    private String nombreGrado;
    private int anioAcademico;
    private String estadoMatricula;

    public EstudianteDTO() {
    }

    public EstudianteDTO(int idEstudiante, String tipoDocumento, String numeroDocumento,
                         String primerNombre, String segundoNombre,
                         String primerApellido, String segundoApellido,
                         int idGrado, String nombreGrado,
                         int anioAcademico, String estadoMatricula) {
        this.idEstudiante = idEstudiante;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.primerNombre = primerNombre;
        this.segundoNombre = segundoNombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.idGrado = idGrado;
        this.nombreGrado = nombreGrado;
        this.anioAcademico = anioAcademico;
        this.estadoMatricula = estadoMatricula;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getPrimerNombre() {
        return primerNombre;
    }

    public void setPrimerNombre(String primerNombre) {
        this.primerNombre = primerNombre;
    }

    public String getSegundoNombre() {
        return segundoNombre;
    }

    public void setSegundoNombre(String segundoNombre) {
        this.segundoNombre = segundoNombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public int getIdGrado() {
        return idGrado;
    }

    public void setIdGrado(int idGrado) {
        this.idGrado = idGrado;
    }

    public String getNombreGrado() {
        return nombreGrado;
    }

    public void setNombreGrado(String nombreGrado) {
        this.nombreGrado = nombreGrado;
    }

    public int getAnioAcademico() {
        return anioAcademico;
    }

    public void setAnioAcademico(int anioAcademico) {
        this.anioAcademico = anioAcademico;
    }

    public String getEstadoMatricula() {
        return estadoMatricula;
    }

    public void setEstadoMatricula(String estadoMatricula) {
        this.estadoMatricula = estadoMatricula;
    }

    /**
     * Construye el nombre completo evitando espacios dobles si el segundo nombre
     * o segundo apellido es nulo o vacío (Caso 3 del requerimiento).
     */
    public String getNombreCompleto() {
        StringBuilder sb = new StringBuilder();
        if (primerNombre != null && !primerNombre.trim().isEmpty()) {
            sb.append(primerNombre.trim());
        }
        if (segundoNombre != null && !segundoNombre.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(segundoNombre.trim());
        }
        if (primerApellido != null && !primerApellido.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(primerApellido.trim());
        }
        if (segundoApellido != null && !segundoApellido.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(segundoApellido.trim());
        }
        return sb.toString();
    }

    /**
     * Retorna el tipo y número de documento formateado (Ej: "TI 1023456789").
     */
    public String getDocumentoFormateado() {
        String td = (tipoDocumento != null) ? tipoDocumento.trim() : "";
        String nd = (numeroDocumento != null) ? numeroDocumento.trim() : "";
        if (td.isEmpty()) return nd;
        if (nd.isEmpty()) return td;
        return td + " " + nd;
    }

    @Override
    public String toString() {
        return "EstudianteDTO{" +
                "idEstudiante=" + idEstudiante +
                ", nombreCompleto='" + getNombreCompleto() + '\'' +
                ", documento='" + getDocumentoFormateado() + '\'' +
                ", grado='" + nombreGrado + '\'' +
                ", anio=" + anioAcademico +
                ", estado='" + estadoMatricula + '\'' +
                '}';
    }
}
