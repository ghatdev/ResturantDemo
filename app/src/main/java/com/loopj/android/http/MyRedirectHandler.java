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


import java.net.URI;
import java.net.URISyntaxException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpHost;
import cz.msebera.android.httpclient.HttpRequest;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.ProtocolException;
import cz.msebera.android.httpclient.client.CircularRedirectException;
import cz.msebera.android.httpclient.client.utils.URIUtils;
import cz.msebera.android.httpclient.impl.client.DefaultRedirectHandler;
import cz.msebera.android.httpclient.impl.client.RedirectLocations;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.protocol.HttpContext;

class MyRedirectHandler
        extends DefaultRedirectHandler
{
    private static final String REDIRECT_LOCATIONS = "http.protocol.redirect-locations";
    private final boolean enableRedirects;

    public MyRedirectHandler(boolean allowRedirects)
    {
        this.enableRedirects = allowRedirects;
    }

    public boolean isRedirectRequested(HttpResponse response, HttpContext context)
    {
        if (!this.enableRedirects) {
            return false;
        }
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        int statusCode = response.getStatusLine().getStatusCode();
        switch (statusCode)
        {
            case 301:
            case 302:
            case 303:
            case 307:
                return true;
        }
        return false;
    }

    public URI getLocationURI(HttpResponse response, HttpContext context)
            throws ProtocolException
    {
        URI uri;

        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        Header locationHeader = response.getFirstHeader("location");
        if (locationHeader == null) {
            throw new ProtocolException("Received redirect response " + response.getStatusLine() + " but no location header");
        }
        String location = locationHeader.getValue().replaceAll(" ", "%20");
        try
        {
            uri = new URI(location);
        }
        catch (URISyntaxException ex)
        {
            throw new ProtocolException("Invalid redirect URI: " + location, ex);
        }
        HttpParams params = response.getParams();
        if (!uri.isAbsolute())
        {
            if (params.isParameterTrue("http.protocol.reject-relative-redirect")) {
                throw new ProtocolException("Relative redirect location '" + uri + "' not allowed");
            }
            HttpHost target = (HttpHost)context.getAttribute("http.target_host");
            if (target == null) {
                throw new IllegalStateException("Target host not available in the HTTP context");
            }
            HttpRequest request = (HttpRequest)context.getAttribute("http.request");
            try
            {
                URI requestURI = new URI(request.getRequestLine().getUri());
                URI absoluteRequestURI = URIUtils.rewriteURI(requestURI, target, true);
                uri = URIUtils.resolve(absoluteRequestURI, uri);
            }
            catch (URISyntaxException ex)
            {
                throw new ProtocolException(ex.getMessage(), ex);
            }
        }
        if (params.isParameterFalse("http.protocol.allow-circular-redirects"))
        {
            RedirectLocations redirectLocations = (RedirectLocations)context.getAttribute("http.protocol.redirect-locations");
            if (redirectLocations == null)
            {
                redirectLocations = new RedirectLocations();
                context.setAttribute("http.protocol.redirect-locations", redirectLocations);
            }
            URI redirectURI;
            if (uri.getFragment() != null) {
                try
                {
                    HttpHost target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
                    redirectURI = URIUtils.rewriteURI(uri, target, true);
                }
                catch (URISyntaxException ex)
                {
                    throw new ProtocolException(ex.getMessage(), ex);
                }
            } else {
                redirectURI = uri;
            }
            if (redirectLocations.contains(redirectURI)) {
                throw new CircularRedirectException("Circular redirect to '" + redirectURI + "'");
            }
            redirectLocations.add(redirectURI);
        }
        return uri;
    }
}
