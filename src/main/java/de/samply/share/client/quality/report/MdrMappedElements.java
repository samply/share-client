package de.samply.share.client.quality.report;/*
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

import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.connector.LdmConnectorCentraxx;
import de.samply.share.client.util.connector.centraxx.CxxMappingElement;
import de.samply.share.common.utils.MdrIdDatatype;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MdrMappedElements {

    private Map<MdrIdDatatype, CxxMappingElement> mdrId_cxxMappingElement_Map = new HashMap<>();

    public MdrMappedElements(LdmConnector ldmConnector) {

        addMappedElements(ldmConnector);

    }

    private void addMappedElements(LdmConnector<?, ?, ?> ldmConnector) {

        if (ldmConnector.isLdmCentraxx()) {

            List<CxxMappingElement> mapping = ((LdmConnectorCentraxx)ldmConnector).getMapping();

            for (CxxMappingElement mappingElement : mapping) {

                MdrIdDatatype mdrId = mappingElement.getMdrId();
                MdrIdDatatype basicMdrId = getBasicMdrIdDataType(mdrId);

                mdrId_cxxMappingElement_Map.put(basicMdrId, mappingElement);

            }

        }

    }

    private MdrIdDatatype getBasicMdrIdDataType(MdrIdDatatype mdrIdDatatype){
        return new MdrIdDatatype(mdrIdDatatype.getMajor());
    }

    public boolean isMapped (MdrIdDatatype mdrId){

        boolean isMapped = true;

        if (mdrId_cxxMappingElement_Map.size() > 0){

            MdrIdDatatype basicMdrId = getBasicMdrIdDataType(mdrId);
            isMapped = mdrId_cxxMappingElement_Map.containsKey(basicMdrId);

        }

        return isMapped;

    }

    public boolean isMapped (String mdrId){
        return isMapped(new MdrIdDatatype(mdrId));
    }

    public CxxMappingElement getCxxMappingElement (MdrIdDatatype mdrId){

        MdrIdDatatype basicMdrId = getBasicMdrIdDataType(mdrId);
        return mdrId_cxxMappingElement_Map.get(basicMdrId);

    }

}
