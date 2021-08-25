package com.bujian.uploadfile.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * UploadFile
 * @author bujian
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadFile {
    private String fileName;
    private String url;

}
