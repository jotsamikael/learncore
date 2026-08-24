package com.dodibo.learncore.fileUpload;

public final class CloudinaryFolders {

    private CloudinaryFolders() {
    }

    public static String forPurpose(FilePurpose purpose, String rootFolder) {
        String base = rootFolder.endsWith("/") ? rootFolder.substring(0, rootFolder.length() - 1) : rootFolder;
        return switch (purpose) {
            case PROFILE_PICTURE -> base + "/profile_pictures";
            case TENANT_LOGO -> base + "/tenants";
            case CATEGORY_IMAGE -> base + "/categories";
            case LESSON_IMAGE, LESSON_BANK -> base + "/lessons";
            case QUESTION_IMAGE, OPTION_IMAGE, QUESTION_BANK -> base + "/questions";
        };
    }
}
