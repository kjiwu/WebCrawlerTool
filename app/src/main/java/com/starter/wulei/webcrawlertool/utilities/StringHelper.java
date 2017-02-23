package com.starter.wulei.webcrawlertool.utilities;

/**
 * Created by wulei on 2017/2/23.
 */

public class StringHelper {
    public static String getCookingId(String url) {
        String id = null;
        if(null != url) {
            int index = url.lastIndexOf("/");
            String sub = url.substring(index + 1);
            index = sub.indexOf(".");
            return sub.substring(0, index);
        }
        return id;
    }

    public static String getImageName(String url) {
        String id = null;
        if(null != url) {
            int index = url.lastIndexOf("/");
            return url.substring(index + 1);
        }
        return id;
    }
}
