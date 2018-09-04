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


import android.os.SystemClock;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;

import javax.net.ssl.SSLException;

import cz.msebera.android.httpclient.NoHttpResponseException;
import cz.msebera.android.httpclient.client.HttpRequestRetryHandler;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.protocol.HttpContext;

class RetryHandler
        implements HttpRequestRetryHandler
{
    private static final HashSet<Class<?>> exceptionWhitelist = new HashSet();
    private static final HashSet<Class<?>> exceptionBlacklist = new HashSet();
    private final int maxRetries;
    private final int retrySleepTimeMS;

    static
    {
        exceptionWhitelist.add(NoHttpResponseException.class);

        exceptionWhitelist.add(UnknownHostException.class);

        exceptionWhitelist.add(SocketException.class);

        exceptionBlacklist.add(InterruptedIOException.class);

        exceptionBlacklist.add(SSLException.class);
    }

    public RetryHandler(int maxRetries, int retrySleepTimeMS)
    {
        this.maxRetries = maxRetries;
        this.retrySleepTimeMS = retrySleepTimeMS;
    }

    static void addClassToWhitelist(Class<?> cls)
    {
        exceptionWhitelist.add(cls);
    }

    static void addClassToBlacklist(Class<?> cls)
    {
        exceptionBlacklist.add(cls);
    }

    public boolean retryRequest(IOException exception, int executionCount, HttpContext context)
    {
        boolean retry = true;

        Boolean b = (Boolean)context.getAttribute("http.request_sent");
        boolean sent = (b != null) && (b.booleanValue());
        if (executionCount > this.maxRetries) {
            retry = false;
        } else if (isInList(exceptionWhitelist, exception)) {
            retry = true;
        } else if (isInList(exceptionBlacklist, exception)) {
            retry = false;
        } else if (!sent) {
            retry = true;
        }
        if (retry)
        {
            HttpUriRequest currentReq = (HttpUriRequest)context.getAttribute("http.request");
            if (currentReq == null) {
                return false;
            }
        }
        if (retry) {
            SystemClock.sleep(this.retrySleepTimeMS);
        } else {
            exception.printStackTrace();
        }
        return retry;
    }

    protected boolean isInList(HashSet<Class<?>> list, Throwable error)
    {
        for (Class<?> aList : list) {
            if (aList.isInstance(error)) {
                return true;
            }
        }
        return false;
    }
}
