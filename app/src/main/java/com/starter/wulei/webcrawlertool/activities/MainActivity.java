package com.starter.wulei.webcrawlertool.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.starter.wulei.webcrawlertool.R;
import com.starter.wulei.webcrawlertool.databse.CookingsDBHelper;
import com.starter.wulei.webcrawlertool.fragments.WebViewFragment;
import com.starter.wulei.webcrawlertool.resolvers.CookingBookResolver;
import com.starter.wulei.webcrawlertool.resolvers.HTMLResolver;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Button mButtonTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.replace(R.id.activity_main, new WebViewFragment());
        trans.commit();

        mButtonTest = (Button) findViewById(R.id.button_test);
        mButtonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* CookingsDBHelper dbHelper = new CookingsDBHelper(MainActivity.this);
                CookingMaterial material = new CookingMaterial();
                material.difficulty = "容易";
                material.materials = "A,B,C";
                dbHelper.updateCooking("9742", material);*/

                startLoadCookBooks();

                /*CookingBookResolver resolver = new CookingBookResolver(MainActivity.this);
                resolver.resolveHtml(0, "http://www.chinacaipu.com/caipu/7866.html", null);*/
            }
        });
    }

    private void startLoadCookBooks() {
        Observable observable = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(final ObservableEmitter e) throws Exception {
                CookingsDBHelper dbHelper = new CookingsDBHelper(MainActivity.this);
                final CookingBookResolver resolver = new CookingBookResolver(MainActivity.this);
                final List<String> urls = dbHelper.getCookingUrls();
                Log.d(HTMLResolver.ST_RESOLVER_TAG, "共有" + urls.size() + "个数据要下载");

                CookingBookResolver.OnCookingBookCompletedListener listener = new CookingBookResolver.OnCookingBookCompletedListener() {
                    @Override
                    public void cookingBookCompleted(int index) {
                        int newIndex = index + 1;
                        if(newIndex < urls.size()) {
                            Log.d(HTMLResolver.ST_RESOLVER_TAG, "当前下载的菜谱编号:" + newIndex);
                            resolver.resolveHtml(newIndex, urls.get(newIndex), this);
                        } else {
                            e.onComplete();
                        }
                    }
                };

                Log.d(HTMLResolver.ST_RESOLVER_TAG, "当前下载的菜谱编号:0");
                resolver.resolveHtml(0, urls.get(0), listener);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        observable.subscribe();
    }
}
