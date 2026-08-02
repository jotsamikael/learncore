package com.dodibo.learncore.elearningcore.category;

import com.dodibo.learncore.elearningcore.category.dto.CategoryResponse;
import com.dodibo.learncore.elearningcore.category.dto.CreateCategoryRequest;
import com.dodibo.learncore.elearningcore.category.dto.FindCategoriesQuery;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest) {
        return null;
    }

    @Override
    public Page<CategoryResponse> getCategories(FindCategoriesQuery query) {
        return null;
    }
}
