package com.dodibo.learncore.fileUpload;

import com.dodibo.learncore.common.BaseEntity;
import com.dodibo.learncore.common.UuidGenerator;
import jakarta.persistence.Entity;

@Entity
public class FileUpload extends BaseEntity {

    private String originalName;

    private String fileUrl;

    private Integer size;

    private String cloudinaryPublicId;

    private String mimeType;

    private FilePurpose purpose;

    @Override
    protected String uuidPrefix() {
        return UuidGenerator.generate("uf");
    }
}
