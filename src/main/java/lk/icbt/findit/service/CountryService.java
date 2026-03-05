package lk.icbt.findit.service;

import lk.icbt.findit.dto.CountryDTO;

import java.util.List;

public interface CountryService {

    /**
     * Get all countries, optionally filtered by name (case-insensitive contains).
     *
     * @param name optional search term for country name
     * @return list of country DTOs ordered by name
     */
    List<CountryDTO> getAll(String name);
}
