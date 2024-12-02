package com.zup.br.taxes_management.services.tax_type;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zup.br.taxes_management.controllers.dtos.TaxTypeRegisterDTO;
import com.zup.br.taxes_management.services.infra.TaxTypeNotFoundException;
import com.zup.br.taxes_management.models.TaxType;
import com.zup.br.taxes_management.repositories.TaxTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaxTypeServiceImpl implements TaxTypeService {

    @Autowired
    private TaxTypeRepository taxTypeRepository;

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public TaxType registerTaxType (TaxTypeRegisterDTO taxTypeRegisterDTO) {
        return taxTypeRepository.save(mapper.convertValue(taxTypeRegisterDTO, TaxType.class));
    }



    @Override
    public List<TaxType> displayAllTaxTypes() {
        return taxTypeRepository.findAll();
    }



    @Override
    public TaxType displayTaxTypeById (Long idTaxType) {
        if (taxTypeRepository.findById(idTaxType).isEmpty()) {
            throw new TaxTypeNotFoundException("Tax type not found");
        }

        return taxTypeRepository.findById(idTaxType).get();
    }



    @Override
    public boolean deleteTaxTypeById (Long idTaxType) throws TaxTypeNotFoundException {
        if (!taxTypeRepository.existsById(idTaxType)) {
            throw new TaxTypeNotFoundException("Tax type not found");
        }

        taxTypeRepository.deleteById(idTaxType);
        return true;
    }

}
