package com.study.oss.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.oss.common.utils.PageUtils;
import com.study.oss.common.utils.Query;
import com.study.oss.dao.SysOssDao;
import com.study.oss.entity.SysOssEntity;
import com.study.oss.service.SysOssService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("sysOssService")
public class SysOssServiceImpl extends ServiceImpl<SysOssDao, SysOssEntity> implements SysOssService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		LambdaQueryWrapper<SysOssEntity> wrapper = new LambdaQueryWrapper<>();
		//根据目录查
		if (params.containsKey("parentId")){
			String parentId = params.get("parentId").toString();

			wrapper.eq(SysOssEntity::getParentId, parentId);
		}
		//文件名模糊搜索
		if(params.containsKey("fileName")){
			wrapper.like(SysOssEntity::getFileName,params.get("fileName"));
		}
        //文件来源
        if(params.containsKey("source")){
            wrapper.eq(SysOssEntity::getSource,params.get("source"));
        }

		IPage<SysOssEntity> page = this.page(
			new Query<SysOssEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}
	
}
