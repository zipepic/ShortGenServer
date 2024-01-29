package com.example.shortgenserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;


@RestController
public class VideoController {
    private final com.example.shortgenserver.service.GenerationService generationService;
    @Autowired
    public VideoController(com.example.shortgenserver.service.GenerationService generationService) {
        this.generationService = generationService;
    }

    @GetMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<InputStreamResource> generateVideo() {


        InputStream videoStream = new ByteArrayInputStream(generateVideoBytes().toByteArray());

        InputStreamResource inputStreamResource = new InputStreamResource(videoStream);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"video.mp4\"")
                .body(inputStreamResource);
    }

    private ByteArrayOutputStream generateVideoBytes() {
        return generationService.createAndSaveVideo("Кто знает кто такие бобры?", "Бобер - курва", "@zz1p");
    }


}
