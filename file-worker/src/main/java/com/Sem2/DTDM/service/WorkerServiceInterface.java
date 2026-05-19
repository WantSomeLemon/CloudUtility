package com.Sem2.DTDM.service;

import com.Sem2.DTDM.common.entity.ConversionType;

public interface WorkerServiceInterface {
    void processTask(String taskId, ConversionType conversionType);
}
