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
import java.lang.ref.WeakReference;

public class RequestHandle
{
    private final WeakReference<AsyncHttpRequest> request;

    public RequestHandle(AsyncHttpRequest request)
    {
        this.request = new WeakReference(request);
    }

    public boolean cancel(final boolean mayInterruptIfRunning)
    {
        final AsyncHttpRequest _request = (AsyncHttpRequest)this.request.get();
        if (_request != null)
        {
            if (Looper.myLooper() == Looper.getMainLooper())
            {
                new Thread(new Runnable()
                {
                    public void run()
                    {
                        _request.cancel(mayInterruptIfRunning);
                    }
                }).start();
                return true;
            }
            return _request.cancel(mayInterruptIfRunning);
        }
        return false;
    }

    public boolean isFinished()
    {
        AsyncHttpRequest _request = (AsyncHttpRequest)this.request.get();
        return (_request == null) || (_request.isDone());
    }

    public boolean isCancelled()
    {
        AsyncHttpRequest _request = (AsyncHttpRequest)this.request.get();
        return (_request == null) || (_request.isCancelled());
    }

    public boolean shouldBeGarbageCollected()
    {
        boolean should = (isCancelled()) || (isFinished());
        if (should) {
            this.request.clear();
        }
        return should;
    }

    public Object getTag()
    {
        AsyncHttpRequest _request = (AsyncHttpRequest)this.request.get();
        return _request == null ? null : _request.getTag();
    }

    public RequestHandle setTag(Object tag)
    {
        AsyncHttpRequest _request = (AsyncHttpRequest)this.request.get();
        if (_request != null) {
            _request.setRequestTag(tag);
        }
        return this;
    }
}
