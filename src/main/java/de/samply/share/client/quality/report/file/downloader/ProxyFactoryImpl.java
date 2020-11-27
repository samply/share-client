package de.samply.share.client.quality.report.file.downloader;/*
* Copyright (C) 2017 Medizinische Informatik in der Translationalen Onkologie,
* Deutsches Krebsforschungszentrum in Heidelberg
*
* This program is free software; you can redistribute it and/or modify it under
* the terms of the GNU Affero General Public License as published by the Free
* Software Foundation; either version 3 of the License, or (at your option) any
* later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program; if not, see http://www.gnu.org/licenses.
*
* Additional permission under GNU GPL version 3 section 7:
*
* If you modify this Program, or any covered work, by linking or combining it
* with Jersey (https://jersey.java.net) (or a modified version of that
* library), containing parts covered by the terms of the General Public
* License, version 2.0, the licensors of this Program grant you additional
* permission to convey the resulting work.
*/

import de.samply.common.http.HttpConnector;
import de.samply.share.client.control.ApplicationBean;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;

public class ProxyFactoryImpl implements ProxyFactory {

    @Override
    public Proxy getProxy() {
//
//        de.samply.common.config.Proxy proxy = ApplicationBean.getConfiguration().getProxy();
//
//        HttpConnector httpConnector = ApplicationBean.getHttpConnector();
//        URL url = null;
//        HttpGet httpGet = new HttpGet(uri);
//        try {
//            CloseableHttpResponse execute = httpConnector.getHttpClient(url).execute(httpGet);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    private Proxy convert (de.samply.common.config.Proxy proxy){


//    String hostname = proxy.getHTTP().getUrl().getHost();
//    int port = proxy.getHTTP().getUrl().getPort();
//
//    SocketAddress socketAdress = new InetSocketAddress(hostname, port);
//    Proxy myProxy = new Proxy(Proxy.Type.DIRECT, socketAdress);
//myProxy.


//        return myProxy;
return null;
    }

}
