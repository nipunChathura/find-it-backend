package lk.icbt.findit.service;

import lk.icbt.findit.dto.ProvinceDTO;

import java.util.List;

public interface ProvinceService {

    
    List<ProvinceDTO> getAll(String name, String description);
}
