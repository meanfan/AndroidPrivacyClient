package com.mean.androidprivacy.provider;

import com.crossbowffs.remotepreferences.RemotePreferenceProvider;
import com.mean.androidprivacy.utils.PreferenceUtils;

/**
 * @ClassName: ModulePreferenceProvider
 * @Description: Preference提供者，用于保存开启状态
 * @Author: MeanFan
 * @Version: 1.0
 */
public class ModulePreferenceProvider extends RemotePreferenceProvider {
    public ModulePreferenceProvider(){
        super(PreferenceUtils.PACKAGE_NAME, new String[] {PreferenceUtils.MODULE_CONFIG_NAME});
    }
}
