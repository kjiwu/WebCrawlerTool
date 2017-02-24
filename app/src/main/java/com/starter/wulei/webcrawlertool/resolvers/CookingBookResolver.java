package com.starter.wulei.webcrawlertool.resolvers;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.starter.wulei.webcrawlertool.databse.CookingsDBHelper;
import com.starter.wulei.webcrawlertool.models.CookBook;
import com.starter.wulei.webcrawlertool.models.CookingMaterial;
import com.starter.wulei.webcrawlertool.models.CookingStep;
import com.starter.wulei.webcrawlertool.models.MaterialInfo;
import com.starter.wulei.webcrawlertool.utilities.ImageDownloader;
import com.starter.wulei.webcrawlertool.utilities.StringHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.ObservableEmitter;

/**
 * Created by wulei on 2017/2/23.
 */

public class CookingBookResolver {

    public interface OnCookingBookResolver {
        void cookingBookCompleted();
    }

    private Context mContext;
    private OnCookingBookResolver mResolver;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(null != mResolver) {
                mResolver.cookingBookCompleted();
            }
        }
    };

    public CookingBookResolver(Context context) {
        mContext = context;
    }

    public void resolveHtml(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL newUrl = new URL(url);
                    URLConnection connect = newUrl.openConnection();
                    DataInputStream dis = new DataInputStream(connect.getInputStream());
                    BufferedReader in = new BufferedReader(new InputStreamReader(dis,"UTF-8"));
                    String html = "";
                    String readLine = null;
                    while((readLine = in.readLine()) != null) {
                        html += readLine;
                    }
                    in.close();
                    resolve(StringHelper.getCookingId(url), html);
                }
                catch(MalformedURLException me) {
                }
                catch(IOException ioe) {
                }
            }
        }).start();
    }

    private void resolve(String bookId, String source) {
        Document htmlDoc = Jsoup.parse(source);

        //解析菜谱的烹饪难度，时间和食材
        CookingMaterial material = getMaterial(htmlDoc);
        CookingsDBHelper dbHelper = new CookingsDBHelper(mContext);
        dbHelper.updateCooking(bookId, material);

        Message message = Message.obtain();
        mHandler.sendMessage(message);
    }

    //解析菜谱的烹饪难度，时间和食材
    private CookingMaterial getMaterial(Document htmlDoc) {
        CookingMaterial material = new CookingMaterial();
        Elements material_table = htmlDoc.select("table.cp-show-tab");
        if(material_table.size() > 0) {
            Element table = material_table.first();
            if(null != table) {
                Elements trs = table.select("table tr");
                if(trs.size() > 0) {
                    material.difficulty = trs.get(0).child(0).ownText();

                    StringBuilder builder = new StringBuilder();

                    int index = 2;
                    for(; index < trs.size(); index++) {
                        Element tr = trs.get(index);
                        if(tr.children().size() > 0 && tr.child(0).hasAttr("colspan")) {
                            break;
                        }

                        Elements tds = tr.select("tr td");
                        if(tds.size() > 0) {
                            for(Element e : tds) {
                                builder.append(e.child(0).ownText());
                                builder.append(",");
                            }
                        }
                    }

                    index++;
                    for(; index < trs.size(); index++) {
                        Element tr = trs.get(index);
                        Elements tds = tr.select("tr td");
                        if(tds.size() > 0) {
                            for(Element e : tds) {
                                builder.append(e.child(0).ownText());
                                builder.append(",");
                            }
                        }
                    }
                    builder.deleteCharAt(builder.length() - 1);
                    material.materials = builder.toString();
                }
            }
        }
        return material;
    }
}