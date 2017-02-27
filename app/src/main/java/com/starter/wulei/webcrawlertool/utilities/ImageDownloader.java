package com.starter.wulei.webcrawlertool.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.starter.wulei.webcrawlertool.resolvers.HTMLResolver;

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

    private final static int IMAGE_QUALITY = 50;
    private final static String IMAGE_PREFIX = "st";

    private Context mContext;

    private int mQuality = IMAGE_QUALITY;

    public void setImageQuality(int quality) {
        if(quality < 0 || quality > 100) {
            mQuality = IMAGE_QUALITY;
        }
        mQuality = quality;
    }

    public ImageDownloader(Context context) {
        mContext = context;
    }

    public interface ImageDownloadListener {
        void downloadCompleted(int index);
    }

    public String getFileDirPath(String subDirName, String fileName) {
        if(null == fileName) {
            return null;
        }

        String path = null;
        if(null != subDirName) {
            if(subDirName.indexOf("/") == 0) {
                path = mContext.getFilesDir() + subDirName;
            } else {
                path = mContext.getFilesDir() + "/" + subDirName;
            }

            if(path.lastIndexOf("/") == path.length() - 1) {
                path = path.substring(0, path.length() - 2);
            }

            File f = new File(path);
            if(!f.exists()) {
                f.mkdirs();
            }

            if(fileName.indexOf("/") == 0) {
                path += fileName;
            } else {
                path += "/" + fileName;
            }
        } else {
            if(fileName.indexOf("/") == 0) {
                path = mContext.getFilesDir() + fileName;
            } else {
                path = mContext.getFilesDir() + "/" + fileName;
            }
        }

        return path;
    }

    public void download(final int index, final String subDir, final String imageUrl, final ImageDownloadListener listener) {
        Observable.empty()
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        try {
                            Log.d(HTMLResolver.ST_RESOLVER_TAG, "图片开始下载:" + imageUrl);
                            URL url = new URL(imageUrl);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.connect();
                            InputStream in = connection.getInputStream();
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            Bitmap empty = BitmapFactory.decodeStream(in, null, options);
                            float scale = empty.getWidth() / 160;
                            options.inJustDecodeBounds = false;
                            options.inSampleSize = Math.round(scale);
                            options.inPreferredConfig = Bitmap.Config.RGB_565;
                            Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
                            String imageName = IMAGE_PREFIX + StringHelper.getImageName(imageUrl);
                            String path = getFileDirPath(subDir, imageName);
                            File file = new File(path);
                            FileOutputStream out = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, mQuality, out);
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
                        Log.d(HTMLResolver.ST_RESOLVER_TAG, "下载图片完成");
                        if(null != listener) {
                            listener.downloadCompleted(index);
                        }
                    }
                })
        .subscribe();
    }

}
