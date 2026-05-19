package com.Sem2.DTDM.service.interFace;

import com.Sem2.DTDM.common.dto.FileRequest;
import com.Sem2.DTDM.common.entity.ConversionType;
import com.Sem2.DTDM.common.entity.FileTask;

public interface FileTaskServiceInterface {
    FileTask createTask(FileRequest request, ConversionType type);
}
