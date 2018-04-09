package com.mofic.skindemo.skin.attrs;

import android.view.View;

/**
 * @author lanweining
 * @Date 2018/4/9 上午9:41
 *
 * 需要替换资源的属性实现该抽象类
 */
public abstract class AttrItem {
    private int mResId;
    private String mEntryName;
    private String mTypeName;
    private View mView;

    public int getResId() {
        return mResId;
    }

    public void setResId(int resId) {
        this.mResId = resId;
    }

    public String getEntryName() {
        return mEntryName;
    }

    public void setEntryName(String entryName) {
        this.mEntryName = entryName;
    }

    public String getTypeName() {
        return mTypeName;
    }

    public void setTypeName(String typeName) {
        this.mTypeName = typeName;
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        mView = view;
    }

    /**
     * 资源文件改变的回调
     */
    public abstract void changeRes();
}
