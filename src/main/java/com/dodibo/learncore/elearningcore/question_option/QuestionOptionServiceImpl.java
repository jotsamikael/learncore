package com.dodibo.learncore.elearningcore.question_option;

import com.dodibo.learncore.common.tenant.TenantStaffResolver;
import com.dodibo.learncore.elearningcore.question.Question;
import com.dodibo.learncore.elearningcore.question.QuestionRepository;
import com.dodibo.learncore.elearningcore.question.enums.QuestionType;
import com.dodibo.learncore.elearningcore.question_option.dto.CreateQuestionOptionRequest;
import com.dodibo.learncore.elearningcore.question_option.dto.FindQuestionOptionQuery;
import com.dodibo.learncore.elearningcore.question_option.dto.QuestionOptionResponseDto;
import com.dodibo.learncore.elearningcore.question_option.dto.UpdateQuestionOptionRequest;
import com.dodibo.learncore.exception.OperationNotPermittedException;
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
public class QuestionOptionServiceImpl implements QuestionOptionService {

    private final QuestionOptionRepository questionOptionRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionMapper questionOptionMapper;
    private final TenantStaffResolver tenantStaffResolver;
    private final FileUploadService fileUploadService;

    @Override
    @Transactional
    public QuestionOptionResponseDto createQuestionOption(CreateQuestionOptionRequest request, MultipartFile image) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Question question = requireQuestion(request.questionUuid(), tenant.getId());
        rejectWrittenAnswerQuestion(question);
        String imageUrl = uploadImageIfPresent(image);
        QuestionOption option = questionOptionMapper.toEntity(request, question, imageUrl);
        return questionOptionMapper.toResponse(questionOptionRepository.save(option));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionOptionResponseDto> getQuestionOptions(FindQuestionOptionQuery query) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Specification<QuestionOption> spec = QuestionOptionSpecification.fromQuery(query, tenant.getId());
        return questionOptionRepository.findAll(spec, query.toPageable())
                .map(questionOptionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionOptionResponseDto getQuestionOption(String uuid) {
        return questionOptionMapper.toResponse(requireQuestionOption(uuid));
    }

    @Override
    @Transactional
    public QuestionOptionResponseDto updateQuestionOption(
            String uuid,
            UpdateQuestionOptionRequest request,
            MultipartFile image) {
        QuestionOption option = requireQuestionOption(uuid);
        questionOptionMapper.applyUpdate(option, request);
        if (image != null && !image.isEmpty()) {
            option.setImageUrl(fileUploadService.uploadImage(image, FilePurpose.OPTION_IMAGE).url());
        }
        return questionOptionMapper.toResponse(questionOptionRepository.save(option));
    }

    @Override
    @Transactional
    public void deleteQuestionOption(String uuid) {
        QuestionOption option = requireQuestionOption(uuid);
        option.setDeleted(true);
        questionOptionRepository.save(option);
    }

    private String uploadImageIfPresent(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null;
        }
        return fileUploadService.uploadImage(image, FilePurpose.OPTION_IMAGE).url();
    }

    private void rejectWrittenAnswerQuestion(Question question) {
        if (question.getQuestionType() == QuestionType.STRUCTURAL
                || question.getQuestionType() == QuestionType.ESSAY) {
            throw new OperationNotPermittedException(
                    "Question options are not supported for written answer questions");
        }
    }

    private QuestionOption requireQuestionOption(String uuid) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        return questionOptionRepository.findByUuidAndTenantId(uuid, tenant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Question option not found: " + uuid));
    }

    private Question requireQuestion(String questionUuid, Long tenantId) {
        return questionRepository.findByUuidAndTenantIdAndIsDeletedFalse(questionUuid, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + questionUuid));
    }
}
