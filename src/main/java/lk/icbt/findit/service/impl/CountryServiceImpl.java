package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.dto.CountryDTO;
import lk.icbt.findit.entity.Country;
import lk.icbt.findit.repository.CountryRepository;
import lk.icbt.findit.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    @Override
    public List<CountryDTO> getAll(String name) {
        String nameParam = (name != null && !name.isBlank()) ? name.trim() : null;
        List<Country> list = countryRepository.findAllWithSearch(nameParam);
        return list.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private CountryDTO mapToDto(Country c) {
        CountryDTO dto = new CountryDTO();
        dto.setStatus(ResponseStatus.SUCCESS.getStatus());
        dto.setResponseCode(ResponseCodes.SUCCESS_CODE);
        dto.setCountryId(c.getCountryId());
        dto.setName(c.getName());
        dto.setCode(c.getCode());
        return dto;
    }
}
