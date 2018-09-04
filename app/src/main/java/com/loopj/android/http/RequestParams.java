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


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.utils.URLEncodedUtils;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class RequestParams
        implements Serializable
{
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String APPLICATION_JSON = "application/json";
    protected static final String LOG_TAG = "RequestParams";
    protected final ConcurrentHashMap<String, String> urlParams = new ConcurrentHashMap();
    protected final ConcurrentHashMap<String, StreamWrapper> streamParams = new ConcurrentHashMap();
    protected final ConcurrentHashMap<String, FileWrapper> fileParams = new ConcurrentHashMap();
    protected final ConcurrentHashMap<String, List<FileWrapper>> fileArrayParams = new ConcurrentHashMap();
    protected final ConcurrentHashMap<String, Object> urlParamsWithObjects = new ConcurrentHashMap();
    protected boolean isRepeatable;
    protected boolean forceMultipartEntity = false;
    protected boolean useJsonStreamer;
    protected String elapsedFieldInJsonStreamer = "_elapsed";
    protected boolean autoCloseInputStreams;
    protected String contentEncoding = "UTF-8";

    public RequestParams()
    {
        this((Map)null);
    }

    public RequestParams(Map<String, String> source)
    {
        if (source != null) {
            for (Map.Entry<String, String> entry : source.entrySet()) {
                put((String)entry.getKey(), (String)entry.getValue());
            }
        }
    }

    public RequestParams(String key, final String value)
    {
        this(new HashMap() {});
    }

    public RequestParams(Object... keysAndValues)
    {
        int len = keysAndValues.length;
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Supplied arguments must be even");
        }
        for (int i = 0; i < len; i += 2)
        {
            String key = String.valueOf(keysAndValues[i]);
            String val = String.valueOf(keysAndValues[(i + 1)]);
            put(key, val);
        }
    }

    public void setContentEncoding(String encoding)
    {
        if (encoding != null) {
            this.contentEncoding = encoding;
        } else {
            AsyncHttpClient.log.d("RequestParams", "setContentEncoding called with null attribute");
        }
    }

    public void setForceMultipartEntityContentType(boolean force)
    {
        this.forceMultipartEntity = force;
    }

    public void put(String key, String value)
    {
        if ((key != null) && (value != null)) {
            this.urlParams.put(key, value);
        }
    }

    public void put(String key, File[] files)
            throws FileNotFoundException
    {
        put(key, files, null, null);
    }

    public void put(String key, File[] files, String contentType, String customFileName)
            throws FileNotFoundException
    {
        if (key != null)
        {
            List<FileWrapper> fileWrappers = new ArrayList();
            for (File file : files)
            {
                if ((file == null) || (!file.exists())) {
                    throw new FileNotFoundException();
                }
                fileWrappers.add(new FileWrapper(file, contentType, customFileName));
            }
            this.fileArrayParams.put(key, fileWrappers);
        }
    }

    public void put(String key, File file)
            throws FileNotFoundException
    {
        put(key, file, null, null);
    }

    public void put(String key, String customFileName, File file)
            throws FileNotFoundException
    {
        put(key, file, null, customFileName);
    }

    public void put(String key, File file, String contentType)
            throws FileNotFoundException
    {
        put(key, file, contentType, null);
    }

    public void put(String key, File file, String contentType, String customFileName)
            throws FileNotFoundException
    {
        if ((file == null) || (!file.exists())) {
            throw new FileNotFoundException();
        }
        if (key != null) {
            this.fileParams.put(key, new FileWrapper(file, contentType, customFileName));
        }
    }

    public void put(String key, InputStream stream)
    {
        put(key, stream, null);
    }

    public void put(String key, InputStream stream, String name)
    {
        put(key, stream, name, null);
    }

    public void put(String key, InputStream stream, String name, String contentType)
    {
        put(key, stream, name, contentType, this.autoCloseInputStreams);
    }

    public void put(String key, InputStream stream, String name, String contentType, boolean autoClose)
    {
        if ((key != null) && (stream != null)) {
            this.streamParams.put(key, StreamWrapper.newInstance(stream, name, contentType, autoClose));
        }
    }

    public void put(String key, Object value)
    {
        if ((key != null) && (value != null)) {
            this.urlParamsWithObjects.put(key, value);
        }
    }

    public void put(String key, int value)
    {
        if (key != null) {
            this.urlParams.put(key, String.valueOf(value));
        }
    }

    public void put(String key, long value)
    {
        if (key != null) {
            this.urlParams.put(key, String.valueOf(value));
        }
    }

    public void add(String key, String value)
    {
        if ((key != null) && (value != null))
        {
            Object params = this.urlParamsWithObjects.get(key);
            if (params == null)
            {
                params = new HashSet();
                put(key, params);
            }
            if ((params instanceof List)) {
                ((List)params).add(value);
            } else if ((params instanceof Set)) {
                ((Set)params).add(value);
            }
        }
    }

    public void remove(String key)
    {
        this.urlParams.remove(key);
        this.streamParams.remove(key);
        this.fileParams.remove(key);
        this.urlParamsWithObjects.remove(key);
        this.fileArrayParams.remove(key);
    }

    public boolean has(String key)
    {
        return (this.urlParams.get(key) != null) || (this.streamParams.get(key) != null) || (this.fileParams.get(key) != null) || (this.urlParamsWithObjects.get(key) != null) || (this.fileArrayParams.get(key) != null);
    }

    public String toString()
    {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : this.urlParams.entrySet())
        {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append((String)entry.getKey());
            result.append("=");
            result.append((String)entry.getValue());
        }
        for (Map.Entry<String, StreamWrapper> entry : this.streamParams.entrySet())
        {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append((String)entry.getKey());
            result.append("=");
            result.append("STREAM");
        }
        for (Map.Entry<String, FileWrapper> entry : this.fileParams.entrySet())
        {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append((String)entry.getKey());
            result.append("=");
            result.append("FILE");
        }

        for (Iterator it = this.fileArrayParams.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry)it.next();
            if (result.length() > 0) {
                result.append("&");
            }
            result.append((String)entry.getKey());
            result.append("=");
            result.append("FILES(SIZE=").append(((List)entry.getValue()).size()).append(")");
        }
        Map.Entry<String, List<FileWrapper>> entry;
        Object params = getParamsList(null, this.urlParamsWithObjects);
        for (BasicNameValuePair kv : (List<BasicNameValuePair>)params)
        {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(kv.getName());
            result.append("=");
            result.append(kv.getValue());
        }
        return result.toString();
    }

    public void setHttpEntityIsRepeatable(boolean flag)
    {
        this.isRepeatable = flag;
    }

    public void setUseJsonStreamer(boolean flag)
    {
        this.useJsonStreamer = flag;
    }

    public void setElapsedFieldInJsonStreamer(String value)
    {
        this.elapsedFieldInJsonStreamer = value;
    }

    public void setAutoCloseInputStreams(boolean flag)
    {
        this.autoCloseInputStreams = flag;
    }

    public HttpEntity getEntity(ResponseHandlerInterface progressHandler)
            throws IOException
    {
        if (this.useJsonStreamer) {
            return createJsonStreamerEntity(progressHandler);
        }
        if ((!this.forceMultipartEntity) && (this.streamParams.isEmpty()) && (this.fileParams.isEmpty()) && (this.fileArrayParams.isEmpty())) {
            return createFormEntity();
        }
        return createMultipartEntity(progressHandler);
    }

    private HttpEntity createJsonStreamerEntity(ResponseHandlerInterface progressHandler)
            throws IOException
    {
        JsonStreamerEntity entity = new JsonStreamerEntity(progressHandler, (!this.fileParams.isEmpty()) || (!this.streamParams.isEmpty()), this.elapsedFieldInJsonStreamer);
        for (Map.Entry<String, String> entry : this.urlParams.entrySet()) {
            entity.addPart((String)entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Object> entry : this.urlParamsWithObjects.entrySet()) {
            entity.addPart((String)entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, FileWrapper> entry : this.fileParams.entrySet()) {
            entity.addPart((String)entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, StreamWrapper> entry : this.streamParams.entrySet())
        {
            StreamWrapper stream = (StreamWrapper)entry.getValue();
            if (stream.inputStream != null) {
                entity.addPart((String)entry.getKey(),
                        StreamWrapper.newInstance(stream.inputStream, stream.name, stream.contentType, stream.autoClose));
            }
        }
        return entity;
    }

    private HttpEntity createFormEntity()
    {
        try
        {
            return new UrlEncodedFormEntity(getParamsList(), this.contentEncoding);
        }
        catch (UnsupportedEncodingException e)
        {
            AsyncHttpClient.log.e("RequestParams", "createFormEntity failed", e);
        }
        return null;
    }

    private HttpEntity createMultipartEntity(ResponseHandlerInterface progressHandler)
            throws IOException
    {
        SimpleMultipartEntity entity = new SimpleMultipartEntity(progressHandler);
        entity.setIsRepeatable(this.isRepeatable);
        for (Iterator localIterator1 = this.urlParams.entrySet().iterator(); localIterator1.hasNext();)
        {
            Map.Entry entry = (Map.Entry)localIterator1.next();
            entity.addPartWithCharset((String)entry.getKey(), (String)entry.getValue(), this.contentEncoding);
        }
        Object params = getParamsList(null, this.urlParamsWithObjects);
        for (BasicNameValuePair kv : (List<BasicNameValuePair>)params) {
            entity.addPartWithCharset(kv.getName(), kv.getValue(), this.contentEncoding);
        }
        for (Map.Entry<String, StreamWrapper> entry : this.streamParams.entrySet())
        {
            StreamWrapper stream = (StreamWrapper)entry.getValue();
            if (stream.inputStream != null) {
                entity.addPart((String)entry.getKey(), stream.name, stream.inputStream, stream.contentType);
            }
        }
        for (Map.Entry<String, FileWrapper> entry : this.fileParams.entrySet())
        {
            FileWrapper fileWrapper = (FileWrapper)entry.getValue();
            entity.addPart((String)entry.getKey(), fileWrapper.file, fileWrapper.contentType, fileWrapper.customFileName);
        }
        for (Iterator it = this.fileArrayParams.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry)it.next();
            List<FileWrapper> fileWrapper = (List)entry.getValue();
            for (FileWrapper fw : fileWrapper) {
                entity.addPart((String)entry.getKey(), fw.file, fw.contentType, fw.customFileName);
            }
        }
        Map.Entry<String, List<FileWrapper>> entry;
        return entity;
    }

    protected List<BasicNameValuePair> getParamsList()
    {
        List<BasicNameValuePair> lparams = new LinkedList();
        for (Map.Entry<String, String> entry : this.urlParams.entrySet()) {
            lparams.add(new BasicNameValuePair((String)entry.getKey(), (String)entry.getValue()));
        }
        lparams.addAll(getParamsList(null, this.urlParamsWithObjects));

        return lparams;
    }

    private List<BasicNameValuePair> getParamsList(String key, Object value)
    {
        List<BasicNameValuePair> params = new LinkedList();
        Map map;
        if ((value instanceof Map))
        {
            map = (Map)value;
            List list = new ArrayList(map.keySet());
            if ((list.size() > 0) && ((list.get(0) instanceof Comparable))) {
                Collections.sort(list);
            }
            for (Object nestedKey : list) {
                if ((nestedKey instanceof String))
                {
                    Object nestedValue = map.get(nestedKey);
                    if (nestedValue != null) {
                        params.addAll(getParamsList(key == null ? (String)nestedKey : String.format(Locale.US, "%s[%s]", new Object[] { key, nestedKey }), nestedValue));
                    }
                }
            }
        }
        else if ((value instanceof List))
        {
            List list = (List)value;
            int listSize = list.size();
            for (int nestedValueIndex = 0; nestedValueIndex < listSize; nestedValueIndex++) {
                params.addAll(getParamsList(String.format(Locale.US, "%s[%d]", new Object[] { key, Integer.valueOf(nestedValueIndex) }), list.get(nestedValueIndex)));
            }
        }
        else
        {
            int arrayLength;
            if ((value instanceof Object[]))
            {
                Object[] array = (Object[])value;
                arrayLength = array.length;
                for (int nestedValueIndex = 0; nestedValueIndex < arrayLength; nestedValueIndex++) {
                    params.addAll(getParamsList(String.format(Locale.US, "%s[%d]", new Object[] { key, Integer.valueOf(nestedValueIndex) }), array[nestedValueIndex]));
                }
            }
            else if ((value instanceof Set))
            {
                Set set = (Set)value;
                for (Object nestedValue : set) {
                    params.addAll(getParamsList(key, nestedValue));
                }
            }
            else
            {
                params.add(new BasicNameValuePair(key, value.toString()));
            }
        }
        return params;
    }

    protected String getParamString()
    {
        return URLEncodedUtils.format(getParamsList(), this.contentEncoding);
    }

    public static class FileWrapper
            implements Serializable
    {
        public final File file;
        public final String contentType;
        public final String customFileName;

        public FileWrapper(File file, String contentType, String customFileName)
        {
            this.file = file;
            this.contentType = contentType;
            this.customFileName = customFileName;
        }
    }

    public static class StreamWrapper
    {
        public final InputStream inputStream;
        public final String name;
        public final String contentType;
        public final boolean autoClose;

        public StreamWrapper(InputStream inputStream, String name, String contentType, boolean autoClose)
        {
            this.inputStream = inputStream;
            this.name = name;
            this.contentType = contentType;
            this.autoClose = autoClose;
        }

        static StreamWrapper newInstance(InputStream inputStream, String name, String contentType, boolean autoClose)
        {
            return new StreamWrapper(inputStream, name, contentType == null ? "application/octet-stream" : contentType, autoClose);
        }
    }
}
