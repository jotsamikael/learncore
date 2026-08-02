package com.dodibo.learncore.elearningcore.category;

import com.dodibo.learncore.elearningcore.category.dto.CategoryResponse;
import com.dodibo.learncore.elearningcore.category.dto.CreateCategoryRequest;
import com.dodibo.learncore.elearningcore.category.dto.FindCategoriesQuery;
import com.dodibo.learncore.role.dto.FindRolesQuery;
import com.dodibo.learncore.role.dto.RoleResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService {

   CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest);

    Page<CategoryResponse> getCategories(FindCategoriesQuery query);

}
