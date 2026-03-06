package lk.icbt.findit.controller;

import lk.icbt.findit.dto.CityDTO;
import lk.icbt.findit.response.CityResponse;
import lk.icbt.findit.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * City APIs. Get cities by district ID with optional search by name.
 */
@RestController
@RequestMapping("/api/districts/{districtId}/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<CityResponse>> getByDistrictId(
            @PathVariable Long districtId,
            @RequestParam(required = false) String name) {
        List<CityDTO> list = cityService.getByDistrictId(districtId, name);
        List<CityResponse> response = list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private CityResponse mapToResponse(CityDTO dto) {
        CityResponse response = new CityResponse();
        response.setStatus(dto.getStatus());
        response.setResponseCode(dto.getResponseCode());
        response.setResponseMessage(dto.getResponseMessage());
        response.setCityId(dto.getCityId());
        response.setName(dto.getName());
        response.setDescription(dto.getDescription());
        response.setDistrictId(dto.getDistrictId());
        return response;
    }
}
