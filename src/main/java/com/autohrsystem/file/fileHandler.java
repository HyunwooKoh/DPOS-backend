package com.autohrsystem.file;

import com.autohrsystem.common.api.CommonApi;

import io.vertx.core.json.JsonArray;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class FileHandler {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public String getFile(String uuid) {
        String localPath = CommonApi.generateTempPath() + "/" + uuid;
        if (!checkFileIsExist(uuid)) {
            // TODO: throw Error
        }

        return localPath;
    }

    private boolean checkFileIsExist(String uuid) {
        try {
            UriComponents uri = UriComponentsBuilder.fromHttpUrl("http://fileServer/get/").build();
            ResponseEntity<?> resultMap = new RestTemplate().exchange(
                    uri.toString(), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Object.class);

            if (resultMap.getStatusCode().value() != 200) {
                return false;
            }

            JsonArray files = new JsonArray(resultMap.getBody().toString());
            for (int i = 0 ; i < files.size(); i++) {
                if(files.getString(i) == uuid) {
                    return true;
                }
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error occur during get file list from file server\n" +
                    e.toString() + "\n");
            return false;
        }
        return true;
    }

}