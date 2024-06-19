package com.study.oss.controller;


import cn.hutool.http.HttpResponse;
import com.study.oss.cloud.AliyunCloudStorageService;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

	
	/**
	 * 列表
     * params:curPage limit parentId
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		//根据存储源查找
	    params.put("source",cloudStorageConfigProperties.getType());
		PageUtils page = sysOssService.queryPage(params);
		return R.ok().put("page", page);
	}



    /**
     * 云存储配置信息
     */
    @GetMapping("/config")
    public R config(){


        return R.ok().put("config", cloudStorageConfigProperties);
    }


	/**
	 * 上传文件到指定文件夹
	 * TODO 云存储
	 */
	@PostMapping("/upload/{parentId}")
	public R upload(@RequestParam("file") MultipartFile file,@PathVariable("parentId") Long parentId) throws Exception {
		if (file.isEmpty()) {
			throw new CommonException("上传文件不能为空");
		}
		//上传文件
		String s = cloudStorageService.uploadSuffix(file.getBytes(), file.getOriginalFilename(), parentId);
		return R.ok().put("id", s);
	}

    /**
     * 创建文件夹
     * TODO 云存储
     */
    @PostMapping("/mkdir/{parentId}")
    public R mkdir(@PathVariable("parentId") Long parentId,String dirName) throws Exception {
        cloudStorageService.mkdir(parentId,dirName);

        return R.ok();
    }

	/**
	 * 上传文件
	 */
	@PostMapping("/upload")
	public R upload(@RequestParam("file") MultipartFile file) throws Exception {
		if (file.isEmpty()) {
			throw new CommonException("上传文件不能为空");
		}
		//上传文件
		String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		String url = cloudStorageService.uploadSuffix(file.getBytes(), suffix,null);
		return R.ok().put("url", url);
	}


	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public R delete(@RequestBody Long[] ids){
		sysOssService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

	/**
	 * 文件下载
	 * @param fileId
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@GetMapping("/file/{fileId}/download")
	public ResponseEntity<Resource> downLoad(@PathVariable("fileId") String fileId, HttpServletRequest request) throws UnsupportedEncodingException {
		Resource resource = null;
		SysOssEntity ossEntity = sysOssService.getById(fileId);
		resource = cloudStorageService.loadResource(fileId);
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException e) {
			// pass
		}
		if (Objects.isNull(contentType)){
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + URLEncoder.encode(ossEntity.getFileName(),  "UTF-8"))
				.body(resource);


	}


}
