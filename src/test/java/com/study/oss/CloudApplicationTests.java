package com.study.oss;

import com.study.oss.cloud.CloudStorageConfigProperties;
import com.study.oss.common.utils.FileConstant;
import com.study.oss.common.utils.PageUtils;
import com.study.oss.controller.SysOssController;
import com.study.oss.entity.SysOssEntity;
import com.study.oss.service.SysOssService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.*;

@SpringBootTest
public class CloudApplicationTests {
    @Autowired
    private SysOssService sysOssService;
    @Autowired
    private CloudStorageConfigProperties properties;

    @Test
    public void testList(){
        Map<String,Object> map = new HashMap<>();
        map.put("parentId","35");
        PageUtils pageUtils = sysOssService.queryPage(map);
        for (Object o : pageUtils.getList()) {
            System.out.println(o.toString());
        }
    }

    @Test
    public void testTree(){
        List<SysOssEntity> sysOssEntities = sysOssService.listWithTrees();
        for (SysOssEntity sysOssEntity : sysOssEntities) {
            System.out.println(sysOssEntity);
        }
    }

    /**
     * 测试根据ids列表 获取下面的所有sysosseneity类型为文件的实体类
     */
    @Test
    public void testGetSysOssList(){
        List<Long> ids= new ArrayList<>();
        ids.add(35L);
        List<SysOssEntity> sysOssList = sysOssService.getSysOssList(ids);
        System.out.println(sysOssList.size());
        for (SysOssEntity sysOssEntity : sysOssList) {
            System.out.println(sysOssEntity);
        }
    }

}
