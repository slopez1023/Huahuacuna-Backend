
package com.huahuacuna.apadrinamiento.controller;

import com.huahuacuna.apadrinamiento.dto.ActualizarNinoRequest;
import com.huahuacuna.apadrinamiento.dto.CambiarEstadoRequest;
import com.huahuacuna.apadrinamiento.dto.CrearNinoRequest;
import com.huahuacuna.apadrinamiento.dto.NinoResponse;
import com.huahuacuna.apadrinamiento.model.EstadoNino;
import com.huahuacuna.apadrinamiento.service.NinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ninos")
// 1. REGLA GENERAL: Solo el ADMIN puede tocar los endpoints de esta clase
@PreAuthorize("hasRole('ADMIN')")
public class NinoController {

    @Autowired
    private NinoService ninoService;

    /**
     * [C]RUD: Crear un nuevo niño
     * Hereda @PreAuthorize("hasRole('ADMIN')") de la clase. ¡Correcto!
     */
    @PostMapping
    public ResponseEntity<NinoResponse> agregarNino(@RequestBody CrearNinoRequest request) {
        NinoResponse nuevoNino = ninoService.agregarNino(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoNino);
    }

    /**
     * C[R]UD: Leer todos los niños
     * 2. EXCEPCIÓN: Permitimos ver a CUALQUIER USUARIO CON CUENTA.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()") // <-- CAMBIADO DE permitAll()
    public ResponseEntity<List<NinoResponse>> obtenerTodosLosNinos(
            @RequestParam(required = false) EstadoNino estado) {
        List<NinoResponse> ninos = ninoService.obtenerTodosLosNinos(estado);
        return ResponseEntity.ok(ninos);
    }

    /**
     * C[R]UD: Leer un niño específico por ID
     * 3. EXCEPCIÓN: Permitimos ver a CUALQUIER USUARIO CON CUENTA.
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // <-- AÑADIDO
    public ResponseEntity<NinoResponse> obtenerNinoPorId(@PathVariable Long id) {
        NinoResponse nino = ninoService.obtenerNinoPorId(id);
        return ResponseEntity.ok(nino);
    }

    /**
     * CR[U]D: Actualizar un niño por ID
     * Hereda @PreAuthorize("hasRole('ADMIN')") de la clase. ¡Correcto!
     */
    @PutMapping("/{id}")
    public ResponseEntity<NinoResponse> actualizarNino(
            @PathVariable Long id,
            @RequestBody ActualizarNinoRequest request) {
        NinoResponse ninoActualizado = ninoService.actualizarNino(id, request);
        return ResponseEntity.ok(ninoActualizado);
    }

    /**
     * CR[U]D: Actualización parcial - Cambiar estado
     * Hereda @PreAuthorize("hasRole('ADMIN')") de la clase. ¡Correcto!
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<NinoResponse> cambiarEstadoNino(
            @PathVariable Long id,
            @RequestBody CambiarEstadoRequest request) {
        NinoResponse ninoActualizado = ninoService.cambiarEstadoNino(id, request);
        return ResponseEntity.ok(ninoActualizado);
    }

    /**
     * CRU[D]: Eliminar un niño por ID
     * Hereda @PreAuthorize("hasRole('ADMIN')") de la clase. ¡Correcto!
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNino(@PathVariable Long id) {
        ninoService.eliminarNino(id);
        return ResponseEntity.noContent().build();
    }
}