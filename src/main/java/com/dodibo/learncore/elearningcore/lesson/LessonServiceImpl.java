package com.dodibo.learncore.elearningcore.lesson;

import com.dodibo.learncore.common.tenant.TenantStaffResolver;
import com.dodibo.learncore.elearningcore.category.Category;
import com.dodibo.learncore.elearningcore.category.CategoryRepository;
import com.dodibo.learncore.elearningcore.lesson.dto.CreateLessonRequest;
import com.dodibo.learncore.elearningcore.lesson.dto.FindLessonQuery;
import com.dodibo.learncore.elearningcore.lesson.dto.LessonResponse;
import com.dodibo.learncore.elearningcore.lesson.dto.UpdateLessonRequest;
import com.dodibo.learncore.exception.ResourceNotFoundException;
import com.dodibo.learncore.tenant.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final CategoryRepository categoryRepository;
    private final LessonMapper lessonMapper;
    private final TenantStaffResolver tenantStaffResolver;

    @Override
    @Transactional
    public LessonResponse createLesson(CreateLessonRequest request) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Category category = requireCategory(request.categoryUuid(), tenant.getId());
        Lesson lesson = lessonMapper.toEntity(request, tenant, category);
        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LessonResponse> getLessons(FindLessonQuery query) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Specification<Lesson> spec = LessonSpecification.fromQuery(query, tenant.getId());
        return lessonRepository.findAll(spec, query.toPageable())
                .map(lessonMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public LessonResponse getLesson(String uuid) {
        return lessonMapper.toResponse(requireLesson(uuid));
    }

    @Override
    @Transactional
    public LessonResponse updateLesson(String uuid, UpdateLessonRequest request) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Lesson lesson = requireLesson(uuid);
        Category category = requireCategory(request.categoryUuid(), tenant.getId());
        lessonMapper.applyUpdate(lesson, request, category);
        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    public void deleteLesson(String uuid) {
        Lesson lesson = requireLesson(uuid);
        lesson.setDeleted(true);
        lessonRepository.save(lesson);
    }

    private Lesson requireLesson(String uuid) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        return lessonRepository.findByUuidAndTenant_IdAndIsDeletedFalse(uuid, tenant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found: " + uuid));
    }

    private Category requireCategory(String categoryUuid, Long tenantId) {
        return categoryRepository.findByUuidAndTenantIdAndIsDeletedFalse(categoryUuid, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryUuid));
    }
}
