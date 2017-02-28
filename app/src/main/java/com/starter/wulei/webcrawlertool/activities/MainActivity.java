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

    private Button mButtonTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeDatabase();

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

                //startLoadCookBooks();

                /*CookingBookResolver resolver = new CookingBookResolver(MainActivity.this);
                resolver.resolveHtml(0, "http://www.chinacaipu.com/caipu/7866.html", null);*/

                startLoadCookingImages();

                /*
                String url1 = "http://static.chinacaipu.com/upload/e/148790841797.jpg";
                String url2 = "http://static.chinacaipu.com/upload/0/148791566527.jpg";
                String url3 = "http://static.chinacaipu.com/upload/c/148388185631.png";
                String url4 = "http://static.chinacaipu.com/upload/a/14785910454.gif";

                ImageDownloader downloader_a = new ImageDownloader(MainActivity.this);
                downloader_a.download(0, "a", url1, null);

                ImageDownloader downloader_b = new ImageDownloader(MainActivity.this);
                downloader_b.setImageQuality(25);
                downloader_b.download(0, "a", url2, null);

                ImageDownloader downloader_c = new ImageDownloader(MainActivity.this);
                downloader_b.setImageQuality(25);
                downloader_b.download(0, "a", url3, null);

                ImageDownloader downloader_d = new ImageDownloader(MainActivity.this);
                downloader_b.setImageQuality(25);
                downloader_b.download(0, "a", url4, null);
                */
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
