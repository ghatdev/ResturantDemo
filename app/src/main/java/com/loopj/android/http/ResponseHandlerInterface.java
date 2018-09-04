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
import java.net.URI;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;

public abstract interface ResponseHandlerInterface
{
  public abstract void sendResponseMessage(HttpResponse paramHttpResponse)
          throws IOException;

  public abstract void sendStartMessage();

  public abstract void sendFinishMessage();

  public abstract void sendProgressMessage(long paramLong1, long paramLong2);

  public abstract void sendCancelMessage();

  public abstract void sendSuccessMessage(int paramInt, Header[] paramArrayOfHeader, byte[] paramArrayOfByte);

  public abstract void sendFailureMessage(int paramInt, Header[] paramArrayOfHeader, byte[] paramArrayOfByte, Throwable paramThrowable);

  public abstract void sendRetryMessage(int paramInt);

  public abstract URI getRequestURI();

  public abstract void setRequestURI(URI paramURI);

  public abstract Header[] getRequestHeaders();

  public abstract void setRequestHeaders(Header[] paramArrayOfHeader);

  public abstract boolean getUseSynchronousMode();

  public abstract void setUseSynchronousMode(boolean paramBoolean);

  public abstract boolean getUsePoolThread();

  public abstract void setUsePoolThread(boolean paramBoolean);

  public abstract void onPreProcessResponse(ResponseHandlerInterface paramResponseHandlerInterface, HttpResponse paramHttpResponse);

  public abstract void onPostProcessResponse(ResponseHandlerInterface paramResponseHandlerInterface, HttpResponse paramHttpResponse);

  public abstract Object getTag();

  public abstract void setTag(Object paramObject);
}
