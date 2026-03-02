package lk.icbt.findit.service;

import lk.icbt.findit.dto.DistrictDTO;

import java.util.List;

public interface DistrictService {

    /**
     * Get districts by province ID with optional search by district name (case-insensitive contains).
     */
    List<DistrictDTO> getByProvinceId(Long provinceId, String name);
}
