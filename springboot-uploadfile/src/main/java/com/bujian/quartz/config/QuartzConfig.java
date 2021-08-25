package com.bujian.quartz.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Quartz组件加载提示
 * @author bujian
 */
@Component
public class QuartzConfig implements CommandLineRunner {

    @Override
    public void run(String... args) {
        System.out.println(
                " __             __  ___ __ \n" +
                "/  \\ |  |  /\\  |__)  |   / \n" +
                "\\__X \\__/ /~~\\ |  \\  |  /_ \n" +
                "                           \n" +
                "version: 1.0.0,\tFont Name: JS Stick Letters\n" +
                "banner: http://patorjk.com/software/taag/"
        );
    }
}
