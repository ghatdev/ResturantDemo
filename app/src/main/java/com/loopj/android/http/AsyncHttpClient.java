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
import android.os.Looper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HeaderElement;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpException;
import cz.msebera.android.httpclient.HttpHost;
import cz.msebera.android.httpclient.HttpRequest;
import cz.msebera.android.httpclient.HttpRequestInterceptor;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpResponseInterceptor;
import cz.msebera.android.httpclient.HttpVersion;
import cz.msebera.android.httpclient.auth.AuthScope;
import cz.msebera.android.httpclient.auth.AuthState;
import cz.msebera.android.httpclient.auth.Credentials;
import cz.msebera.android.httpclient.auth.UsernamePasswordCredentials;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.client.CredentialsProvider;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.RedirectHandler;
import cz.msebera.android.httpclient.client.methods.HttpEntityEnclosingRequestBase;
import cz.msebera.android.httpclient.client.methods.HttpHead;
import cz.msebera.android.httpclient.client.methods.HttpPatch;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.conn.ClientConnectionManager;
import cz.msebera.android.httpclient.conn.params.ConnManagerParams;
import cz.msebera.android.httpclient.conn.params.ConnPerRouteBean;
import cz.msebera.android.httpclient.conn.scheme.PlainSocketFactory;
import cz.msebera.android.httpclient.conn.scheme.Scheme;
import cz.msebera.android.httpclient.conn.scheme.SchemeRegistry;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;
import cz.msebera.android.httpclient.entity.HttpEntityWrapper;
import cz.msebera.android.httpclient.impl.auth.BasicScheme;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.impl.conn.tsccm.ThreadSafeClientConnManager;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.params.HttpProtocolParams;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;
import cz.msebera.android.httpclient.protocol.SyncBasicHttpContext;

public class AsyncHttpClient
{
    public static final String LOG_TAG = "AsyncHttpClient";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_RANGE = "Content-Range";
    public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    public static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    public static final String ENCODING_GZIP = "gzip";
    public static final int DEFAULT_MAX_CONNECTIONS = 10;
    public static final int DEFAULT_SOCKET_TIMEOUT = 10000;
    public static final int DEFAULT_MAX_RETRIES = 5;
    public static final int DEFAULT_RETRY_SLEEP_TIME_MILLIS = 1500;
    public static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
    public static LogInterface log = new LogHandler();
    private final DefaultHttpClient httpClient;
    private final HttpContext httpContext;
    private final Map<Context, List<RequestHandle>> requestMap;
    private final Map<String, String> clientHeaderMap;
    private int maxConnections = 10;
    private int connectTimeout = 10000;
    private int responseTimeout = 10000;
    private ExecutorService threadPool;
    private boolean isUrlEncodingEnabled = true;

    public AsyncHttpClient()
    {
        this(false, 80, 443);
    }

    public AsyncHttpClient(int httpPort)
    {
        this(false, httpPort, 443);
    }

    public AsyncHttpClient(int httpPort, int httpsPort)
    {
        this(false, httpPort, httpsPort);
    }

    public AsyncHttpClient(boolean fixNoHttpResponseException, int httpPort, int httpsPort)
    {
        this(getDefaultSchemeRegistry(fixNoHttpResponseException, httpPort, httpsPort));
    }

    public AsyncHttpClient(SchemeRegistry schemeRegistry)
    {
        BasicHttpParams httpParams = new BasicHttpParams();

        ConnManagerParams.setTimeout(httpParams, this.connectTimeout);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(this.maxConnections));
        ConnManagerParams.setMaxTotalConnections(httpParams, 10);

        HttpConnectionParams.setSoTimeout(httpParams, this.responseTimeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, this.connectTimeout);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpConnectionParams.setSocketBufferSize(httpParams, 8192);

        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

        ClientConnectionManager cm = createConnectionManager(schemeRegistry, httpParams);
        Utils.asserts(cm != null, "Custom implementation of #createConnectionManager(SchemeRegistry, BasicHttpParams) returned null");

