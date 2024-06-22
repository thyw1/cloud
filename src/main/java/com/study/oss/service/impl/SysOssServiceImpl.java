package com.study.oss.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.oss.cloud.CloudStorageConfigProperties;
import com.study.oss.common.utils.FileConstant;
import com.study.oss.common.utils.PageUtils;
import com.study.oss.common.utils.Query;
import com.study.oss.dao.SysOssDao;
import com.study.oss.entity.SysOssEntity;
import com.study.oss.service.SysOssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service("sysOssService")
public class SysOssServiceImpl extends ServiceImpl<SysOssDao, SysOssEntity> implements SysOssService {

	@Autowired
	private CloudStorageConfigProperties cloudStorageConfigProperties;
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


	public List<SysOssEntity> getSysOssList1(List<Long> ids,List<SysOssEntity> list,List<SysOssEntity> resultList) {
		// 对初始ID列表中的每个ID调用递归函数
		for (Long id : ids) {
			SysOssEntity entity = getById(id);
			if(entity!=null && entity.getType()== FileConstant.FileType.FOLDER.getCode()){
				List<Long> longList = getChildren(entity, list).stream().map(SysOssEntity::getId).collect(Collectors.toList());
				getSysOssList1(longList,list,resultList);
			}
			else
				resultList.add(entity);
		}
		return resultList;
	}




	@Override
	public List<SysOssEntity> getChildren(SysOssEntity root, List<SysOssEntity> sysOssEntities) {
		List<SysOssEntity> children = sysOssEntities.stream().filter(sysOssEntity -> sysOssEntity.getParentId().longValue() == root.getId().longValue())
				.map(ossEntity -> {

					ossEntity.setChildren(getChildren(ossEntity, sysOssEntities));
					return ossEntity;
				}).collect(Collectors.toList());
		return children;
	}

	@Override
	public List<SysOssEntity> getSysOssList(List<Long> ids) {
		List<SysOssEntity> list = list();
		List<SysOssEntity> res = new ArrayList<>();
		return getSysOssList1(ids,list,res);
	}


	//文件树
	@Override
	public List<SysOssEntity> listWithTrees() {
		Long parentId = getOne(new LambdaQueryWrapper<SysOssEntity>().eq(SysOssEntity::getFileName, cloudStorageConfigProperties.getBasicPath())).getId();
		List<SysOssEntity> sysOssEntities = baseMapper.selectList(null);
		List<SysOssEntity> collect = sysOssEntities.stream().filter((sysOssEntity -> sysOssEntity.getParentId() == parentId))
				.map((ossEntity) -> {

					ossEntity.setChildren(getChildren(ossEntity, sysOssEntities));
					return ossEntity;
				}).collect(Collectors.toList());
		return collect;
	}
	
}
