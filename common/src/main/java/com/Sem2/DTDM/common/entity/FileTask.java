package com.Sem2.DTDM.common.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Data
@Document(collection = "file_task")
public class FileTask {
    @Id
    private String id;
    private String fileNameOriginal;
    private ConversionType conversionType;
    private TaskStatus status;
    private String storagePathInput;
    private String storagePathOutput;
    
    // Số lần đã retry (mặc định 0)
    private int retryCount = 0;

    // Lý do thất bại (chỉ có giá trị khi status = FAILED)
    private String errorMessage;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
