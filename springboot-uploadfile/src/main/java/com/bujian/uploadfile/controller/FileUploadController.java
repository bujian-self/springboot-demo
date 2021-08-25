package com.bujian.uploadfile.controller;

import com.bujian.uploadfile.bean.UploadFile;
import com.bujian.uploadfile.service.FileStorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件上传
 * @Author bujian 改
 * @see <a href="https://github.com/ramostear/springboot2.0-action">
 *     Spring Boot 2.0实现基于Restful风格的文件上传与下载APIs 案例</a>
 **/
@Api(tags = "文件上传模块")
@RestController
@RequestMapping("fileUpload")
public class FileUploadController {

    @Autowired
    FileStorageService fileStorageService;

    @ApiImplicitParam(name = "file", value = "文件", required = true)
    @ApiOperation(value = "上传文件")
    @PostMapping("upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file){
        try {
            fileStorageService.save(file);
            return "Upload file successfully: "+file.getOriginalFilename();
        }catch (Exception e){
            throw new RuntimeException("Could not upload the file:"+file.getOriginalFilename());
        }
    }

    @ApiOperation(value = "获取所有文件信息")
    @GetMapping("files")
    @ResponseBody
    public List<UploadFile> files() throws Exception {
        return fileStorageService.load()
                .map(path -> {
                    String fileName = path.getFileName().toString();
                    return new UploadFile(fileName,MvcUriComponentsBuilder.fromMethodName(
                            this.getClass(),"getFile",fileName
                    ).build().toString());
                }).collect(Collectors.toList());
    }

    @ApiImplicitParam(name = "filename", value = "文件名称", required = true)
    @ApiOperation(value = "下载文件")
    @GetMapping("files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable("filename") String filename) throws Exception {
        Resource file = fileStorageService.load(filename);
        String fileName = URLEncoder.encode(file.getFilename(), "UTF-8");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="+fileName)
                .body(file);
    }

    @ApiImplicitParam(name = "filename", value = "文件名称", required = true)
    @ApiOperation(value = "删除文件")
    @GetMapping("files/remove/{filename:.+}")
    @ResponseBody
    public String delFile(@PathVariable("filename") String filename) {
        fileStorageService.clear(filename);
        return "文件删除成功";
    }
}
