package de.samply.share.client.quality.report.localdatamanagement;/*
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

import de.samply.share.common.utils.SamplyShareUtils;

import java.util.HashMap;
import java.util.Map;

public class MyUri{

    private String basicUrl;
    private String urlSuffix;


    private Map<String, String> parameters = new HashMap<>();


    public MyUri (String basicUrl, String urlSuffix){
        this.basicUrl = basicUrl;
        this.urlSuffix = urlSuffix;
    }

    public void setBasicUrl(String basicUrl) {
        this.basicUrl = basicUrl;
    }

    public void setUrlSuffix(String urlSuffix) {
        this.urlSuffix = urlSuffix;
    }

    public void addParameter (String key, String value){
        parameters.put(key, value);
    }

    public String toString(){

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(SamplyShareUtils.addTrailingSlash(basicUrl));
        if (urlSuffix != null) {
            stringBuilder.append(urlSuffix);
        }

        if (parameters.size() > 0){

            stringBuilder.append('?');

            boolean isFirstElement = true;
            for (Map.Entry<String, String> parameter : parameters.entrySet()){

                if (!isFirstElement){
                    stringBuilder.append(';');
                }

                stringBuilder.append(parameter.getKey())
                        .append("=")
                        .append(parameter.getValue());
                isFirstElement = false;

            }
        }

        return stringBuilder.toString();
    }

}
