package com.autohrsystem.file;

import com.autohrsystem.common.CommonApi;
import com.autohrsystem.common.Error;
import com.autohrsystem.common.ErrorCode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.vertx.core.json.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileHandler {
    @Autowired
    private Environment env;
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final String m_uuid;
    private final String m_ext;
    private final String m_originFilePath;
    private final String m_resulFilePath;
    private final String m_fileServerUrl;
    public FileHandler(String uuid, String ext, String inputFilePath, String outputFilePath) {
        m_uuid = uuid;
        m_ext = "." + ext;
        m_originFilePath = inputFilePath;
        m_resulFilePath = outputFilePath;
        m_fileServerUrl = env.getProperty("FILE_SERVER_URL") + "/files/";
    }

    public String getFile() throws Error {
        if (!checkFileIsExist(m_uuid + m_ext)) {
            throw new Error(ErrorCode.FSV_FILE_NOT_FOUND,"Cannot find file on server, ID : " + m_uuid);
        }

        File file = new File(m_originFilePath);
        if (file.exists()) {
            throw new Error(ErrorCode.FSV_FILE_ALREADY_EXIST,"File already exist, localFilePath : " + m_originFilePath);
        }
        if (!getFileFromServer(file)) {
            throw new Error(ErrorCode.FSV_FILE_DOWNLOAD_FAILED,
                    "File Download failed, uuid : " + m_uuid + ", localFilePath : " + m_originFilePath);
        }
        return m_originFilePath;
    }

    public void uploadResult() throws Error {
        File result = new File(m_resulFilePath);
        if (!result.exists()) {
            throw new Error(ErrorCode.FILE_NOT_FOUND,"Cannot find file result json, path : " + m_resulFilePath);
        }

        if (!uploadResultFile()) {
            throw new Error(ErrorCode.FSV_FILE_UPLOAD_FAILED, "Failed to upload result json");
        }

        if (!removeFiles()) {
            throw new Error(ErrorCode.FILE_REMOVE_FAILED, "Failed to remove files on server");
        }
    }

    private boolean uploadResultFile() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            Resource resultJson = new FileSystemResource(m_resulFilePath);
            body.add("files", resultJson);

            UriComponents uri = UriComponentsBuilder.fromHttpUrl(m_fileServerUrl + m_uuid + "/result.json").build();
            ResponseEntity<?> resultMap = new RestTemplate().exchange(
                    uri.toString(), HttpMethod.POST, new HttpEntity<>(body, headers), Object.class);

            if (resultMap.getStatusCode().value() != 200) {
                return false;
            }
        } catch (HttpClientErrorException | HttpServerErrorException httpError) {
            logger.error("Error occur during upload result file to file server");
            httpError.printStackTrace();
            return false;
        }
        return true;
    }

    public String getResultData() {
        // TODO : 파일 서버에 있는 결과 파일 로컬에 다운
        return "";
    }

    private boolean removeFiles() {
        File originFile = new File(m_originFilePath);
        File resultFile = new File(m_resulFilePath);

        boolean result = true;
        if (!originFile.exists() || !originFile.delete()) {
            logger.warn("origin file is not exist or failed to remove, path : " + originFile.getAbsolutePath());
            result = false;
        }

        if (!resultFile.exists() || !resultFile.delete()) {
            logger.warn("origin file is not exist or failed to remove, path : " + originFile.getAbsolutePath());
            result = false;
        }
        return result;
    }

    private boolean getFileFromServer(File file) {
        try {
            UriComponents uri = UriComponentsBuilder.fromHttpUrl(m_fileServerUrl + m_uuid + "/origin" + m_ext).build();
            ResponseEntity<?> resultMap = new RestTemplate().exchange(
                    uri.toString(), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Object.class);

            if (resultMap.getStatusCode().value() != 200 || resultMap.getBody() == null) {
                return false;
            }
            if (file.createNewFile()) {
                logger.error("Error occur during create file on server");
                return false;
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(resultMap.getBody().toString().getBytes());
            outputStream.close();
        } catch (HttpClientErrorException | HttpServerErrorException httpError) {
            logger.error("Error occur during get target file from file server");
            httpError.printStackTrace();
            return false;
        } catch (IOException ioError) {
            logger.error("Error occur during write file on local path");
            ioError.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean checkFileIsExist(String targetFile) {
        try {
            // TODO : use ENV for file server Address
            UriComponents uri = UriComponentsBuilder.fromHttpUrl(m_fileServerUrl).build();
            ResponseEntity<?> resultMap = new RestTemplate().exchange(
                    uri.toString(), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Object.class);

            if (resultMap.getStatusCode().value() != 200 || resultMap.getBody() == null) {
                return false;
            }

            JsonArray files = new JsonArray(resultMap.getBody().toString());
            for (int i = 0 ; i < files.size(); i++) {
                if(files.getString(i).equals(targetFile)) {
                    return true;
                }
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error occur during get file list from file server");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}