        this.threadPool = getDefaultThreadPool();
        this.requestMap = Collections.synchronizedMap(new WeakHashMap());
        this.clientHeaderMap = new HashMap();

        this.httpContext = new SyncBasicHttpContext(new BasicHttpContext());
        this.httpClient = new DefaultHttpClient(cm, httpParams);
        this.httpClient.addRequestInterceptor(new HttpRequestInterceptor()
        {
            public void process(HttpRequest request, HttpContext context)
            {
                if (!request.containsHeader("Accept-Encoding")) {
                    request.addHeader("Accept-Encoding", "gzip");
                }
                for (String header : AsyncHttpClient.this.clientHeaderMap.keySet())
                {
                    if (request.containsHeader(header))
                    {
                        Header overwritten = request.getFirstHeader(header);
                        AsyncHttpClient.log.d("AsyncHttpClient",
                                String.format("Headers were overwritten! (%s | %s) overwrites (%s | %s)", new Object[] { header,
                                        AsyncHttpClient.this.clientHeaderMap.get(header), overwritten
                                        .getName(), overwritten.getValue() }));

                        request.removeHeader(overwritten);
                    }
                    request.addHeader(header, (String)AsyncHttpClient.this.clientHeaderMap.get(header));
                }
            }
        });
        this.httpClient.addResponseInterceptor(new HttpResponseInterceptor()
        {
            public void process(HttpResponse response, HttpContext context)
            {
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    return;
                }
                Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase("gzip"))
                        {
                            response.setEntity(new InflatingEntity(entity));
                            break;
                        }
                    }
                }
            }
        });
        this.httpClient.addRequestInterceptor(new HttpRequestInterceptor()
        {
            public void process(HttpRequest request, HttpContext context)
                    throws HttpException, IOException
            {
                AuthState authState = (AuthState)context.getAttribute("http.auth.target-scope");
                CredentialsProvider credsProvider = (CredentialsProvider)context.getAttribute("http.auth.credentials-provider");

                HttpHost targetHost = (HttpHost)context.getAttribute("http.target_host");
                if (authState.getAuthScheme() == null)
                {
                    AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
                    Credentials creds = credsProvider.getCredentials(authScope);
                    if (creds != null)
                    {
                        authState.setAuthScheme(new BasicScheme());
                        authState.setCredentials(creds);
                    }
                }
            }
        }, 0);

        this.httpClient.setHttpRequestRetryHandler(new RetryHandler(5, 1500));
    }

    private static SchemeRegistry getDefaultSchemeRegistry(boolean fixNoHttpResponseException, int httpPort, int httpsPort)
    {
        if (fixNoHttpResponseException) {
            log.d("AsyncHttpClient", "Beware! Using the fix is insecure, as it doesn't verify SSL certificates.");
        }
        if (httpPort < 1)
        {
            httpPort = 80;
            log.d("AsyncHttpClient", "Invalid HTTP port number specified, defaulting to 80");
        }
        if (httpsPort < 1)
        {
            httpsPort = 443;
            log.d("AsyncHttpClient", "Invalid HTTPS port number specified, defaulting to 443");
        }
        SSLSocketFactory sslSocketFactory;
        if (fixNoHttpResponseException) {
            sslSocketFactory = MySSLSocketFactory.getFixedSocketFactory();
        } else {
            sslSocketFactory = SSLSocketFactory.getSocketFactory();
        }
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), httpPort));
        schemeRegistry.register(new Scheme("https", sslSocketFactory, httpsPort));

        return schemeRegistry;
    }

    public static void allowRetryExceptionClass(Class<?> cls)
    {
        if (cls != null) {
            RetryHandler.addClassToWhitelist(cls);
        }
    }

    public static void blockRetryExceptionClass(Class<?> cls)
    {
        if (cls != null) {
            RetryHandler.addClassToBlacklist(cls);
        }
    }

    public static String getUrlWithQueryString(boolean shouldEncodeUrl, String url, RequestParams params)
    {
        if (url == null) {
            return null;
        }
        if (shouldEncodeUrl) {
            try
            {
                String decodedURL = URLDecoder.decode(url, "UTF-8");
                URL _url = new URL(decodedURL);
                URI _uri = new URI(_url.getProtocol(), _url.getUserInfo(), _url.getHost(), _url.getPort(), _url.getPath(), _url.getQuery(), _url.getRef());
                url = _uri.toASCIIString();
            }
            catch (Exception ex)
            {
                log.e("AsyncHttpClient", "getUrlWithQueryString encoding URL", ex);
            }
        }
        if (params != null)
        {
            String paramString = params.getParamString().trim();
            if ((!paramString.equals("")) && (!paramString.equals("?")))
            {
                url = url + (url.contains("?") ? "&" : "?");
                url = url + paramString;
            }
        }
        return url;
    }

    public static boolean isInputStreamGZIPCompressed(PushbackInputStream inputStream)
            throws IOException
    {
        if (inputStream == null) {
            return false;
        }
        byte[] signature = new byte[2];
        int count = 0;
        try
        {
            while (count < 2)
            {
                int readCount = inputStream.read(signature, count, 2 - count);
                if (readCount < 0) {
                    return false;
                }
                count += readCount;
            }
        }
        finally
        {
            inputStream.unread(signature, 0, count);
        }
        int streamHeader = signature[0] & 0xFF | signature[1] << 8 & 0xFF00;
        return 35615 == streamHeader;
    }

    public static void silentCloseInputStream(InputStream is)
    {
        try
        {
            if (is != null) {
                is.close();
            }
        }
        catch (IOException e)
        {
            log.w("AsyncHttpClient", "Cannot close input stream", e);
        }
    }

    public static void silentCloseOutputStream(OutputStream os)
    {
        try
        {
            if (os != null) {
                os.close();
            }
        }
        catch (IOException e)
        {
            log.w("AsyncHttpClient", "Cannot close output stream", e);
        }
    }

    public static void endEntityViaReflection(HttpEntity entity)
    {
        if ((entity instanceof HttpEntityWrapper)) {
            try
            {
                Field f = null;
                Field[] fields = HttpEntityWrapper.class.getDeclaredFields();
                for (Field ff : fields) {
                    if (ff.getName().equals("wrappedEntity"))
                    {
                        f = ff;
                        break;
                    }
                }
                if (f != null)
                {
                    f.setAccessible(true);
                    HttpEntity wrapped = (HttpEntity)f.get(entity);
                    if (wrapped != null) {
                        wrapped.consumeContent();
                    }
                }
            }
            catch (Throwable t)
            {
                log.e("AsyncHttpClient", "wrappedEntity consume", t);
            }
        }
    }

    public HttpClient getHttpClient()
    {
        return this.httpClient;
    }

    public HttpContext getHttpContext()
    {
        return this.httpContext;
    }

    public boolean isLoggingEnabled()
    {
        return log.isLoggingEnabled();
    }

    public void setLoggingEnabled(boolean loggingEnabled)
    {
        log.setLoggingEnabled(loggingEnabled);
    }

    public int getLoggingLevel()
    {
        return log.getLoggingLevel();
    }

    public void setLoggingLevel(int logLevel)
    {
        log.setLoggingLevel(logLevel);
    }

    public LogInterface getLogInterface()
    {
        return log;
    }

    public void setLogInterface(LogInterface logInterfaceInstance)
    {
        if (logInterfaceInstance != null) {
            log = logInterfaceInstance;
        }
    }

    public void setCookieStore(CookieStore cookieStore)
    {
        this.httpContext.setAttribute("http.cookie-store", cookieStore);
    }

    public ExecutorService getThreadPool()
    {
        return this.threadPool;
    }

    public void setThreadPool(ExecutorService threadPool)
    {
        this.threadPool = threadPool;
    }

    protected ExecutorService getDefaultThreadPool()
    {
        return Executors.newCachedThreadPool();
    }

    protected ClientConnectionManager createConnectionManager(SchemeRegistry schemeRegistry, BasicHttpParams httpParams)
    {
        return new ThreadSafeClientConnManager(httpParams, schemeRegistry);
    }

    public void setEnableRedirects(boolean enableRedirects, boolean enableRelativeRedirects, boolean enableCircularRedirects)
    {
        this.httpClient.getParams().setBooleanParameter("http.protocol.reject-relative-redirect", !enableRelativeRedirects);
        this.httpClient.getParams().setBooleanParameter("http.protocol.allow-circular-redirects", enableCircularRedirects);
        this.httpClient.setRedirectHandler(new MyRedirectHandler(enableRedirects));
    }

    public void setEnableRedirects(boolean enableRedirects, boolean enableRelativeRedirects)
    {
        setEnableRedirects(enableRedirects, enableRelativeRedirects, true);
    }

    public void setEnableRedirects(boolean enableRedirects)
    {
        setEnableRedirects(enableRedirects, enableRedirects, enableRedirects);
    }

    public void setRedirectHandler(RedirectHandler customRedirectHandler)
    {
        this.httpClient.setRedirectHandler(customRedirectHandler);
    }

    public void setUserAgent(String userAgent)
    {
        HttpProtocolParams.setUserAgent(this.httpClient.getParams(), userAgent);
    }

    public int getMaxConnections()
    {
        return this.maxConnections;
    }

    public void setMaxConnections(int maxConnections)
    {
        if (maxConnections < 1) {
            maxConnections = 10;
        }
        this.maxConnections = maxConnections;
        HttpParams httpParams = this.httpClient.getParams();
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(this.maxConnections));
    }

    public void setTimeout(int value)
    {
        value = value < 1000 ? 10000 : value;
        setConnectTimeout(value);
        setResponseTimeout(value);
    }

    public int getConnectTimeout()
    {
        return this.connectTimeout;
    }

    public void setConnectTimeout(int value)
    {
        this.connectTimeout = (value < 1000 ? 10000 : value);
        HttpParams httpParams = this.httpClient.getParams();
        ConnManagerParams.setTimeout(httpParams, this.connectTimeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, this.connectTimeout);
    }

    public int getResponseTimeout()
    {
        return this.responseTimeout;
    }

    public void setResponseTimeout(int value)
    {
        this.responseTimeout = (value < 1000 ? 10000 : value);
        HttpParams httpParams = this.httpClient.getParams();
        HttpConnectionParams.setSoTimeout(httpParams, this.responseTimeout);
    }

    public void setProxy(String hostname, int port)
    {
        HttpHost proxy = new HttpHost(hostname, port);
        HttpParams httpParams = this.httpClient.getParams();
        httpParams.setParameter("http.route.default-proxy", proxy);
    }

    public void setProxy(String hostname, int port, String username, String password)
    {
        this.httpClient.getCredentialsProvider().setCredentials(new AuthScope(hostname, port), new UsernamePasswordCredentials(username, password));

        HttpHost proxy = new HttpHost(hostname, port);
        HttpParams httpParams = this.httpClient.getParams();
        httpParams.setParameter("http.route.default-proxy", proxy);
    }

    public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory)
    {
        this.httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", sslSocketFactory, 443));
    }

    public void setMaxRetriesAndTimeout(int retries, int timeout)
    {
        this.httpClient.setHttpRequestRetryHandler(new RetryHandler(retries, timeout));
    }

    public void removeAllHeaders()
    {
        this.clientHeaderMap.clear();
    }

    public void addHeader(String header, String value)
    {
        this.clientHeaderMap.put(header, value);
    }

    public void removeHeader(String header)
    {
        this.clientHeaderMap.remove(header);
    }

    public void setBasicAuth(String username, String password)
    {
        setBasicAuth(username, password, false);
    }

    public void setBasicAuth(String username, String password, boolean preemptive)
    {
        setBasicAuth(username, password, null, preemptive);
    }

    public void setBasicAuth(String username, String password, AuthScope scope)
    {
        setBasicAuth(username, password, scope, false);
    }

    public void setBasicAuth(String username, String password, AuthScope scope, boolean preemptive)
    {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        setCredentials(scope, credentials);
        setAuthenticationPreemptive(preemptive);
    }

    public void setCredentials(AuthScope authScope, Credentials credentials)
    {
        if (credentials == null)
        {
            log.d("AsyncHttpClient", "Provided credentials are null, not setting");
            return;
        }
        this.httpClient.getCredentialsProvider().setCredentials(authScope == null ? AuthScope.ANY : authScope, credentials);
    }

    public void setAuthenticationPreemptive(boolean isPreemptive)
    {
        if (isPreemptive) {
            this.httpClient.addRequestInterceptor(new PreemptiveAuthorizationHttpRequestInterceptor(), 0);
        } else {
            this.httpClient.removeRequestInterceptorByClass(PreemptiveAuthorizationHttpRequestInterceptor.class);
        }
    }

    public void clearCredentialsProvider()
    {
        this.httpClient.getCredentialsProvider().clear();
    }

    public void cancelRequests(Context context, final boolean mayInterruptIfRunning)
    {
        if (context == null)
        {
            log.e("AsyncHttpClient", "Passed null Context to cancelRequests");
            return;
        }
        final List<RequestHandle> requestList = (List)this.requestMap.get(context);
        this.requestMap.remove(context);
        if (Looper.myLooper() == Looper.getMainLooper())
        {
            Runnable runnable = new Runnable()
            {
                public void run()
                {
                    AsyncHttpClient.this.cancelRequests(requestList, mayInterruptIfRunning);
                }
            };
            this.threadPool.submit(runnable);
        }
        else
        {
            cancelRequests(requestList, mayInterruptIfRunning);
        }
    }

    private void cancelRequests(List<RequestHandle> requestList, boolean mayInterruptIfRunning)
    {
        if (requestList != null) {
            for (RequestHandle requestHandle : requestList) {
                requestHandle.cancel(mayInterruptIfRunning);
            }
        }
    }

    public void cancelAllRequests(boolean mayInterruptIfRunning)
    {
        for (List<RequestHandle> requestList : this.requestMap.values()) {
            if (requestList != null) {
                for (RequestHandle requestHandle : requestList) {
                    requestHandle.cancel(mayInterruptIfRunning);
                }
            }
        }
        this.requestMap.clear();
    }

    public void cancelRequestsByTAG(Object TAG, boolean mayInterruptIfRunning)
    {
        if (TAG == null)
        {
            log.d("AsyncHttpClient", "cancelRequestsByTAG, passed TAG is null, cannot proceed");
            return;
        }
        for (List<RequestHandle> requestList : this.requestMap.values()) {
            if (requestList != null) {
                for (RequestHandle requestHandle : requestList) {
                    if (TAG.equals(requestHandle.getTag())) {
                        requestHandle.cancel(mayInterruptIfRunning);
                    }
                }
            }
        }
    }

    public RequestHandle head(String url, ResponseHandlerInterface responseHandler)
    {
        return head(null, url, null, responseHandler);
    }

    public RequestHandle head(String url, RequestParams params, ResponseHandlerInterface responseHandler)
    {
        return head(null, url, params, responseHandler);
    }

    public RequestHandle head(Context context, String url, ResponseHandlerInterface responseHandler)
    {
        return head(context, url, null, responseHandler);
    }

    public RequestHandle head(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler)
    {
        return sendRequest(this.httpClient, this.httpContext, new HttpHead(getUrlWithQueryString(this.isUrlEncodingEnabled, url, params)), null, responseHandler, context);
    }

    public RequestHandle head(Context context, String url, Header[] headers, RequestParams params, ResponseHandlerInterface responseHandler)
    {
        HttpUriRequest request = new HttpHead(getUrlWithQueryString(this.isUrlEncodingEnabled, url, params));
        if (headers != null) {
            request.setHeaders(headers);
        }
        return sendRequest(this.httpClient, this.httpContext, request, null, responseHandler, context);
    }

    public RequestHandle get(String url, ResponseHandlerInterface responseHandler)
    {
        return get(null, url, null, responseHandler);
    }

    public RequestHandle get(String url, RequestParams params, ResponseHandlerInterface responseHandler)
    {
        return get(null, url, params, responseHandler);
    }

    public RequestHandle get(Context context, String url, ResponseHandlerInterface responseHandler)
    {
        return get(context, url, null, responseHandler);
    }

    public RequestHandle get(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler)
    {
        return sendRequest(this.httpClient, this.httpContext, new HttpGet(getUrlWithQueryString(this.isUrlEncodingEnabled, url, params)), null, responseHandler, context);
    }

    public RequestHandle get(Context context, String url, Header[] headers, RequestParams params, ResponseHandlerInterface responseHandler)
    {
        HttpUriRequest request = new HttpGet(getUrlWithQueryString(this.isUrlEncodingEnabled, url, params));
        if (headers != null) {
            request.setHeaders(headers);
        }
        return sendRequest(this.httpClient, this.httpContext, request, null, responseHandler, context);
    }

    public RequestHandle get(Context context, String url, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler)
    {
        return sendRequest(this.httpClient, this.httpContext, addEntityToRequestBase(new HttpGet(URI.create(url).normalize()), entity), contentType, responseHandler, context);
    }

    public RequestHandle post(String url, ResponseHandlerInterface responseHandler)
    {
        return post(null, url, null, responseHandler);
    }

    public RequestHandle post(String url, RequestParams params, ResponseHandlerInterface responseHandler)
    {
        return post(null, url, params, responseHandler);
    }

    public RequestHandle post(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler)
    {
        return post(context, url, paramsToEntity(params, responseHandler), null, responseHandler);
    }

    public RequestHandle post(Context context, String url, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler)
    {
        return sendRequest(this.httpClient, this.httpContext, addEntityToRequestBase(new HttpPost(getURI(url)), entity), contentType, responseHandler, context);
    }

    public RequestHandle post(Context context, String url, Header[] headers, RequestParams params, String contentType, ResponseHandlerInterface responseHandler)
    {
        HttpEntityEnclosingRequestBase request = new HttpPost(getURI(url));
        if (params != null) {
            request.setEntity(paramsToEntity(params, responseHandler));
        }
        if (headers != null) {
            request.setHeaders(headers);
        }
        return sendRequest(this.httpClient, this.httpContext, request, contentType, responseHandler, context);
    }

    public RequestHandle post(Context context, String url, Header[] headers, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler)
    {
        HttpEntityEnclosingRequestBase request = addEntityToRequestBase(new HttpPost(getURI(url)), entity);
        if (headers != null) {
            request.setHeaders(headers);
        }
        return sendRequest(this.httpClient, this.httpContext, request, contentType, responseHandler, context);
    }

    public RequestHandle put(String url, ResponseHandlerInterface responseHandler)
    {
        return put(null, url, null, responseHandler);
    }

    public RequestHandle put(String url, RequestParams params, ResponseHandlerInterface responseHandler)
    {
        return put(null, url, params, responseHandler);
    }

    public RequestHandle put(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler)
    {
        return put(context, url, paramsToEntity(params, responseHandler), null, responseHandler);
    }

    public RequestHandle put(Context context, String url, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler)
    {
        return sendRequest(this.httpClient, this.httpContext, addEntityToRequestBase(new HttpPut(getURI(url)), entity), contentType, responseHandler, context);
    }

    public RequestHandle put(Context context, String url, Header[] headers, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler)
    {
        HttpEntityEnclosingRequestBase request = addEntityToRequestBase(new HttpPut(getURI(url)), entity);
        if (headers != null) {
            request.setHeaders(headers);
        }
        return sendRequest(this.httpClient, this.httpContext, request, contentType, responseHandler, context);
    }

    public RequestHandle patch(String url, ResponseHandlerInterface responseHandler)
    {
        return patch(null, url, null, responseHandler);
    }

    public RequestHandle patch(String url, RequestParams params, ResponseHandlerInterface responseHandler)
    {
        return patch(null, url, params, responseHandler);
    }

    public RequestHandle patch(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler)
    {
        return patch(context, url, paramsToEntity(params, responseHandler), null, responseHandler);
    }

    public RequestHandle patch(Context context, String url, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler)
    {
        return sendRequest(this.httpClient, this.httpContext, addEntityToRequestBase(new HttpPatch(getURI(url)), entity), contentType, responseHandler, context);
    }

    public RequestHandle patch(Context context, String url, Header[] headers, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler)
    {
        HttpEntityEnclosingRequestBase request = addEntityToRequestBase(new HttpPatch(getURI(url)), entity);
        if (headers != null) {
            request.setHeaders(headers);
        }
        return sendRequest(this.httpClient, this.httpContext, request, contentType, responseHandler, context);
    }

    public RequestHandle delete(String url, ResponseHandlerInterface responseHandler)
    {
        return delete(null, url, responseHandler);
    }

    public RequestHandle delete(Context context, String url, ResponseHandlerInterface responseHandler)
    {
        HttpDelete delete = new HttpDelete(getURI(url));
        return sendRequest(this.httpClient, this.httpContext, delete, null, responseHandler, context);
    }

    public RequestHandle delete(Context context, String url, Header[] headers, ResponseHandlerInterface responseHandler)
    {
        HttpDelete delete = new HttpDelete(getURI(url));
        if (headers != null) {
            delete.setHeaders(headers);
        }
        return sendRequest(this.httpClient, this.httpContext, delete, null, responseHandler, context);
    }

    public void delete(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
    {
        HttpDelete delete = new HttpDelete(getUrlWithQueryString(this.isUrlEncodingEnabled, url, params));
        sendRequest(this.httpClient, this.httpContext, delete, null, responseHandler, null);
    }

    public RequestHandle delete(Context context, String url, Header[] headers, RequestParams params, ResponseHandlerInterface responseHandler)
    {
        HttpDelete httpDelete = new HttpDelete(getUrlWithQueryString(this.isUrlEncodingEnabled, url, params));
        if (headers != null) {
            httpDelete.setHeaders(headers);
        }
        return sendRequest(this.httpClient, this.httpContext, httpDelete, null, responseHandler, context);
    }

    public RequestHandle delete(Context context, String url, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler)
    {
        return sendRequest(this.httpClient, this.httpContext, addEntityToRequestBase(new HttpDelete(URI.create(url).normalize()), entity), contentType, responseHandler, context);
    }

    protected AsyncHttpRequest newAsyncHttpRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest, String contentType, ResponseHandlerInterface responseHandler, Context context)
    {
        return new AsyncHttpRequest(client, httpContext, uriRequest, responseHandler);
    }

    protected RequestHandle sendRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest, String contentType, ResponseHandlerInterface responseHandler, Context context)
    {
        if (uriRequest == null) {
            throw new IllegalArgumentException("HttpUriRequest must not be null");
        }
        if (responseHandler == null) {
            throw new IllegalArgumentException("ResponseHandler must not be null");
        }
        if ((responseHandler.getUseSynchronousMode()) && (!responseHandler.getUsePoolThread())) {
            throw new IllegalArgumentException("Synchronous ResponseHandler used in AsyncHttpClient. You should create your response handler in a looper thread or use SyncHttpClient instead.");
        }
        if (contentType != null) {
            if (((uriRequest instanceof HttpEntityEnclosingRequestBase)) && (((HttpEntityEnclosingRequestBase)uriRequest).getEntity() != null) && (uriRequest.containsHeader("Content-Type"))) {
                log.w("AsyncHttpClient", "Passed contentType will be ignored because HttpEntity sets content type");
            } else {
                uriRequest.setHeader("Content-Type", contentType);
            }
        }
        responseHandler.setRequestHeaders(uriRequest.getAllHeaders());
        responseHandler.setRequestURI(uriRequest.getURI());

        AsyncHttpRequest request = newAsyncHttpRequest(client, httpContext, uriRequest, contentType, responseHandler, context);
        this.threadPool.submit(request);
        RequestHandle requestHandle = new RequestHandle(request);
        if (context != null)
        {
            synchronized (this.requestMap)
            {
                List<RequestHandle> requestList = (List)this.requestMap.get(context);
                if (requestList == null)
                {
                    requestList = Collections.synchronizedList(new LinkedList());
                    this.requestMap.put(context, requestList);
                }
            }
            List<RequestHandle> requestList = new ArrayList<RequestHandle>();
            requestList.add(requestHandle);

            Iterator<RequestHandle> iterator = requestList.iterator();
            while (iterator.hasNext()) {
                if (((RequestHandle)iterator.next()).shouldBeGarbageCollected()) {
                    iterator.remove();
                }
            }
        }
        return requestHandle;
    }

    protected URI getURI(String url)
    {
        return URI.create(url).normalize();
    }

    public void setURLEncodingEnabled(boolean enabled)
    {
        this.isUrlEncodingEnabled = enabled;
    }

    private HttpEntity paramsToEntity(RequestParams params, ResponseHandlerInterface responseHandler)
    {
        HttpEntity entity = null;
        try
        {
            if (params != null) {
                entity = params.getEntity(responseHandler);
            }
        }
        catch (IOException e)
        {
            if (responseHandler != null) {
                responseHandler.sendFailureMessage(0, null, null, e);
            } else {
                e.printStackTrace();
            }
        }
        return entity;
    }

    public boolean isUrlEncodingEnabled()
    {
        return this.isUrlEncodingEnabled;
    }

    private HttpEntityEnclosingRequestBase addEntityToRequestBase(HttpEntityEnclosingRequestBase requestBase, HttpEntity entity)
    {
        if (entity != null) {
            requestBase.setEntity(entity);
        }
        return requestBase;
    }

    private static class InflatingEntity
            extends HttpEntityWrapper
    {
        InputStream wrappedStream;
        PushbackInputStream pushbackStream;
        GZIPInputStream gzippedStream;

        public InflatingEntity(HttpEntity wrapped)
        {
            super(wrapped);
        }

        public InputStream getContent()
                throws IOException
        {
            this.wrappedStream = this.wrappedEntity.getContent();
            this.pushbackStream = new PushbackInputStream(this.wrappedStream, 2);
            if (AsyncHttpClient.isInputStreamGZIPCompressed(this.pushbackStream))
            {
                this.gzippedStream = new GZIPInputStream(this.pushbackStream);
                return this.gzippedStream;
            }
            return this.pushbackStream;
        }

        public long getContentLength()
        {
            return this.wrappedEntity == null ? 0L : this.wrappedEntity.getContentLength();
        }

        public void consumeContent()
                throws IOException
        {
            AsyncHttpClient.silentCloseInputStream(this.wrappedStream);
            AsyncHttpClient.silentCloseInputStream(this.pushbackStream);
            AsyncHttpClient.silentCloseInputStream(this.gzippedStream);
            super.consumeContent();
        }
    }
}
