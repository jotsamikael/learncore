package com.dodibo.learncore.elearningcore.category;

import com.dodibo.learncore.elearningcore.category.dto.CategoryResponse;
import com.dodibo.learncore.elearningcore.category.dto.CreateCategoryRequest;
import com.dodibo.learncore.elearningcore.category.dto.FindCategoriesQuery;
import com.dodibo.learncore.elearningcore.category.dto.UpdateCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest, MultipartFile image);

    Page<CategoryResponse> getCategories(FindCategoriesQuery query);

    CategoryResponse getCategory(String uuid);

    CategoryResponse updateCategory(String uuid, UpdateCategoryRequest request, MultipartFile image);

    void deleteCategory(String uuid);
}
