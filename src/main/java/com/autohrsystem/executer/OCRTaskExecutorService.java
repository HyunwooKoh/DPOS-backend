package com.autohrsystem.executer;

import java.util.LinkedList;
import java.util.Queue;

import com.autohrsystem.db.RepoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.autohrsystem.common.CommonApi;
import com.autohrsystem.common.Error.ErrorCode;
import com.autohrsystem.executer.task.OcrTask;
import com.autohrsystem.ocr.OcrParams;
import com.autohrsystem.db.task.TaskEntity;
import com.autohrsystem.db.task.TaskRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Service
public class OCRTaskExecutorService {
    final ThreadPoolTaskExecutor m_taskExecutor;
    @Autowired
    TaskRepository m_taskRepository;
    @Autowired
    RepoManager m_repoManager;
    Queue<OcrTask> m_tasks;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public OCRTaskExecutorService(@Qualifier("OCRTaskExecutor") ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        m_taskExecutor = threadPoolTaskExecutor;
        m_tasks = new LinkedList<>();
    }

    @Scheduled(fixedDelay = 100)
    private void pollingTask() {
        if (m_taskExecutor.getQueueSize() == m_taskExecutor.getQueueCapacity()) {
            logger.info(getClass().toString() + ": m_taskExecutor reached max queue size. doubled size");
            m_taskExecutor.setQueueCapacity(m_taskExecutor.getQueueCapacity() * 2);
        }
        if (!m_tasks.isEmpty()) {
            for (int i = 0; i < m_tasks.size(); i++) {
                OcrTask task = m_tasks.peek();
                TaskEntity entity = m_taskRepository.findByUuid(task.getUuid());
                entity.setStatus("Processing");
                m_taskExecutor.execute(task);
                m_taskRepository.save(entity);
            }
        }
    }

    @Async
    public void addTask(String uuid, String ext, String reqType) {
        String inputFilePath = CommonApi.getTempDir(uuid) + "origin" + ext;
        String outputFilePath = CommonApi.getTempDir(uuid) + "result.json";
        // TODO : FileHandler fileHandler = new FileHandler(uuid, ext, inputFilePath, outputFilePath); -> 비동기 처리 (threadPoolExecute)
        OcrParams param = new OcrParams(inputFilePath, outputFilePath, System.getenv("OCR_SERVER_URL"), ext);
        TaskEntity entity = new TaskEntity(uuid, "waiting", inputFilePath, outputFilePath);

        if (!reqType.isEmpty() && !param.isValidReqType(reqType)) {
            logger.error("Invalid reqType. uuid : " + uuid + ", reqType : " + reqType);
            entity.setStatus("Failure");
            entity.setErrorCode(ErrorCode.OCR_INVALID_REQ_TYPE);
            entity.setErrorMsg("Invalid request type");
        } else {
            param.setReqOption(reqType);
            OcrTask task = new OcrTask(param, uuid, reqType, m_repoManager);
            if (m_taskExecutor.getQueueSize() == 0) {
                entity.setStatus("Processing");
                m_taskRepository.save(entity);
                m_taskExecutor.execute(task);
            } else {
                m_tasks.add(task);
                m_taskRepository.save(entity);
            }
        }
    }
}
