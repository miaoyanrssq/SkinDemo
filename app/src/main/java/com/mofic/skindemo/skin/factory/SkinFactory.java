package com.mofic.skindemo.skin.factory;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;

import com.mofic.skindemo.skin.Constant;
import com.mofic.skindemo.skin.SkinManager;
import com.mofic.skindemo.skin.attrs.AttrItem;
import com.mofic.skindemo.skin.attrs.BackgroundItem;
import com.mofic.skindemo.skin.attrs.TextColorItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lanweining
 * @Date 2018/4/9 上午9:28
 */
public class SkinFactory implements LayoutInflater.Factory {

    private List<AttrItem> mAttrItems = new ArrayList<>();

    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app.",
            "android.view."
    };

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = createView(name, context, attrs);

        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            String attrValue = attrs.getAttributeValue(i);
            String attrName = attrs.getAttributeName(i);
            if (!attrValue.startsWith("@") && !isSupport(attrName)) {
                //排除不以@开头的attrValue（非引用类型的资源）和不支持替换的属性
                continue;
            }

            int resId = Integer.valueOf(attrValue.substring(1));
            //资源id名称
            String entryName = context.getResources().getResourceEntryName(resId);
            //资源类型
            String typeName = context.getResources().getResourceTypeName(resId);

            AttrItem attrItem = null;
            switch (attrName) {
                //需要支持更多的类型在这里添加AttrItem的实现类
                case Constant.ATTR_NAME_TEXT_COLOR:
                    attrItem = new TextColorItem();
                    break;
                case Constant.ATTR_NAME_BACKGROUND:
                    attrItem = new BackgroundItem();
                    break;
            }
            if (attrItem != null) {
                attrItem.setResId(resId);
                attrItem.setEntryName(entryName);
                attrItem.setTypeName(typeName);
                attrItem.setView(view);
                //缓存能够替换资源的属性
                mAttrItems.add(attrItem);
            }
        }
        String currentSkin = SkinManager.getInstance().getCurrentSkin();
        if (!TextUtils.isEmpty(currentSkin)) {
            //已经加载了皮肤
            refresh();
        }
        return view;
    }

    private boolean isSupport(String attrName) {
        //在这里添加支持的类型
        return Constant.ATTR_NAME_TEXT_COLOR.equals(attrName) ||
                Constant.ATTR_NAME_BACKGROUND.equals(attrName);
    }

    private View createView(String name, Context context, AttributeSet attrs) {
        View view = null;
        try {
            //这里参考PhoneLayoutInflater中View的创建过程
            if (-1 == name.indexOf('.')) {
                for (String prefix : sClassPrefixList) {
                    view = LayoutInflater.from(context).createView(name, prefix, attrs);
                    if (view != null) {
                        return view;
                    }
                }
            } else {
                view = LayoutInflater.from(context).createView(name, null, attrs);
            }
        } catch (ClassNotFoundException | InflateException e) {
            e.printStackTrace();
        }
        return view;
    }

    /**
     * 刷新替换皮肤资源
     */
    public void refresh() {
        for (AttrItem item : mAttrItems) {
            item.changeRes();
        }
    }
}
