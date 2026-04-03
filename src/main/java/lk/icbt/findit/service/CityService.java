package lk.icbt.findit.service;

import lk.icbt.findit.dto.CityDTO;

import java.util.List;

public interface CityService {

    
    List<CityDTO> getByDistrictId(Long districtId, String name);
}
