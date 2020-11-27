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

import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;

import java.util.Set;

public class QualityResultsFilter implements QualityResults {

    protected QualityResults qualityResults;


    public QualityResultsFilter(QualityResults qualityResults) {
        this.qualityResults = qualityResults;
    }

    @Override
    public QualityResult getResult(MdrIdDatatype mdrId, String value) {
        return qualityResults.getResult(mdrId, value);
    }

    @Override
    public void put(MdrIdDatatype mdrId, String value, QualityResult result) {
        qualityResults.put(mdrId, value, result);
    }

    @Override
    public Set<String> getValues(MdrIdDatatype mdrId) {
        return qualityResults.getValues(mdrId);
    }

    @Override
    public Set<MdrIdDatatype> getMdrIds() {
        return qualityResults.getMdrIds();
    }

    @Override
    public void setAsValid(MdrIdDatatype mdrId, String value) {
        qualityResults.setAsValid(mdrId,value);
    }

    @Override
    public void addPatientLocalId(MdrIdDatatype mdrId, String value, String patientLocalId) {
        qualityResults.addPatientLocalId(mdrId, value, patientLocalId);
    }

    @Override
    public void addPatientDktkId(MdrIdDatatype mdrId, String value, String patientDktkId) {
        qualityResults.addPatientDktkId(mdrId, value, patientDktkId);
    }

    @Override
    public void addPatientLocalIds(MdrIdDatatype mdrId, String value, Set<String> patientLocalIds) {
        qualityResults.addPatientLocalIds(mdrId, value, patientLocalIds);
    }

    @Override
    public void addPatientDktkIds(MdrIdDatatype mdrId, String value, Set<String> patientDktkIds) {
        qualityResults.addPatientDktkIds(mdrId, value, patientDktkIds);
    }

    @Override
    public void updateValue(MdrIdDatatype mdrId, String oldValue, String newValue) {
        qualityResults.updateValue(mdrId, oldValue, newValue);
    }

}
