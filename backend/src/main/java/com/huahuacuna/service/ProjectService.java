package com.huahuacuna.service;

import com.huahuacuna.model.Project;
import com.huahuacuna.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public Project updateProject(Long id, Project details) {
        return projectRepository.findById(id).map(project -> {
            project.setTitle(details.getTitle());
            project.setDescription(details.getDescription());
            project.setGoalAmount(details.getGoalAmount());
            project.setCurrentAmount(details.getCurrentAmount());
            project.setImageUrl(details.getImageUrl());
            return projectRepository.save(project);
        }).orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    public Project publishProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        project.setPublished(true);
        return projectRepository.save(project);
    }

}