package com.Sem2.DTDM.service.impl;

import com.Sem2.DTDM.common.entity.FileTask;
import com.Sem2.DTDM.common.entity.TaskStatus;
import com.Sem2.DTDM.common.repository.FileTaskRepository;
import com.Sem2.DTDM.common.entity.ConversionType;
import com.Sem2.DTDM.service.WorkerServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkerServiceImpl implements WorkerServiceInterface {
    private final FileTaskRepository taskRepository;
    
    
    @Override
    public void processTask(String taskId, ConversionType conversionType) {
        log.info("[Worker] Starting to process task: {}", taskId);
        // 1. Tìm task trong MongoDB
        FileTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        try {
            // 2. Update trạng thái sang PROCESSING
            task.setStatus(TaskStatus.PROCESSING);
            taskRepository.save(task);

            // 3. Phân nhánh logic dựa trên loại conversion
            if (conversionType == ConversionType.WORD_TO_PDF) {
                handleWordToPdf(task);
            } else {
                handlePdfToWord(task);
            }

            // 4. Update trạng thái sang COMPLETED
            task.setStatus(TaskStatus.COMPLETED);
            taskRepository.save(task);
            log.info("[Worker] Successfully processed task: {}", taskId);

        } catch (Exception e) {
            // 5. Update trạng thái sang FAILED và lưu lỗi
            log.error("[Worker] Error while processing task {}: {}", taskId, e.getMessage());
            task.setStatus(TaskStatus.FAILED);
            task.setErrorMessage(e.getMessage());
            taskRepository.save(task);
        }
    }

    private void handleWordToPdf(FileTask task) {
        log.info("[Worker] Executing Word to PDF logic for file: {}", task.getFileNameOriginal());
        // TODO: Use POI to read .docx and OpenPDF to generate .pdf here
    }
    private void handlePdfToWord(FileTask task) {
        log.info("[Worker] Executing PDF to Word logic for file: {}", task.getFileNameOriginal());
        // TODO: Use PDFBox to extract text from .pdf and create .docx here
    }
}
