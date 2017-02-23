package com.starter.wulei.webcrawlertool.utilities;

import android.content.Context;
import android.util.Log;

import com.starter.wulei.webcrawlertool.models.CookingItem;
import com.starter.wulei.webcrawlertool.models.IHTMLResolver;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by wulei on 2017/2/23.
 */

public class HTMLResolver implements IHTMLResolver {
    private String mHtml;
    private Document htmlDoc;
    private CookingsDBHelper mDBHelper;

    public HTMLResolver(Context context) {
        mDBHelper = new CookingsDBHelper(context);
    }

    public void setHtml(String html) {
        mHtml = html;
        htmlDoc = Jsoup.parse(mHtml);
    }

    @Override
    public String getCurrentPage() {
        if(null == htmlDoc) { return null; }

        Elements elements = htmlDoc.select("div.cp-u-my-page a.cur");
        if(elements.size() > 0) {
            return elements.get(0).ownText();
        }
        return null;
    }

    @Override
    public boolean haveNextPage() {
        if(null == htmlDoc) { return false; }

        Elements elements = htmlDoc.select("div.cp-u-my-page a");
        if(elements.size() > 0) {
            int next_index = elements.size() - 2;
            Element nextButton = elements.get(next_index);
            return nextButton.hasAttr("class") == false;
        }
        return false;
    }

    public void getCookingList() {
        if(null == htmlDoc) { return; }

        Elements elements = htmlDoc.select("#list_t");
        if(elements.size() > 0) {
            Elements as = elements.get(0).getElementsByTag("a");
            int a_count = as.size();
            if(a_count > 0) {
                ArrayList<CookingItem> list = new ArrayList<>();
                for (int i = 0; i < a_count; i++) {
                    Element a = as.get(i);
                    if(a.hasAttr("class") && a.attr("class").equals("cp-edit")) {
                        continue;
                    }

                    if(a.hasAttr("href") && a.attr("href") == null) {
                        continue;
                    }

                    CookingItem item = new CookingItem();
                    item.name = a.children().get(1).children().get(0).ownText();
                    item.cookingId = StringHelper.getCookingId(item.name);
                    if(a.hasAttr("href")) {
                        item.url = a.attr("href");
                    }
                    item.image = a.child(0).attr("src");
                    item.image_name = StringHelper.getImageName(item.image);
                    Log.d("AA", item.name + " " + item.url + " " + item.image);
                    list.add(item);
                }
                mDBHelper.insertCooking(list);
            }
        }
    }
}
