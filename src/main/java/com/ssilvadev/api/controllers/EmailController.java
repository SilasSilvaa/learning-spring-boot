package com.ssilvadev.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssilvadev.api.controllers.docs.EmailControllerDocs;
import com.ssilvadev.api.data.dto.request.EmailRequestDTO;
import com.ssilvadev.api.service.EmailService;

@RestController
@RequestMapping("/api/email/v1")
public class EmailController implements EmailControllerDocs {

    @Autowired
    private EmailService service;

    @Override
    @PostMapping
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO request) {
        service.sendSimpleEmail(request);

        return ResponseEntity.ok().body("e-mail sent with success");
    }

    @Override
    @PostMapping(value = "/withAttachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> sendEmailWithAttachment(
            @RequestParam("json") String json,
            @RequestParam(name = "attachment") MultipartFile attachment) {

        service.sendEmailWithAttachment(json, attachment);

        return ResponseEntity.ok().body("e-mail sent with success");
    }

}
