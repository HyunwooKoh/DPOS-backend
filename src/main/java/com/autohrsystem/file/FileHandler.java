package com.autohrsystem.file;

import com.autohrsystem.common.CommonApi;
import com.autohrsystem.common.Error;
import com.autohrsystem.common.ErrorCode;

import java.io.File;
import io.vertx.core.json.JsonArray;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;

public class FileHandler {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final String m_uuid;
    private final String m_ext;
    private final String m_localTempDir;

    public FileHandler(String uuid, String ext) {
        m_uuid = uuid;
        m_ext = "." + ext;
        m_localTempDir = CommonApi.getTempDir() + "/" + uuid + "/";
    }

    public String getFile() throws Error {
        String localFilePath = m_localTempDir + "origin" + m_ext;

        if (!checkOriginFileIsExist()) {
            throw new Error(ErrorCode.FSV_FILE_NOT_FOUND,"Cannot find file on server, ID : " + m_uuid);
        }

        File file = new File(m_localTempDir + "origin" + m_ext);
        if (file.exists()) {
            throw new Error(ErrorCode.FSV_FILE_ALREADY_EXIST,"File already exist, localFilePath : " + m_localTempDir + "origin" + m_ext);
        }
        if (!getFileFromServer(file)) {
            throw new Error(ErrorCode.FSV_FILE_DOWNLOAD_FAILED,
                    "File Download failed, uuid : " + m_uuid + ", localFilePath : " + m_localTempDir + "origin" + m_ext);
        }
        return m_localTempDir + "origin" + m_ext;
    }

    public boolean uploadResultFile(String resultFilePath) {
        return true;
    }

    public boolean removeFiles() {
        return true;
    }

    private boolean checkOriginFileIsExist() {
        try {
            // TODO : use ENV for file server Address
            UriComponents uri = UriComponentsBuilder.fromHttpUrl("http://hq.epapyrus.com:11058/files/").build();
            ResponseEntity<?> resultMap = new RestTemplate().exchange(
                    uri.toString(), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Object.class);

            if (resultMap.getStatusCode().value() != 200 || resultMap.getBody() == null) {
                return false;
            }

            JsonArray files = new JsonArray(resultMap.getBody().toString());
            for (int i = 0 ; i < files.size(); i++) {
                if(files.getString(i).equals(m_uuid + m_ext)) {
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

    private boolean getFileFromServer(File file) {
        try {
            UriComponents uri = UriComponentsBuilder.fromHttpUrl("http://hq.epapyrus.com:11058/files/" + m_uuid + "/origin" + m_ext).build();
            ResponseEntity<?> resultMap = new RestTemplate().exchange(
                    uri.toString(), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Object.class);

            if (resultMap.getStatusCode().value() != 200 || resultMap.getBody() == null) {
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
}