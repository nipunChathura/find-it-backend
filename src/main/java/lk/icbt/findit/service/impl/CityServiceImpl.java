package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.dto.CityDTO;
import lk.icbt.findit.entity.City;
import lk.icbt.findit.repository.CityRepository;
import lk.icbt.findit.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    @Override
    public List<CityDTO> getByDistrictId(Long districtId, String name) {
        String nameParam = (name != null && !name.isBlank()) ? name.trim() : null;
        List<City> list = cityRepository.findByDistrictIdWithNameSearch(districtId, nameParam);
        return list.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private CityDTO mapToDto(City c) {
        CityDTO dto = new CityDTO();
        dto.setStatus(ResponseStatus.SUCCESS.getStatus());
        dto.setResponseCode(ResponseCodes.SUCCESS_CODE);
        dto.setCityId(c.getCityId());
        dto.setName(c.getName());
        dto.setDescription(c.getDescription());
        dto.setDistrictId(c.getDistrict() != null ? c.getDistrict().getDistrictId() : null);
        return dto;
    }
}
