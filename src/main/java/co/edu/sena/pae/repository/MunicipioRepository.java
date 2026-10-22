package co.edu.sena.pae.repository;

import co.edu.sena.pae.model.Municipio;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio del recurso municipio: acceso a la tabla "municipio".
 * Spring genera las consultas SQL a partir del nombre de cada metodo.
 */
public interface MunicipioRepository extends JpaRepository<Municipio, Integer> {

    // Busca por nombreMunicipio para validar que no se repita
    Optional<Municipio> findByNombreMunicipio(String nombreMunicipio);
}
