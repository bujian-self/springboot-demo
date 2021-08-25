package com.bujian.uploadfile.config;

import com.bujian.uploadfile.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * FileUploadConfig
 * @author bujian
 */
@Component
public class FileUploadConfig implements CommandLineRunner {

    @Value("${spring.servlet.delete-before-starting:true}")
    private boolean deleteBeforeStarting;

    @Autowired
    FileStorageService fileStorageService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(
                " ___         ___          __        __        __              ___    \n" +
                "|__  | |    |__     |  | |__) |    /  \\  /\\  |  \\    | |\\ | |  |     \n" +
                "|    | |___ |___    \\__/ |    |___ \\__/ /~~\\ |__/    | | \\| |  |  ...\n" +
                "                                                                     \n" +
                "version: 1.0.0,\tFont Name: JS Stick Letters\n" +
                "banner: http://patorjk.com/software/taag/\n" +
                "GitHub：https://github.com/ramostear/springboot2.0-action"
        );
        if (deleteBeforeStarting) {
            fileStorageService.clearAll();
        }
        fileStorageService.init();
    }
}
