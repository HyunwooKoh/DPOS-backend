package com.autohrsystem.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import com.autohrsystem.common.CommonApi;
import com.autohrsystem.db.RepoManager;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    RepoManager repoManager;

    @GetMapping("/resultImage/{uuid}")
    public ResponseEntity<Resource> getResultImage(@PathVariable String uuid, HttpServletRequest request) {
        if (repoManager.taskSuccessed(uuid)) {
            Path imagePath = Path.of(CommonApi.getTempDir(uuid) + "diff.png");
            try {
                Resource res = new UrlResource(imagePath.toUri());
                String contentType = request.getServletContext().getMimeType(res.getFile().getAbsolutePath());

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + res.getFilename() + "\"")
                        .body(res);

            } catch (IOException ignored) {}
        }
        return ResponseEntity.badRequest().body(null);
    }

}
