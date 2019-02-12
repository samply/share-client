package de.samply.share.client.quality.report.file.excel.row.context;/*
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
import de.samply.share.common.utils.MdrIdDatatype;

public class ExcelRowParameters_002 {


    private MdrIdDatatype mdrId;
    private String value;
    private QualityResult qualityResult;
    private Integer mismatchOrdinal;
    private Double percentageOutOfPatientWithDataElement;
    private Double percentageOutOfTotalPatients;


    public MdrIdDatatype getMdrId() {
        return mdrId;
    }

    public void setMdrId(MdrIdDatatype mdrId) {
        this.mdrId = mdrId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public QualityResult getQualityResult() {
        return qualityResult;
    }

    public void setQualityResult(QualityResult qualityResult) {
        this.qualityResult = qualityResult;
    }

    public Integer getMismatchOrdinal() {
        return mismatchOrdinal;
    }

    public void setMismatchOrdinal(Integer mismatchOrdinal) {
        this.mismatchOrdinal = mismatchOrdinal;
    }

    public Double getPercentageOutOfPatientWithDataElement() {
        return percentageOutOfPatientWithDataElement;
    }

    public void setPercentageOutOfPatientWithDataElement(Double percentageOutOfPatientWithDataElement) {
        this.percentageOutOfPatientWithDataElement = percentageOutOfPatientWithDataElement;
    }

    public Double getPercentageOutOfTotalPatients() {
        return percentageOutOfTotalPatients;
    }

    public void setPercentageOutOfTotalPatients(Double percentageOutOfTotalPatients) {
        this.percentageOutOfTotalPatients = percentageOutOfTotalPatients;
    }
}
