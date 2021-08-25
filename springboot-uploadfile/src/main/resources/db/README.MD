![这是图片](https://img-blog.csdnimg.cn/20201103152305676.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MTkzMTI3OA==,size_16,color_FFFFFF,t_70#pic_center "命名规范")

    起始版本必须大于 1

    V1.0.1__create_core_table.sql

    V是前缀 表示每个文件只会被执行一次

    1.0.1 为版本号 ，高版本的执行后不会在执行低版本的脚本。比如1.0.5先执行了，1.0.3就不会被执行了。

    __ ： 两个下划线表示分隔符

    create_core_table :脚本功能表述

    .sql: 后缀
