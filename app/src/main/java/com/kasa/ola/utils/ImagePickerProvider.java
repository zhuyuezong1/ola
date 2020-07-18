package com.kasa.ola.utils;

import android.content.Context;

import androidx.core.content.FileProvider;

/**
 * 自定义Provider，避免上层发生provider冲突
 */
public class ImagePickerProvider extends FileProvider {

    public static String getFileProviderName(Context context) {
        return context.getPackageName() + ".fileprovider";
    }
}
