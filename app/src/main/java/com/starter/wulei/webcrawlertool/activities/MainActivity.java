package com.starter.wulei.webcrawlertool.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.starter.wulei.webcrawlertool.R;
import com.starter.wulei.webcrawlertool.databse.CookingsDBHelper;
import com.starter.wulei.webcrawlertool.fragments.WebViewFragment;
import com.starter.wulei.webcrawlertool.models.CookBook;
import com.starter.wulei.webcrawlertool.resolvers.CookingBookResolver;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button mButtonLoadCookBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.replace(R.id.activity_main, new WebViewFragment());
        trans.commit();

        mButtonLoadCookBooks = (Button) findViewById(R.id.button_load_cookbooks);
        mButtonLoadCookBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonLoadCookBooks.setEnabled(false);
                CookingsDBHelper dbHelper = new CookingsDBHelper(MainActivity.this);
                List<String> urls = dbHelper.getCookingUrls();
                if(null != urls && urls.size() > 0) {
                    loadCookBook(0, urls);
                }
            }
        });
    }

    private void loadCookBook(final int index, final List<String> urls) {
        if(index >= urls.size()) {
            mButtonLoadCookBooks.setEnabled(true);
            Toast.makeText(MainActivity.this, "load cookbook over.", Toast.LENGTH_SHORT);
            return;
        }

        CookingBookResolver resolver = new CookingBookResolver(MainActivity.this);
        resolver.setOnCookBookResolver(new CookingBookResolver.OnCookingBookResolver() {
            @Override
            public void cookingBookCompleted(CookBook book) {
                Log.d("MainActivity", "get cookbook completed, name:" + book.getTitle());
                int i = index + 1;
                loadCookBook(i, urls);
            }
        });
        resolver.resolveHtml(urls.get(index));
    }
}
