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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import cz.msebera.android.httpclient.Header;

public class JsonHttpResponseHandler
        extends TextHttpResponseHandler
{
    private static final String LOG_TAG = "JsonHttpRH";
    private boolean useRFC5179CompatibilityMode = true;

    public JsonHttpResponseHandler()
    {
        super("UTF-8");
    }

    public JsonHttpResponseHandler(String encoding)
    {
        super(encoding);
    }

    public JsonHttpResponseHandler(boolean useRFC5179CompatibilityMode)
    {
        super("UTF-8");
        this.useRFC5179CompatibilityMode = useRFC5179CompatibilityMode;
    }

    public JsonHttpResponseHandler(String encoding, boolean useRFC5179CompatibilityMode)
    {
        super(encoding);
        this.useRFC5179CompatibilityMode = useRFC5179CompatibilityMode;
    }

    public void onSuccess(int statusCode, Header[] headers, JSONObject response)
    {
        AsyncHttpClient.log.w("JsonHttpRH", "onSuccess(int, Header[], JSONObject) was not overriden, but callback was received");
    }

    public void onSuccess(int statusCode, Header[] headers, JSONArray response)
    {
        AsyncHttpClient.log.w("JsonHttpRH", "onSuccess(int, Header[], JSONArray) was not overriden, but callback was received");
    }

    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)
    {
        AsyncHttpClient.log.w("JsonHttpRH", "onFailure(int, Header[], Throwable, JSONObject) was not overriden, but callback was received", throwable);
    }

    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse)
    {
        AsyncHttpClient.log.w("JsonHttpRH", "onFailure(int, Header[], Throwable, JSONArray) was not overriden, but callback was received", throwable);
    }

    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
    {
        AsyncHttpClient.log.w("JsonHttpRH", "onFailure(int, Header[], String, Throwable) was not overriden, but callback was received", throwable);
    }

    public void onSuccess(int statusCode, Header[] headers, String responseString)
    {
        AsyncHttpClient.log.w("JsonHttpRH", "onSuccess(int, Header[], String) was not overriden, but callback was received");
    }

    public final void onSuccess(final int statusCode, final Header[] headers, final byte[] responseBytes)
    {
        if (statusCode != 204)
        {
//            Runnable parser = new Runnable()
//            {
//                public void run()
//                {
//                    try
//                    {
//                        final Object jsonResponse = JsonHttpResponseHandler.this.parseResponse(responseBytes);
//                        JsonHttpResponseHandler.this.postRunnable(new Runnable()
//                        {
//                            public void run()
//                            {
//                                if ((!JsonHttpResponseHandler.this.useRFC5179CompatibilityMode) && (jsonResponse == null)) {
//                                    JsonHttpResponseHandler.this.onSuccess(JsonHttpResponseHandler.1.this.val$statusCode, JsonHttpResponseHandler.1.this.val$headers, (String)null);
//                                } else if ((jsonResponse instanceof JSONObject)) {
//                                    JsonHttpResponseHandler.this.onSuccess(JsonHttpResponseHandler.1.this.val$statusCode, JsonHttpResponseHandler.1.this.val$headers, (JSONObject)jsonResponse);
//                                } else if ((jsonResponse instanceof JSONArray)) {
//                                    JsonHttpResponseHandler.this.onSuccess(JsonHttpResponseHandler.1.this.val$statusCode, JsonHttpResponseHandler.1.this.val$headers, (JSONArray)jsonResponse);
//                                } else if ((jsonResponse instanceof String))
//                                {
//                                    if (JsonHttpResponseHandler.this.useRFC5179CompatibilityMode) {
//                                        JsonHttpResponseHandler.this.onFailure(JsonHttpResponseHandler.1.this.val$statusCode, JsonHttpResponseHandler.1.this.val$headers, (String)jsonResponse, new JSONException("Response cannot be parsed as JSON data"));
//                                    } else {
//                                        JsonHttpResponseHandler.this.onSuccess(JsonHttpResponseHandler.1.this.val$statusCode, JsonHttpResponseHandler.1.this.val$headers, (String)jsonResponse);
//                                    }
//                                }
//                                else {
//                                    JsonHttpResponseHandler.this.onFailure(JsonHttpResponseHandler.1.this.val$statusCode, JsonHttpResponseHandler.1.this.val$headers, new JSONException("Unexpected response type " + jsonResponse.getClass().getName()), (JSONObject)null);
//                                }
//                            }
//                        });
//                    }
//                    catch (JSONException ex)
//                    {
//                        JsonHttpResponseHandler.this.postRunnable(new Runnable()
//                        {
//                            public void run()
//                            {
//                                JsonHttpResponseHandler.this.onFailure(JsonHttpResponseHandler.1.this.val$statusCode, JsonHttpResponseHandler.1.this.val$headers, ex, (JSONObject)null);
//                            }
//                        });
//                    }
//                }
//            };
//            if ((!getUseSynchronousMode()) && (!getUsePoolThread())) {
//                new Thread(parser).start();
//            } else {
//                parser.run();
//            }
        }
        else
        {
            onSuccess(statusCode, headers, new JSONObject());
        }
    }

    public final void onFailure(final int statusCode, final Header[] headers, final byte[] responseBytes, final Throwable throwable)
    {
//        if (responseBytes != null)
//        {
//            Runnable parser = new Runnable()
//            {
//                public void run()
//                {
//                    try
//                    {
//                        final Object jsonResponse = JsonHttpResponseHandler.this.parseResponse(responseBytes);
//                        JsonHttpResponseHandler.this.postRunnable(new Runnable()
//                        {
//                            public void run()
//                            {
//                                if ((!JsonHttpResponseHandler.this.useRFC5179CompatibilityMode) && (jsonResponse == null)) {
//                                    JsonHttpResponseHandler.this.onFailure(JsonHttpResponseHandler.2.this.val$statusCode, JsonHttpResponseHandler.2.this.val$headers, (String)null, JsonHttpResponseHandler.2.this.val$throwable);
//                                } else if ((jsonResponse instanceof JSONObject)) {
//                                    JsonHttpResponseHandler.this.onFailure(JsonHttpResponseHandler.2.this.val$statusCode, JsonHttpResponseHandler.2.this.val$headers, JsonHttpResponseHandler.2.this.val$throwable, (JSONObject)jsonResponse);
//                                } else if ((jsonResponse instanceof JSONArray)) {
//                                    JsonHttpResponseHandler.this.onFailure(JsonHttpResponseHandler.2.this.val$statusCode, JsonHttpResponseHandler.2.this.val$headers, JsonHttpResponseHandler.2.this.val$throwable, (JSONArray)jsonResponse);
//                                } else if ((jsonResponse instanceof String)) {
//                                    JsonHttpResponseHandler.this.onFailure(JsonHttpResponseHandler.2.this.val$statusCode, JsonHttpResponseHandler.2.this.val$headers, (String)jsonResponse, JsonHttpResponseHandler.2.this.val$throwable);
//                                } else {
//                                    JsonHttpResponseHandler.this.onFailure(JsonHttpResponseHandler.2.this.val$statusCode, JsonHttpResponseHandler.2.this.val$headers, new JSONException("Unexpected response type " + jsonResponse.getClass().getName()), (JSONObject)null);
//                                }
//                            }
//                        });
//                    }
//                    catch (JSONException ex)
//                    {
//                        JsonHttpResponseHandler.this.postRunnable(new Runnable()
//                        {
//                            public void run()
//                            {
//                                JsonHttpResponseHandler.this.onFailure(JsonHttpResponseHandler.2.this.val$statusCode, JsonHttpResponseHandler.2.this.val$headers, ex, (JSONObject)null);
//                            }
//                        });
//                    }
//                }
//            };
//            if ((!getUseSynchronousMode()) && (!getUsePoolThread())) {
//                new Thread(parser).start();
//            } else {
//                parser.run();
//            }
//        }
//        else
//        {
//            AsyncHttpClient.log.v("JsonHttpRH", "response body is null, calling onFailure(Throwable, JSONObject)");
//            onFailure(statusCode, headers, throwable, (JSONObject)null);
//        }
    }

    protected Object parseResponse(byte[] responseBody)
            throws JSONException
    {
        if (null == responseBody) {
            return null;
        }
        Object result = null;

        String jsonString = getResponseString(responseBody, getCharset());
        if (jsonString != null)
        {
            jsonString = jsonString.trim();
            if (this.useRFC5179CompatibilityMode)
            {
                if ((jsonString.startsWith("{")) || (jsonString.startsWith("["))) {
                    result = new JSONTokener(jsonString).nextValue();
                }
            }
            else if (((jsonString.startsWith("{")) && (jsonString.endsWith("}"))) || (
                    (jsonString.startsWith("[")) && (jsonString.endsWith("]")))) {
                result = new JSONTokener(jsonString).nextValue();
            } else if ((jsonString.startsWith("\"")) && (jsonString.endsWith("\""))) {
                result = jsonString.substring(1, jsonString.length() - 1);
            }
        }
        if (result == null) {
            result = jsonString;
        }
        return result;
    }

    public boolean isUseRFC5179CompatibilityMode()
    {
        return this.useRFC5179CompatibilityMode;
    }

    public void setUseRFC5179CompatibilityMode(boolean useRFC5179CompatibilityMode)
    {
        this.useRFC5179CompatibilityMode = useRFC5179CompatibilityMode;
    }
}
