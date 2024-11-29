package com.zup.br.taxes_management.infra;

public class TaxTypeNotFoundException extends RuntimeException {

        public TaxTypeNotFoundException(String message) {
            super(message);
        }
}
