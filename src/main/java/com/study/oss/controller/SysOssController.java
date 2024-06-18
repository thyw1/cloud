package com.study.oss.controller;


import com.study.oss.cloud.CloudStorageConfigProperties;
import com.study.oss.cloud.CloudStorageService;
import com.study.oss.cloud.LocalCloudStorageService;
import com.study.oss.common.exception.CommonException;
import com.study.oss.common.utils.Constant;
import com.study.oss.common.utils.PageUtils;
import com.study.oss.common.utils.R;
import com.study.oss.common.validator.ValidatorUtils;
import com.study.oss.entity.SysOssEntity;
import com.study.oss.service.SysOssService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping("sys/oss")
public class SysOssController {
	@Autowired
	private SysOssService sysOssService;
	@Autowired
	private CloudStorageService cloudStorageService;
	@Autowired
	private CloudStorageConfigProperties cloudStorageConfigProperties;
	@Autowired
	private LocalCloudStorageService localCloudStorageService;
	
	/**
	 * 列表
	 */
	@GetMapping("/list")
	@RequiresPermissions("sys:oss:all")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = sysOssService.queryPage(params);

		return R.ok().put("page", page);
	}


    /**
     * 云存储配置信息
     */
    @GetMapping("/config")
    @RequiresPermissions("sys:oss:all")
    public R config(){


        return R.ok().put("config", cloudStorageConfigProperties);
    }



	/**
	 * 上传文件
	 */
	@PostMapping("/upload")
	@RequiresPermissions("sys:oss:all")
	public R upload(@RequestParam("file") MultipartFile file) throws Exception {
		if (file.isEmpty()) {
			throw new CommonException("上传文件不能为空");
		}

		//上传文件
		String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		String url = cloudStorageService.uploadSuffix(file.getBytes(), suffix);
		if(cloudStorageConfigProperties.getType()>=1 && cloudStorageConfigProperties.getType()<=3){
            //保存文件信息
            SysOssEntity ossEntity = new SysOssEntity();
            ossEntity.setFileName(file.getOriginalFilename());
            ossEntity.setUrl(url);
            ossEntity.setCreateTime(new Date());
            sysOssService.save(ossEntity);
        }

		return R.ok().put("url", url);
	}


	/**
	 * 删除
	 */
	@PostMapping("/delete")
	@RequiresPermissions("sys:oss:all")
	public R delete(@RequestBody Long[] ids){
		sysOssService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

	@GetMapping("/file/{fileId}/download")
	public ResponseEntity<Resource> downLoad(@PathVariable("fileId") String fileId, HttpServletRequest request) throws UnsupportedEncodingException {
		LocalCloudStorageService.ResourceWrapper resourceWrapper = localCloudStorageService.loadResource(fileId);
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resourceWrapper.resource.getFile().getAbsolutePath());
		} catch (IOException e) {
			// pass
		}
		if (Objects.isNull(contentType)){
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + URLEncoder.encode(resourceWrapper.file.getFileName(), "UTF-8"))
				.body(resourceWrapper.resource);

	}


}
