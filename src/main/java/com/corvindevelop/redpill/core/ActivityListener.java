package com.corvindevelop.redpill.core;

public interface ActivityListener {
    void onStateChanged(boolean active);

    void onAction(long actionCount);

    void onLog(String message);

    void onError(Throwable error);
}
