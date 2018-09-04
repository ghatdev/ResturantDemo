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
import cz.msebera.android.httpclient.HttpResponse;

public class BlackholeHttpResponseHandler
        extends AsyncHttpResponseHandler
{
  public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {}

  public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}

  public void onProgress(long bytesWritten, long totalSize) {}

  public void onCancel() {}

  public void onFinish() {}

  public void onPostProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {}

  public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {}

  public void onRetry(int retryNo) {}

  public void onStart() {}

  public void onUserException(Throwable error) {}
}
