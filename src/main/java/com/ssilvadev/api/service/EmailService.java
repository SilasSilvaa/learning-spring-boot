package com.ssilvadev.api.service;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssilvadev.api.config.EmailConfig;
import com.ssilvadev.api.data.dto.request.EmailRequestDTO;
import com.ssilvadev.api.mail.EmailSender;

@Service
public class EmailService {

    @Autowired
    private EmailSender sender;

    @Autowired
    private EmailConfig config;

    public void sendSimpleEmail(EmailRequestDTO request) {
        sender
            .to(request.getTo())
            .withSubject(request.getSubject())
            .withMessage(request.getBody())
            .send(config);
    }

    public void sendEmailWithAttachment(String json, MultipartFile attachment) {
        File tempFile = null;
        try {
            var request = new ObjectMapper().readValue(json, EmailRequestDTO.class);
            tempFile = File.createTempFile("attachment", attachment.getOriginalFilename());

            sender
                .to(request.getTo())
                .withSubject(request.getSubject())
                .withMessage(request.getBody())
                .withAttachment(tempFile.getAbsolutePath())
                .send(config);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing e-mail request ", e);
        } catch (IOException e) {
            throw new RuntimeException("Error processing the attachment ", e);
        } finally {
                if (tempFile != null && tempFile.exists()) tempFile.delete(); 
        }
    }

}
