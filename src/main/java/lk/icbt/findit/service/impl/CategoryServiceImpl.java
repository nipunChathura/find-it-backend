package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.Constants;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.dto.CategoryDTO;
import lk.icbt.findit.entity.Category;
import lk.icbt.findit.entity.CategoryType;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.CategoryRepository;
import lk.icbt.findit.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDTO create(CategoryDTO dto) {
        Category category = new Category();
        category.setCategoryName(dto.getCategoryName() != null ? dto.getCategoryName().trim() : null);
        category.setCategoryDescription(dto.getCategoryDescription() != null ? dto.getCategoryDescription().trim() : null);
        category.setCategoryImage(dto.getCategoryImage());
        category.setCategoryType(dto.getCategoryType());
        category.setStatus(dto.getCategoryStatus() != null && !dto.getCategoryStatus().isBlank()
                ? dto.getCategoryStatus()
                : Constants.CATEGORY_ACTIVE_STATUS);

        Date now = new Date();
        category.setCreatedDatetime(now);
        category.setModifiedDatetime(now);
        category.setVersion(1);

        Category saved = categoryRepository.save(category);
        return mapToDto(saved, "Category created successfully.");
    }

    @Override
    public CategoryDTO getById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.CATEGORY_NOT_FOUND_CODE,
                        "Category not found"
                ));
        return mapToDto(category, null);
    }

    @Override
    public List<CategoryDTO> getAll(String name, String categoryType, String status) {
        String nameParam = (name != null && !name.isBlank()) ? name.trim() : null;
        CategoryType typeParam = null;
        if (categoryType != null && !categoryType.isBlank()) {
            String t = categoryType.trim().toUpperCase();
            if ("ITEM".equals(t)) typeParam = CategoryType.ITEM;
            else if ("SERVICE".equals(t)) typeParam = CategoryType.SERVICE;
        }
        String statusParam = (status != null && !status.isBlank()) ? status.trim() : null;
        return categoryRepository.findAllWithFilters(nameParam, typeParam, statusParam).stream()
                .map(c -> mapToDto(c, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDTO update(Long categoryId, CategoryDTO dto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.CATEGORY_NOT_FOUND_CODE,
                        "Category not found"
                ));
        if (dto.getCategoryName() != null) category.setCategoryName(dto.getCategoryName().trim());
        if (dto.getCategoryDescription() != null) category.setCategoryDescription(dto.getCategoryDescription().trim());
        if (dto.getCategoryImage() != null) category.setCategoryImage(dto.getCategoryImage());
        if (dto.getCategoryType() != null) category.setCategoryType(dto.getCategoryType());
        if (dto.getCategoryStatus() != null && (Constants.CATEGORY_ACTIVE_STATUS.equals(dto.getCategoryStatus())
                || Constants.CATEGORY_INACTIVE_STATUS.equals(dto.getCategoryStatus())
                || Constants.CATEGORY_DELETED_STATUS.equals(dto.getCategoryStatus()))) {
            category.setStatus(dto.getCategoryStatus());
        }

        category.setModifiedDatetime(new Date());
        Category saved = categoryRepository.save(category);
        return mapToDto(saved, "Category updated successfully.");
    }

    @Override
    @Transactional
    public CategoryDTO delete(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.CATEGORY_NOT_FOUND_CODE,
                        "Category not found"
                ));
        category.setStatus(Constants.CATEGORY_DELETED_STATUS);
        categoryRepository.save(category);
        CategoryDTO result = new CategoryDTO();
        result.setStatus(ResponseStatus.SUCCESS.getStatus());
        result.setResponseCode(ResponseCodes.SUCCESS_CODE);
        result.setResponseMessage("Category deleted successfully.");
        return result;
    }

    private CategoryDTO mapToDto(Category category, String message) {
        CategoryDTO dto = new CategoryDTO();
        dto.setStatus(ResponseStatus.SUCCESS.getStatus());
        dto.setResponseCode(ResponseCodes.SUCCESS_CODE);
        if (message != null) dto.setResponseMessage(message);
        dto.setCategoryId(category.getCategoryId());
        dto.setCategoryName(category.getCategoryName());
        dto.setCategoryDescription(category.getCategoryDescription());
        dto.setCategoryImage(category.getCategoryImage());
        dto.setCategoryType(category.getCategoryType());
        dto.setCategoryStatus(category.getStatus());
        dto.setCreatedDatetime(category.getCreatedDatetime());
        return dto;
    }
}
