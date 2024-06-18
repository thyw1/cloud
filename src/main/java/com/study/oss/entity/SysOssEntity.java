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





	private String fileName;

	private String filePath;

	private String type;
	private Long size;


	private Date createTime = new Date();
	private Date updateTime = new Date();

}
