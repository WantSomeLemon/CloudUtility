package com.Sem2.DTDM.service;

import com.Sem2.DTDM.common.entity.TaskStatus;
import com.Sem2.DTDM.common.entity.FileTask;
import com.Sem2.DTDM.common.repository.FileTaskRepository;
import com.Sem2.DTDM.service.interFace.FileTaskServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileTaskService implements FileTaskServiceInterface {
    @Autowired
    private FileTaskRepository repository;
    
    
    @Override
    public FileTask createTask(String fileName) {
        FileTask task = new FileTask();
        task.setFileNameOriginal(fileName);
        task.setStatus(TaskStatus.PENDING);
        return repository.save(task);
    }
}
