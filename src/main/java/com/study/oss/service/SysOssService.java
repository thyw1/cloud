package com.study.oss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.oss.common.utils.PageUtils;
import com.study.oss.entity.SysOssEntity;

import java.util.Map;


public interface SysOssService extends IService<SysOssEntity> {

	PageUtils queryPage(Map<String, Object> params);
}
