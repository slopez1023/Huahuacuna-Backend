package com.huahuacuna.service;

import com.huahuacuna.model.Child;
import com.huahuacuna.model.ChildStatus;
import com.huahuacuna.repository.ChildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChildService {

    @Autowired
    private ChildRepository childRepository;

    public List<Child> getAllChildren() {
        return childRepository.findAll();
    }

    public List<Child> getAvailableChildren() {
        return childRepository.findByStatus(ChildStatus.AVAILABLE);
    }

    public Optional<Child> getChildById(Long id) {
        return childRepository.findById(id);
    }

    public Child createChild(Child child) {
        // Asegurar que tenga un estado inicial
        if (child.getStatus() == null) {
            child.setStatus(ChildStatus.AVAILABLE);
        }
        return childRepository.save(child);
    }

    public Child updateChild(Long id, Child childDetails) {
        return childRepository.findById(id).map(child -> {
            child.setFirstName(childDetails.getFirstName());
            child.setLastName(childDetails.getLastName());
            child.setBirthDate(childDetails.getBirthDate());
            child.setGender(childDetails.getGender());
            child.setStory(childDetails.getStory());
            child.setImageUrl(childDetails.getImageUrl());
            child.setNeeds(childDetails.getNeeds());
            child.setStatus(childDetails.getStatus());

            return childRepository.save(child);
        }).orElseThrow(() -> new RuntimeException("Niño no encontrado con id " + id));
    }

    /**
     * Inhabilita un niño con una razón específica.
     * Cambia el estado a INACTIVE y registra la información de inhabilitación.
     *
     * @param id El ID del niño a inhabilitar
     * @param reason La razón de la inhabilitación
     * @return El niño inhabilitado
     */
    public Child inactivateChild(Long id, String reason) {
        return childRepository.findById(id).map(child -> {
            child.setStatus(ChildStatus.INACTIVE);
            child.setInactivationReason(reason);
            child.setInactivatedAt(LocalDateTime.now());
            // Si tienes un sistema de autenticación, puedes agregar:
            // child.setInactivatedBy(SecurityContextHolder.getContext().getAuthentication().getName());

            return childRepository.save(child);
        }).orElseThrow(() -> new RuntimeException("Niño no encontrado con id " + id));
    }

    public void deleteChild(Long id) {
        childRepository.deleteById(id);
    }
}