package lk.icbt.findit.service;

import lk.icbt.findit.dto.ProvinceDTO;

import java.util.List;

public interface ProvinceService {

    /**
     * Get all provinces, optionally filtered by name and/or description (case-insensitive contains).
     *
     * @param name        optional search term for name
     * @param description optional search term for description
     * @return list of province DTOs
     */
    List<ProvinceDTO> getAll(String name, String description);
}
