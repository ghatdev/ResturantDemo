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


import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;

public abstract class FileAsyncHttpResponseHandler
        extends AsyncHttpResponseHandler
{
    private static final String LOG_TAG = "FileAsyncHttpRH";
    protected final File file;
    protected final boolean append;
    protected final boolean renameIfExists;
    protected File frontendFile;

    public FileAsyncHttpResponseHandler(File file)
    {
        this(file, false);
    }

    public FileAsyncHttpResponseHandler(File file, boolean append)
    {
        this(file, append, false);
    }

    public FileAsyncHttpResponseHandler(File file, boolean append, boolean renameTargetFileIfExists)
    {
        this(file, append, renameTargetFileIfExists, false);
    }

    public FileAsyncHttpResponseHandler(File file, boolean append, boolean renameTargetFileIfExists, boolean usePoolThread)
    {
        super(usePoolThread);
        Utils.asserts(file != null, "File passed into FileAsyncHttpResponseHandler constructor must not be null");
        if ((!file.isDirectory()) && (!file.getParentFile().isDirectory())) {
            Utils.asserts(file.getParentFile().mkdirs(), "Cannot create parent directories for requested File location");
        }
        if ((file.isDirectory()) &&
                (!file.mkdirs())) {
            AsyncHttpClient.log.d("FileAsyncHttpRH", "Cannot create directories for requested Directory location, might not be a problem");
        }
        this.file = file;
        this.append = append;
        this.renameIfExists = renameTargetFileIfExists;
    }

    public FileAsyncHttpResponseHandler(Context context)
    {
        this.file = getTemporaryFile(context);
        this.append = false;
        this.renameIfExists = false;
    }

    public boolean deleteTargetFile()
    {
        return (getTargetFile() != null) && (getTargetFile().delete());
    }

    protected File getTemporaryFile(Context context)
    {
        Utils.asserts(context != null, "Tried creating temporary file without having Context");
        try
        {
            return File.createTempFile("temp_", "_handled", context.getCacheDir());
        }
        catch (IOException e)
        {
            AsyncHttpClient.log.e("FileAsyncHttpRH", "Cannot create temporary file", e);
        }
        return null;
    }

    protected File getOriginalFile()
    {
        Utils.asserts(this.file != null, "Target file is null, fatal!");
        return this.file;
    }

    public File getTargetFile()
    {
        if (this.frontendFile == null) {
            this.frontendFile = (getOriginalFile().isDirectory() ? getTargetFileByParsingURL() : getOriginalFile());
        }
        return this.frontendFile;
    }

    protected File getTargetFileByParsingURL()
    {
        Utils.asserts(getOriginalFile().isDirectory(), "Target file is not a directory, cannot proceed");
        Utils.asserts(getRequestURI() != null, "RequestURI is null, cannot proceed");
        String requestURL = getRequestURI().toString();
        String filename = requestURL.substring(requestURL.lastIndexOf('/') + 1, requestURL.length());
        File targetFileRtn = new File(getOriginalFile(), filename);
        if ((targetFileRtn.exists()) && (this.renameIfExists))
        {
            String format;
            if (!filename.contains(".")) {
                format = filename + " (%d)";
            } else {
                format = filename.substring(0, filename.lastIndexOf('.')) + " (%d)" + filename.substring(filename.lastIndexOf('.'), filename.length());
            }
            int index = 0;
            for (;;)
            {
                targetFileRtn = new File(getOriginalFile(), String.format(format, new Object[] { Integer.valueOf(index) }));
                if (!targetFileRtn.exists()) {
                    return targetFileRtn;
                }
                index++;
            }
        }
        return targetFileRtn;
    }

    public final void onFailure(int statusCode, Header[] headers, byte[] responseBytes, Throwable throwable)
    {
        onFailure(statusCode, headers, throwable, getTargetFile());
    }

    public abstract void onFailure(int paramInt, Header[] paramArrayOfHeader, Throwable paramThrowable, File paramFile);

    public final void onSuccess(int statusCode, Header[] headers, byte[] responseBytes)
    {
        onSuccess(statusCode, headers, getTargetFile());
    }

    public abstract void onSuccess(int paramInt, Header[] paramArrayOfHeader, File paramFile);

    protected byte[] getResponseData(HttpEntity entity)
            throws IOException
    {
        if (entity != null)
        {
            InputStream instream = entity.getContent();
            long contentLength = entity.getContentLength();
            FileOutputStream buffer = new FileOutputStream(getTargetFile(), this.append);
            if (instream != null) {
                try
                {
                    byte[] tmp = new byte['?'];
                    int count = 0;
                    int l;
                    while (((l = instream.read(tmp)) != -1) && (!Thread.currentThread().isInterrupted()))
                    {
                        count += l;
                        buffer.write(tmp, 0, l);
                        sendProgressMessage(count, contentLength);
                    }
                }
                finally
                {
                    AsyncHttpClient.silentCloseInputStream(instream);
                    buffer.flush();
                    AsyncHttpClient.silentCloseOutputStream(buffer);
                }
            }
        }
        return null;
    }
}
