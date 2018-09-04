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


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URI;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpResponseException;
import cz.msebera.android.httpclient.util.ByteArrayBuffer;

public abstract class AsyncHttpResponseHandler
        implements ResponseHandlerInterface
{
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String UTF8_BOM = "?";
    protected static final int SUCCESS_MESSAGE = 0;
    protected static final int FAILURE_MESSAGE = 1;
    protected static final int START_MESSAGE = 2;
    protected static final int FINISH_MESSAGE = 3;
    protected static final int PROGRESS_MESSAGE = 4;
    protected static final int RETRY_MESSAGE = 5;
    protected static final int CANCEL_MESSAGE = 6;
    protected static final int BUFFER_SIZE = 4096;
    private static final String LOG_TAG = "AsyncHttpRH";
    private String responseCharset = "UTF-8";
    private Handler handler;
    private boolean useSynchronousMode;
    private boolean usePoolThread;
    private URI requestURI = null;
    private Header[] requestHeaders = null;
    private Looper looper = null;
    private WeakReference<Object> TAG = new WeakReference(null);

    public AsyncHttpResponseHandler()
    {
        this(null);
    }

    public AsyncHttpResponseHandler(Looper looper)
    {
        this.looper = (looper == null ? Looper.myLooper() : looper);

        setUseSynchronousMode(false);

        setUsePoolThread(false);
    }

    public AsyncHttpResponseHandler(boolean usePoolThread)
    {
        setUsePoolThread(usePoolThread);
        if (!getUsePoolThread())
        {
            this.looper = Looper.myLooper();

            setUseSynchronousMode(false);
        }
    }

    public Object getTag()
    {
        return this.TAG.get();
    }

    public void setTag(Object TAG)
    {
        this.TAG = new WeakReference(TAG);
    }

    public URI getRequestURI()
    {
        return this.requestURI;
    }

    public void setRequestURI(URI requestURI)
    {
        this.requestURI = requestURI;
    }

    public Header[] getRequestHeaders()
    {
        return this.requestHeaders;
    }

    public void setRequestHeaders(Header[] requestHeaders)
    {
        this.requestHeaders = requestHeaders;
    }

    public boolean getUseSynchronousMode()
    {
        return this.useSynchronousMode;
    }

    public void setUseSynchronousMode(boolean sync)
    {
        if ((!sync) && (this.looper == null))
        {
            sync = true;
            AsyncHttpClient.log.w("AsyncHttpRH", "Current thread has not called Looper.prepare(). Forcing synchronous mode.");
        }
        if ((!sync) && (this.handler == null)) {
            this.handler = new ResponderHandler(this, this.looper);
        } else if ((sync) && (this.handler != null)) {
            this.handler = null;
        }
        this.useSynchronousMode = sync;
    }

    public boolean getUsePoolThread()
    {
        return this.usePoolThread;
    }

    public void setUsePoolThread(boolean pool)
    {
        if (pool)
        {
            this.looper = null;
            this.handler = null;
        }
        this.usePoolThread = pool;
    }

    public String getCharset()
    {
        return this.responseCharset == null ? "UTF-8" : this.responseCharset;
    }

    public void setCharset(String charset)
    {
        this.responseCharset = charset;
    }

    public void onProgress(long bytesWritten, long totalSize)
    {
        AsyncHttpClient.log.v("AsyncHttpRH", String.format("Progress %d from %d (%2.0f%%)", new Object[] { Long.valueOf(bytesWritten), Long.valueOf(totalSize), Double.valueOf(totalSize > 0L ? bytesWritten * 1.0D / totalSize * 100.0D : -1.0D) }));
    }

    public void onStart() {}

    public void onFinish() {}

    public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {}

    public void onPostProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {}

    public abstract void onSuccess(int paramInt, Header[] paramArrayOfHeader, byte[] paramArrayOfByte);

    public abstract void onFailure(int paramInt, Header[] paramArrayOfHeader, byte[] paramArrayOfByte, Throwable paramThrowable);

    public void onRetry(int retryNo)
    {
        AsyncHttpClient.log.d("AsyncHttpRH", String.format("Request retry no. %d", new Object[] { Integer.valueOf(retryNo) }));
    }

    public void onCancel()
    {
        AsyncHttpClient.log.d("AsyncHttpRH", "Request got cancelled");
    }

    public void onUserException(Throwable error)
    {
        AsyncHttpClient.log.e("AsyncHttpRH", "User-space exception detected!", error);
        throw new RuntimeException(error);
    }

    public final void sendProgressMessage(long bytesWritten, long bytesTotal)
    {
        sendMessage(obtainMessage(4, new Object[] { Long.valueOf(bytesWritten), Long.valueOf(bytesTotal) }));
    }

    public final void sendSuccessMessage(int statusCode, Header[] headers, byte[] responseBytes)
    {
        sendMessage(obtainMessage(0, new Object[] { Integer.valueOf(statusCode), headers, responseBytes }));
    }

    public final void sendFailureMessage(int statusCode, Header[] headers, byte[] responseBody, Throwable throwable)
    {
        sendMessage(obtainMessage(1, new Object[] { Integer.valueOf(statusCode), headers, responseBody, throwable }));
    }

    public final void sendStartMessage()
    {
        sendMessage(obtainMessage(2, null));
    }

    public final void sendFinishMessage()
    {
        sendMessage(obtainMessage(3, null));
    }

    public final void sendRetryMessage(int retryNo)
    {
        sendMessage(obtainMessage(5, new Object[] { Integer.valueOf(retryNo) }));
    }

    public final void sendCancelMessage()
    {
        sendMessage(obtainMessage(6, null));
    }

    protected void handleMessage(Message message)
    {
        try
        {
            Object[] response = null;
            switch (message.what)
            {
                case 0:
                    response =  (Object[])message.obj;
                    if ((response != null) && (response.length >= 3)) {
                        onSuccess(((Integer)response[0]).intValue(), (Header[])response[1], (byte[])response[2]);
                    } else {
                        AsyncHttpClient.log.e("AsyncHttpRH", "SUCCESS_MESSAGE didn't got enough params");
                    }
                    break;
                case 1:
                    response = (Object[])message.obj;
                    if ((response != null) && (response.length >= 4)) {
                        onFailure(((Integer)response[0]).intValue(), (Header[])response[1], (byte[])response[2], (Throwable)response[3]);
                    } else {
                        AsyncHttpClient.log.e("AsyncHttpRH", "FAILURE_MESSAGE didn't got enough params");
                    }
                    break;
                case 2:
                    onStart();
                    break;
                case 3:
                    onFinish();
                    break;
                case 4:
                    response = (Object[])message.obj;
                    if ((response != null) && (response.length >= 2)) {
                        try
                        {
                            onProgress(((Long)response[0]).longValue(), ((Long)response[1]).longValue());
                        }
                        catch (Throwable t)
                        {
                            AsyncHttpClient.log.e("AsyncHttpRH", "custom onProgress contains an error", t);
                        }
                    } else {
                        AsyncHttpClient.log.e("AsyncHttpRH", "PROGRESS_MESSAGE didn't got enough params");
                    }
                    break;
                case 5:
                    response = (Object[])message.obj;
                    if ((response != null) && (response.length == 1)) {
                        onRetry(((Integer)response[0]).intValue());
                    } else {
                        AsyncHttpClient.log.e("AsyncHttpRH", "RETRY_MESSAGE didn't get enough params");
                    }
                    break;
                case 6:
                    onCancel();
            }
        }
        catch (Throwable error)
        {
            onUserException(error);
        }
    }

    protected void sendMessage(Message msg)
    {
        if ((getUseSynchronousMode()) || (this.handler == null))
        {
            handleMessage(msg);
        }
        else if (!Thread.currentThread().isInterrupted())
        {
            Utils.asserts(this.handler != null, "handler should not be null!");
            this.handler.sendMessage(msg);
        }
    }

    protected void postRunnable(Runnable runnable)
    {
        if (runnable != null) {
            if ((getUseSynchronousMode()) || (this.handler == null)) {
                runnable.run();
            } else {
                this.handler.post(runnable);
            }
        }
    }

    protected Message obtainMessage(int responseMessageId, Object responseMessageData)
    {
        return Message.obtain(this.handler, responseMessageId, responseMessageData);
    }

    public void sendResponseMessage(HttpResponse response)
            throws IOException
    {
        if (!Thread.currentThread().isInterrupted())
        {
            StatusLine status = response.getStatusLine();

            byte[] responseBody = getResponseData(response.getEntity());
            if (!Thread.currentThread().isInterrupted()) {
                if (status.getStatusCode() >= 300) {
                    sendFailureMessage(status.getStatusCode(), response.getAllHeaders(), responseBody, new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()));
                } else {
                    sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(), responseBody);
                }
            }
        }
    }

    byte[] getResponseData(HttpEntity entity)
            throws IOException
    {
        byte[] responseBody = null;
        if (entity != null)
        {
            InputStream instream = entity.getContent();
            if (instream != null)
            {
                long contentLength = entity.getContentLength();
                if (contentLength > 2147483647L) {
                    throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
                }
                int buffersize = contentLength <= 0L ? 4096 : (int)contentLength;
                try
                {
                    ByteArrayBuffer buffer = new ByteArrayBuffer(buffersize);
                    try
                    {
                        byte[] tmp = new byte['?'];
                        long count = 0L;
                        int l;
                        while (((l = instream.read(tmp)) != -1) && (!Thread.currentThread().isInterrupted()))
                        {
                            count += l;
                            buffer.append(tmp, 0, l);
                            sendProgressMessage(count, contentLength <= 0L ? 1L : contentLength);
                        }
                    }
                    finally
                    {
                        AsyncHttpClient.silentCloseInputStream(instream);
                        AsyncHttpClient.endEntityViaReflection(entity);
                    }
                    responseBody = buffer.toByteArray();
                }
                catch (OutOfMemoryError e)
                {
                    System.gc();
                    throw new IOException("File too large to fit into available memory");
                }
            }
        }
        return responseBody;
    }

    private static class ResponderHandler
            extends Handler
    {
        private final AsyncHttpResponseHandler mResponder;

        ResponderHandler(AsyncHttpResponseHandler mResponder, Looper looper)
        {
            super();
            this.mResponder = mResponder;
        }

        public void handleMessage(Message msg)
        {
            this.mResponder.handleMessage(msg);
        }
    }
}
