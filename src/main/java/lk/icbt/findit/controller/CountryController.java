package lk.icbt.findit.controller;

import lk.icbt.findit.dto.CountryDTO;
import lk.icbt.findit.response.CountryResponse;
import lk.icbt.findit.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Country API. Get all countries with optional search by name. Public for customer app (e.g. onboarding).
 */
@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<CountryResponse>> getAll(
            @RequestParam(required = false) String name) {
        List<CountryDTO> list = countryService.getAll(name);
        List<CountryResponse> response = list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private CountryResponse mapToResponse(CountryDTO dto) {
        CountryResponse response = new CountryResponse();
        BeanUtils.copyProperties(dto, response);
        return response;
    }
}
