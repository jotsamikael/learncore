package com.dodibo.learncore.elearningcore.lesson;

import com.dodibo.learncore.common.tenant.TenantStaffResolver;
import com.dodibo.learncore.elearningcore.category.Category;
import com.dodibo.learncore.elearningcore.category.CategoryRepository;
import com.dodibo.learncore.elearningcore.common.DisplayOrderHelper;
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
    private final LessonQuestionRepository lessonQuestionRepository;
    private final CategoryRepository categoryRepository;
    private final LessonMapper lessonMapper;
    private final TenantStaffResolver tenantStaffResolver;

    @Override
    @Transactional
    public LessonResponse createLesson(CreateLessonRequest request) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Category category = requireCategory(request.categoryUuid(), tenant.getId());

        long currentCount = lessonRepository.countByCategory_IdAndIsDeletedFalse(category.getId());
        int position = DisplayOrderHelper.resolveOrderForCreate(request.position(), currentCount);
        if (position <= currentCount) {
            lessonRepository.incrementPositionsFrom(category.getId(), position);
        }

        Lesson lesson = lessonMapper.toEntity(request, tenant, category, position);
        return toResponse(lessonRepository.save(lesson));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LessonResponse> getLessons(FindLessonQuery query) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Specification<Lesson> spec = LessonSpecification.fromQuery(query, tenant.getId());
        return lessonRepository.findAll(spec, query.toPageable())
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public LessonResponse getLesson(String uuid) {
        return toResponse(requireLesson(uuid));
    }

    @Override
    @Transactional
    public LessonResponse updateLesson(String uuid, UpdateLessonRequest request) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Lesson lesson = requireLesson(uuid);
        Category category = requireCategory(request.categoryUuid(), tenant.getId());

        Long oldCategoryId = lesson.getCategory().getId();
        int oldPosition = lesson.getPosition();
        Long newCategoryId = category.getId();
        int requestedPosition = request.position();

        if (!oldCategoryId.equals(newCategoryId)) {
            lessonRepository.decrementPositionsAbove(oldCategoryId, oldPosition);
            long newCount = lessonRepository.countByCategory_IdAndIsDeletedFalse(newCategoryId);
            int position = DisplayOrderHelper.resolveOrderForCreate(requestedPosition, newCount);
            if (position <= newCount) {
                lessonRepository.incrementPositionsFrom(newCategoryId, position);
            }
            lessonMapper.applyUpdate(lesson, request, category, position);
        } else {
            long currentCount = lessonRepository.countByCategory_IdAndIsDeletedFalse(oldCategoryId);
            int newPosition = DisplayOrderHelper.requireOrderForUpdate(requestedPosition, currentCount);
            if (oldPosition != newPosition) {
                if (newPosition < oldPosition) {
                    lessonRepository.shiftPositionsUp(oldCategoryId, newPosition, oldPosition, lesson.getId());
                } else {
                    lessonRepository.shiftPositionsDown(oldCategoryId, oldPosition, newPosition, lesson.getId());
                }
            }
            lessonMapper.applyUpdate(lesson, request, category, newPosition);
        }

        return toResponse(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    public void deleteLesson(String uuid) {
        Lesson lesson = requireLesson(uuid);
        Long categoryId = lesson.getCategory().getId();
        int deletedPosition = lesson.getPosition();

        lesson.setDeleted(true);
        lessonRepository.decrementPositionsAbove(categoryId, deletedPosition);
        lesson.setPosition(-Math.toIntExact(lesson.getId()));
        lessonRepository.save(lesson);
    }

    private LessonResponse toResponse(Lesson lesson) {
        int exerciseCount = (int) lessonQuestionRepository.countByLesson_Id(lesson.getId());
        return lessonMapper.toResponse(lesson, exerciseCount);
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
