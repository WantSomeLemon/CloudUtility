package com.Sem2.DTDM.common.dto;

import com.Sem2.DTDM.common.entity.ConversionType;
import lombok.Data;

@Data
public class ConversionMessage {
    private String taskId;
    private ConversionType conversionType;
}
