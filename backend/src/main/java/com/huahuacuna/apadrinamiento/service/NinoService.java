
package com.huahuacuna.apadrinamiento.service;

import com.huahuacuna.apadrinamiento.dto.ActualizarNinoRequest;
import com.huahuacuna.apadrinamiento.dto.CambiarEstadoRequest;
import com.huahuacuna.apadrinamiento.dto.CrearNinoRequest;
import com.huahuacuna.apadrinamiento.dto.NinoResponse;
import com.huahuacuna.apadrinamiento.model.EstadoNino;
import java.util.List;

public interface NinoService {

    /**
     * Obtiene una lista de todos los niños, opcionalmente filtrada por estado.
     * @param estado (Opcional) Filtra por EstadoNino.
     * @return Lista de DTOs de Niño.
     */
    List<NinoResponse> obtenerTodosLosNinos(EstadoNino estado);

    /**
     * Obtiene un niño específico por su ID.
     * @param id El ID del niño.
     * @return El DTO del niño.
     */
    NinoResponse obtenerNinoPorId(Long id);

    /**
     * Crea un nuevo niño en la base de datos.
     * @param request El DTO con la información para crear.
     * @return El DTO del niño recién creado (con su ID).
     */
    NinoResponse agregarNino(CrearNinoRequest request);

    /**
     * Actualiza la información de un niño existente.
     * @param id El ID del niño a actualizar.
     * @param request El DTO con la nueva información.
     * @return El DTO del niño actualizado.
     */
    NinoResponse actualizarNino(Long id, ActualizarNinoRequest request);

    /**
     * Cambia el estado de un niño (ej. de DISPONIBLE a APADRINADO).
     * @param id El ID del niño a cambiar.
     * @param request El DTO que contiene el nuevo estado.
     * @return El DTO del niño con su estado actualizado.
     */
    NinoResponse cambiarEstadoNino(Long id, CambiarEstadoRequest request);

    /**
     * Elimina un niño de la base de datos.
     * @param id El ID del niño a eliminar.
     */
    void eliminarNino(Long id);
}