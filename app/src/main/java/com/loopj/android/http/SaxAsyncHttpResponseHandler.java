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


import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;

public abstract class SaxAsyncHttpResponseHandler<T extends DefaultHandler>
        extends AsyncHttpResponseHandler
{
    private static final String LOG_TAG = "SaxAsyncHttpRH";
    private T handler = null;

    public SaxAsyncHttpResponseHandler(T t)
    {
        if (t == null) {
            throw new Error("null instance of <T extends DefaultHandler> passed to constructor");
        }
        this.handler = t;
    }

    protected byte[] getResponseData(HttpEntity entity) throws IOException
    {
        if (entity != null)
        {
            InputStream instream = entity.getContent();
            InputStreamReader inputStreamReader = null;
            if (instream != null) {
                try {
                    SAXParserFactory sfactory = SAXParserFactory.newInstance();
                    SAXParser sparser = sfactory.newSAXParser();
                    XMLReader rssReader = sparser.getXMLReader();
                    rssReader.setContentHandler(this.handler);
                    inputStreamReader = new InputStreamReader(instream, getCharset());
                    rssReader.parse(new InputSource(inputStreamReader));

                    return null;
                } catch (SAXException e) {
                    AsyncHttpClient.log.e("SaxAsyncHttpRH", "getResponseData exception", e);
                } catch (ParserConfigurationException e) {
                    AsyncHttpClient.log.e("SaxAsyncHttpRH", "getResponseData exception", e);
                } finally {
                    AsyncHttpClient.silentCloseInputStream(instream);
                    if (inputStreamReader != null) {
                        try {
                            inputStreamReader.close();
                        } catch (IOException localIOException3) {}
                    }
                }
            }
        }

        return null;
    }

    public abstract void onSuccess(int paramInt, Header[] paramArrayOfHeader, T paramT);

    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
    {
        onSuccess(statusCode, headers, this.handler);
    }

    public abstract void onFailure(int paramInt, Header[] paramArrayOfHeader, T paramT);

    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
    {
        onFailure(statusCode, headers, this.handler);
    }
}
