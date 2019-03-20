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

import de.samply.share.common.utils.MdrIdDatatype;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CxxMappingElement {

    private MdrIdDatatype mdrId;
    private String cxxName;
    private String teilerBaseViewColumn;
    private Map<String, Set<String>> mdrValue_cxxValue_Map = new HashMap<>();

    public MdrIdDatatype getMdrId() {
        return mdrId;
    }

    public void setMdrId(MdrIdDatatype mdrIdDatatype) {
        this.mdrId = mdrIdDatatype;
    }

    public String getCxxName() {
        return cxxName;
    }

    public void setCxxName(String cxxName) {
        this.cxxName = cxxName;
    }

    public String getTeilerBaseViewColumn() {
        return teilerBaseViewColumn;
    }

    public void setTeilerBaseViewColumn(String teilerBaseViewColumn) {
        this.teilerBaseViewColumn = teilerBaseViewColumn;
    }


    public Set<String> getCxxValues (String mdrValue){
        return mdrValue_cxxValue_Map.get(mdrValue);
    }

    public void addValue (String mdrValue, String cxxValue){

        Set<String> cxxValues = mdrValue_cxxValue_Map.get(mdrValue);

        if (cxxValues == null){

            cxxValues = new HashSet<>();
            mdrValue_cxxValue_Map.put(mdrValue,cxxValues);

        }

        cxxValues.add(cxxValue);

    }

}
