package com.zup.br.taxes_management.repositories;

import com.zup.br.taxes_management.models.TaxType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxTypeRepository extends JpaRepository<TaxType, Long> {
}
