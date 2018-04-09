package com.mofic.skindemo;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.mofic.skindemo.skin.BaseActivity;
import com.mofic.skindemo.skin.SkinManager;

public class Main2Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        findViewById(R.id.btn_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        change();
    }

    private void change() {
        String path = Environment.getExternalStorageDirectory().getPath() + "/skin.skin";
        if (SkinManager.getInstance().isDefSkin()) {
            SkinManager.getInstance().load(path, null);
        } else {
            SkinManager.getInstance().unLoad();
        }
    }
}
