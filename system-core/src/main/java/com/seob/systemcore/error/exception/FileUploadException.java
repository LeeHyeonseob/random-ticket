package com.seob.systemcore.error.exception;

import com.seob.systemcore.error.ErrorCode;

public class FileUploadException extends BaseException {
    public static final FileUploadException FILE_UPLOAD_FAILED = new FileUploadException(ErrorCode.FILE_UPLOAD_FAILED);
    public static final FileUploadException FILE_DELETE_FAILED = new FileUploadException(ErrorCode.FILE_DELETE_FAILED);
    public static final FileUploadException INVALID_FILE_FORMAT = new FileUploadException(ErrorCode.INVALID_FILE_FORMAT);

    private FileUploadException(ErrorCode errorCode) {
        super(errorCode);
    }
}
