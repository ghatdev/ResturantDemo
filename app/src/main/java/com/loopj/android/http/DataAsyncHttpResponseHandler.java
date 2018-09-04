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


import android.os.Message;

import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.util.ByteArrayBuffer;

public abstract class DataAsyncHttpResponseHandler
        extends AsyncHttpResponseHandler
{
    protected static final int PROGRESS_DATA_MESSAGE = 7;
    private static final String LOG_TAG = "DataAsyncHttpRH";

    public static byte[] copyOfRange(byte[] original, int start, int end)
            throws ArrayIndexOutOfBoundsException, IllegalArgumentException, NullPointerException
    {
        if (start > end) {
            throw new IllegalArgumentException();
        }
        int originalLength = original.length;
        if ((start < 0) || (start > originalLength)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int resultLength = end - start;
        int copyLength = Math.min(resultLength, originalLength - start);
        byte[] result = new byte[resultLength];
        System.arraycopy(original, start, result, 0, copyLength);
        return result;
    }

    public void onProgressData(byte[] responseBody)
    {
        AsyncHttpClient.log.d("DataAsyncHttpRH", "onProgressData(byte[]) was not overriden, but callback was received");
    }

    public final void sendProgressDataMessage(byte[] responseBytes)
    {
        sendMessage(obtainMessage(7, new Object[] { responseBytes }));
    }

    protected void handleMessage(Message message)
    {
        super.handleMessage(message);
        switch (message.what)
        {
            case 7:
                Object[] response = (Object[])message.obj;
                if ((response != null) && (response.length >= 1)) {
                    try
                    {
                        onProgressData((byte[])response[0]);
                    }
                    catch (Throwable t)
                    {
                        AsyncHttpClient.log.e("DataAsyncHttpRH", "custom onProgressData contains an error", t);
                    }
                } else {
                    AsyncHttpClient.log.e("DataAsyncHttpRH", "PROGRESS_DATA_MESSAGE didn't got enough params");
                }
                break;
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
                if (contentLength < 0L) {
                    contentLength = 4096L;
                }
                try
                {
                    ByteArrayBuffer buffer = new ByteArrayBuffer((int)contentLength);
                    try
                    {
                        byte[] tmp = new byte['?'];
                        int count = 0;
                        int l;
                        while (((l = instream.read(tmp)) != -1) && (!Thread.currentThread().isInterrupted()))
                        {
                            buffer.append(tmp, 0, l);
                            sendProgressDataMessage(copyOfRange(tmp, 0, l));
                            sendProgressMessage(count, contentLength);
                        }
                    }
                    finally
                    {
                        AsyncHttpClient.silentCloseInputStream(instream);
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
}
