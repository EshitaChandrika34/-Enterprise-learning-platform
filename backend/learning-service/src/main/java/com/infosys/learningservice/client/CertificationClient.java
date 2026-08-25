package com.infosys.learningservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.infosys.learningservice.dto.CertificationRequestDTO;

@FeignClient(name = "certificationservice")
public interface CertificationClient {

    @PostMapping("/certifications")
    ResponseEntity<Object> createCertification(
            @RequestBody CertificationRequestDTO request
    );

    @GetMapping("/certifications")
    ResponseEntity<Object> getAllCertifications();
}