package com.dodibo.learncore.elearningcore.category;

import com.dodibo.learncore.common.tenant.TenantStaffResolver;
import com.dodibo.learncore.elearningcore.category.dto.CategoryResponse;
import com.dodibo.learncore.elearningcore.category.dto.CreateCategoryRequest;
import com.dodibo.learncore.elearningcore.category.dto.FindCategoriesQuery;
import com.dodibo.learncore.elearningcore.category.dto.UpdateCategoryRequest;
import com.dodibo.learncore.elearningcore.language.Language;
import com.dodibo.learncore.elearningcore.language.LanguageRepository;
import com.dodibo.learncore.exception.OperationNotPermittedException;
import com.dodibo.learncore.exception.ResourceAlreadyExistsException;
import com.dodibo.learncore.exception.ResourceNotFoundException;
import com.dodibo.learncore.fileUpload.FilePurpose;
import com.dodibo.learncore.fileUpload.FileUploadService;
import com.dodibo.learncore.tenant.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final LanguageRepository languageRepository;
    private final CategoryMapper categoryMapper;
    private final TenantStaffResolver tenantStaffResolver;
    private final FileUploadService fileUploadService;

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest, MultipartFile image) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        if (categoryRepository.findBySlugAndTenantId(createCategoryRequest.slug(), tenant.getId()).isPresent()) {
            throw new ResourceAlreadyExistsException(
                    "Category with slug " + createCategoryRequest.slug() + " already exists");
        }
        Category parent = resolveParent(createCategoryRequest.parentUuid(), tenant.getId(), null);
        Language language = resolveLanguage(createCategoryRequest.languageUuid(), tenant.getId());
        String imageUrl = uploadImageIfPresent(image);
        Category category = categoryMapper.toEntity(
                createCategoryRequest, tenant.getId(), parent, language, imageUrl);
        return toResponseWithChildren(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getCategories(FindCategoriesQuery query) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Specification<Category> spec = CategorySpecification.fromQuery(query, tenant.getId());
        return categoryRepository.findAll(spec, query.toPageable())
                .map(this::toResponseWithChildren);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategory(String uuid) {
        return toResponseWithChildren(requireCategory(uuid));
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(String uuid, UpdateCategoryRequest request, MultipartFile image) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Category category = requireCategory(uuid);
        if (categoryRepository.findBySlugAndTenantIdAndUuidNot(request.slug(), tenant.getId(), uuid).isPresent()) {
            throw new ResourceAlreadyExistsException("Category with slug " + request.slug() + " already exists");
        }
        Category parent = resolveParent(request.parentUuid(), tenant.getId(), uuid);
        Language language = resolveLanguage(request.languageUuid(), tenant.getId());
        categoryMapper.applyUpdate(category, request, parent, language);
        if (image != null && !image.isEmpty()) {
            category.setImageUrl(fileUploadService.uploadImage(image, FilePurpose.CATEGORY_IMAGE).url());
        }
        return toResponseWithChildren(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(String uuid) {
        Category category = requireCategory(uuid);
        category.setDeleted(true);
        categoryRepository.save(category);
    }

    private CategoryResponse toResponseWithChildren(Category category) {
        return toResponseWithChildren(category, new HashSet<>());
    }

    private CategoryResponse toResponseWithChildren(Category category, Set<Long> visited) {
        if (category == null) {
            return null;
        }
        if (!visited.add(category.getId())) {
            return categoryMapper.toResponse(category);
        }
        List<CategoryResponse> children = categoryRepository
                .findByTenantIdAndParent_IdAndIsDeletedFalseOrderByNameAsc(category.getTenantId(), category.getId())
                .stream()
                .map(child -> toResponseWithChildren(child, visited))
                .toList();
        return categoryMapper.toResponse(category, children);
    }

    private String uploadImageIfPresent(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null;
        }
        return fileUploadService.uploadImage(image, FilePurpose.CATEGORY_IMAGE).url();
    }

    private Category requireCategory(String uuid) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        return categoryRepository.findByUuidAndTenantIdAndIsDeletedFalse(uuid, tenant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + uuid));
    }

    private Category resolveParent(String parentUuid, Long tenantId, String currentUuid) {
        if (!StringUtils.hasText(parentUuid)) {
            return null;
        }
        if (currentUuid != null && parentUuid.equals(currentUuid)) {
            throw new OperationNotPermittedException("Category cannot be its own parent");
        }
        return categoryRepository.findByUuidAndTenantIdAndIsDeletedFalse(parentUuid, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent category not found: " + parentUuid));
    }

    private Language resolveLanguage(String languageUuid, Long tenantId) {
        if (!StringUtils.hasText(languageUuid)) {
            return null;
        }
        return languageRepository.findByUuidAndTenantIdAndIsDeletedFalse(languageUuid, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Language not found: " + languageUuid));
    }
}
