package com.huahuacuna.controller;

import com.huahuacuna.model.Donation;
import com.huahuacuna.model.DonationRequest;
import com.huahuacuna.model.dto.DonationStatsDTO;
import com.huahuacuna.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DonationController {

    private final DonationService donationService;

    @PostMapping
    public ResponseEntity<?> createDonation(@RequestBody DonationRequest request) {
        try {
            Donation donation = donationService.createDonation(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("donation", donation);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al procesar la donación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<?> getDonations(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type
    ) {
        try {
            List<Donation> donations = donationService.getAllDonations(startDate, endDate, status, type);

            Map<String, Object> response = new HashMap<>();
            response.put("donations", donations);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al obtener las donaciones: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/reports")
    public ResponseEntity<?> getReports(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        try {
            DonationStatsDTO stats = donationService.getDonationStats(startDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al generar el reporte: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportDonations(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "csv") String format
    ) {
        try {
            if ("csv".equalsIgnoreCase(format)) {
                String csv = donationService.exportToCSV(startDate, endDate);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType("text/csv"));
                headers.setContentDispositionFormData("attachment",
                        "donaciones_" + LocalDateTime.now() + ".csv");

                return new ResponseEntity<>(csv, headers, HttpStatus.OK);
            }

            return ResponseEntity.badRequest().body("Formato no soportado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al exportar: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        try {
            String status = request.get("status");
            Donation donation = donationService.updateDonationStatus(id, status);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("donation", donation);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al actualizar el estado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}