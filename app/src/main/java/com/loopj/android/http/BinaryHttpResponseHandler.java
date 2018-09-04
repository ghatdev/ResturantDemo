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


import android.os.Looper;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpResponseException;

public abstract class BinaryHttpResponseHandler
        extends AsyncHttpResponseHandler
{
    private static final String LOG_TAG = "BinaryHttpRH";
    private String[] mAllowedContentTypes = { "application/octet-stream", "image/jpeg", "image/png", "image/gif" };

    public BinaryHttpResponseHandler() {}

    public BinaryHttpResponseHandler(String[] allowedContentTypes)
    {
        if (allowedContentTypes != null) {
            this.mAllowedContentTypes = allowedContentTypes;
        } else {
            AsyncHttpClient.log.e("BinaryHttpRH", "Constructor passed allowedContentTypes was null !");
        }
    }

    public BinaryHttpResponseHandler(String[] allowedContentTypes, Looper looper)
    {
        super(looper);
        if (allowedContentTypes != null) {
            this.mAllowedContentTypes = allowedContentTypes;
        } else {
            AsyncHttpClient.log.e("BinaryHttpRH", "Constructor passed allowedContentTypes was null !");
        }
    }

    public String[] getAllowedContentTypes()
    {
        return this.mAllowedContentTypes;
    }

    public abstract void onSuccess(int paramInt, Header[] paramArrayOfHeader, byte[] paramArrayOfByte);

    public abstract void onFailure(int paramInt, Header[] paramArrayOfHeader, byte[] paramArrayOfByte, Throwable paramThrowable);

    public final void sendResponseMessage(HttpResponse response)
            throws IOException
    {
        StatusLine status = response.getStatusLine();
        Header[] contentTypeHeaders = response.getHeaders("Content-Type");
        if (contentTypeHeaders.length != 1)
        {
            sendFailureMessage(status
                    .getStatusCode(), response
                    .getAllHeaders(), null, new HttpResponseException(status

                    .getStatusCode(), "None, or more than one, Content-Type Header found!"));

            return;
        }
        Header contentTypeHeader = contentTypeHeaders[0];
        boolean foundAllowedContentType = false;
        for (String anAllowedContentType : getAllowedContentTypes()) {
            try
            {
                if (Pattern.matches(anAllowedContentType, contentTypeHeader.getValue())) {
                    foundAllowedContentType = true;
                }
            }
            catch (PatternSyntaxException e)
            {
                AsyncHttpClient.log.e("BinaryHttpRH", "Given pattern is not valid: " + anAllowedContentType, e);
            }
        }
        if (!foundAllowedContentType)
        {
            sendFailureMessage(status
                    .getStatusCode(), response
                    .getAllHeaders(), null, new HttpResponseException(status

                    .getStatusCode(), "Content-Type (" + contentTypeHeader
                    .getValue() + ") not allowed!"));

            return;
        }
        super.sendResponseMessage(response);
    }
}
