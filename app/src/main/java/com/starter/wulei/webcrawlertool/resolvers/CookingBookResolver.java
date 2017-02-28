package com.starter.wulei.webcrawlertool.resolvers;

import android.content.Context;
import android.util.Log;

import com.starter.wulei.webcrawlertool.databse.CookingsDBHelper;
import com.starter.wulei.webcrawlertool.models.CookingMaterial;
import com.starter.wulei.webcrawlertool.utilities.StringHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wulei on 2017/2/23.
 */

public class CookingBookResolver {

    public interface OnCookingBookCompletedListener {
        void cookingBookCompleted(int index);
    }

    private Context mContext;

    public CookingBookResolver(Context context) {
        mContext = context;
    }

    public void resolveHtml(final int index, final String url, final OnCookingBookCompletedListener listener) {
        Observable.create(new ObservableOnSubscribe<CookingMaterial>() {
            @Override
            public void subscribe(ObservableEmitter<CookingMaterial> e) throws Exception {
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
                CookingMaterial material = resolve(StringHelper.getCookingId(url), html);
                if(null != material) {
                    e.onNext(material);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CookingMaterial>() {
                    CookingMaterial mMaterial;

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CookingMaterial value) {
                        mMaterial = value;
                        if(null != mMaterial) {
                            Log.d(HTMLResolver.ST_RESOLVER_TAG, "菜谱难度:" + mMaterial.difficulty + " 材料:" + mMaterial.materials);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        if(null != listener) {
                            Log.d(HTMLResolver.ST_RESOLVER_TAG, "菜谱下载完成， url: " + url);
                            listener.cookingBookCompleted(index);
                        }
                    }
                });
    }

    private CookingMaterial resolve(String bookId, String source) {
        Document htmlDoc = Jsoup.parse(source);

        //解析菜谱的烹饪难度，时间和食材
        CookingMaterial material = getMaterial(htmlDoc);
        Elements intros = htmlDoc.select("div.cp-show-intro");
        if(intros.size() > 0) {
            material.intro = intros.get(0).ownText();
        }
        CookingsDBHelper dbHelper = new CookingsDBHelper(mContext);
        dbHelper.updateCooking(bookId, material);
        return material;
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
                    if(builder.length() > 0) {
                        builder.deleteCharAt(builder.length() - 1);
                    }
                    material.materials = builder.toString();
                }
            }
        }
        return material;
    }
}