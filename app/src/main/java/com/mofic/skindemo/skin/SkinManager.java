package com.mofic.skindemo.skin;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.mofic.skindemo.skin.callback.SkinChangeCallback;
import com.mofic.skindemo.skin.callback.SkinLoadCallback;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lanweining
 * @Date 2018/4/9 上午9:17
 */
public class SkinManager {

    private String mPackageName;
    private Resources mExtRes;
    private Context mContext;
    private AssetManager mAssetManager;
    private Method mAddAssetPath;
    private SharedPreferences mSp;
    private List<SkinChangeCallback> mSkinChangeCallbacks = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private static final String SKIN_SP = "skin_sp";
    private static final String CURRENT_SKIN = "current_skin";
    private static final String CURRENT_SKIN_PATH = "current_skin_path";

    private static class Holder {
        static SkinManager sSkinManager = new SkinManager();
    }

    public static SkinManager getInstance() {
        return Holder.sSkinManager;
    }

    private SkinManager() {
    }

    /**
     * 初始化SkinManager，在Application中调用
     */
    public void init(Context context) {
        mContext = context;
        mSp = context.getSharedPreferences(SKIN_SP, Context.MODE_PRIVATE);
        //加载已保存的皮肤
        load();
    }

    /**
     * 返回加载了外部资源的的Resources
     *
     * @return 加载了外部资源的的Resources
     */
    public Resources getExtRes() {
        return mExtRes;
    }

    /**
     * 获取当前皮肤包的文件名
     *
     * @return 当前皮肤包的文件名, 默认皮肤是空名称
     */
    public String getCurrentSkin() {
        return mSp.getString(CURRENT_SKIN, "");
    }

    /**
     * 当前是否是默认皮肤
     */
    public boolean isDefSkin() {
        return TextUtils.isEmpty(mSp.getString(CURRENT_SKIN, ""));
    }

    private void load() {
        load(mSp.getString(CURRENT_SKIN_PATH, ""), null);
    }

    /**
     * 加载皮肤,需要有文件读取权限，否则无法加载
     *
     * @param path     皮肤文件路径
     * @param callback 回调
     */
    public void load(final String path, final SkinLoadCallback callback) {
        final File file = new File(path);
        //文件名必须有皮肤包的后缀
        if (!file.exists() || !path.endsWith(Constant.SKIN_PKG_SUFFIX)) {
            if (callback != null) {
                callback.onFailed();
            }
            return;
        }
        if (callback != null) {
            callback.onStart();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadPkg(path, file, callback);
            }
        }).start();
    }

    private synchronized void loadPkg(final String path, File file, final SkinLoadCallback callback) {
        try {
            //反射加载皮肤包
            if (mAssetManager == null) {
                mAssetManager = AssetManager.class.newInstance();
            }
            if (mAddAssetPath == null) {
                mAddAssetPath = mAssetManager.getClass().getMethod("addAssetPath", String.class);
            }
            mAddAssetPath.invoke(mAssetManager, path);
            //获取皮肤包名
            PackageInfo mInfo = mContext.getPackageManager().getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
            mPackageName = mInfo.packageName;
            //创建Resources
            Resources defRes = mContext.getResources();
            mExtRes = new Resources(mAssetManager, defRes.getDisplayMetrics(), defRes.getConfiguration());

            mSp.edit().putString(CURRENT_SKIN, file.getName())
                    .putString(CURRENT_SKIN_PATH, path)
                    .apply();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                    notifySkinChange();
                }
            });
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (callback != null) {
                        callback.onFailed();
                    }
                }
            });
        }
    }

    /**
     * 切回默认皮肤
     */
    public void unLoad() {
        mSp.edit().remove(CURRENT_SKIN)
                .remove(CURRENT_SKIN_PATH)
                .apply();
        notifySkinChange();
    }

    /**
     * 获取加载的皮肤包包名，如果没有加载成功，返回null
     */
    public String getPackageName() {
        return mPackageName;
    }

    /**
     * 根据皮肤获取颜色
     *
     * @param defId 默认的资源id
     */
    public int getColor(int defId) {
        int color;
        if (isDefSkin()) {
            color = mContext.getResources().getColor(defId);
        } else {
            try {
                color = mExtRes.getColor(getExtId(defId));
            } catch (Resources.NotFoundException e) {
                color = mContext.getResources().getColor(defId);
            }
        }
        return color;
    }

    /**
     * 根据皮肤获取ColorStateList
     *
     * @param defId 默认的资源id
     */
    public ColorStateList getColorStateList(int defId) {
        ColorStateList colorStateList;
        if (isDefSkin()) {
            colorStateList = mContext.getResources().getColorStateList(defId);
        } else {
            try {
                colorStateList = mExtRes.getColorStateList(getExtId(defId));
            } catch (Resources.NotFoundException e) {
                colorStateList = mContext.getResources().getColorStateList(defId);
            }
        }
        return colorStateList;
    }

    /**
     * 根据皮肤获取Drawable
     *
     * @param defId 默认的资源id
     */
    public Drawable getDrawable(int defId) {
        Drawable drawable;
        if (isDefSkin()) {
            drawable = mContext.getResources().getDrawable(defId);
        } else {
            try {
                drawable = mExtRes.getDrawable(getExtId(defId));
            } catch (Resources.NotFoundException e) {
                drawable = mContext.getResources().getDrawable(defId);
            }
        }
        return drawable;
    }

    /**
     * 获取皮肤包中的资源id
     *
     * @param defId 默认的资源id
     */
    private int getExtId(int defId) {
        String entryName = mContext.getResources().getResourceEntryName(defId);
        String typeName = mContext.getResources().getResourceTypeName(defId);
        return mExtRes.getIdentifier(entryName, typeName, mPackageName);
    }

    /**
     * 绑定换肤回调
     */
    public void attach(SkinChangeCallback callback) {
        mSkinChangeCallbacks.add(callback);
    }

    /**
     * 解绑换肤回调
     */
    public void detach(SkinChangeCallback callback) {
        mSkinChangeCallbacks.remove(callback);
    }

    /**
     * 通知刷新皮肤
     */
    private void notifySkinChange() {
        for (SkinChangeCallback callback : mSkinChangeCallbacks) {
            callback.onSkinChange();
        }
    }
}
