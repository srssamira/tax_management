    package com.zup.br.taxes_management.controllers.dtos;

    import lombok.Data;

    @Data
    public class TaxCalculationResponseDTO {

        private String taxType;
        private Double baseValue;
        private Double aliquot;
        private Double taxValue;

        public TaxCalculationResponseDTO(String taxType, Double baseValue, Double aliquot, Double taxValue) {
            this.taxType = taxType;
            this.baseValue = baseValue;
            this.aliquot = aliquot;
            this.taxValue = taxValue;
        }

    }
