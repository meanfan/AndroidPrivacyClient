package com.mean.androidprivacy.hook;

import de.robv.android.xposed.XC_MethodReplacement;

public class HookMethods {
    public static XC_MethodReplacement BooleanReturnMethodReplacement(final boolean rt){
        return new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return rt;
            }
        };
    }
}
