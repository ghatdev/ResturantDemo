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


import cz.msebera.android.httpclient.Header;

public abstract class BaseJsonHttpResponseHandler<JSON_TYPE>
        extends TextHttpResponseHandler
{
    private static final String LOG_TAG = "BaseJsonHttpRH";

    public BaseJsonHttpResponseHandler()
    {
        this("UTF-8");
    }

    public BaseJsonHttpResponseHandler(String encoding)
    {
        super(encoding);
    }

    public abstract void onSuccess(int paramInt, Header[] paramArrayOfHeader, String paramString, JSON_TYPE paramJSON_TYPE);

    public abstract void onFailure(int paramInt, Header[] paramArrayOfHeader, Throwable paramThrowable, String paramString, JSON_TYPE paramJSON_TYPE);

    public final void onSuccess(final int statusCode, final Header[] headers, final String responseString)
    {
        if (statusCode != 204)
        {
            Runnable parser = new Runnable()
            {
                public void run()
                {
//                    try
//                    {
//                        final JSON_TYPE jsonResponse = BaseJsonHttpResponseHandler.this.parseResponse(responseString, false);
//                        BaseJsonHttpResponseHandler.this.postRunnable(new Runnable()
//                        {
//                            public void run()
//                            {
//                                BaseJsonHttpResponseHandler.this.onSuccess(BaseJsonHttpResponseHandler.1.this.val$statusCode, BaseJsonHttpResponseHandler.1.this.val$headers, BaseJsonHttpResponseHandler.1.this.val$responseString, jsonResponse);
//                            }
//                        });
//                    }
//                    catch (Throwable t)
//                    {
//                        AsyncHttpClient.log.d("BaseJsonHttpRH", "parseResponse thrown an problem", t);
//                        BaseJsonHttpResponseHandler.this.postRunnable(new Runnable()
//                        {
//                            public void run()
//                            {
//                                BaseJsonHttpResponseHandler.this.onFailure(BaseJsonHttpResponseHandler.1.this.val$statusCode, BaseJsonHttpResponseHandler.1.this.val$headers, t, BaseJsonHttpResponseHandler.1.this.val$responseString, null);
//                            }
//                        });
//                    }
                }
            };
            if ((!getUseSynchronousMode()) && (!getUsePoolThread())) {
                new Thread(parser).start();
            } else {
                parser.run();
            }
        }
        else
        {
            onSuccess(statusCode, headers, null, null);
        }
    }

    public final void onFailure(final int statusCode, final Header[] headers, final String responseString, final Throwable throwable)
    {
        if (responseString != null)
        {
            Runnable parser = new Runnable()
            {
                public void run()
                {
//                    try
//                    {
//                        final JSON_TYPE jsonResponse = BaseJsonHttpResponseHandler.this.parseResponse(responseString, true);
//                        BaseJsonHttpResponseHandler.this.postRunnable(new Runnable()
//                        {
//                            public void run()
//                            {
//                                BaseJsonHttpResponseHandler.this.onFailure(BaseJsonHttpResponseHandler.2.this.val$statusCode, BaseJsonHttpResponseHandler.2.this.val$headers, BaseJsonHttpResponseHandler.2.this.val$throwable, BaseJsonHttpResponseHandler.2.this.val$responseString, jsonResponse);
//                            }
//                        });
//                    }
//                    catch (Throwable t)
//                    {
//                        AsyncHttpClient.log.d("BaseJsonHttpRH", "parseResponse thrown an problem", t);
//                        BaseJsonHttpResponseHandler.this.postRunnable(new Runnable()
//                        {
//                            public void run()
//                            {
//                                BaseJsonHttpResponseHandler.this.onFailure(BaseJsonHttpResponseHandler.2.this.val$statusCode, BaseJsonHttpResponseHandler.2.this.val$headers, BaseJsonHttpResponseHandler.2.this.val$throwable, BaseJsonHttpResponseHandler.2.this.val$responseString, null);
//                            }
//                        });
//                    }
                }
            };
            if ((!getUseSynchronousMode()) && (!getUsePoolThread())) {
                new Thread(parser).start();
            } else {
                parser.run();
            }
        }
        else
        {
            onFailure(statusCode, headers, throwable, null, null);
        }
    }

    protected abstract JSON_TYPE parseResponse(String paramString, boolean paramBoolean)
            throws Throwable;
}
