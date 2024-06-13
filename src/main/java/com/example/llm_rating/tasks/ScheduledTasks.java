package com.example.llm_rating.tasks;

import com.aliyun.oss.model.PutObjectResult;
import com.aliyuncs.exceptions.ClientException;

import com.example.llm_rating.service.CommunicationService;
import com.example.llm_rating.service.FileService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

@Component
@AllArgsConstructor
public class ScheduledTasks {

    private final CommunicationService communicationService;
    private final FileService fileService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void generateLeaderBoard() throws  ClientException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedYesterday = yesterday.format(formatter);
        CompletableFuture<PutObjectResult> future = fileService.uploadFile("llmbattle",formattedYesterday+"-conv.json","Logs/");
        future.thenAccept(putObjectResult -> {
            try {
                communicationService.computeElo();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


    }
}
