package com.monkey.demo.controller;

import com.bimface.exception.BimfaceException;
import com.bimface.sdk.BimfaceClient;
import com.monkey.demo.util.bimfaceUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
public class webController {

    @Value("${fileId}")
    private String fileId;

    @Value("${appKey}")
    private String appKey;

    @Value("${appSecret}")
    private String appSecret;

    @GetMapping("/getviewtoken")
    public Map getviewtoken() {
        Map<String,String> map = new HashMap<>();
        String viewToken = null;
        BimfaceClient bimfaceClient = bimfaceUtil.getBimfaceClient(appKey,appSecret);
        try {
            viewToken = bimfaceClient.getViewTokenByFileId(Long.valueOf(fileId));
        } catch (NumberFormatException e) {
        } catch (BimfaceException e) {
        }
        map.put("viewToken",viewToken);
        return map;
    }



}
