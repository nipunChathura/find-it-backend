package lk.icbt.findit.controller;

import jakarta.validation.Valid;
import lk.icbt.findit.dto.CategoryDTO;
import lk.icbt.findit.request.CategoryRequest;
import lk.icbt.findit.response.CategoryResponse;
import lk.icbt.findit.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        CategoryDTO dto = mapRequestToDto(request);
        CategoryDTO result = categoryService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(result));
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @GetMapping(value = "/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CategoryResponse> getById(@PathVariable Long categoryId) {
        CategoryDTO result = categoryService.getById(categoryId);
        return ResponseEntity.ok(mapToResponse(result));
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT', 'CUSTOMER')")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<CategoryResponse>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String categoryType,
            @RequestParam(required = false) String status) {
        List<CategoryDTO> list = categoryService.getAll(name, categoryType, status);
        List<CategoryResponse> response = list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @PutMapping(value = "/{categoryId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CategoryResponse> update(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryRequest request) {
        CategoryDTO dto = mapRequestToDto(request);
        CategoryDTO result = categoryService.update(categoryId, dto);
        return ResponseEntity.ok(mapToResponse(result));
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @DeleteMapping(value = "/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CategoryResponse> delete(@PathVariable Long categoryId) {
        CategoryDTO result = categoryService.delete(categoryId);
        CategoryResponse response = new CategoryResponse();
        response.setStatus(result.getStatus());
        response.setResponseCode(result.getResponseCode());
        response.setResponseMessage(result.getResponseMessage());
        return ResponseEntity.ok(response);
    }

    private CategoryDTO mapRequestToDto(CategoryRequest request) {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryName(request.getCategoryName());
        dto.setCategoryDescription(request.getCategoryDescription());
        dto.setCategoryImage(request.getCategoryImage());
        dto.setCategoryType(request.getCategoryType());
        dto.setCategoryStatus(request.getStatus());
        return dto;
    }

    private CategoryResponse mapToResponse(CategoryDTO dto) {
        CategoryResponse response = new CategoryResponse();
        response.setStatus(dto.getStatus());
        response.setResponseCode(dto.getResponseCode());
        response.setResponseMessage(dto.getResponseMessage());
        response.setCategoryId(dto.getCategoryId());
        response.setCategoryName(dto.getCategoryName());
        response.setCategoryDescription(dto.getCategoryDescription());
        response.setCategoryImage(dto.getCategoryImage());
        response.setCategoryType(dto.getCategoryType());
        response.setCategoryStatus(dto.getCategoryStatus());
        response.setCreatedDatetime(dto.getCreatedDatetime());
        return response;
    }
}
