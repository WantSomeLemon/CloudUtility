package com.Sem2.DTDM.service;

import com.Sem2.DTDM.common.dto.ConversionMessage;
import com.Sem2.DTDM.common.dto.FileRequest;
import com.Sem2.DTDM.common.entity.ConversionType;
import com.Sem2.DTDM.common.entity.TaskStatus;
import com.Sem2.DTDM.common.entity.FileTask;
import com.Sem2.DTDM.common.repository.FileTaskRepository;
import com.Sem2.DTDM.service.interFace.FileTaskServiceInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileTaskService implements FileTaskServiceInterface {
    
    private final FileTaskRepository repository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.queue.conversion-queue}")
    private String queueName;
    
    @Override
    public FileTask createTask(FileRequest request, ConversionType type) {
        log.info("Initiating task creation for file: {}", request.getFileName());

        // 1. Save task to MongoDB
        FileTask task = new FileTask();
        task.setFileNameOriginal(request.getFileName());
        task.setConversionType(type);
        task.setStatus(TaskStatus.PENDING);
        task.setStoragePathInput("/app/storage/" + request.getFileName());

        FileTask savedTask = repository.save(task);
        log.info("Task saved successfully with ID: {}", savedTask.getId());

        // 2. Publish message to Redis Queue
        try {
            
            ConversionMessage msg = new ConversionMessage();
            msg.setTaskId(savedTask.getId());
            msg.setConversionType(type);

            String jsonMessage = objectMapper.writeValueAsString(msg);
            redisTemplate.convertAndSend(queueName, jsonMessage);

            log.info("Message published to Redis queue '{}' for task ID: {}", queueName, savedTask.getId());
        } catch (Exception e) {
            log.error("Failed to publish message to Redis for task ID: {}. Error: {}", savedTask.getId(), e.getMessage());
            // Optionally: Update task status to FAILED if queueing fails
        }
        
        return savedTask;
    }
}
