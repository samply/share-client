package de.samply.share.client.util.connector.centraxx;/*
 * Copyright (C) 2019 Medizinische Informatik in der Translationalen Onkologie,
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.samply.share.common.utils.MdrIdDatatype;

import java.util.ArrayList;
import java.util.List;

public class CxxMappingParser {



    public List<CxxMappingElement> parse(String httpEntity){

        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = jsonParser.parse(httpEntity).getAsJsonArray();

        return parse (jsonArray);

    }

    private List<CxxMappingElement> parse (JsonArray jsonArray){

        List<CxxMappingElement> cxxMappingElementList = new ArrayList<>();

        if (jsonArray != null){

            for (JsonElement jsonElement : jsonArray){

                CxxMappingElement cxxMappingElement = getCxxMappingElement(jsonElement);
                if (cxxMappingElement != null){
                    cxxMappingElementList.add(cxxMappingElement);
                }

            }

        }

        return cxxMappingElementList;

    }

    private CxxMappingElement getCxxMappingElement (JsonElement jsonElement){

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        CxxMappingElement cxxMappingElement = new CxxMappingElement();

        String cxxName = getStringOfJsonObject(jsonObject, CxxConstants.CXX_NAME);
        String teilerBaseViewColumn = getStringOfJsonObject(jsonObject, CxxConstants.TEILER_BASE_VIEW_COLUMN);
        MdrIdDatatype mdrId = getMdrId(jsonObject);

        cxxMappingElement.setMdrName(cxxName);
        cxxMappingElement.setTeilerBaseViewColumn(teilerBaseViewColumn);
        cxxMappingElement.setMdrId(mdrId);

        cxxMappingElement = addMdrRepresentations(cxxMappingElement, jsonObject);



        return cxxMappingElement;

    }

    private CxxMappingElement addMdrRepresentations (CxxMappingElement cxxMappingElement, JsonObject jsonObject){

        JsonElement jsonElementMdrRepresentations = jsonObject.get(CxxConstants.MDR_REPRESENTATIONS);

        if (jsonElementMdrRepresentations != null){

            JsonArray jsonArrayMdrRepresentations = jsonElementMdrRepresentations.getAsJsonArray();

            for ( JsonElement jsonElementMdrRepresentation : jsonArrayMdrRepresentations){

                JsonObject jsonObjectMdrRepresentation = jsonElementMdrRepresentation.getAsJsonObject();
                String mdrValue = getStringOfJsonObject(jsonObjectMdrRepresentation, CxxConstants.MDR_PERMITTED_VALUE);

                JsonElement jsonElementCxxRepresentations = jsonObjectMdrRepresentation.get(CxxConstants.CXX_REPRESENTATIONS);
                JsonArray jsonArrayCxxRepresentations = jsonElementCxxRepresentations.getAsJsonArray();

                for (JsonElement jsonElementCxxRepresentation : jsonArrayCxxRepresentations){

                    JsonObject jsonObjectCxxRepresentation = jsonElementCxxRepresentation.getAsJsonObject();
                    String cxxValue = getStringOfJsonObject(jsonObjectCxxRepresentation, CxxConstants.CXX_VALUE_NAME);

                    cxxMappingElement.addValue(mdrValue, cxxValue);

                }

            }
        }

        return cxxMappingElement;
    }

    private MdrIdDatatype getMdrId(JsonObject jsonObject){

        String urnNamespace = getStringOfJsonObject(jsonObject, CxxConstants.URN_NAMESPACE);
        String urnElementId = getStringOfJsonObject(jsonObject, CxxConstants.URN_ELEMENT_ID);
        String urnRevision = getStringOfJsonObject(jsonObject, CxxConstants.URN_REVISION);

        MdrIdDatatype mdrId = null;

        if (urnNamespace != null && urnElementId != null) {

            mdrId = new MdrIdDatatype(urnNamespace + ":" + urnElementId);

            if (urnRevision != null) {
                mdrId.setVersion(urnRevision);
            }

        }

        return mdrId;

    }


    private String getStringOfJsonObject (JsonObject jsonObject, String jsonObjectName){

        JsonElement jsonElement = jsonObject.get(jsonObjectName);
        return (jsonElement != null) ? jsonElement.getAsString() : null;

    }

}
