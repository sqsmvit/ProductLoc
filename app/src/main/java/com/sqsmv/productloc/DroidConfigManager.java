package com.sqsmv.productloc;

import android.content.Context;
import android.content.SharedPreferences;

public class DroidConfigManager
{
    public final static String DROPBOX_ACCESS_TOKEN = "dropboxAccessToken";
    public final static String BUILD_DATE = "buildDate";
    public final static String SCANNER_LOCK = "scannerLock";
    public final static String CURRENT_APK_REV = "currentAPKRev";
    public final static String PRIOR_VERSION = "priorVersion";
    public final static String PRODUCTLENS_RESET_DATE = "ProductLensResetDate";
    public final static String EXPORT_MODE_CHOICE = "ExportModeChoice";
    public final static String INVENTORY_MODE_CHOICE = "InventoryModeChoice";
    public final static String IS_AUTO_COUNT = "isAutoCount";
    public final static String AUTO_COUNT_IDX = "autoCountIdx";
    public final static String AUTO_COUNT = "autoCount";
    public final static String CUSTOM_AUTO_COUNT = "customAutoCount";
    public final static String IS_BOX_QTY = "isBoxQty";
    public final static String BOX_QTY = "boxQty";
    public final static String LENS_SELECTION_ID = "lensSelectionId";
    public final static String LENS_SELECT_IDX = "lensSelectIdx";
    public final static String LENS_SELECTION = "lensSelection";

    public final static String ROOM_SELECTION_ID = "roomSelectionId";
    public final static String ROOM_SELECTION_IDX = "roomSelectionIdx";
    public final static String ROOM_SELECTION = "roomSelection";


    private SharedPreferences config;
    private final static String prefName = "scanConfig";

    DroidConfigManager(Context activityContext)
    {
        config = activityContext.getSharedPreferences(prefName, 0);
    }

    public String accessString(String key, String value, String defaultVal)
    {
        if(value != null)
        {
            config.edit().putString(key, value).apply();
        }
        return config.getString(key, defaultVal);
    }

    public int accessInt(String key, Integer value, int defaultVal)
    {
        if(value != null)
        {
            config.edit().putInt(key, value).apply();
        }
        return config.getInt(key, defaultVal);
    }

    public float accessFloat(String key, Float value, float defaultVal)
    {
        if(value != null)
        {
            config.edit().putFloat(key, value).apply();
        }
        return config.getFloat(key, defaultVal);
    }

    public boolean accessBoolean(String key, Boolean value, boolean defaultVal)
    {
        if(value != null)
        {
            config.edit().putBoolean(key, value).apply();
        }
        return config.getBoolean(key, defaultVal);
    }
}
