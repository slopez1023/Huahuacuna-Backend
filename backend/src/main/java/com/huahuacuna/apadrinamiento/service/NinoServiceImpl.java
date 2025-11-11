

package com.huahuacuna.apadrinamiento.service;

import com.huahuacuna.apadrinamiento.dto.ActualizarNinoRequest;
import com.huahuacuna.apadrinamiento.dto.CambiarEstadoRequest;
import com.huahuacuna.apadrinamiento.dto.CrearNinoRequest;
import com.huahuacuna.apadrinamiento.dto.NinoResponse;
import com.huahuacuna.apadrinamiento.exception.ResourceNotFoundException;
import com.huahuacuna.apadrinamiento.mapper.NinoMapper;
import com.huahuacuna.apadrinamiento.model.EstadoNino;
import com.huahuacuna.apadrinamiento.model.Nino;
import com.huahuacuna.apadrinamiento.repository.NinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NinoServiceImpl implements NinoService {

    @Autowired
    private NinoRepository ninoRepository;

    @Autowired
    private NinoMapper ninoMapper;

    // Método de ayuda reutilizable para encontrar un niño o lanzar un error
    private Nino findNinoById(Long id) {
        return ninoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Niño no encontrado con id: " + id));
    }

    @Override
    public List<NinoResponse> obtenerTodosLosNinos(EstadoNino estado) {
        List<Nino> ninos;
        if (estado != null) {
            // Si se proporciona un estado, filtra por él
            ninos = ninoRepository.findByEstado(estado);
        } else {
            // Si no, trae todos
            ninos = ninoRepository.findAll();
        }

        // Convierte la lista de Entidades (Nino) a DTOs (NinoResponse)
        return ninos.stream()
                .map(ninoMapper::toNinoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NinoResponse obtenerNinoPorId(Long id) {
        Nino nino = findNinoById(id);
        return ninoMapper.toNinoResponse(nino);
    }

    @Override
    public NinoResponse agregarNino(CrearNinoRequest request) {
        // 1. Convertir DTO (request) a Entidad (nino)
        Nino nino = ninoMapper.toNino(request);

        // 2. Guardar la entidad en la base de datos
        Nino ninoGuardado = ninoRepository.save(nino);

        // 3. Convertir la entidad guardada (con ID) a DTO y devolverla
        return ninoMapper.toNinoResponse(ninoGuardado);
    }

    @Override
    public NinoResponse actualizarNino(Long id, ActualizarNinoRequest request) {
        // 1. Buscar el niño existente
        Nino ninoExistente = findNinoById(id);

        // 2. Actualizar los campos
        ninoExistente.setNombres(request.nombres());
        ninoExistente.setApellidos(request.apellidos());
        ninoExistente.setFechaNacimiento(request.fechaNacimiento());
        ninoExistente.setHistoria(request.historia());
        ninoExistente.setUrlFotoPrincipal(request.urlFotoPrincipal());

        // 3. Guardar los cambios
        Nino ninoActualizado = ninoRepository.save(ninoExistente);

        // 4. Devolver el DTO actualizado
        return ninoMapper.toNinoResponse(ninoActualizado);
    }

    @Override
    public NinoResponse cambiarEstadoNino(Long id, CambiarEstadoRequest request) {
        // 1. Buscar al niño
        Nino nino = findNinoById(id);

        // 2. Cambiar el estado (Este es el método que te faltaba)
        nino.setEstado(request.nuevoEstado());

        // 3. Guardar y retornar
        Nino ninoActualizado = ninoRepository.save(nino);
        return ninoMapper.toNinoResponse(ninoActualizado);
    }

    @Override
    public void eliminarNino(Long id) {
        // 1. Verifica que el niño exista
        Nino nino = findNinoById(id);

        // 2. Elimina
        ninoRepository.delete(nino);
    }
}