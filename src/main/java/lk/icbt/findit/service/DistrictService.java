package lk.icbt.findit.service;

import lk.icbt.findit.dto.DistrictDTO;

import java.util.List;

public interface DistrictService {

    
    List<DistrictDTO> getByProvinceId(Long provinceId, String name);
}
