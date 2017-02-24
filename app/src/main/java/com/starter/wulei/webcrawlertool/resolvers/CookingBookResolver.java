package com.starter.wulei.webcrawlertool.resolvers;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.starter.wulei.webcrawlertool.databse.CookBookDBHelper;
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

/**
 * Created by wulei on 2017/2/23.
 */

public class CookingBookResolver {

    public interface OnCookingBookResolver {
        void cookingBookCompleted(CookBook book);
    }

    private Context mContext;
    private OnCookingBookResolver mResolver;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(null != mResolver) {
                CookBook book = (CookBook) msg.getData().getSerializable("book");
                mResolver.cookingBookCompleted(book);
            }
        }
    };

    private CookBookDBHelper mBookDBHelper;

    public void setOnCookBookResolver(OnCookingBookResolver resolver) {
        mResolver = resolver;
    }

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
        ImageDownloader imageDownloader = new ImageDownloader(mContext);
        CookBook book = new CookBook();
        book.setId(bookId);

        //解析菜谱的标题
        Elements titles = htmlDoc.select("div.cp-show-main-tt");
        if(titles.size() > 0) {
            book.setTitle(titles.get(0).child(0).ownText());
        }

        //解析菜谱的大图
        Elements big_images = htmlDoc.select("img.cp-show-pic");
        if(big_images.size() > 0) {
            book.setPic_path(big_images.get(0).attr("src"));
            imageDownloader.download(bookId, book.getPic_path());
        }


        //解析菜谱的简介
        Elements intros = htmlDoc.select("div.cp-show-intro");
        if(intros.size() > 0) {
            book.setIntro(intros.get(0).ownText());
        }

        //解析菜谱的烹饪难度，时间和食材
        CookingMaterial material = getMaterial(htmlDoc);
        book.setMaterial(material);

        //解析烹饪步骤
        book.setSteps(getCookingStep(bookId, htmlDoc));

        //解析烹饪成品图
        book.setCompletedPics(getCompletedImages(bookId, htmlDoc));

        //解析小窍门
        Elements tips = htmlDoc.select("div.cp-show-main-trick p");
        if(tips.size() > 0) {
            ArrayList<String> list = new ArrayList<>();
            for (Element tip : tips) {
                list.add(tip.ownText());
            }
            book.setTips(list);
        }

        mBookDBHelper = new CookBookDBHelper(mContext);
        mBookDBHelper.insertCookBook(book);

        Bundle bundle = new Bundle();
        bundle.putSerializable("book", book);
        Message message = Message.obtain();
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    //解析菜谱的烹饪难度，时间和食材
    private CookingMaterial getMaterial(Document htmlDoc) {
        CookingMaterial material = new CookingMaterial();
        material.setUuid(UUID.randomUUID());
        Elements material_table = htmlDoc.select("table.cp-show-tab");
        if(material_table.size() > 0) {
            Element table = material_table.first();
            if(null != table) {
                Elements trs = table.select("table tr");
                if(trs.size() > 0) {
                    material.setDifficulty(trs.get(0).child(0).ownText());
                    material.setCookingTime(trs.get(0).child(1).ownText());

                    int index = 2;
                    ArrayList<MaterialInfo> main_infos = new ArrayList<>();
                    for(; index < trs.size(); index++) {
                        Element tr = trs.get(index);
                        if(tr.children().size() > 0 && tr.child(0).hasAttr("colspan")) {
                            break;
                        }

                        Elements tds = tr.select("tr td");
                        if(tds.size() > 0) {
                            for(Element e : tds) {
                                MaterialInfo info = new MaterialInfo();
                                info.setName(e.child(0).ownText());
                                info.setDosage(e.child(1).ownText());
                                main_infos.add(info);
                            }
                            material.setMainMaterials(main_infos);
                        }
                    }

                    index++;
                    ArrayList<MaterialInfo> o_infos = new ArrayList<>();
                    for(; index < trs.size(); index++) {
                        Element tr = trs.get(index);
                        Elements tds = tr.select("tr td");
                        if(tds.size() > 0) {
                            for(Element e : tds) {
                                MaterialInfo info = new MaterialInfo();
                                info.setName(e.child(0).ownText());
                                info.setDosage(e.child(1).ownText());
                                o_infos.add(info);
                            }
                            material.setIngredients(o_infos);
                        }
                    }
                }
            }
        }
        return material;
    }

    //解析烹饪步骤
    private List<CookingStep> getCookingStep(String bookId, Document htmlDoc) {
        ArrayList<CookingStep> steps = null;
        ImageDownloader imageDownloader = new ImageDownloader(mContext);

        Elements lis = htmlDoc.select("ol.wz_list li");
        if(lis.size() > 0) {
            steps = new ArrayList<>();
            for(Element li : lis) {
                CookingStep step = new CookingStep();
                step.setName(li.ownText());
                String order = li.child(0).ownText();
                order = order.substring(0, order.length() - 1);
                step.setOrder(Integer.parseInt(order));
                steps.add(step);
            }
        }

        Elements steps_e = htmlDoc.select("div.cp-show-main-step-item");
        if(steps_e.size() > 0) {
            steps = new ArrayList<>();
            for(Element s : steps_e) {
                CookingStep step = new CookingStep();
                step.setOrder(Integer.parseInt(s.child(0).ownText()));
                step.setName(s.child(1).ownText());
                step.setImg_path(s.child(2).attr("src"));
                imageDownloader.download(bookId, step.getImg_path());
                steps.add(step);
            }
        }

        return steps;
    }

    //解析烹饪成品图
    private List<String> getCompletedImages(String bookId, Document htmlDoc) {
        ArrayList<String> images = null;
        ImageDownloader imageDownloader = new ImageDownloader(mContext);
        Elements e_images = htmlDoc.select("div.wz_pic img");
        if(e_images.size() > 0) {
            images = new ArrayList<>();
            for (Element e : e_images) {
                String image = e.attr("src");
                imageDownloader.download(bookId, image);
                images.add(image);
            }
        }
        return images;
    }
}