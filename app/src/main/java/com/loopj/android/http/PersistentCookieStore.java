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
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.cookie.Cookie;

public class PersistentCookieStore
        implements CookieStore
{
    private static final String LOG_TAG = "PersistentCookieStore";
    private static final String COOKIE_PREFS = "CookiePrefsFile";
    private static final String COOKIE_NAME_STORE = "names";
    private static final String COOKIE_NAME_PREFIX = "cookie_";
    private final ConcurrentHashMap<String, Cookie> cookies;
    private final SharedPreferences cookiePrefs;
    private boolean omitNonPersistentCookies = false;

    public PersistentCookieStore(Context context)
    {
        this.cookiePrefs = context.getSharedPreferences("CookiePrefsFile", 0);
        this.cookies = new ConcurrentHashMap();

        String storedCookieNames = this.cookiePrefs.getString("names", null);
        if (storedCookieNames != null)
        {
            String[] cookieNames = TextUtils.split(storedCookieNames, ",");
            for (String name : cookieNames)
            {
                String encodedCookie = this.cookiePrefs.getString("cookie_" + name, null);
                if (encodedCookie != null)
                {
                    Cookie decodedCookie = decodeCookie(encodedCookie);
                    if (decodedCookie != null) {
                        this.cookies.put(name, decodedCookie);
                    }
                }
            }
            clearExpired(new Date());
        }
    }

    public void addCookie(Cookie cookie)
    {
        if ((this.omitNonPersistentCookies) && (!cookie.isPersistent())) {
            return;
        }
        String name = cookie.getName() + cookie.getDomain();
        if (!cookie.isExpired(new Date())) {
            this.cookies.put(name, cookie);
        } else {
            this.cookies.remove(name);
        }
        SharedPreferences.Editor prefsWriter = this.cookiePrefs.edit();
        prefsWriter.putString("names", TextUtils.join(",", this.cookies.keySet()));
        prefsWriter.putString("cookie_" + name, encodeCookie(new SerializableCookie(cookie)));
        prefsWriter.commit();
    }

    public void clear()
    {
        SharedPreferences.Editor prefsWriter = this.cookiePrefs.edit();
        for (String name : this.cookies.keySet()) {
            prefsWriter.remove("cookie_" + name);
        }
        prefsWriter.remove("names");
        prefsWriter.commit();

        this.cookies.clear();
    }

    public boolean clearExpired(Date date)
    {
        boolean clearedAny = false;
        SharedPreferences.Editor prefsWriter = this.cookiePrefs.edit();
        for (Map.Entry<String, Cookie> entry : this.cookies.entrySet())
        {
            String name = (String)entry.getKey();
            Cookie cookie = (Cookie)entry.getValue();
            if (cookie.isExpired(date))
            {
                this.cookies.remove(name);

                prefsWriter.remove("cookie_" + name);

                clearedAny = true;
            }
        }
        if (clearedAny) {
            prefsWriter.putString("names", TextUtils.join(",", this.cookies.keySet()));
        }
        prefsWriter.commit();

        return clearedAny;
    }

    public List<Cookie> getCookies()
    {
        return new ArrayList(this.cookies.values());
    }

    public void setOmitNonPersistentCookies(boolean omitNonPersistentCookies)
    {
        this.omitNonPersistentCookies = omitNonPersistentCookies;
    }

    public void deleteCookie(Cookie cookie)
    {
        String name = cookie.getName() + cookie.getDomain();
        this.cookies.remove(name);
        SharedPreferences.Editor prefsWriter = this.cookiePrefs.edit();
        prefsWriter.remove("cookie_" + name);
        prefsWriter.commit();
    }

    protected String encodeCookie(SerializableCookie cookie)
    {
        if (cookie == null) {
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try
        {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookie);
        }
        catch (IOException e)
        {
            AsyncHttpClient.log.d("PersistentCookieStore", "IOException in encodeCookie", e);
            return null;
        }
        return byteArrayToHexString(os.toByteArray());
    }

    protected Cookie decodeCookie(String cookieString)
    {
        byte[] bytes = hexStringToByteArray(cookieString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Cookie cookie = null;
        try
        {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            cookie = ((SerializableCookie)objectInputStream.readObject()).getCookie();
        }
        catch (IOException e)
        {
            AsyncHttpClient.log.d("PersistentCookieStore", "IOException in decodeCookie", e);
        }
        catch (ClassNotFoundException e)
        {
            AsyncHttpClient.log.d("PersistentCookieStore", "ClassNotFoundException in decodeCookie", e);
        }
        return cookie;
    }

    protected String byteArrayToHexString(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte element : bytes)
        {
            int v = element & 0xFF;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.US);
    }

    protected byte[] hexStringToByteArray(String hexString)
    {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[(i / 2)] = ((byte)((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16)));
        }
        return data;
    }
}
