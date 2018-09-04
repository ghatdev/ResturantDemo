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


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;

public class SerializableCookie
        implements Serializable
{
    private static final long serialVersionUID = 6374381828722046732L;
    private final transient Cookie cookie;
    private transient BasicClientCookie clientCookie;

    public SerializableCookie(Cookie cookie)
    {
        this.cookie = cookie;
    }

    public Cookie getCookie()
    {
        Cookie bestCookie = this.cookie;
        if (this.clientCookie != null) {
            bestCookie = this.clientCookie;
        }
        return bestCookie;
    }

    private void writeObject(ObjectOutputStream out)
            throws IOException
    {
        out.writeObject(this.cookie.getName());
        out.writeObject(this.cookie.getValue());
        out.writeObject(this.cookie.getComment());
        out.writeObject(this.cookie.getDomain());
        out.writeObject(this.cookie.getExpiryDate());
        out.writeObject(this.cookie.getPath());
        out.writeInt(this.cookie.getVersion());
        out.writeBoolean(this.cookie.isSecure());
    }

    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException
    {
        String key = (String)in.readObject();
        String value = (String)in.readObject();
        this.clientCookie = new BasicClientCookie(key, value);
        this.clientCookie.setComment((String)in.readObject());
        this.clientCookie.setDomain((String)in.readObject());
        this.clientCookie.setExpiryDate((Date)in.readObject());
        this.clientCookie.setPath((String)in.readObject());
        this.clientCookie.setVersion(in.readInt());
        this.clientCookie.setSecure(in.readBoolean());
    }
}
