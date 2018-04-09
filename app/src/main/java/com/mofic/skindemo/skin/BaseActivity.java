package com.mofic.skindemo.skin;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;

import com.mofic.skindemo.skin.callback.SkinChangeCallback;
import com.mofic.skindemo.skin.factory.SkinFactory;


/**
 * @author lanweining
 * @Date 2018/4/9 下午1:19
 */
public class BaseActivity extends Activity implements SkinChangeCallback {
    private SkinFactory mSkinFactory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSkinFactory = new SkinFactory();
        LayoutInflater.from(this).setFactory(mSkinFactory);
        SkinManager.getInstance().attach(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            SkinManager.getInstance().detach(this);
        }
    }

    @Override
    public void onSkinChange() {
        if (mSkinFactory != null) {
            mSkinFactory.refresh();
        }
    }
}
