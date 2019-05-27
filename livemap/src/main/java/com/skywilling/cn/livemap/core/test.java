package com.skywilling.cn.livemap.core;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * ClassName test
 * Author  Lin
 * Date 2019/5/27 11:16
 **/

public class test {
    public static void main(String[] args){
        try {
            //在开发测试模式时，得到的地址为：{项目(模块)跟目录}/target/static/images/upload/
            //在打包成jar正式发布时，得到的地址为：{发布jar包目录}/static/images/upload/
            File path = new File(ResourceUtils.getURL("classpath:").getPath());
            if(!path.exists()) path = new File("");
            System.out.println("class path: "+path.getAbsolutePath());
            System.out.println(System.getProperty("user.dir"));
            File upload = new File(path.getAbsoluteFile(),"doc/Map/lanes");
            String rootpath = System.getProperty("user.dir");
            String fullpath = rootpath+"/doc/Map/lanes/"+"yuquanxiaoqu3";
            File lanes = new File(fullpath);
            if(!path.exists()) lanes = new File("");
            System.out.println("lanes: "+lanes.getAbsolutePath());
            File[] lanes_file = lanes.listFiles();
            System.out.println(lanes_file.length);
            if(!upload.exists()) upload.mkdirs();
            System.out.println("upload ur: "+upload.getAbsolutePath());
            System.out.println("file separator: "+System.getProperty("file.separator"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
