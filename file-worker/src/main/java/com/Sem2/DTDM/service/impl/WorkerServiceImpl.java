package com.Sem2.DTDM.service.impl;

import com.Sem2.DTDM.common.entity.FileTask;
import com.Sem2.DTDM.common.entity.TaskStatus;
import com.Sem2.DTDM.common.repository.FileTaskRepository;
import com.Sem2.DTDM.common.entity.ConversionType;
import com.Sem2.DTDM.service.WorkerServiceInterface;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkerServiceImpl implements WorkerServiceInterface {
    private final FileTaskRepository taskRepository;

    @Value("${app.storage.path}")
    private String baseStoragePath;
    
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

    private void handleWordToPdf(FileTask task) throws Exception{
        log.info("[Worker] Executing Word to PDF logic for file: {}", task.getFileNameOriginal());
        // TODO: Use POI to read .docx and OpenPDF to generate .pdf here

        File inputFile = new File(baseStoragePath, task.getFileNameOriginal());
        String outputFileName = task.getFileNameOriginal() + task.getId() + ".pdf";
        File outputFile = new File(baseStoragePath, outputFileName);

        log.info("[Worker] Converting: {} -> {}", inputFile.getAbsolutePath(), outputFile.getAbsolutePath());
        
        // 1. Load the Word document
        try (FileInputStream fis = new FileInputStream(inputFile);
             XWPFDocument document = new XWPFDocument(fis)) {

            // 2. Initialize PDF Document
            try (FileOutputStream fos = new FileOutputStream(outputFile);
                 Document pdfDocument = new Document()) {

                PdfWriter.getInstance(pdfDocument, fos);
                pdfDocument.open();

                // 3. Extract text from paragraphs and write to PDF
                for (XWPFParagraph paragraph : document.getParagraphs()) {
                    pdfDocument.add(new Paragraph(paragraph.getText()));
                }
                pdfDocument.close();
            }
        }
        task.setStoragePathOutput(outputFile.getAbsolutePath());
        log.info("[Worker] PDF generation completed at: {}", task.getStoragePathOutput());
    }
    private void handlePdfToWord(FileTask task) throws Exception{
        log.info("[Worker] Executing PDF to Word logic for file: {}", task.getFileNameOriginal());
        // TODO: Use PDFBox to extract text from .pdf and create .docx here

        File inputFile = new File(baseStoragePath, task.getFileNameOriginal());
        String outputFileName = task.getFileNameOriginal() + task.getId() + ".docx";
        File outputFile = new File(baseStoragePath, outputFileName);

        log.info("[Worker] Converting: {} -> {}", inputFile.getAbsolutePath(), outputFile.getAbsolutePath());

        // 1. Load PDF document
        try (PDDocument pdfDocument = Loader.loadPDF(inputFile);
             XWPFDocument wordDocument = new XWPFDocument()) {

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(pdfDocument);

            // 2. Create Word paragraph and write content
            XWPFParagraph paragraph = wordDocument.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(text);

            // 3. Save Word document
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                wordDocument.write(fos);
            }

            
        }
        task.setStoragePathOutput(outputFile.getAbsolutePath());
        log.info("[Worker] Word generation completed at: {}", task.getStoragePathOutput());
    }
}
