package com.loopj.android.http;

/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    https://loopj.com
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
        https://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/


import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

public class LogHandler
        implements LogInterface
{
    boolean mLoggingEnabled = true;
    int mLoggingLevel = 2;

    public boolean isLoggingEnabled()
    {
        return this.mLoggingEnabled;
    }

    public void setLoggingEnabled(boolean loggingEnabled)
    {
        this.mLoggingEnabled = loggingEnabled;
    }

    public int getLoggingLevel()
    {
        return this.mLoggingLevel;
    }

    public void setLoggingLevel(int loggingLevel)
    {
        this.mLoggingLevel = loggingLevel;
    }

    public boolean shouldLog(int logLevel)
    {
        return logLevel >= this.mLoggingLevel;
    }

    public void log(int logLevel, String tag, String msg)
    {
        logWithThrowable(logLevel, tag, msg, null);
    }

    public void logWithThrowable(int logLevel, String tag, String msg, Throwable t)
    {
        if ((isLoggingEnabled()) && (shouldLog(logLevel))) {
            switch (logLevel)
            {
                case 2:
                    Log.v(tag, msg, t);
                    break;
                case 5:
                    Log.w(tag, msg, t);
                    break;
                case 6:
                    Log.e(tag, msg, t);
                    break;
                case 3:
                    Log.d(tag, msg, t);
                    break;
                case 8:
                    if (Integer.valueOf(Build.VERSION.SDK).intValue() > 8) {
                        checkedWtf(tag, msg, t);
                    } else {
                        Log.e(tag, msg, t);
                    }
                    break;
                case 4:
                    Log.i(tag, msg, t);
            }
        }
    }

    @TargetApi(8)
    private void checkedWtf(String tag, String msg, Throwable t)
    {
        Log.wtf(tag, msg, t);
    }

    public void v(String tag, String msg)
    {
        log(2, tag, msg);
    }

    public void v(String tag, String msg, Throwable t)
    {
        logWithThrowable(2, tag, msg, t);
    }

    public void d(String tag, String msg)
    {
        log(2, tag, msg);
    }

    public void d(String tag, String msg, Throwable t)
    {
        logWithThrowable(3, tag, msg, t);
    }

    public void i(String tag, String msg)
    {
        log(4, tag, msg);
    }

    public void i(String tag, String msg, Throwable t)
    {
        logWithThrowable(4, tag, msg, t);
    }

    public void w(String tag, String msg)
    {
        log(5, tag, msg);
    }

    public void w(String tag, String msg, Throwable t)
    {
        logWithThrowable(5, tag, msg, t);
    }

    public void e(String tag, String msg)
    {
        log(6, tag, msg);
    }

    public void e(String tag, String msg, Throwable t)
    {
        logWithThrowable(6, tag, msg, t);
    }

    public void wtf(String tag, String msg)
    {
        log(8, tag, msg);
    }

    public void wtf(String tag, String msg, Throwable t)
    {
        logWithThrowable(8, tag, msg, t);
    }
}
