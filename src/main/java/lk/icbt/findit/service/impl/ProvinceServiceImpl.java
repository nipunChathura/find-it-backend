package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.dto.ProvinceDTO;
import lk.icbt.findit.entity.Province;
import lk.icbt.findit.repository.ProvinceRepository;
import lk.icbt.findit.service.ProvinceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProvinceServiceImpl implements ProvinceService {

    private final ProvinceRepository provinceRepository;

    @Override
    public List<ProvinceDTO> getAll(String name, String description) {
        String nameParam = (name != null && !name.isBlank()) ? name.trim() : null;
        String descriptionParam = (description != null && !description.isBlank()) ? description.trim() : null;
        List<Province> list = provinceRepository.findAllWithSearch(nameParam, descriptionParam);
        return list.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ProvinceDTO mapToDto(Province p) {
        ProvinceDTO dto = new ProvinceDTO();
        dto.setStatus(ResponseStatus.SUCCESS.getStatus());
        dto.setResponseCode(ResponseCodes.SUCCESS_CODE);
        dto.setProvinceId(p.getProvinceId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setCode(p.getCode());
        return dto;
    }
}
