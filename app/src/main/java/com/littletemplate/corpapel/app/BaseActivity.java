package com.littletemplate.corpapel.app;

import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import com.facebook.FacebookSdk;

import butterknife.ButterKnife;

/**
 * Created by ucweb02 on 27/12/2016.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }
}
