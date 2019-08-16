package de.samply.share.client.quality.report.dktk;/*
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

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.Slot;
import de.samply.share.common.utils.MdrIdDatatype;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DktkId_MdrId_ConverterImpl implements DktkId_MdrId_Converter {

    private final static String DKTK_ID = "DKTK_ID";
    private final static String ADT_ID = "ADT_ID";

    private MdrClient mdrClient;
    private Map<MdrIdDatatype, String> dktkIds = new HashMap<>();


    public DktkId_MdrId_ConverterImpl(MdrClient mdrClient) {
        this.mdrClient = mdrClient;
    }

    @Override
    public String getDktkId (MdrIdDatatype mdrId){

        String dktkId = dktkIds.get(mdrId);
        if (dktkId == null){

            dktkId = requestDktkId(mdrId);

            if (dktkId == null){
                dktkId = mdrId.getNamespace()+"-"+mdrId.getId();
            }
            dktkIds.put(mdrId, dktkId);

        }

        return dktkId;

    }

    private String requestDktkId (MdrIdDatatype mdrId){

        List<Slot> slots = getSlots(mdrId);
        return getDktkId(slots);

    }

    private String getDktkId (List<Slot> slots){

        if (slots != null) {
            for (Slot slot : slots) {

                if (isSlotOfType(slot, DKTK_ID) || isSlotOfType(slot, ADT_ID)) {
                    return slot.getSlotValue();
                }

            }
        }

        return null;

    }

    private boolean isSlotOfType (Slot slot, String slotType){
        return slot.getSlotName() != null && slot.getSlotName().replaceAll("\\s+","").equals(slotType);
    }

    private List<Slot> getSlots (MdrIdDatatype mdrId){

        try {
            return mdrClient.getDataElementSlots(mdrId.toString());
        } catch (MdrConnectionException | ExecutionException | MdrInvalidResponseException e) {
            return null;
        }
    }

}
