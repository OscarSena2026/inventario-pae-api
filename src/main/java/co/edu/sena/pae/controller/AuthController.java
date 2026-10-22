package co.edu.sena.pae.controller;

import co.edu.sena.pae.dto.CredencialesDTO;
import co.edu.sena.pae.dto.RegistroUsuarioDTO;
import co.edu.sena.pae.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Registro e inicio de sesion (HU-01), reutilizados de la evidencia EV01.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/registro -> 201 registrado, 400 datos invalidos, 409 usuario repetido
    @PostMapping("/registro")
    public ResponseEntity<String> registrar(@Valid @RequestBody RegistroUsuarioDTO datos) {
        authService.registrar(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado correctamente");
    }

    // POST /api/auth/login -> 200 satisfactoria, 401 error, 400 datos vacios
    // El mensaje de error es siempre el mismo para no dar pistas a un atacante.
    @PostMapping("/login")
    public ResponseEntity<String> iniciarSesion(@Valid @RequestBody CredencialesDTO datos) {
        if (authService.autenticar(datos)) {
            return ResponseEntity.ok("Autenticación satisfactoria");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error en la autenticación");
    }
}
