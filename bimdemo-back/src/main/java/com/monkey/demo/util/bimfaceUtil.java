package com.monkey.demo.util;

import com.bimface.exception.BimfaceException;
import com.bimface.sdk.BimfaceClient;

public class bimfaceUtil {

        public static BimfaceClient getBimfaceClient(String appKey, String appSecret){
            BimfaceClient bimfaceClient = new BimfaceClient(appKey, appSecret);
            return bimfaceClient;
        }

}
