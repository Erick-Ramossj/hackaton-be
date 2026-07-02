package Pool.hackaton.dto;

import Pool.hackaton.model.Usuario;
import lombok.Data;

/**
 * DTO de respuesta al login.
 * Devuelve solo los datos necesarios al frontend,
 * NUNCA el password (aunque sea texto plano).
 */
@Data
public class UsuarioLoginResponseDTO {

    private Integer idUsuario;
    private String  nombre;
    private String  usuario;
    private String  rol;
    private Boolean estado;

    // Convierte la entidad a DTO omitiendo el password
    public static UsuarioLoginResponseDTO from(Usuario u) {
        UsuarioLoginResponseDTO dto = new UsuarioLoginResponseDTO();
        dto.setIdUsuario(u.getIdUsuario());
        dto.setNombre(u.getNombre());
        dto.setUsuario(u.getUsuario());
        dto.setRol(u.getRol());
        dto.setEstado(u.getEstado());
        return dto;
    }
}

