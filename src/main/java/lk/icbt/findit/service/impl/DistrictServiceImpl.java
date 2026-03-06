package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.dto.DistrictDTO;
import lk.icbt.findit.entity.District;
import lk.icbt.findit.repository.DistrictRepository;
import lk.icbt.findit.service.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DistrictServiceImpl implements DistrictService {

    private final DistrictRepository districtRepository;

    @Override
    public List<DistrictDTO> getByProvinceId(Long provinceId, String name) {
        String nameParam = (name != null && !name.isBlank()) ? name.trim() : null;
        List<District> list = districtRepository.findByProvinceIdWithNameSearch(provinceId, nameParam);
        return list.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private DistrictDTO mapToDto(District d) {
        DistrictDTO dto = new DistrictDTO();
        dto.setStatus(ResponseStatus.SUCCESS.getStatus());
        dto.setResponseCode(ResponseCodes.SUCCESS_CODE);
        dto.setDistrictId(d.getDistrictId());
        dto.setName(d.getName());
        dto.setDescription(d.getDescription());
        dto.setCode(d.getCode());
        dto.setProvinceId(d.getProvince() != null ? d.getProvince().getProvinceId() : null);
        return dto;
    }
}
