package com.starter.wulei.webcrawlertool.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wulei on 2017/2/24.
 */

public class ImageDownloader {

    private Context mContext;

    public ImageDownloader(Context context) {
        mContext = context;
    }

    public interface ImageDownloadListener {
        void downloadCompleted(Bitmap image);
        void downloadMultiCompleted();
    }

    private ImageDownloadListener mDownloadListener = null;
    public void setDownloadListener(ImageDownloadListener listener) {
        mDownloadListener = listener;
    }

    private void createFileDir(String bookId) {
        String path = mContext.getFilesDir() + "/CaiPu/" + bookId;
        File f = new File(path);
        if(!f.exists()) {
            f.mkdirs();
        }
    }

    public void download(final String bookId, final String imageUrl) {
        Observable.empty()
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        try {
                            URL url = new URL(imageUrl);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.connect();
                            InputStream in = connection.getInputStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(in);
                            String imageName = StringHelper.getImageName(imageUrl);
                            createFileDir(bookId);
                            String path = mContext.getFilesDir().getPath() + "/CaiPu/" + bookId + "/" + imageName;
                            File file = new File(path);
                            FileOutputStream out = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();
                            in.close();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.d("DownLoader", "update UI");
                    }
                })
        .subscribe();
    }

}
