package com.enterpriselearning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationStatsResponse {
    private long totalCertifications;
    private Map<String, Long> certificationsByOrganization;
}
