package com.angle.multimediademo.Utils;

import android.content.Context;

import java.io.File;

public class FileUtils {

    /**
     * 获取项目根目录地址
     *
     * @param context 上下文对象
     * @return 文件全目录
     */
    public static String getVideoFilePath(Context context, String pathName) {
        final File dir = context.getExternalFilesDir(null);
        return (dir == null ? "" : (dir.getAbsolutePath() + File.separator))
                + pathName;
    }
}
