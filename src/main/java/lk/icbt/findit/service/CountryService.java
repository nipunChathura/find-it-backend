package lk.icbt.findit.service;

import lk.icbt.findit.dto.CountryDTO;

import java.util.List;

public interface CountryService {

    
    List<CountryDTO> getAll(String name);
}
