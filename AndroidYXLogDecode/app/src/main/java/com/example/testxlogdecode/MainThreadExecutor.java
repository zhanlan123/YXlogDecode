package com.example.testxlogdecode;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * Created by ZP on 2016/10/28.
 */

public class MainThreadExecutor implements Executor {

    private MainThreadExecutor() {

    }

    private static MainThreadExecutor sMainThreadExecutor;

    public synchronized static MainThreadExecutor getInstance() {
        if (sMainThreadExecutor == null) {
            return new MainThreadExecutor();
        }
        return sMainThreadExecutor;
    }

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(Runnable command) {
        handler.post(command);
    }
}
