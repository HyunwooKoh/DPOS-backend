package com.autohrsystem.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ExtractBody {
    private String reqType;
    private String ext;
    private Mono<MultipartFile> files;
}
