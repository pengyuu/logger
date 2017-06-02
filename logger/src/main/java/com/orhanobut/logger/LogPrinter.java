package com.orhanobut.logger;

import android.support.annotation.NonNull;

import lombok.Getter;
import timber.log.Timber;

/**
 * extends {@link timber.log.Timber.Tree} for make log pretty
 */
public final class LogPrinter extends Timber.DebugTree {

    private PrintStyle style;

    /**
     * 因为如果设置了tag，那么会在timber中多走一个方法，方法栈会发生变化，造成不准确的情况。
     */
    private boolean isCustomTag = true;

    @Getter
    private final Settings settings;

    private static final String PROPERTY = System.getProperty("line.separator");

    LogPrinter(Settings settings) {
        this.settings = settings;
        this.style = settings.style;
    }

    /**
     * Auto tag
     */
    @Override
    protected String createStackElementTag(StackTraceElement ignored) {
        isCustomTag = false;
        int offset = Logger.STACK_OFFSET + settings.methodOffset - 1; // 调整栈的位置
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        int length = stackTrace.length;
        return super.createStackElementTag(length > offset ? stackTrace[offset] : stackTrace[stackTrace.length - 1]);
    }

    @Override
    protected void log(int priority, String tag, @NonNull String message, Throwable ignored) {
        if (style.beforePrint() != null) {
            super.log(priority, tag, style.beforePrint(), null);
        }

        String[] lines = message.split(PROPERTY);
        for (int i = 0, length = lines.length; i < length; i++) {
            String logStr = style.printLog(lines[i], i, length);
            super.log(priority, tag, logStr, null);
        }

        if (style.afterPrint() != null) {
            super.log((priority), tag, style.afterPrint(), null);
        }
        isCustomTag = true;
    }

    /**
     * 根据级别显示log
     *
     * @return 默认所有级别都显示
     */
    @Override
    protected boolean isLoggable(String tag, int priority) {
        return priority >= settings.priority;
    }

    public boolean isCustomTag() {
        return isCustomTag;
    }
}
