package com.starter.wulei.webcrawlertool.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.starter.wulei.webcrawlertool.models.JavaScriptInterface;

/**
 * Created by wulei on 2016/9/10.
 */
public class WebViewFragment extends Fragment {

    private final static String url_str = "http://www.chinacaipu.com/caipu/fancier";
    private WebView mWebView;

    private JavaScriptInterface mJSInterface;

    private Handler mGetCookingListHandler = new Handler();
    private Runnable mGetCookingListRunnable = new Runnable() {
        @Override
        public void run() {
            mWebView.loadUrl("javascript:window.androidObj.getWebSource(document.getElementsByTagName('html')[0].innerHTML)");
        }
    };

    private Runnable mGetNextPageRunnable = new Runnable() {
        @Override
        public void run() {
            mWebView.loadUrl("javascript:page(" + mJSInterface.getNextPage() + ")");
            mGetCookingListHandler.postDelayed(mGetCookingListRunnable, 1000);
        }
    };

    private WebViewClient mWebClinet = new WebViewClient() {

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d("WebView", "onPageFinished");
            mGetCookingListHandler.postDelayed(mGetCookingListRunnable, 1000);
            super.onPageFinished(view, url);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout view = new RelativeLayout(inflater.getContext());
        mJSInterface = new JavaScriptInterface(view.getContext()) {
            @Override
            public void getSourceOver(boolean isOver) {
                if(isOver) {
                    this.getCookings();
                    if(getNextPage() != null) {
                        mGetCookingListHandler.post(mGetNextPageRunnable);
                    }
                }
            }
        };

        mWebView = new WebView(view.getContext());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        mWebView.setLayoutParams(lp);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(mJSInterface, "androidObj");
        mWebView.setWebViewClient(mWebClinet);
        mWebView.loadUrl(url_str);
        view.addView(mWebView);

        return view;
    }
}
