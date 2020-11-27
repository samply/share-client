package de.samply.share.client.quality.report.results.operations;/*
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

import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.model.ccp.Attribute;
import de.samply.share.model.ccp.Container;
import de.samply.share.model.ccp.Patient;
import de.samply.share.model.ccp.QueryResult;
import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.QualityResultsImpl;

import java.util.List;
import java.util.Set;

public class QualityResultsAnalyzer {




    public QualityResults analyze (QualityResults qualityResults, QueryResult queryResult ){



        if (queryResult != null){
            for (Patient patient : queryResult.getPatient()){

                QualityResults temporaryQualityResults = new QualityResultsImpl();
                analyze(patient, temporaryQualityResults);
                qualityResults = updateQualityResults(temporaryQualityResults, qualityResults);

            }
        }

        return qualityResults;

    }

    private QualityResults updateQualityResults (QualityResults temporaryQualityResults, QualityResults finalQualityResults){

        for (MdrIdDatatype mdrId : temporaryQualityResults.getMdrIds()){

            for (String value : temporaryQualityResults.getValues(mdrId)){

                QualityResult temporaryQualityResult = temporaryQualityResults.getResult(mdrId, value);
                updateQualityResults(mdrId, value, temporaryQualityResult, finalQualityResults);

            }
        }

        return finalQualityResults;

    }

    private void updateQualityResults (MdrIdDatatype mdrId, String value, QualityResult temporaryQualityResult, QualityResults finalQualityResults){

        Set<String> patientLocalIds = temporaryQualityResult.getPatientLocalIds();
        Set<String> patientDktkIds = temporaryQualityResult.getPatientDktkIds();

        finalQualityResults.addPatientLocalIds(mdrId, value, patientLocalIds);
        finalQualityResults.addPatientDktkIds(mdrId, value, patientDktkIds);


    }

    private void analyze (Patient patient, QualityResults qualityResults){
        analyze(patient, patient.getAttribute(), patient.getContainer(), qualityResults);
    }

    private void analyze (Patient patient, Container container, QualityResults qualityResults){
        analyze(patient, container.getAttribute(), container.getContainer(), qualityResults);
    }

    private void analyze (Patient patient, List<Attribute> attributeList, List<Container> containerList, QualityResults qualityResults){

        for (Attribute attribute : attributeList){
            analyze(patient, attribute, qualityResults);
        }

        for (Container container : containerList){
            analyze(patient, container, qualityResults);
        }

    }

    private void analyze (Patient patient, Attribute attribute, QualityResults qualityResults){

        MdrIdDatatype mdrId = new MdrIdDatatype(attribute.getMdrKey());
        String value = attribute.getValue().getValue();

        qualityResults.addPatientLocalId(mdrId, value, patient.getId());
        qualityResults.addPatientDktkId(mdrId, value, patient.getDktkId());

    }





}
