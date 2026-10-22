package co.edu.sena.pae.repository;

import co.edu.sena.pae.model.Entrada;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de la cabecera de entradas: acceso a la tabla "entrada".
 */
public interface EntradaRepository extends JpaRepository<Entrada, Integer> {
}
