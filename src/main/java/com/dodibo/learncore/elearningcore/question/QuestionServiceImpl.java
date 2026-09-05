package com.dodibo.learncore.elearningcore.question;

import com.dodibo.learncore.common.tenant.TenantStaffResolver;
import com.dodibo.learncore.elearningcore.category.Category;
import com.dodibo.learncore.elearningcore.category.CategoryRepository;
import com.dodibo.learncore.elearningcore.question.dto.CreateQuestionDto;
import com.dodibo.learncore.elearningcore.question.dto.FindQuestionQuery;
import com.dodibo.learncore.elearningcore.question.dto.GetQuestionResponse;
import com.dodibo.learncore.elearningcore.question.dto.UpdateQuestionDto;
import com.dodibo.learncore.exception.ResourceNotFoundException;
import com.dodibo.learncore.fileUpload.FilePurpose;
import com.dodibo.learncore.fileUpload.FileUploadService;
import com.dodibo.learncore.tenant.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionMapper questionMapper;
    private final QuestionTypeValidator questionTypeValidator;
    private final TenantStaffResolver tenantStaffResolver;
    private final FileUploadService fileUploadService;

    @Override
    @Transactional
    public GetQuestionResponse createQuestion(CreateQuestionDto request, MultipartFile image) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        questionTypeValidator.validateWrittenAnswerConfig(request.questionType(), request.writtenAnswerConfig());
        Category category = requireCategory(request.categoryUuid(), tenant.getId());
        String imageUrl = uploadImageIfPresent(image);
        Question question = questionMapper.toEntity(request, tenant.getId(), category, imageUrl);
        Question saved = questionRepository.save(question);
        return questionMapper.toResponse(requireQuestionWithWrittenConfig(saved.getUuid(), tenant.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetQuestionResponse> getQuestions(FindQuestionQuery query) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Specification<Question> spec = QuestionSpecification.fromQuery(query, tenant.getId());
        return questionRepository.findAll(spec, query.toPageable())
                .map(question -> questionMapper.toResponse(question, false));
    }

    @Override
    @Transactional(readOnly = true)
    public GetQuestionResponse getQuestion(String uuid) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        return questionMapper.toResponse(requireQuestionWithWrittenConfig(uuid, tenant.getId()));
    }

    @Override
    @Transactional
    public GetQuestionResponse updateQuestion(String uuid, UpdateQuestionDto request, MultipartFile image) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        questionTypeValidator.validateWrittenAnswerConfig(request.questionType(), request.writtenAnswerConfig());
        Question question = requireQuestionWithWrittenConfig(uuid, tenant.getId());
        Category category = requireCategory(request.categoryUuid(), tenant.getId());
        questionMapper.applyUpdate(question, request, category);
        if (image != null && !image.isEmpty()) {
            question.setImageUrl(fileUploadService.uploadImage(image, FilePurpose.QUESTION_IMAGE).url());
        }
        Question saved = questionRepository.save(question);
        return questionMapper.toResponse(requireQuestionWithWrittenConfig(saved.getUuid(), tenant.getId()));
    }

    @Override
    @Transactional
    public void deleteQuestion(String uuid) {
        Question question = requireQuestion(uuid);
        question.setDeleted(true);
        questionRepository.save(question);
    }

    private String uploadImageIfPresent(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null;
        }
        return fileUploadService.uploadImage(image, FilePurpose.QUESTION_IMAGE).url();
    }

    private Question requireQuestion(String uuid) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        return questionRepository.findByUuidAndTenantIdAndIsDeletedFalse(uuid, tenant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + uuid));
    }

    private Question requireQuestionWithWrittenConfig(String uuid, Long tenantId) {
        return questionRepository.findWithWrittenConfigByUuidAndTenantIdAndIsDeletedFalse(uuid, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + uuid));
    }

    private Category requireCategory(String categoryUuid, Long tenantId) {
        return categoryRepository.findByUuidAndTenantIdAndIsDeletedFalse(categoryUuid, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryUuid));
    }
}
