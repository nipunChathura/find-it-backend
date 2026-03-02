package lk.icbt.findit.service;

import lk.icbt.findit.dto.CityDTO;

import java.util.List;

public interface CityService {

    /**
     * Get cities by district ID with optional search by city name (case-insensitive contains).
     */
    List<CityDTO> getByDistrictId(Long districtId, String name);
}
