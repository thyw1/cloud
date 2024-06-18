package com.study.oss.config;



import com.study.oss.cloud.*;
import com.study.oss.common.utils.Constant;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@EnableConfigurationProperties(CloudStorageConfigProperties.class)
@Configuration
public class OSSFactory {

    @Bean
    public CloudStorageService cloudStorageService(CloudStorageConfigProperties properties){
        //获取云存储配置信息
        Integer type = properties.getType();

        if(type == Constant.CloudService.QINIU.getValue()){
            return new QiniuCloudStorageService(properties);
        }else if(type == Constant.CloudService.ALIYUN.getValue()){
            return new AliyunCloudStorageService(properties);
        }else if(type == Constant.CloudService.QCLOUD.getValue()){
            return new QcloudCloudStorageService(properties);
        }else {
            return new LocalCloudStorageService(properties);
        }
    }

}
