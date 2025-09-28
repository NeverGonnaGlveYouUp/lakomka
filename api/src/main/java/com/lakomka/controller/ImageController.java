package com.lakomka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ImageController {

    @Autowired
    private ResourceLoader resourceLoader;

    @RequestMapping(path = "/getImage/{filePath}", method = RequestMethod.GET)
    public ResponseEntity<Resource> getImage(@PathVariable String filePath) throws IOException {
        Path path = Paths.get("api/target/classes/META-INF/images/"+filePath);
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

}
