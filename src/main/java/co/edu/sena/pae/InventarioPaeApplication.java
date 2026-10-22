package co.edu.sena.pae;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la API del Sistema de Inventario PAE.
 * Al ejecutarla, Spring Boot levanta el servidor en http://localhost:8080
 */
@SpringBootApplication
public class InventarioPaeApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventarioPaeApplication.class, args);
    }
}
