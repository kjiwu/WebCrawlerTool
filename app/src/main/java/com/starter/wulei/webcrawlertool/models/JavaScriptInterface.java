package com.starter.wulei.webcrawlertool.models;

import android.content.Context;
import android.webkit.JavascriptInterface;
import com.starter.wulei.webcrawlertool.resolvers.HTMLResolver;


/**
 * Created by wulei on 2017/2/23.
 */

public class JavaScriptInterface implements IJavaScriptInterface {

    private HTMLResolver mResolver;
    private Context mContext;

    public JavaScriptInterface(Context context) {
        mContext = context;
        mResolver = new HTMLResolver(mContext);
    }

    @JavascriptInterface
    public void getWebSource(String source) {
        if(null != source) {
            mResolver.setHtml(source);
            getSourceOver(true);
        }
    }

    public String getNextPage() {
        if(null != mResolver){
            if(mResolver.haveNextPage()) {
                String curPage = mResolver.getCurrentPage();
                return String.valueOf(Integer.decode(curPage) + 1);
            } else {
                return null;
            }
        } else {
            return "1";
        }
    }

    public void getCookings() {
        mResolver.getCookingList();
    }

    @Override
    public void getSourceOver(boolean isOver) {

    }
}
