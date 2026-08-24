package com.dodibo.learncore.elearningcore.category;

import com.dodibo.learncore.elearningcore.category.dto.CategoryResponse;
import com.dodibo.learncore.elearningcore.category.dto.CreateCategoryForm;
import com.dodibo.learncore.elearningcore.category.dto.CreateCategoryRequest;
import com.dodibo.learncore.elearningcore.category.dto.FindCategoriesQuery;
import com.dodibo.learncore.elearningcore.category.dto.UpdateCategoryForm;
import com.dodibo.learncore.elearningcore.category.dto.UpdateCategoryRequest;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.dodibo.learncore.permission.PermissionCodes.TENANT_CATEGORY_CREATE;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_CATEGORY_DELETE;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_CATEGORY_READ;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_CATEGORY_UPDATE;

@RestController
@RequestMapping("categories")
@RequiredArgsConstructor
@Tag(name = "Categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authz.can('" + TENANT_CATEGORY_CREATE + "')")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
            schema = @Schema(implementation = CreateCategoryForm.class)
    ))
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @ModelAttribute CreateCategoryRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(request, image));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authz.can('" + TENANT_CATEGORY_READ + "')")
    public ResponseEntity<Page<CategoryResponse>> getCategories(@ParameterObject FindCategoriesQuery query) {
        return ResponseEntity.ok(categoryService.getCategories(query));
    }

    @GetMapping("{uuid}")
    @PreAuthorize("@authz.can('" + TENANT_CATEGORY_READ + "')")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable String uuid) {
        return ResponseEntity.ok(categoryService.getCategory(uuid));
    }

    @PatchMapping(value = "{uuid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@authz.can('" + TENANT_CATEGORY_UPDATE + "')")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
            schema = @Schema(implementation = UpdateCategoryForm.class)
    ))
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable String uuid,
            @Valid @ModelAttribute UpdateCategoryRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(categoryService.updateCategory(uuid, request, image));
    }

    @DeleteMapping("{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authz.can('" + TENANT_CATEGORY_DELETE + "')")
    public ResponseEntity<Void> deleteCategory(@PathVariable String uuid) {
        categoryService.deleteCategory(uuid);
        return ResponseEntity.noContent().build();
    }
}
