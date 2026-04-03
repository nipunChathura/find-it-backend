package lk.icbt.findit.controller;

import lk.icbt.findit.dto.ProvinceDTO;
import lk.icbt.findit.response.ProvinceResponse;
import lk.icbt.findit.service.ProvinceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/provinces")
@RequiredArgsConstructor
public class ProvinceController {

    private final ProvinceService provinceService;

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<ProvinceResponse>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description) {
        List<ProvinceDTO> list = provinceService.getAll(name, description);
        List<ProvinceResponse> response = list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private ProvinceResponse mapToResponse(ProvinceDTO dto) {
        ProvinceResponse response = new ProvinceResponse();
        response.setStatus(dto.getStatus());
        response.setResponseCode(dto.getResponseCode());
        response.setResponseMessage(dto.getResponseMessage());
        response.setProvinceId(dto.getProvinceId());
        response.setName(dto.getName());
        response.setDescription(dto.getDescription());
        response.setCode(dto.getCode());
        return response;
    }
}
