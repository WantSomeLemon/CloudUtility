package com.Sem2.DTDM.repository;

import com.Sem2.DTDM.entity.FileTask;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileTaskRepository extends MongoRepository<FileTask,String> {
}
