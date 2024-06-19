package com.study.oss.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 文件类
 *
 */
@Data
@TableName("sys_oss")
public class SysOssEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@TableId
	private Long id;
	//URL地址
	private String url;
	//加入文件层级概念
	private Long parentId;
	//文件来源
	private int source;
	//fileName+filePath 是完整路径
	private String fileName;
	private String filePath;
	private int type;
	private Long size;

	private Date createTime = new Date();
	private Date updateTime = new Date();

}
