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

    private boolean getFileFromServer(String uuid, String localFilePath) {
        //Spring restTemplate
        HashMap<String, Object> result = new HashMap<String, Object>();
        try {
            UriComponents uri = UriComponentsBuilder.fromHttpUrl("http://fileServer/get/" + uuid).build();
            ResponseEntity<?> resultMap = new RestTemplate().exchange(
                    uri.toString(), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Object.class);

            result.put("statusCode", resultMap.getStatusCode()); //http status code를 확인
            result.put("header", resultMap.getHeaders()); //헤더 정보 확인
            result.put("body", resultMap.getBody()); //실제 데이터 정보 확인

            //에러처리해야댐
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            result.put("statusCode", e.getRawStatusCode());
            result.put("body", e.getStatusText());
            System.out.println("error");
            System.out.println(e.toString());
            return false;
        } catch (Exception e) {
            result.put("statusCode", "999");
            result.put("body", "excpetion오류");
            System.out.println(e.toString());
            return false;
        }
        return true;
    }
}