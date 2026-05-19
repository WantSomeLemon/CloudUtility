package com.Sem2.DTDM.listener;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class FileTaskListener implements MessageListener {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileTaskListener.class);
    
    @Autowired
    private WorkerServiceInterface workerService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String payload = new String(message.getBody());
        log.info("[Listener] Nhận message từ Redis: {}", payload);

        try {
            ConversionMessage message = objectMapper.readValue(payload, ConversionMessage.class);
            log.info("[Listener] Parse thành công - taskId: {}, conversionType: {}",
                    message.getTaskId(), message.getConversionType());

            workerService.processTask(message.getTaskId(), message.getConversionType());

        } catch (Exception e) {
            log.error("[Listener] Parse message thất bại - payload: {}, lỗi: {}", payload, e.getMessage());
        }
    }
}
