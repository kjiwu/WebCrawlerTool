package com.starter.wulei.webcrawlertool.utilities;

import java.util.Arrays;
import java.util.List;

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

    public static String getTipsString(List<String> tips) {
        StringBuilder builder = new StringBuilder(tips.size() * 2 -1);
        for(int i = 0; i < tips.size(); i++) {
            builder.append(tips.get(i));
            builder.append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static List<String> getTipsArray(String tips) {
        String[] list = tips.split(",");
        return Arrays.asList(list);
    }
}
