package com.huahuacuna.service;

import com.huahuacuna.model.Child;
import com.huahuacuna.model.ChildStatus;
import com.huahuacuna.repository.ChildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return childRepository.save(child);
    }

    public Child updateChild(Long id, Child childDetails) {
        return childRepository.findById(id).map(child -> {
            child.setFirstName(childDetails.getFirstName());
            child.setLastName(childDetails.getLastName());
            child.setBirthDate(childDetails.getBirthDate());
            child.setStory(childDetails.getStory());
            child.setImageUrl(childDetails.getImageUrl());
            child.setStatus(childDetails.getStatus());
            return childRepository.save(child);
        }).orElseThrow(() -> new RuntimeException("Niño no encontrado con id " + id));
    }

    public void deleteChild(Long id) {
        childRepository.deleteById(id);
    }

}