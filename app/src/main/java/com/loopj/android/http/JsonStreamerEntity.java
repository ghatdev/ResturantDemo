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


import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.message.BasicHeader;

public class JsonStreamerEntity
        implements HttpEntity
{
    private static final String LOG_TAG = "JsonStreamerEntity";
    private static final UnsupportedOperationException ERR_UNSUPPORTED = new UnsupportedOperationException("Unsupported operation in this implementation.");
    private static final int BUFFER_SIZE = 4096;
    private static final byte[] JSON_TRUE = "true".getBytes();
    private static final byte[] JSON_FALSE = "false".getBytes();
    private static final byte[] JSON_NULL = "null".getBytes();
    private static final byte[] STREAM_NAME = escape("name");
    private static final byte[] STREAM_TYPE = escape("type");
    private static final byte[] STREAM_CONTENTS = escape("contents");
    private static final Header HEADER_JSON_CONTENT = new BasicHeader("Content-Type", "application/json");
    private static final Header HEADER_GZIP_ENCODING = new BasicHeader("Content-Encoding", "gzip");
    private final byte[] buffer = new byte['?'];
    private final Map<String, Object> jsonParams = new HashMap();
    private final Header contentEncoding;
    private final byte[] elapsedField;
    private final ResponseHandlerInterface progressHandler;

    public JsonStreamerEntity(ResponseHandlerInterface progressHandler, boolean useGZipCompression, String elapsedField)
    {
        this.progressHandler = progressHandler;
        this.contentEncoding = (useGZipCompression ? HEADER_GZIP_ENCODING : null);
        this.elapsedField = (TextUtils.isEmpty(elapsedField) ? null :

                escape(elapsedField));
    }

    static byte[] escape(String string)
    {
        if (string == null) {
            return JSON_NULL;
        }
        StringBuilder sb = new StringBuilder(128);

        sb.append('"');

        int length = string.length();int pos = -1;
        for (;;)
        {
            pos++;
            if (pos >= length) {
                break;
            }
            char ch = string.charAt(pos);
            switch (ch)
            {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if ((ch <= '\037') || ((ch >= '') && (ch <= '?')) || ((ch >= '?') && (ch <= '?')))
                    {
                        String intString = Integer.toHexString(ch);
                        sb.append("\\u");
                        int intLength = 4 - intString.length();
                        for (int zero = 0; zero < intLength; zero++) {
                            sb.append('0');
                        }
                        sb.append(intString.toUpperCase(Locale.US));
                    }
                    else
                    {
                        sb.append(ch);
                    }
                    break;
            }
        }
        sb.append('"');

        return sb.toString().getBytes();
    }

    public void addPart(String key, Object value)
    {
        this.jsonParams.put(key, value);
    }

    public boolean isRepeatable()
    {
        return false;
    }

    public boolean isChunked()
    {
        return false;
    }

    public boolean isStreaming()
    {
        return false;
    }

    public long getContentLength()
    {
        return -1L;
    }

    public Header getContentEncoding()
    {
        return this.contentEncoding;
    }

    public Header getContentType()
    {
        return HEADER_JSON_CONTENT;
    }

    public void consumeContent()
            throws IOException, UnsupportedOperationException
    {}

    public InputStream getContent()
            throws IOException, UnsupportedOperationException
    {
        throw ERR_UNSUPPORTED;
    }

    public void writeTo(OutputStream out)
            throws IOException
    {
        if (out == null) {
            throw new IllegalStateException("Output stream cannot be null.");
        }
        long now = System.currentTimeMillis();

        OutputStream os = this.contentEncoding != null ? new GZIPOutputStream(out, 4096) : out;

        os.write(123);

        Set<String> keys = this.jsonParams.keySet();

        int keysCount = keys.size();
        if (0 < keysCount)
        {
            int keysProcessed = 0;
            for (String key : keys)
            {
                keysProcessed++;
                try
                {
                    Object value = this.jsonParams.get(key);

                    os.write(escape(key));
                    os.write(58);
                    if (value == null)
                    {
                        os.write(JSON_NULL);
                    }
                    else
                    {
                        boolean isFileWrapper = value instanceof RequestParams.FileWrapper;
                        if ((isFileWrapper) || ((value instanceof RequestParams.StreamWrapper)))
                        {
                            os.write(123);
                            if (isFileWrapper) {
                                writeToFromFile(os, (RequestParams.FileWrapper)value);
                            } else {
                                writeToFromStream(os, (RequestParams.StreamWrapper)value);
                            }
                            os.write(125);
                        }
                        else if ((value instanceof JsonValueInterface))
                        {
                            os.write(((JsonValueInterface)value).getEscapedJsonValue());
                        }
                        else if ((value instanceof JSONObject))
                        {
                            os.write(value.toString().getBytes());
                        }
                        else if ((value instanceof JSONArray))
                        {
                            os.write(value.toString().getBytes());
                        }
                        else if ((value instanceof Boolean))
                        {
                            os.write(((Boolean)value).booleanValue() ? JSON_TRUE : JSON_FALSE);
                        }
                        else if ((value instanceof Long))
                        {
                            os.write((((Number)value).longValue() + "").getBytes());
                        }
                        else if ((value instanceof Double))
                        {
                            os.write((((Number)value).doubleValue() + "").getBytes());
                        }
                        else if ((value instanceof Float))
                        {
                            os.write((((Number)value).floatValue() + "").getBytes());
                        }
                        else if ((value instanceof Integer))
                        {
                            os.write((((Number)value).intValue() + "").getBytes());
                        }
                        else
                        {
                            os.write(escape(value.toString()));
                        }
                    }
                }
                finally
                {
                    if ((this.elapsedField != null) || (keysProcessed < keysCount)) {
                        os.write(44);
                    }
                }
            }
            long elapsedTime = System.currentTimeMillis() - now;
            if (this.elapsedField != null)
            {
                os.write(this.elapsedField);
                os.write(58);
                os.write((elapsedTime + "").getBytes());
            }
            AsyncHttpClient.log.i("JsonStreamerEntity", "Uploaded JSON in " + Math.floor(elapsedTime / 1000L) + " seconds");
        }
        os.write(125);

        os.flush();
        AsyncHttpClient.silentCloseOutputStream(os);
    }

    private void writeToFromStream(OutputStream os, RequestParams.StreamWrapper entry)
            throws IOException
    {
        writeMetaData(os, entry.name, entry.contentType);

        Base64OutputStream bos = new Base64OutputStream(os, 18);
        int bytesRead;
        while ((bytesRead = entry.inputStream.read(this.buffer)) != -1) {
            bos.write(this.buffer, 0, bytesRead);
        }
        AsyncHttpClient.silentCloseOutputStream(bos);

        endMetaData(os);
        if (entry.autoClose) {
            AsyncHttpClient.silentCloseInputStream(entry.inputStream);
        }
    }

    private void writeToFromFile(OutputStream os, RequestParams.FileWrapper wrapper)
            throws IOException
    {
        writeMetaData(os, wrapper.file.getName(), wrapper.contentType);

        long bytesWritten = 0L;long totalSize = wrapper.file.length();

        FileInputStream in = new FileInputStream(wrapper.file);

        Base64OutputStream bos = new Base64OutputStream(os, 18);
        int bytesRead;
        while ((bytesRead = in.read(this.buffer)) != -1)
        {
            bos.write(this.buffer, 0, bytesRead);
            bytesWritten += bytesRead;
            this.progressHandler.sendProgressMessage(bytesWritten, totalSize);
        }
        AsyncHttpClient.silentCloseOutputStream(bos);

        endMetaData(os);

        AsyncHttpClient.silentCloseInputStream(in);
    }

    private void writeMetaData(OutputStream os, String name, String contentType)
            throws IOException
    {
        os.write(STREAM_NAME);
        os.write(58);
        os.write(escape(name));
        os.write(44);

        os.write(STREAM_TYPE);
        os.write(58);
        os.write(escape(contentType));
        os.write(44);

        os.write(STREAM_CONTENTS);
        os.write(58);
        os.write(34);
    }

    private void endMetaData(OutputStream os)
            throws IOException
    {
        os.write(34);
    }
}
