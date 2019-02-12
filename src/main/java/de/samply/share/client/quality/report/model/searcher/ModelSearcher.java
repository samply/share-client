package de.samply.share.client.quality.report.model.searcher;/*
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

import de.samply.common.mdrclient.domain.Validations;
import de.samply.share.client.quality.report.MdrIdAndValidations;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.common.utils.MdrIdDatatype;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelSearcher {

    Map<String, Validations> mdrIdAndValidationsMap = new HashMap<>();

    public ModelSearcher(Model model) {

        List<MdrIdAndValidations> mdrIdAndValidationsList = model.getMdrIdAndValidations();

        for (MdrIdAndValidations mdrIdAndValidations : mdrIdAndValidationsList){
            String key = getKey(mdrIdAndValidations.getMdrId());
            if (key != null) {
                mdrIdAndValidationsMap.put(key, mdrIdAndValidations.getValidations());
            }
        }

    }

    public Validations getValidations (MdrIdDatatype mdrId){

        String key = getKey(mdrId);
        return mdrIdAndValidationsMap.get(key);

    }

    // Only the most recently version of the MDR element will be considered
    private String getKey (MdrIdDatatype mdrId){
        return (mdrId == null)? null : mdrId.getLatestCentraxx();
    }

}
