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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.message.BasicHeader;

class SimpleMultipartEntity
        implements HttpEntity
{
    private static final String LOG_TAG = "SimpleMultipartEntity";
    private static final String STR_CR_LF = "\r\n";
    private static final byte[] CR_LF = "\r\n".getBytes();
    private static final byte[] TRANSFER_ENCODING_BINARY = "Content-Transfer-Encoding: binary\r\n"
            .getBytes();
    private static final char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            .toCharArray();
    private final String boundary;
    private final byte[] boundaryLine;
    private final byte[] boundaryEnd;
    private final List<FilePart> fileParts = new ArrayList();
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ResponseHandlerInterface progressHandler;
    private boolean isRepeatable;
    private long bytesWritten;
    private long totalSize;

    public SimpleMultipartEntity(ResponseHandlerInterface progressHandler)
    {
        StringBuilder buf = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        this.boundary = buf.toString();
        this.boundaryLine = ("--" + this.boundary + "\r\n").getBytes();
        this.boundaryEnd = ("--" + this.boundary + "--" + "\r\n").getBytes();

        this.progressHandler = progressHandler;
    }

    public void addPart(String key, String value, String contentType)
    {
        try
        {
            this.out.write(this.boundaryLine);
            this.out.write(createContentDisposition(key));
            this.out.write(createContentType(contentType));
            this.out.write(CR_LF);
            this.out.write(value.getBytes());
            this.out.write(CR_LF);
        }
        catch (IOException e)
        {
            AsyncHttpClient.log.e("SimpleMultipartEntity", "addPart ByteArrayOutputStream exception", e);
        }
    }

    public void addPartWithCharset(String key, String value, String charset)
    {
        if (charset == null) {
            charset = "UTF-8";
        }
        addPart(key, value, "text/plain; charset=" + charset);
    }

    public void addPart(String key, String value)
    {
        addPartWithCharset(key, value, null);
    }

    public void addPart(String key, File file)
    {
        addPart(key, file, null);
    }

    public void addPart(String key, File file, String type)
    {
        this.fileParts.add(new FilePart(key, file, normalizeContentType(type)));
    }

    public void addPart(String key, File file, String type, String customFileName)
    {
        this.fileParts.add(new FilePart(key, file, normalizeContentType(type), customFileName));
    }

    public void addPart(String key, String streamName, InputStream inputStream, String type)
            throws IOException
    {
        this.out.write(this.boundaryLine);

        this.out.write(createContentDisposition(key, streamName));
        this.out.write(createContentType(type));
        this.out.write(TRANSFER_ENCODING_BINARY);
        this.out.write(CR_LF);

        byte[] tmp = new byte['?'];
        int l;
        while ((l = inputStream.read(tmp)) != -1) {
            this.out.write(tmp, 0, l);
        }
        this.out.write(CR_LF);
        this.out.flush();
    }

    private String normalizeContentType(String type)
    {
        return type == null ? "application/octet-stream" : type;
    }

    private byte[] createContentType(String type)
    {
        String result = "Content-Type: " + normalizeContentType(type) + "\r\n";
        return result.getBytes();
    }

    private byte[] createContentDisposition(String key)
    {
        return ("Content-Disposition: form-data; name=\"" + key + "\"" + "\r\n").getBytes();
    }

    private byte[] createContentDisposition(String key, String fileName)
    {
        return ("Content-Disposition: form-data; name=\"" + key + "\"" + "; filename=\"" + fileName + "\"" + "\r\n").getBytes();
    }

    private void updateProgress(long count)
    {
        this.bytesWritten += count;
        this.progressHandler.sendProgressMessage(this.bytesWritten, this.totalSize);
    }

    public long getContentLength()
    {
        long contentLen = this.out.size();
        for (FilePart filePart : this.fileParts)
        {
            long len = filePart.getTotalLength();
            if (len < 0L) {
                return -1L;
            }
            contentLen += len;
        }
        contentLen += this.boundaryEnd.length;
        return contentLen;
    }

    public Header getContentType()
    {
        return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + this.boundary);
    }

    public boolean isChunked()
    {
        return false;
    }

    public void setIsRepeatable(boolean isRepeatable)
    {
        this.isRepeatable = isRepeatable;
    }

    public boolean isRepeatable()
    {
        return this.isRepeatable;
    }

    public boolean isStreaming()
    {
        return false;
    }

    public void writeTo(OutputStream outstream)
            throws IOException
    {
        this.bytesWritten = 0L;
        this.totalSize = ((int)getContentLength());
        this.out.writeTo(outstream);
        updateProgress(this.out.size());
        for (FilePart filePart : this.fileParts) {
            filePart.writeTo(outstream);
        }
        outstream.write(this.boundaryEnd);
        updateProgress(this.boundaryEnd.length);
    }

    public Header getContentEncoding()
    {
        return null;
    }

    public void consumeContent()
            throws IOException, UnsupportedOperationException
    {
        if (isStreaming()) {
            throw new UnsupportedOperationException("Streaming entity does not implement #consumeContent()");
        }
    }

    public InputStream getContent()
            throws IOException, UnsupportedOperationException
    {
        throw new UnsupportedOperationException("getContent() is not supported. Use writeTo() instead.");
    }

    private class FilePart
    {
        public final File file;
        public final byte[] header;

        public FilePart(String key, File file, String type, String customFileName)
        {
            this.header = createHeader(key, TextUtils.isEmpty(customFileName) ? file.getName() : customFileName, type);
            this.file = file;
        }

        public FilePart(String key, File file, String type)
        {
            this.header = createHeader(key, file.getName(), type);
            this.file = file;
        }

        private byte[] createHeader(String key, String filename, String type)
        {
            ByteArrayOutputStream headerStream = new ByteArrayOutputStream();
            try
            {
                headerStream.write(SimpleMultipartEntity.this.boundaryLine);

                headerStream.write(SimpleMultipartEntity.this.createContentDisposition(key, filename));
                headerStream.write(SimpleMultipartEntity.this.createContentType(type));
                headerStream.write(SimpleMultipartEntity.TRANSFER_ENCODING_BINARY);
                headerStream.write(SimpleMultipartEntity.CR_LF);
            }
            catch (IOException e)
            {
                AsyncHttpClient.log.e("SimpleMultipartEntity", "createHeader ByteArrayOutputStream exception", e);
            }
            return headerStream.toByteArray();
        }

        public long getTotalLength()
        {
            long streamLength = this.file.length() + SimpleMultipartEntity.CR_LF.length;
            return this.header.length + streamLength;
        }

        public void writeTo(OutputStream out)
                throws IOException
        {
            out.write(this.header);
            SimpleMultipartEntity.this.updateProgress(this.header.length);

            FileInputStream inputStream = new FileInputStream(this.file);
            byte[] tmp = new byte['?'];
            int bytesRead;
            while ((bytesRead = inputStream.read(tmp)) != -1)
            {
                out.write(tmp, 0, bytesRead);
                SimpleMultipartEntity.this.updateProgress(bytesRead);
            }
            out.write(SimpleMultipartEntity.CR_LF);
            SimpleMultipartEntity.this.updateProgress(SimpleMultipartEntity.CR_LF.length);
            out.flush();
            AsyncHttpClient.silentCloseInputStream(inputStream);
        }
    }
}
