package com.starter.wulei.webcrawlertool.resolvers;

import android.content.Context;
import android.util.Log;

import com.starter.wulei.webcrawlertool.databse.CookingsDBHelper;
import com.starter.wulei.webcrawlertool.models.CookingItem;
import com.starter.wulei.webcrawlertool.models.IHTMLResolver;
import com.starter.wulei.webcrawlertool.models.IJavaScriptInterface;
import com.starter.wulei.webcrawlertool.utilities.StringHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

public class HTMLResolver implements IHTMLResolver {
    public final static String ST_RESOLVER_TAG = "X-MAN";

    private String mHtml;
    private Document htmlDoc;
    private CookingsDBHelper mDBHelper;
    private Context mContext;

    public HTMLResolver(Context context) {
        mContext = context;
        mDBHelper = new CookingsDBHelper(context);
    }

    public void setHtml(String html) {
        mHtml = html;
        htmlDoc = Jsoup.parse(mHtml);
    }

    @Override
    public String getCurrentPage() {
        if (null == htmlDoc) {
            return null;
        }

        Elements elements = htmlDoc.select("div.cp-u-my-page a.cur");
        if (elements.size() > 0) {
            return elements.get(0).ownText();
        }
        return null;
    }

    @Override
    public boolean haveNextPage() {
        if (null == htmlDoc) {
            return false;
        }

        Elements elements = htmlDoc.select("div.cp-u-my-page a");
        if (elements.size() > 0) {
            int next_index = elements.size() - 2;
            Element nextButton = elements.get(next_index);
            return nextButton.hasAttr("class") == false;
        }
        return false;
    }

    public void getCookingList(final IJavaScriptInterface jsInterface) {
        if (null == htmlDoc) {
            return;
        }

        Observable.create(new ObservableOnSubscribe<CookingItem>() {
            @Override
            public void subscribe(ObservableEmitter<CookingItem> e) throws Exception {
                Elements elements = htmlDoc.select("#list_t li a");
                for (Element element : elements) {
                    if (element.hasAttr("class") && element.attr("class").equals("cp-edit")) {
                        continue;
                    }

                    if (element.hasAttr("href") && element.attr("href") == null) {
                        continue;
                    }
                    e.onNext(getCooking(element));
                }
                e.onComplete();
            }
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<CookingItem>() {
            Disposable mDisposable;

            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onNext(CookingItem value) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                jsInterface.getSourceOver(true);
            }
        });
    }

    private CookingItem getCooking(Element element) {
        CookingItem item = new CookingItem();
        item.name = element.children().get(1).children().get(0).ownText();
        item.cookingId = StringHelper.getCookingId(item.name);
        if (element.hasAttr("href")) {
            item.url = element.attr("href");
        }
        item.image = element.child(0).attr("src");
        item.image_name = StringHelper.getImageName(item.image);
        Log.d(ST_RESOLVER_TAG, "cookingId: " + item.cookingId +"name: " + item.name + " url:" + item.url + " image:" + item.image);
        mDBHelper.insertCooking(item);
        return item;
    }
}
