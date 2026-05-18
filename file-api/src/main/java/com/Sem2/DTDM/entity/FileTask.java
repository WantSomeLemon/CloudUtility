package com.Sem2.DTDM.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.Sem2.DTDM.common.entity.TaskStatus;


@Data
@Document(collection = "file_task")
public class FileTask {
    @Id
    private String id;
    private String fileName;
    private TaskStatus status;
}
