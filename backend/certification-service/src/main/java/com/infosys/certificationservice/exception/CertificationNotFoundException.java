package com.infosys.certificationservice.exception;

public class CertificationNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CertificationNotFoundException(String message) {
        super(message);
    }
}