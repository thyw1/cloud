package com.study.oss.cloud;

import cn.hutool.core.io.resource.Resource;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.study.oss.common.utils.Constant;
import com.study.oss.common.utils.FileConstant;
import com.study.oss.entity.SysOssEntity;
import com.study.oss.service.SysOssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;

/**
 * 阿里云存储
 */
public class AliyunCloudStorageService extends CloudStorageService {
    private OSSClient client;
    @Autowired
    private SysOssService sysOssService;

    public AliyunCloudStorageService(CloudStorageConfigProperties config) {
        this.config = config;

        //初始化
        init();
    }

    private void init() {
        client = new OSSClient(config.getAliyunEndPoint(), config.getAliyunAccessKeyId(),
                config.getAliyunAccessKeySecret());
    }

    @Override
    public String upload(byte[] data, String path, Long parentId) {
        return upload(new ByteArrayInputStream(data), path, parentId);
    }

    /**
     * @param inputStream 字节流
     * @param path        文件路径，2024/0618/rf.py
     * @param parentId
     * @return
     */
    @Override
    public String upload(InputStream inputStream, String path, Long parentId) {
        SysOssEntity ossEntity = new SysOssEntity();
        try {
            client.putObject(config.getAliyunBucketName(), path, inputStream);

            ossEntity.setFileName(path.substring(path.lastIndexOf("/") + 1));
            ossEntity.setFilePath(path.substring(0, path.lastIndexOf("/")));
            ossEntity.setUrl(config.getAliyunDomain() + "/" + path);
            ossEntity.setType(FileConstant.FileType.DEFAULT.getCode());
            ossEntity.setSource(Constant.CloudService.ALIYUN.getValue());
            ossEntity.setCreateTime(new Date());
            sysOssService.save(ossEntity);
        } catch (Exception e) {
            throw new RuntimeException("上传文件失败，请检查配置信息", e);
        }

        return ossEntity.getId().toString();
    }

    @Override
    public String uploadSuffix(byte[] data, String suffix, Long parentId) {
        if (parentId == null)
            return upload(data, getPath(config.getAliyunPrefix(), suffix.substring(suffix.lastIndexOf("."))), parentId);
        SysOssEntity entity = sysOssService.getById(parentId);
        return upload(data, entity.getFilePath() + "/" + entity.getFileName() + "/" + suffix, parentId);
    }

    @Override
    public String uploadSuffix(InputStream inputStream, String suffix, Long parentId) {
        return upload(inputStream, getPath(config.getAliyunPrefix(), suffix), parentId);
    }


    @Override
    public void mkdir(Long parentId, String dirName) {

    }

    @Override
    public org.springframework.core.io.Resource loadResource(String fileId) {
        SysOssEntity entity = sysOssService.getById(fileId);
        if (Objects.isNull(entity)) {
            throw new RuntimeException("没有该文件");
        }

        OSSObject object = client.getObject(config.getAliyunBucketName(), entity.getFilePath() + "/" + entity.getFileName());
        return new InputStreamResource(object.getObjectContent());
    }

}
