package com.study.oss.cloud;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.oss.common.utils.Constant;
import com.study.oss.common.utils.FileConstant;
import com.study.oss.entity.SysOssEntity;
import com.study.oss.service.SysOssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;

import javax.naming.CommunicationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * 本地存储
 */
public class LocalCloudStorageService extends CloudStorageService {

    @Autowired
    private SysOssService sysOssService;
    @Autowired
    private CloudStorageConfigProperties cloudStorageConfigProperties;


    public LocalCloudStorageService(CloudStorageConfigProperties config){
        this.config = config;
    }


    /**
     *
     * @param data    文件字节数组
     * @param filePath D:\\lizi\ms\11_11_219.253731_11_11.TIF
     * @param parentId
     * @return
     */
    @Override
    public String upload(byte[] data, String filePath,Long parentId) {
        if(parentId==null)
            parentId=0L;
        SysOssEntity sysOssEntity = new SysOssEntity();
        String filename = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
        SysOssEntity one = sysOssService.getOne(new LambdaQueryWrapper<SysOssEntity>().eq(SysOssEntity::getParentId, parentId).eq(SysOssEntity::getFileName, filename));
        //TODO MD5判断？
        //文件重名了 就加几个随机字符
        if(one!=null){
            String fileNamePre = filename.substring(0, filename.lastIndexOf("."));
            String suffix = filename.substring(filename.lastIndexOf("."));
            filename= fileNamePre+UUID.randomUUID().toString().substring(0,4)+suffix;
        }

        try {
            String strDir = filePath.substring(0, filePath.lastIndexOf(File.separator)); //  D:\\lizi\ms
            Path dir = Paths.get(strDir);//  D:\\lizi\ms
            filePath=dir+File.separator+filename;  //   D:\lizi\ms\11_11_219.253731_11_1128ee.TIF
            if (!Files.isWritable(dir)) {
                Files.createDirectories(dir);
            }
            Path p = Paths.get(filePath);
            Files.write(p, data);
            sysOssEntity.setFilePath(strDir);
            sysOssEntity.setCreateTime(new Date());
            sysOssEntity.setFileName(filename);
            sysOssEntity.setSource(Constant.CloudService.LOCAL.getValue());
            sysOssEntity.setType(FileConstant.FileType.DEFAULT.getCode());
            sysOssEntity.setParentId(parentId);
            sysOssService.save(sysOssEntity);

        } catch (Exception e){
            throw new RuntimeException("上传文件失败，请检查配置信息", e);
        }

        return sysOssEntity.getId().toString();
    }


    @Override
    public String upload(InputStream inputStream, String path,Long parentId) {
        return null;
    }

    @Override
    public String uploadSuffix(InputStream inputStream, String suffix,Long parentId) {
        return upload(inputStream, getPath(config.getAliyunPrefix(), suffix),parentId);
    }

    public Resource loadResource(String fileId){
        SysOssEntity entity = sysOssService.getById(fileId);
        if (Objects.isNull(entity)) {
            throw new RuntimeException("没有该文件");
        }
        try {
            Path filePath = new java.io.File(entity.getFilePath()+File.separator+entity.getFileName()).toPath();
            org.springframework.core.io.Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()){
                return  resource;
            } else {
                throw new RuntimeException("没有该文件");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("没有该文件");
        }
    }



    @Override
    public void mkdir(Long parentId, String dirName) throws CommunicationException {

        SysOssEntity parentFolder = sysOssService.getById(parentId);
        //先判断是否以及存在
        SysOssEntity one = sysOssService.getOne(new LambdaQueryWrapper<SysOssEntity>().eq(SysOssEntity::getParentId, parentId).eq(SysOssEntity::getFileName, dirName));
        if(one!=null)
            throw new CommunicationException("已存在同名文件夹 不可再创建");
        String filePath = parentFolder.getFilePath() + File.separator + parentFolder.getFileName() + File.separator + dirName;
        //创建文件夹
        Path dir = Paths.get(filePath);
        if (!Files.isWritable(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //数据库信息保存
        SysOssEntity sysOssEntity = new SysOssEntity();
        sysOssEntity.setType(FileConstant.FileType.FOLDER.getCode());
        sysOssEntity.setSource(Constant.CloudService.LOCAL.getValue());
        sysOssEntity.setFileName(dirName);
        sysOssEntity.setParentId(parentId);
        //path是父目录的path 加上父目录名字
        sysOssEntity.setFilePath(parentFolder.getFilePath() + File.separator + parentFolder.getFileName());
        sysOssService.save(sysOssEntity);

    }

    /**
     * 指定路径上传
     * @param bytes
     * @param suffix 11_11_219.253731_11_11.TIF
     * @param parentId
     * @return
     */
    @Override
    public String uploadSuffix(byte[] bytes, String suffix, Long parentId) throws CommunicationException {
        if(parentId==null)
            parentId=0L;
        SysOssEntity entity = sysOssService.getById(parentId);
        if(entity.getType()!= FileConstant.FileType.FOLDER.getCode()){
            throw new CommunicationException("指定文件夹有误");
        }
        return upload(bytes, entity.getFilePath()+File.separator+entity.getFileName()+File.separator+suffix,parentId);
    }
}
