package com.dodibo.learncore.elearningcore.question;

import com.dodibo.learncore.common.tenant.TenantStaffResolver;
import com.dodibo.learncore.elearningcore.category.Category;
import com.dodibo.learncore.elearningcore.category.CategoryRepository;
import com.dodibo.learncore.elearningcore.question.dto.CreateQuestionDto;
import com.dodibo.learncore.elearningcore.question.dto.FindQuestionQuery;
import com.dodibo.learncore.elearningcore.question.dto.GetQuestionResponse;
import com.dodibo.learncore.elearningcore.question.dto.UpdateQuestionDto;
import com.dodibo.learncore.exception.ResourceNotFoundException;
import com.dodibo.learncore.tenant.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionMapper questionMapper;
    private final TenantStaffResolver tenantStaffResolver;

    @Override
    @Transactional
    public GetQuestionResponse createQuestion(CreateQuestionDto request) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Category category = requireCategory(request.categoryUuid(), tenant.getId());
        Question question = questionMapper.toEntity(request, tenant.getId(), category);
        return questionMapper.toResponse(questionRepository.save(question));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetQuestionResponse> getQuestions(FindQuestionQuery query) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Specification<Question> spec = QuestionSpecification.fromQuery(query, tenant.getId());
        return questionRepository.findAll(spec, query.toPageable())
                .map(questionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public GetQuestionResponse getQuestion(String uuid) {
        return questionMapper.toResponse(requireQuestion(uuid));
    }

    @Override
    @Transactional
    public GetQuestionResponse updateQuestion(String uuid, UpdateQuestionDto request) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Question question = requireQuestion(uuid);
        Category category = requireCategory(request.categoryUuid(), tenant.getId());
        questionMapper.applyUpdate(question, request, category);
        return questionMapper.toResponse(questionRepository.save(question));
    }

    @Override
    @Transactional
    public void deleteQuestion(String uuid) {
        Question question = requireQuestion(uuid);
        question.setDeleted(true);
        questionRepository.save(question);
    }

    private Question requireQuestion(String uuid) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        return questionRepository.findByUuidAndTenantIdAndIsDeletedFalse(uuid, tenant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + uuid));
    }

    private Category requireCategory(String categoryUuid, Long tenantId) {
        return categoryRepository.findByUuidAndTenantIdAndIsDeletedFalse(categoryUuid, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryUuid));
    }
}
