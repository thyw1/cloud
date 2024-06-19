package com.study.oss.cloud;

import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.study.oss.common.exception.CommonException;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import javax.naming.CommunicationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 七牛云存储
 */
public class QiniuCloudStorageService extends CloudStorageService {
    private UploadManager uploadManager;
    private String token;

    public QiniuCloudStorageService(CloudStorageConfigProperties config){
        this.config = config;

    }


    @Override
    public String upload(byte[] data, String path, Long parentId) {
        return null;
    }

    @Override
    public String uploadSuffix(byte[] data, String suffix, Long parentId) throws CommunicationException {
        return null;
    }

    @Override
    public String upload(InputStream inputStream, String path, Long parentId) {
        return null;
    }

    @Override
    public String uploadSuffix(InputStream inputStream, String suffix, Long parentId) {
        return null;
    }


    @Override
    public void mkdir(Long parentId, String dirName) {

    }

    @Override
    public Resource loadResource(String fileId) {
        return null;
    }
}
