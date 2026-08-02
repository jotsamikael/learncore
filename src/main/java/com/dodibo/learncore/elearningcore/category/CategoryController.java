package com.dodibo.learncore.elearningcore.category;

import com.dodibo.learncore.elearningcore.category.dto.CategoryResponse;
import com.dodibo.learncore.elearningcore.category.dto.CreateCategoryRequest;
import com.dodibo.learncore.elearningcore.category.dto.FindCategoriesQuery;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("categories")
@RequiredArgsConstructor
@Tag(name = "Categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("")
    public ResponseEntity<CategoryResponse> createCategoryResponse(@RequestBody @Valid CreateCategoryRequest createCategoryRequest){
      return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(createCategoryRequest));
    }

    @GetMapping
    @PreAuthorize("")
    public ResponseEntity<Page<CategoryResponse>> getCategories(@ModelAttribute FindCategoriesQuery query){
        return  ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategories(query));
    }
}
