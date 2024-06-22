package com.study.oss.controller;


import cn.hutool.http.HttpResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.oss.cloud.AliyunCloudStorageService;
import com.study.oss.cloud.CloudStorageConfigProperties;
import com.study.oss.cloud.CloudStorageService;
import com.study.oss.common.exception.CommonException;
import com.study.oss.common.utils.FileConstant;
import com.study.oss.common.utils.PageUtils;
import com.study.oss.common.utils.R;
import com.study.oss.entity.SysOssEntity;
import com.study.oss.service.SysOssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Controller
//@RequestMapping("sys/oss")
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
    @ApiOperation("文件列表（根据文件夹id分页查）")
    @RequestMapping({"/", "index.html", "/list"})
    public String list(@RequestParam Map<String, Object> params, Model model) {
        if (cloudStorageConfigProperties.getType() == 0) {
            if (params.get("parentId") == null) {
                SysOssEntity sysOssEntity = sysOssService.getOne(new LambdaQueryWrapper<SysOssEntity>().eq(SysOssEntity::getFileName, cloudStorageConfigProperties.getBasicPath()));
                if (sysOssEntity == null) {
                    sysOssEntity = new SysOssEntity();
                    sysOssEntity.setSource(cloudStorageConfigProperties.getType());
                    sysOssEntity.setFileName(cloudStorageConfigProperties.getBasicPath());
                    sysOssEntity.setType(FileConstant.FileType.FOLDER.getCode());
                    sysOssEntity.setParentId(-1L);
                    sysOssService.save(sysOssEntity);
                }
                params.put("parentId", sysOssEntity.getId());
            }

        }
        model.addAttribute("parentId", params.get("parentId"));
        //根据存储源查找
        params.put("source", cloudStorageConfigProperties.getType());
        PageUtils page = sysOssService.queryPage(params);
        model.addAttribute("page", page);
        model.addAttribute("source", cloudStorageConfigProperties.getType());
        return "index";
    }


    /**
     * 云存储配置信息
     */
    @GetMapping("/config")
    public R config() {


        return R.ok().put("config", cloudStorageConfigProperties);
    }


    /**
     * 上传文件到指定文件夹
     * TODO 云存储
     */
    @ApiOperation("上传文件到指定文件夹")
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, @RequestParam("parentId") Long parentId, RedirectAttributes attributes, HttpServletRequest request) throws Exception {
        if (file.isEmpty()) {
            throw new CommonException("上传文件不能为空");
        }
        if (parentId == null) {
            //上传文件
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String url = cloudStorageService.uploadSuffix(file.getBytes(), suffix, null);
        } else {
            //上传文件
            String s = cloudStorageService.uploadSuffix(file.getBytes(), file.getOriginalFilename(), parentId);
        }
        attributes.addAttribute("parentId", parentId);

        return "redirect:list";
    }

    /**
     * 创建文件夹
     * TODO 云存储
     */
    @ApiOperation("创建文件夹")
    @PostMapping("/mkdir/{parentId}")
    public R mkdir(@PathVariable("parentId") Long parentId, String dirName) throws Exception {
        cloudStorageService.mkdir(parentId, dirName);

        return R.ok();
    }


    /**
     * 删除
     */
    @ApiOperation("根据id列表删除文件")
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        sysOssService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 文件下载
     *
     * @param fileId
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    @ApiOperation("根据文件id下载文件")
    @GetMapping("/file/download/{fileId}")
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
        if (Objects.isNull(contentType)) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + URLEncoder.encode(ossEntity.getFileName(), "UTF-8"))
                .body(resource);


    }

    @ApiOperation("修改文件名")
    @RequestMapping("/modifyName/{fileId}")
    public void modifyName(String name, @PathVariable("fileId") Long fileId) {
        SysOssEntity sysOssEntity = new SysOssEntity();
        sysOssEntity.setFileName(name);
        sysOssService.updateById(sysOssEntity);
    }

    /**
     * 文件批量下载
     *
     * @param fileIds
     * @param response
     * @throws UnsupportedEncodingException
     */
    @ApiOperation("根据id列表批量下载文件 以压缩包形式")
    @RequestMapping("/downLoadList")
    public void downLoadList(@RequestParam("fileIds") List<Long> fileIds, HttpServletResponse response) throws UnsupportedEncodingException {

        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(UUID.randomUUID().toString().substring(0, 6) + ".zip", "UTF-8"));
        response.setCharacterEncoding("utf-8");

        response.setContentType("application/octet-stream");
        List<SysOssEntity> sysOssList = sysOssService.getSysOssList(fileIds);

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()))) {
            for (SysOssEntity entity : sysOssList) {
                String separator = File.separator;
//				if(cloudStorageConfigProperties.getType()==2)
//					separator="/";
                File file = new File(entity.getFilePath() + separator + entity.getFileName());
                String fileName = file.getName();
                zipOutputStream.putNextEntry(new ZipEntry(fileName));
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                    byte[] bytes = new byte[1024];
                    int i = 0;
                    while ((i = bis.read(bytes)) != -1) {
                        zipOutputStream.write(bytes, 0, i);
                    }
                    zipOutputStream.closeEntry();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
