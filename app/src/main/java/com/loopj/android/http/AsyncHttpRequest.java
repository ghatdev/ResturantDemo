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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpRequestRetryHandler;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.impl.client.AbstractHttpClient;
import cz.msebera.android.httpclient.protocol.HttpContext;

public class AsyncHttpRequest
        implements Runnable
{
    private final AbstractHttpClient client;
    private final HttpContext context;
    private final HttpUriRequest request;
    private final ResponseHandlerInterface responseHandler;
    private final AtomicBoolean isCancelled = new AtomicBoolean();
    private int executionCount;
    private boolean cancelIsNotified;
    private volatile boolean isFinished;
    private boolean isRequestPreProcessed;

    public AsyncHttpRequest(AbstractHttpClient client, HttpContext context, HttpUriRequest request, ResponseHandlerInterface responseHandler)
    {
        this.client = ((AbstractHttpClient)Utils.notNull(client, "client"));
        this.context = ((HttpContext)Utils.notNull(context, "context"));
        this.request = ((HttpUriRequest)Utils.notNull(request, "request"));
        this.responseHandler = ((ResponseHandlerInterface)Utils.notNull(responseHandler, "responseHandler"));
    }

    public void onPreProcessRequest(AsyncHttpRequest request) {}

    public void onPostProcessRequest(AsyncHttpRequest request) {}

    public void run()
    {
        if (isCancelled()) {
            return;
        }
        if (!this.isRequestPreProcessed)
        {
            this.isRequestPreProcessed = true;
            onPreProcessRequest(this);
        }
        if (isCancelled()) {
            return;
        }
        this.responseHandler.sendStartMessage();
        if (isCancelled()) {
            return;
        }
        try
        {
            makeRequestWithRetries();
        }
        catch (IOException e)
        {
            if (!isCancelled()) {
                this.responseHandler.sendFailureMessage(0, null, null, e);
            } else {
                AsyncHttpClient.log.e("AsyncHttpRequest", "makeRequestWithRetries returned error", e);
            }
        }
        if (isCancelled()) {
            return;
        }
        this.responseHandler.sendFinishMessage();
        if (isCancelled()) {
            return;
        }
        onPostProcessRequest(this);

        this.isFinished = true;
    }

    private void makeRequest()
            throws IOException
    {
        if (isCancelled()) {
            return;
        }
        if (this.request.getURI().getScheme() == null) {
            throw new MalformedURLException("No valid URI scheme was provided");
        }
        if ((this.responseHandler instanceof RangeFileAsyncHttpResponseHandler)) {
            ((RangeFileAsyncHttpResponseHandler)this.responseHandler).updateRequestHeaders(this.request);
        }
        HttpResponse response = this.client.execute(this.request, this.context);
        if (isCancelled()) {
            return;
        }
        this.responseHandler.onPreProcessResponse(this.responseHandler, response);
        if (isCancelled()) {
            return;
        }
        this.responseHandler.sendResponseMessage(response);
        if (isCancelled()) {
            return;
        }
        this.responseHandler.onPostProcessResponse(this.responseHandler, response);
    }

    private void makeRequestWithRetries()
            throws IOException
    {
        boolean retry = true;
        IOException cause = null;
        HttpRequestRetryHandler retryHandler = this.client.getHttpRequestRetryHandler();
        try
        {
            while (retry)
            {
                try
                {
                    makeRequest();
                    return;
                }
                catch (UnknownHostException e)
                {
                    cause = new IOException("UnknownHostException exception: " + e.getMessage());
                    retry = (this.executionCount > 0) && (retryHandler.retryRequest(e, ++this.executionCount, this.context));
                }
                catch (NullPointerException e)
                {
                    cause = new IOException("NPE in HttpClient: " + e.getMessage());
                    retry = retryHandler.retryRequest(cause, ++this.executionCount, this.context);
                }
                catch (IOException e)
                {
                    if (isCancelled()) {
                        return;
                    }
                    cause = e;
                    retry = retryHandler.retryRequest(cause, ++this.executionCount, this.context);
                }
                if (retry) {
                    this.responseHandler.sendRetryMessage(this.executionCount);
                }
            }
        }
        catch (Exception e)
        {
            AsyncHttpClient.log.e("AsyncHttpRequest", "Unhandled exception origin cause", e);
            cause = new IOException("Unhandled exception: " + e.getMessage());
        }
        throw cause;
    }

    public boolean isCancelled()
    {
        boolean cancelled = this.isCancelled.get();
        if (cancelled) {
            sendCancelNotification();
        }
        return cancelled;
    }

    private synchronized void sendCancelNotification()
    {
        if ((!this.isFinished) && (this.isCancelled.get()) && (!this.cancelIsNotified))
        {
            this.cancelIsNotified = true;
            this.responseHandler.sendCancelMessage();
        }
    }

    public boolean isDone()
    {
        return (isCancelled()) || (this.isFinished);
    }

    public boolean cancel(boolean mayInterruptIfRunning)
    {
        this.isCancelled.set(true);
        this.request.abort();
        return isCancelled();
    }

    public AsyncHttpRequest setRequestTag(Object TAG)
    {
        this.responseHandler.setTag(TAG);
        return this;
    }

    public Object getTag()
    {
        return this.responseHandler.getTag();
    }
}
