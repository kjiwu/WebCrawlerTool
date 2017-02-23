package com.starter.wulei.webcrawlertool.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.starter.wulei.webcrawlertool.R;
import com.starter.wulei.webcrawlertool.fragments.WebViewFragment;
import com.starter.wulei.webcrawlertool.models.CookBook;
import com.starter.wulei.webcrawlertool.utilities.CookingBookResolver;
import com.starter.wulei.webcrawlertool.utilities.CookingsDBHelper;

public class MainActivity extends AppCompatActivity {

    private Button mButtonTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.replace(R.id.activity_main, new WebViewFragment());
        trans.commit();*/

        mButtonTest = (Button) findViewById(R.id.button_test);
        mButtonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CookingBookResolver resolver = new CookingBookResolver(MainActivity.this);
                resolver.setOnCookBookResolver(new CookingBookResolver.OnCookingBookResolver() {
                    @Override
                    public void cookingBookCompleted(CookBook book) {
                        Log.d("MainActivity", "get cookbook completed, name:" + book.getTitle());
                    }
                });
                resolver.resolveHtml("http://www.chinacaipu.com/caipu/9708.html");
            }
        });
    }
}
