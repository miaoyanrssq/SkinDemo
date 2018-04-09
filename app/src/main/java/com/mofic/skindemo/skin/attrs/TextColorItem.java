package com.mofic.skindemo.skin.attrs;

import android.content.res.ColorStateList;
import android.widget.TextView;

import com.mofic.skindemo.skin.SkinManager;

/**
 * @author lanweining
 * @Date 2018/4/9 上午10:04
 *
 * 文字颜色替换类
 */
public class TextColorItem extends AttrItem {

    @Override
    public void changeRes() {
        if (getView() instanceof TextView) {
            ColorStateList colorStateList = SkinManager.getInstance().getColorStateList(getResId());
            ((TextView) getView()).setTextColor(colorStateList);
        }
    }
}