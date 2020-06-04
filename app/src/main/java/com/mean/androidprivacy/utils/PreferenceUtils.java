package com.mean.androidprivacy.utils;

import com.mean.androidprivacy.App;

/**
 * @ClassName: PreferenceUtils
 * @Description: Preference工具，用于存储基本的配置（例如开启状态）
 * @Author: MeanFan
 * @Version: 1.0
 */
public class PreferenceUtils {
    public static final String PACKAGE_NAME = App.class.getName();
    //public static final String AUTHORY = "com.mean.androidprivacy.pref.module";
    public static final String MODULE_CONFIG_NAME = "module_config";
    public static final String ENABLE = "enable";
    public static final String NOROOT_MODE = "noroot_mode";
}
