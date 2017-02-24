package com.starter.wulei.webcrawlertool.fragments;

import android.content.Context;
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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.starter.wulei.webcrawlertool.R;
import com.starter.wulei.webcrawlertool.activities.MainActivity;
import com.starter.wulei.webcrawlertool.databse.CookingsDBHelper;
import com.starter.wulei.webcrawlertool.models.CookBook;
import com.starter.wulei.webcrawlertool.models.JavaScriptInterface;
import com.starter.wulei.webcrawlertool.resolvers.CookingBookResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wulei on 2016/9/10.
 */
public class WebViewFragment extends Fragment {

    private final static String url_str = "http://www.chinacaipu.com/caipu/fancier";
    private WebView mWebView;
    private Context mContext;
    private Button mButtonLoadCookings;

    private final static int LOAD_PAGESOUCE_DELAY_TIME = 10000;
    private final static int LOAD_NEXTPAGE_DELAY_TIME = 3000;


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
            mGetCookingListHandler.postDelayed(mGetCookingListRunnable, LOAD_NEXTPAGE_DELAY_TIME);
        }
    };

    private WebViewClient mWebClinet = new WebViewClient() {

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d("WebView", "onPageFinished");
            mGetCookingListHandler.postDelayed(mGetCookingListRunnable, LOAD_PAGESOUCE_DELAY_TIME);
            super.onPageFinished(view, url);
        }
    };

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = inflater.getContext();
        RelativeLayout view = new RelativeLayout(mContext);
        mJSInterface = new JavaScriptInterface(view.getContext()) {
            @Override
            public void getSourceOver(boolean isOver) {
                if(isOver) {
                    this.getCookings();
                    if(getNextPage() != null) {
                        mGetCookingListHandler.post(mGetNextPageRunnable);
                    } else {
                        Log.d("AA", "load cooking list over");
                        Toast.makeText(mContext, "load cooking list over", Toast.LENGTH_SHORT);
                        mButtonLoadCookings.setEnabled(true);
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
        view.addView(mWebView);

        mButtonLoadCookings = (Button) getActivity().findViewById(R.id.button_load_cookings);
        mButtonLoadCookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl(url_str);
                mButtonLoadCookings.setEnabled(false);
            }
        });

        return view;
    }
}
