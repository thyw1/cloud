package com.study.oss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.oss.common.utils.PageUtils;
import com.study.oss.entity.SysOssEntity;

import java.util.List;
import java.util.Map;


public interface SysOssService extends IService<SysOssEntity> {

	PageUtils queryPage(Map<String, Object> params);
	List<SysOssEntity> listWithTrees();
	List<SysOssEntity> getChildren(SysOssEntity root, List<SysOssEntity> sysOssEntities);

	/**
	 * 根据给定的id  递归去找id下的所有文件(不包括文件夹） 返回实体类
	 * @param ids
	 * @return
	 */
	List<SysOssEntity> getSysOssList(List<Long> ids);
}
