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
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;


public class FileDownloaderImpl implements FileDownloader{

    private String sourceUrl;
    private String destinationFilePath;


    FileDownloaderImpl() {
    }

    public FileDownloaderImpl(String sourceUrl, String destinationFilePath) {
        this.sourceUrl = sourceUrl;
        this.destinationFilePath = destinationFilePath;
    }

    @Override
    public void download() throws FileDownloaderException {

        try {

            downloadWithoutExceptionManagement();

        } catch (IOException | URISyntaxException e) {
            throw new FileDownloaderException(e);
        }

    }

    private void downloadWithoutExceptionManagement() throws IOException, URISyntaxException {

        URL sourceUrl = new URL (this.sourceUrl);
        download(sourceUrl);

    }



    private void download (URL sourceUrl) throws IOException, URISyntaxException {

        CloseableHttpClient httpClient = getHttpClient(sourceUrl);
        download(httpClient, sourceUrl);

    }

    private CloseableHttpClient getHttpClient (URL url){

        HttpConnector httpConnector = ApplicationBean.createHttpConnector();
        return httpConnector.getHttpClient(url);

    }

    private void download (CloseableHttpClient httpClient, URL url) throws IOException, URISyntaxException {

        try (CloseableHttpResponse httpResponse = getHttpResponse(httpClient, url)){
            download(httpResponse.getEntity().getContent());
        }

    }

    private CloseableHttpResponse getHttpResponse (CloseableHttpClient httpClient, URL url) throws URISyntaxException, IOException {

        HttpGet httpGet = new HttpGet(url.toURI());
        return httpClient.execute(httpGet);

    }

    private void download (InputStream inputStream) throws IOException {

        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destinationFilePath))){

            byte[] chunk = new byte[1024];
            int chunkSize;
            while ((chunkSize = inputStream.read(chunk)) != -1){
                outputStream.write(chunk, 0, chunkSize);
            }

            outputStream.flush();

        }

    }

    void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    void setDestinationFilePath(String destinationFilePath) {
        this.destinationFilePath = destinationFilePath;
    }



}
