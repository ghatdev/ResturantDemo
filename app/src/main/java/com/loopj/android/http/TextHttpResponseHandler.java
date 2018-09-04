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


import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

public abstract class TextHttpResponseHandler
        extends AsyncHttpResponseHandler
{
    private static final String LOG_TAG = "TextHttpRH";

    public TextHttpResponseHandler()
    {
        this("UTF-8");
    }

    public TextHttpResponseHandler(String encoding)
    {
        setCharset(encoding);
    }

    public static String getResponseString(byte[] stringBytes, String charset)
    {
        try
        {
            String toReturn = stringBytes == null ? null : new String(stringBytes, charset);
            if ((toReturn != null) && (toReturn.startsWith("?"))) {
                return toReturn.substring(1);
            }
            return toReturn;
        }
        catch (UnsupportedEncodingException e)
        {
            AsyncHttpClient.log.e("TextHttpRH", "Encoding response into string failed", e);
        }
        return null;
    }

    public abstract void onFailure(int paramInt, Header[] paramArrayOfHeader, String paramString, Throwable paramThrowable);

    public abstract void onSuccess(int paramInt, Header[] paramArrayOfHeader, String paramString);

    public void onSuccess(int statusCode, Header[] headers, byte[] responseBytes)
    {
        onSuccess(statusCode, headers, getResponseString(responseBytes, getCharset()));
    }

    public void onFailure(int statusCode, Header[] headers, byte[] responseBytes, Throwable throwable)
    {
        onFailure(statusCode, headers, getResponseString(responseBytes, getCharset()), throwable);
    }
}
