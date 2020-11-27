package de.samply.share.client.quality.report.results.filter;/*
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

import de.samply.share.client.quality.report.results.QualityResult;

import java.util.*;

public class ValidValueFilterQualityResults {

    private Set<String> validValues = new HashSet<>();
    private QualityResult validQualityResult = new QualityResult();
    private Map<String, QualityResult> invalidQualityResults = new HashMap<>();

    public void addValueAndQualityResult (String value, QualityResult qualityResult){

        if (qualityResult.isValid()){
            addValidValueAndQualityResult(value, qualityResult);
        } else{
            addInvalidValueAndQualityResult(value, qualityResult);
        }

    }

    private void addValidValueAndQualityResult (String value, QualityResult qualityResult){

        validValues.add(value);

        validQualityResult.setValid(true);

        //int numberOfPatients = validQualityResult.getNumberOfPatients();
        //numberOfPatients += qualityResult.getNumberOfPatients();
        //validQualityResult.setNumberOfPatients(numberOfPatients);
        validQualityResult.getPatientLocalIds().addAll(qualityResult.getPatientLocalIds());
        validQualityResult.getPatientDktkIds().addAll(qualityResult.getPatientDktkIds());

    }

    private void addInvalidValueAndQualityResult (String value, QualityResult qualityResult){
        invalidQualityResults.put(value, qualityResult);
    }

    public QualityResult getResult (String value){
        return (validValues.contains(value)) ? validQualityResult : invalidQualityResults.get(value);
    }

    public Set<String> getValues(){

        Set<String> values = new HashSet<>(invalidQualityResults.keySet());
        if (validValues.size() > 0){

            Iterator<String> iterator = validValues.iterator();
            String value = iterator.next();

            while (value.length() == 0 && iterator.hasNext()){
                value = iterator.next();
            }

            values.add(value);
        }

        return values;

    }

}
