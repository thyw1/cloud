package com.study.oss.cloud;

import cn.hutool.core.io.resource.Resource;
import com.study.oss.entity.SysOssEntity;
import com.study.oss.service.SysOssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Objects;

/**
 * 本地存储
 */
public class LocalCloudStorageService extends CloudStorageService {

    @Autowired
    private SysOssService sysOssService;
    @Autowired
    private CloudStorageConfigProperties cloudStorageConfigProperties;

    public class ResourceWrapper{
        public org.springframework.core.io.Resource resource;
        public SysOssEntity file;

        public ResourceWrapper(org.springframework.core.io.Resource resource, SysOssEntity file) {
            this.resource = resource;
            this.file = file;
        }
    }

    public LocalCloudStorageService(CloudStorageConfigProperties config){
        this.config = config;
    }


    @Override
    public String upload(byte[] data, String name) {
        SysOssEntity sysOssEntity = new SysOssEntity();
        try {
            Path dir = Paths.get(name.substring(0,name.lastIndexOf(".")));
            if (!Files.isWritable(dir)) {
                Files.createDirectories(dir);
            }
            Path p = Paths.get(name);
            Files.write(p, data);
            sysOssEntity.setFilePath(name);
            sysOssEntity.setCreateTime(new Date());
            sysOssEntity.setFileName(name.substring(name.lastIndexOf("/")+1));
            sysOssService.save(sysOssEntity);

        } catch (Exception e){
            throw new RuntimeException("上传文件失败，请检查配置信息", e);
        }

        return sysOssEntity.getId().toString();
    }



    @Override
    public String uploadSuffix(byte[] data, String suffix) {
        return upload(data, getPath(config.getBasicPath(), suffix));
    }

    @Override
    public String upload(InputStream inputStream, String path) {
        return null;
    }

    @Override
    public String uploadSuffix(InputStream inputStream, String suffix) {
        return upload(inputStream, getPath(config.getAliyunPrefix(), suffix));
    }

    public ResourceWrapper loadResource(String fileId){
        SysOssEntity entity = sysOssService.getById(fileId);
        if (Objects.isNull(entity)) {
            throw new RuntimeException("没有该文件");
        }
        try {
            Path filePath = new java.io.File(entity.getFilePath()).toPath();
            org.springframework.core.io.Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()){
                return new ResourceWrapper(resource, entity);
            } else {
                throw new RuntimeException("没有该文件");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("没有该文件");
        }
    }



    @Override
    public ResponseEntity<Resource> downLoad(String fileId) {
//        ResourceWrapper resourceWrapper = loadResource(fileId);
        return null;
    }
}
