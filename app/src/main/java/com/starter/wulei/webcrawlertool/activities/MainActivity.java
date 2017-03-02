package com.starter.wulei.webcrawlertool.activities;

import android.content.res.AssetManager;
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
import com.starter.wulei.webcrawlertool.resolvers.MaterialsResolver;
import com.starter.wulei.webcrawlertool.utilities.ImageDownloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Button mButtonSyncDetail;

    private Button mButtonSyncMaterials;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeDatabase();

        final FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.replace(R.id.activity_main, new WebViewFragment());
        trans.commit();

        mButtonSyncDetail = (Button) findViewById(R.id.button_sync_detail);
        mButtonSyncDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoadCookBooks();
            }
        });

        mButtonSyncMaterials = (Button) findViewById(R.id.button_sync_materials);
        mButtonSyncMaterials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialsResolver resolver = new MaterialsResolver(MainActivity.this);
                resolver.resolveHtml();
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

                            startLoadCookingImages();
                        }
                    }
                };

                Log.d(HTMLResolver.ST_RESOLVER_TAG, "当前下载的菜谱编号:0");
                resolver.resolveHtml(0, urls.get(0), listener);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        observable.subscribe();
    }

    private void startLoadCookingImages() {
        Observable observable = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(final ObservableEmitter e) throws Exception {
                CookingsDBHelper dbHelper = new CookingsDBHelper(MainActivity.this);
                final ImageDownloader downloader = new ImageDownloader(MainActivity.this);
                final List<String> urls = dbHelper.getCookingImageUrls(2001);
                Log.d(HTMLResolver.ST_RESOLVER_TAG, "共有" + urls.size() + "个数据要下载");

                ImageDownloader.ImageDownloadListener listener = new ImageDownloader.ImageDownloadListener() {
                    @Override
                    public void downloadCompleted(int index) {
                        int newIndex = index + 1;
                        if(newIndex < urls.size()) {
                            Log.d(HTMLResolver.ST_RESOLVER_TAG, "当前下载的菜谱编号:" + newIndex);
                            downloader.download(newIndex, "thumbs", urls.get(newIndex), this);
                        } else {
                            e.onComplete();
                        }
                    }
                };

                Log.d(HTMLResolver.ST_RESOLVER_TAG, "当前下载的菜谱编号:0");
                downloader.download(0, "thumbs", urls.get(0), listener);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        observable.subscribe();
    }

    private void initializeDatabase() {
        String path = "/data/data/" + this.getApplicationContext().getPackageName() + "/databases";
        File dir = new File(path);
        if(!dir.exists()) {
            dir.mkdir();
        }

        path += "/cookings.db";
        File file = new File(path);
        if(file.exists()) {
            return;
        }

        FileOutputStream out = null;
        InputStream in = null;
        try {
            out = new FileOutputStream(file);
            AssetManager manager = (AssetManager) getAssets();
            in = manager.open("cookings.db");
            byte[] buffer = new byte[1024];
            while (in.read(buffer) > 0) {
                out.write(buffer);
            }
            out.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (null != out) {
                    out.close();
                }
                if(null != in) {
                    in.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
