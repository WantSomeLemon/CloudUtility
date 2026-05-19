package com.Sem2.DTDM.listener;


import com.Sem2.DTDM.common.dto.ConversionMessage;
import com.Sem2.DTDM.service.WorkerServiceInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class FileTaskListener implements MessageListener {
    
    private static final Logger log = LoggerFactory.getLogger(FileTaskListener.class);
    
    @Autowired
    private WorkerServiceInterface workerService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String payload = new String(message.getBody());
        log.info("[Listener] Nhận taskMessage từ Redis: {}", payload);

        try {
            ConversionMessage taskMessage = objectMapper.readValue(payload, ConversionMessage.class);
            log.info("[Listener] Parse thành công - taskId: {}, conversionType: {}",
                    taskMessage.getTaskId(), taskMessage.getConversionType());

            workerService.processTask(taskMessage.getTaskId(), taskMessage.getConversionType());

        } catch (Exception e) {
            log.error("[Listener] Parse taskMessage thất bại - payload: {}, lỗi: {}", payload, e.getMessage());
        }
    }
}
