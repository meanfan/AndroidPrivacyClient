package com.mean.androidprivacy;

import com.crossbowffs.remotepreferences.RemotePreferenceProvider;
import com.mean.androidprivacy.utils.PreferenceUtils;

public class ModulePreferenceProvider extends RemotePreferenceProvider {
    public ModulePreferenceProvider(){
        super(PreferenceUtils.PACKAGE_NAME, new String[] {PreferenceUtils.MODULE_CONFIG_NAME});
    }
}
