package co.edu.sena.pae.controller;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import co.edu.sena.pae.dto.ProductoDTO;
import co.edu.sena.pae.model.Producto;
import co.edu.sena.pae.service.ProductoService;

/**
 * Endpoints REST del recurso producto (/api/productos).
 * El controlador recibe la peticion, llama al servicio y responde con el codigo HTTP.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    // GET /api/productos -> 200 + lista
    @GetMapping
    public List<Producto> listar(@RequestParam(name = "nombre", required = false) String nombre,
                                 @RequestParam(name = "categoria", required = false) String categoria) {
        return service.listar(nombre, categoria);
    }

    // GET /api/productos/{id} -> 200 + registro, 404 si no existe
    @GetMapping("/{id}")
    public Producto obtener(@PathVariable("id") Integer id) {
        return service.obtener(id);
    }

    // POST /api/productos -> 201 + registro creado, 400 datos invalidos, 404 referencia inexistente
    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    // PUT /api/productos/{id} -> 200 + registro actualizado, 400 o 404
    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable("id") Integer id, @Valid @RequestBody ProductoDTO dto) {
        return service.actualizar(id, dto);
    }

    // DELETE /api/productos/{id} -> 204 sin contenido, 404 si no existe, 409 si tiene registros relacionados
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
