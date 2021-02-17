package com.monkey.demo.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

@CrossOrigin
@RestController
public class xunchaController {

    @Value("${file.path}")
    private String filePath;

//    @RequestMapping(value = "/hello",method = RequestMethod.GET)
//    public String hello(){
//        return "hello";
//    }

    @RequestMapping(value="/upload",method = RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file) throws Exception{
        System.out.println(file);
        System.out.println(file.getOriginalFilename());
        String extname=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String filename = UUID.randomUUID().toString()+extname;
        System.out.println(filename);
        FileCopyUtils.copy(file.getInputStream(),new FileOutputStream(new File(filePath+filename)));
        return filename;
    }

    @GetMapping("/download/{filename}")
    public void download(HttpServletResponse response,@PathVariable("filename") String filename) throws Exception {
        // 文件地址，真实环境是存放在数据库中的
//        String filename="ebada952-e253-4bbe-9310-fa973b0221a3.doc";
        File file = new File(filePath+filename);
        // 穿件输入对象
        FileInputStream fis = new FileInputStream(file);
        // 设置相关格式
        response.setContentType("application/force-download");
        // 设置下载后的文件名以及header
        response.addHeader("Content-disposition", "attachment;fileName=" + filename);
        // 创建输出对象
        OutputStream os = response.getOutputStream();
        // 常规操作
        byte[] buf = new byte[1024];
        int len = 0;
        while((len = fis.read(buf)) != -1) {
            os.write(buf, 0, len);
        }
        fis.close();
    }


}
