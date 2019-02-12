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



import de.dth.mdr.validator.MDRValidator;
import de.dth.mdr.validator.exception.ValidatorException;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.common.utils.QueryValidator;


public class QualityResultsValidator {



    private QueryValidator queryValidator;
    private MDRValidator dthValidator;


    public QualityResultsValidator(MDRValidator dthValidator, QueryValidator queryValidator) {
        this.dthValidator = dthValidator;
        this.queryValidator = queryValidator;
    }

    public QualityResults validate (QualityResults qualityResults) throws QualityResultsValidatorException {

        if (qualityResults != null){

            for (MdrIdDatatype mdrId : qualityResults.getMdrIds()){

                for (String value : qualityResults.getValues(mdrId)){

                    qualityResults = validate(qualityResults, mdrId, value);

                }

            }

        }

        return qualityResults;
    }

    private QualityResults validate (QualityResults qualityResults, MdrIdDatatype mdrId, String value) throws QualityResultsValidatorException {

        value = correctDate(qualityResults, mdrId, value);

        if (isValid (mdrId, value)){
            qualityResults.setAsValid(mdrId, value);
        }

        return qualityResults;

    }

    private String correctDate(QualityResults qualityResults, MdrIdDatatype mdrId, String value){

        String newValue = getDateValueInSlotFormat(mdrId, value);

        if (!value.equals(newValue)){
            qualityResults.updateValue(mdrId, value, newValue);
            value = newValue;
        }

        return value;

    }

    private String getDateValueInSlotFormat(MdrIdDatatype mdrId, String value){

        try {

            return queryValidator.getDateValueInElementValidationFormatWithSourceFormatAutodiscovering(mdrId.toString(), value);

        } catch (Exception e) {
            return value;
        }

    }

    private boolean isValid (MdrIdDatatype mdrId, String value) throws QualityResultsValidatorException {

        try {

            return (value != null) && dthValidator.validate(getMdrKey(mdrId), value);

        } catch (ValidatorException e) {
            throw new QualityResultsValidatorException(e);
        }

    }

    private String getMdrKey (MdrIdDatatype mdrId){
        return mdrId.toString();
    }

}
