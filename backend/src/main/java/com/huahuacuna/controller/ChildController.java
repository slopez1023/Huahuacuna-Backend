package com.huahuacuna.controller;

import com.huahuacuna.model.Child;
import com.huahuacuna.service.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/children")
@CrossOrigin(origins = "http://localhost:3000") // Permite conexión desde React
public class ChildController {

    @Autowired
    private ChildService childService;

    @GetMapping
    public List<Child> getAllChildren() {
        return childService.getAllChildren();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Child> getChildById(@PathVariable Long id) {
        return childService.getChildById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Child createChild(@RequestBody Child child) {
        return childService.createChild(child);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Child> updateChild(@PathVariable Long id, @RequestBody Child childDetails) {
        try {
            Child updatedChild = childService.updateChild(id, childDetails);
            return ResponseEntity.ok(updatedChild);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChild(@PathVariable Long id) {
        childService.deleteChild(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/available")
    public List<Child> getAvailableChildren() {
        return childService.getAvailableChildren();
    }
}
