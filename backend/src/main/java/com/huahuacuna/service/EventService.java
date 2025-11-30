package com.huahuacuna.service;

import com.huahuacuna.model.Event;
import com.huahuacuna.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event details) {
        return eventRepository.findById(id).map(event -> {
            event.setTitle(details.getTitle());
            event.setDescription(details.getDescription());
            event.setDate(details.getDate());
            event.setLocation(details.getLocation());
            event.setImageUrl(details.getImageUrl());
            return eventRepository.save(event);
        }).orElseThrow(() -> new RuntimeException("Evento no encontrado"));
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
    public Event publishEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        event.setPublished(true);
        return eventRepository.save(event);
    }

}