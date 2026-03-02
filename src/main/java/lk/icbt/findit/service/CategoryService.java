package lk.icbt.findit.service;

import lk.icbt.findit.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {

    CategoryDTO create(CategoryDTO dto);

    CategoryDTO getById(Long categoryId);

    List<CategoryDTO> getAll(String name, String categoryType, String status);

    CategoryDTO update(Long categoryId, CategoryDTO dto);

    CategoryDTO delete(Long categoryId);
}
