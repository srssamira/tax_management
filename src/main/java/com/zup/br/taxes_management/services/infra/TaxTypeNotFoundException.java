package com.zup.br.taxes_management.services.infra;

public class TaxTypeNotFoundException extends RuntimeException {

        public TaxTypeNotFoundException(String message) {
            super(message);
        }
}
