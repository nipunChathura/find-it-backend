package lk.icbt.findit.controller;

import lk.icbt.findit.dto.DistrictDTO;
import lk.icbt.findit.response.DistrictResponse;
import lk.icbt.findit.service.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * District APIs. Get districts by province ID with optional search by name.
 */
@RestController
@RequestMapping("/api/provinces/{provinceId}/districts")
@RequiredArgsConstructor
public class DistrictController {

    private final DistrictService districtService;

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<DistrictResponse>> getByProvinceId(
            @PathVariable Long provinceId,
            @RequestParam(required = false) String name) {
        List<DistrictDTO> list = districtService.getByProvinceId(provinceId, name);
        List<DistrictResponse> response = list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private DistrictResponse mapToResponse(DistrictDTO dto) {
        DistrictResponse response = new DistrictResponse();
        response.setStatus(dto.getStatus());
        response.setResponseCode(dto.getResponseCode());
        response.setResponseMessage(dto.getResponseMessage());
        response.setDistrictId(dto.getDistrictId());
        response.setName(dto.getName());
        response.setDescription(dto.getDescription());
        response.setCode(dto.getCode());
        response.setProvinceId(dto.getProvinceId());
        return response;
    }
}
