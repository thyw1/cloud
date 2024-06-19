package com.study.oss.cloud;

import com.study.oss.common.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import javax.naming.CommunicationException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * 云存储
 *
 */
public abstract class CloudStorageService {
    /** 云存储配置信息 */
    CloudStorageConfigProperties config;



    /**
     * 文件路径
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 返回上传路径
     */
    public String getPath(String prefix, String suffix) {
        //生成uuid
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //文件路径
        String path = DateUtils.format(new Date(), "yyyyMMdd") + "/" + uuid;

        if(StringUtils.isNotBlank(prefix)){
            path = prefix + "/" + path;
        }

        return path + suffix;
    }

    /**
     * 文件上传
     * @param data    文件字节数组
     * @param path    文件路径，包含文件名
     * @return        返回http地址
     */
    public abstract String upload(byte[] data, String path,Long parentId);

    /**
     * 文件上传
     * @param data     文件字节数组
     * @param suffix   后缀
     * @return         返回http地址
     */
    public abstract String uploadSuffix(byte[] data, String suffix,Long parentId) throws CommunicationException;

    /**
     * 文件上传
     * @param inputStream   字节流
     * @param path          文件路径，包含文件名
     * @return              返回http地址
     */
    public abstract String upload(InputStream inputStream, String path,Long parentId);

    /**
     * 文件上传
     * @param inputStream  字节流
     * @param suffix       后缀
     * @return             返回http地址
     */
    public abstract String uploadSuffix(InputStream inputStream, String suffix,Long parentId);



    /**
     * 创建文件夹
     * @param parentId
     * @param dirName
     */
    public abstract void mkdir(Long parentId,String dirName) throws CommunicationException;


    public abstract Resource loadResource(String fileId);
}
