package com.bujian.uploadfile.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * 文件操作
 * @author bujian
 */
public interface FileStorageService {

    /**
     * 初始化
     * @throws IOException
     */
    void init() throws IOException;

    /**
     * 保存/上传
     * @param multipartFile
     * @throws IOException
     */
    void save(MultipartFile multipartFile) throws IOException;

    /**
     * 下载/读取
     * @param filename
     * @return
     * @throws Exception
     */
    Resource load(String filename) throws Exception;

    /**
     * 加载
     * @return
     * @throws Exception
     */
    Stream<Path> load() throws Exception;

    /**
     * 删除文件
     */
    void clear(String filename);

    /**
     * 删除所有
     */
    void clearAll();

}
