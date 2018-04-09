package com.mofic.skindemo.skin.attrs;

import com.mofic.skindemo.skin.Constant;
import com.mofic.skindemo.skin.SkinManager;

/**
 * @author lanweining
 * @Date 2018/4/9 上午10:04
 *
 * 背景色替换类
 */
public class BackgroundItem extends AttrItem {

    @Override
    public void changeRes() {
        switch (getTypeName()) {
            case Constant.TYPE_COLOR:
                getView().setBackgroundColor(SkinManager.getInstance().getColor(getResId()));
                break;
            case Constant.TYPE_DRAWABLE:
                getView().setBackground(SkinManager.getInstance().getDrawable(getResId()));
                break;
        }
    }
}
