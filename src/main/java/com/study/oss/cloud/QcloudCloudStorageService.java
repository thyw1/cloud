package com.study.oss.cloud;


import cn.hutool.core.io.resource.Resource;
import com.alibaba.fastjson.JSONObject;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.request.UploadFileRequest;
import com.qcloud.cos.sign.Credentials;
import com.study.oss.common.exception.CommonException;
import org.apache.commons.io.IOUtils;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;

/**
 * 腾讯云存储
 *
 */
public class QcloudCloudStorageService extends CloudStorageService {
    private COSClient client;
    public QcloudCloudStorageService(CloudStorageConfigProperties config){
        this.config = config;

    }

    @Override
    public String upload(byte[] data, String path) {
        return null;
    }

    @Override
    public String uploadSuffix(byte[] data, String suffix) {
        return null;
    }

    @Override
    public String upload(InputStream inputStream, String path) {
        return null;
    }

    @Override
    public String uploadSuffix(InputStream inputStream, String suffix) {
        return null;
    }

    @Override
    public ResponseEntity<Resource> downLoad(String fileId) {
        return null;
    }
}
