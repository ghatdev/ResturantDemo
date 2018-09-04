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


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.HttpVersion;
import cz.msebera.android.httpclient.conn.ClientConnectionManager;
import cz.msebera.android.httpclient.conn.scheme.PlainSocketFactory;
import cz.msebera.android.httpclient.conn.scheme.Scheme;
import cz.msebera.android.httpclient.conn.scheme.SchemeRegistry;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.impl.conn.tsccm.ThreadSafeClientConnManager;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.params.HttpProtocolParams;

public class MySSLSocketFactory
        extends cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory
{
    final SSLContext sslContext = SSLContext.getInstance("TLS");

    public MySSLSocketFactory(KeyStore truststore)
            throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
    {
        super(truststore);

        X509TrustManager tm = new X509TrustManager()
        {
            public void checkClientTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException
            {}

            public void checkServerTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException
            {}

            public X509Certificate[] getAcceptedIssuers()
            {
                return null;
            }
        };
        this.sslContext.init(null, new TrustManager[] { tm }, null);
    }

    public static KeyStore getKeystoreOfCA(InputStream cert)
    {
        InputStream caInput = null;
        Certificate ca = null;
        String keyStoreType = null;
        try
        {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            caInput = new BufferedInputStream(cert);
            ca = cf.generateCertificate(caInput);
            try
            {
                if (caInput != null) {
                    caInput.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            keyStoreType = KeyStore.getDefaultType();
        }
        catch (CertificateException e1)
        {
            e1.printStackTrace();
        }
        finally
        {
            try
            {
                if (caInput != null) {
                    caInput.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        KeyStore keyStore = null;
        try
        {
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return keyStore;
    }

    public static KeyStore getKeystore()
    {
        KeyStore trustStore = null;
        try
        {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        return trustStore;
    }

    public static cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory getFixedSocketFactory()
    {
        cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory socketFactory;
        try
        {
            socketFactory = new MySSLSocketFactory(getKeystore());
            socketFactory.setHostnameVerifier(cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            socketFactory = cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory.getSocketFactory();
        }
        return socketFactory;
    }

    public static DefaultHttpClient getNewHttpClient(KeyStore keyStore)
    {
        try
        {
            cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory sf = new MySSLSocketFactory(keyStore);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, "UTF-8");

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        }
        catch (Exception e) {}
        return new DefaultHttpClient();
    }

    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
            throws IOException
    {
        return this.sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    public Socket createSocket()
            throws IOException
    {
        return this.sslContext.getSocketFactory().createSocket();
    }

    public void fixHttpsURLConnection()
    {
        HttpsURLConnection.setDefaultSSLSocketFactory(this.sslContext.getSocketFactory());
    }
}
