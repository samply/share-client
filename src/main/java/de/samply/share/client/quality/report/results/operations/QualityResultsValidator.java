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
import de.samply.share.client.quality.report.logger.PercentageLogger;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.common.utils.QueryValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class QualityResultsValidator {


    private static final Logger logger = LogManager.getLogger(QualityResultsValidator.class);

    private QueryValidator queryValidator;
    private MDRValidator mdrValidator;


    public QualityResultsValidator(MDRValidator dthValidator, QueryValidator queryValidator) {
        this.mdrValidator = dthValidator;
        this.queryValidator = queryValidator;
    }

    public QualityResults validate (QualityResults qualityResults) throws QualityResultsValidatorException {

        if (qualityResults != null){

            int numberOfQualityResults = getNumberOfQualityResults(qualityResults);
            PercentageLogger percentageLogger = new PercentageLogger(logger, numberOfQualityResults, "validating quality results...");
            for (MdrIdDatatype mdrId : qualityResults.getMdrIds()){

                logger.debug("validating element  "+mdrId );
                for (String value : qualityResults.getValues(mdrId)){

                    qualityResults = validate(qualityResults, mdrId, value);
                    percentageLogger.incrementCounter();

                }

            }

        }

        return qualityResults;
    }

    private int getNumberOfQualityResults(QualityResults qualityResults){

        int counter = 0;

        for (MdrIdDatatype mdrId : qualityResults.getMdrIds()){
            counter += qualityResults.getValues(mdrId).size();
        }

        return counter;

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

            return (value != null) && mdrValidator.validate(mdrId.toString(), value);

        } catch (ValidatorException e) {
             //throw new QualityResultsValidatorException(e);
            logger.debug(mdrId+":"+value);
            logger.debug(e);

            return false;
            //return isValid_LastMdrId(mdrId, value);
        }

    }

    private boolean isValid_LastMdrId (MdrIdDatatype mdrId, String value) throws QualityResultsValidatorException {

        try {
            return (value != null) && mdrValidator.validate(mdrId.getLatestMdr(), value);

        } catch (ValidatorException e) {
            throw new QualityResultsValidatorException(e);
        }
    }


}
