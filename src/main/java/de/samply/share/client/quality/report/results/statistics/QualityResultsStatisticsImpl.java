package de.samply.share.client.quality.report.results.statistics;/*
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

import de.samply.share.client.quality.report.MdrIgnoredElements;
import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.common.utils.MdrIdDatatype;

import java.util.HashSet;
import java.util.Set;

public class QualityResultsStatisticsImpl implements QualityResultsStatistics {

    private QualityResults qualityResults;
    private MdrIgnoredElements mdrIgnoredElements;

    private ANDConditionsEvaluator andConditionsEvaluator = new ANDConditionsEvaluator();
    private ORConditionsEvaluator orConditionsEvaluator = new ORConditionsEvaluator();

    private Integer totalNumberOfPatients;


    public QualityResultsStatisticsImpl(QualityResults qualityResults, MdrIgnoredElements mdrIgnoredElements) {
        this.qualityResults = qualityResults;
        this.mdrIgnoredElements = mdrIgnoredElements;
    }


    private double getPercentage (int part, int total){
        return (total > 0) ? 100.0d * ((double) part) / ((double) total) : 0;
    }

    @Override
    public double getPercentageOf_PatientsWithValue_outOf_PatientsWithMdrId(MdrIdDatatype mdrId, String value) {

        int patientsWithValue = getPatientsWithValue(mdrId, value);
        int patientsWithMdrId = getPatientsWithMdrId(mdrId);

        return getPercentage(patientsWithValue, patientsWithMdrId);

    }

    @Override
    public double getPercentageOf_PatientsWithValue_outOf_TotalPatients(MdrIdDatatype mdrId, String value) {

        int numberOfPatientsWithValue = getPatientsWithValue(mdrId, value);
        int totalNumberOfPatients = getTotalNumberOfPatients();

        return getPercentage(numberOfPatientsWithValue, totalNumberOfPatients);
    }

    private int getPatientsWithValue (MdrIdDatatype mdrID, String value){

        QualityResult result = qualityResults.getResult(mdrID, value);
        return (result == null) ? 0 : result.getNumberOfPatients();

    }

    private interface QualityResultPatientIdsGetter {
        Set<String> getPatientIds(QualityResult qualityResult);
    }

    private int countPatients(MdrIdDatatype mdrId, QualityResultPatientIdsGetter processor){

        Set<String> patientIds = new HashSet<>();

        Set<String> values = qualityResults.getValues(mdrId);

        if (values != null){
            for (String value : values){

                QualityResult qualityResult = qualityResults.getResult(mdrId, value);
                Set<String> tempPatientIds = processor.getPatientIds(qualityResult);
                if (tempPatientIds != null){
                    patientIds.addAll(tempPatientIds);
                }

            }
        }

        return patientIds.size();

    }


    private int getPatientsWithMdrId (MdrIdDatatype mdrId){

        return countPatients(mdrId, qualityResult -> qualityResult.getPatientLocalIds());

    }



    @Override
    public double getPercentageOf_MismatchingPatientsWithValue_outOf_MismatchingPatientsWithMdrId(MdrIdDatatype mdrId, String value) {

        int mismatchingPatientsWithValue = getMismatchingPatientsWithValue(mdrId, value);
        int mismatchingPatientsWithMdrId = getMismatchingPatientsWithMdrId(mdrId);

        return getPercentage(mismatchingPatientsWithValue, mismatchingPatientsWithMdrId);

    }

    private int getMismatchingPatientsWithValue(MdrIdDatatype mdrId, String value){

        QualityResult qualityResult = qualityResults.getResult(mdrId, value);
        return (qualityResult != null && !qualityResult.isValid()) ? qualityResult.getNumberOfPatients() : 0;

    }

    private int getMismatchingPatientsWithMdrId(MdrIdDatatype mdrId){

        return countPatients(mdrId, qualityResult -> (qualityResult.isValid()) ? null : qualityResult.getPatientLocalIds());

    }

    @Override
    public double getPercentageOf_MismatchingPatientsWithMdrId_outOf_PatientsWithMdrId(MdrIdDatatype mdrId) {

        int mismatchingPatientsWithMdrId = getMismatchingPatientsWithMdrId(mdrId);
        int patientsWithMdrId = getPatientsWithMdrId(mdrId);

        return getPercentage(mismatchingPatientsWithMdrId, patientsWithMdrId);

    }



    @Override
    public double getPercentageOf_MatchingPatientsWithMdrId_outOf_PatientsWithMdrId(MdrIdDatatype mdrId) {

        int matchingPatientsWithMdrId = getMatchingPatientsWithMdrId(mdrId);
        int patientsWithMdrId = getPatientsWithMdrId(mdrId);

        return getPercentage(matchingPatientsWithMdrId, patientsWithMdrId);

    }

    private int getMatchingPatientsWithMdrId (MdrIdDatatype mdrId){

        return countPatients(mdrId, qualityResult -> (qualityResult.isValid()) ? qualityResult.getPatientLocalIds() : null);

    }

    @Override
    public int getNumberOf_MismatchingPatientsWithMdrId(MdrIdDatatype mdrId) {

        return getMismatchingPatientsWithMdrId(mdrId);

    }

    @Override
    public int getNumberOf_MatchingPatientsWithMdrId(MdrIdDatatype mdrId) {

        return getMatchingPatientsWithMdrId(mdrId);

    }

    @Override
    public int getNumberOf_PatientsForValidation(MdrIdDatatype mdrId) {

        return getMismatchingPatientsWithMdrId(mdrId);

    }

    @Override
    public int getNumberOf_PatientsWithMdrId(MdrIdDatatype mdrId) {

        return getPatientsWithMdrId(mdrId);

    }

    @Override
    public double getPercentageOf_PatientsWithMdrId_outOf_TotalPatients(MdrIdDatatype mdrId) {

        int numberOfPatientsWithMdrId = getNumberOf_PatientsWithMdrId(mdrId);
        int totalNumberOfPatients = getTotalNumberOfPatients();

        return getPercentage(numberOfPatientsWithMdrId, totalNumberOfPatients);
    }

    @Override
    public int getNumberOf_PatientsWithMatchOnlyWithMdrId(MdrIdDatatype mdrId) {

        int numberOf_patientsWithAnyMismatchWithMdrId = getNumberOf_PatientsWithAnyMismatchWithMdrId(mdrId);
        int numberOf_patientsWithMdrId = getNumberOf_PatientsWithMdrId(mdrId);

        return numberOf_patientsWithMdrId - numberOf_patientsWithAnyMismatchWithMdrId;

    }

    @Override
    public double getPercentageOf_PatientsWithMatchOnlyWithMdrId_outOf_PatientsWithMdrId(MdrIdDatatype mdrId) {

        int numberOfPatientsWithMatchOnlyWithMdrId = getNumberOf_PatientsWithMatchOnlyWithMdrId(mdrId);
        int numberOfPatientsWithMdrId = getNumberOf_PatientsWithMdrId(mdrId);

        return getPercentage(numberOfPatientsWithMatchOnlyWithMdrId, numberOfPatientsWithMdrId);

    }

    @Override
    public double getPercentageOf_PatitentsWithMatchOnlyWithMdrId_outOf_TotalPatients(MdrIdDatatype mdrId) {

        int numberOfPatientsWithMatchOnlyWithMdrId = getNumberOf_PatientsWithMatchOnlyWithMdrId(mdrId);
        int totalNumberOfPatients = getTotalNumberOfPatients();

        return getPercentage(numberOfPatientsWithMatchOnlyWithMdrId, totalNumberOfPatients);

    }

    @Override
    public int getNumberOf_PatientsWithAnyMismatchWithMdrId(MdrIdDatatype mdrId) {
        return countPatients(mdrId, qualityResult -> qualityResult.isValid() ? null : qualityResult.getPatientLocalIds());
    }

    @Override
    public double getPercentageOf_PatientsWithAnyMismatchWithMdrId_outOf_PatientsWithMdrId(MdrIdDatatype mdrId) {

        int numberOfPatientsWithAnyMismatchWithMdrId = getNumberOf_PatientsWithAnyMismatchWithMdrId(mdrId);
        int numberOfPatientsWithMdrId = getNumberOf_PatientsWithMdrId(mdrId);

        return getPercentage(numberOfPatientsWithAnyMismatchWithMdrId, numberOfPatientsWithMdrId);

    }

    @Override
    public double getPercentageOf_PatientsWithAnyMismatchWithMdrId_outOf_TotalPatients(MdrIdDatatype mdrId) {

        int numberOfPatientsWithAnyMismatchWithMdrId = getNumberOf_PatientsWithAnyMismatchWithMdrId(mdrId);
        int totalNumberOfPatients = getTotalNumberOfPatients();

        return getPercentage(numberOfPatientsWithAnyMismatchWithMdrId, totalNumberOfPatients);

    }

    @Override
    public double getPercentageOf_CompletelyMatchingDataelements_outOf_AllDataelements() {

        int matchingDataelements = getCompletelyMatchingDataelements();
        int allDataelements = getAllDataElements();

        return getPercentage(matchingDataelements, allDataelements);

    }


    private interface QualityResultConditionEvaluator{
        boolean fullfillsCondition (QualityResult qualityResult);
    }

    private interface ConditionsEvaluator{
        boolean evaluate (boolean [] conditions);
    }

    private int countAllDateElements (QualityResultConditionEvaluator conditionEvaluator, ConditionsEvaluator conditionsEvaluator){

        int result = 0;

        Set<MdrIdDatatype> mdrIds = qualityResults.getMdrIds();
        for (MdrIdDatatype mdrId : mdrIds){

            Set<String> values = qualityResults.getValues(mdrId);

            boolean[] conditions = new boolean[values.size()];
            int i = 0;
            for (String value : values){

                QualityResult qualityResult = qualityResults.getResult(mdrId, value);
                conditions[i] = conditionEvaluator.fullfillsCondition(qualityResult);
                i++;

            }

            if (conditionsEvaluator.evaluate(conditions)){
                result++;
            }

        }

        return result;

    }

    private class ANDConditionsEvaluator implements ConditionsEvaluator{

        @Override
        public boolean evaluate(boolean[] conditions) {

            boolean result = conditions[0];

            for (boolean condition : conditions){
                result &= condition;
            }

            return result;
        }

    }

    private class ORConditionsEvaluator implements ConditionsEvaluator{

        @Override
        public boolean evaluate(boolean[] conditions) {

            boolean result = conditions[0];

            for (boolean condition : conditions){
                result |= condition;
            }

            return result;

        }

    }

    private int getMatchingDataElements (){

        return countAllDateElements(qualityResult -> qualityResult.isValid(), andConditionsEvaluator);

    }

    private int getAllDataElements(){

        return countAllDateElements(qualityResult -> true, andConditionsEvaluator);

    }

    @Override
    public double getPercentageOf_NotCompletelyMismatchingDataelements_outOf_AllDataelements() {

        int notCompletelyMismatchingDataelements = getNotCompletelyMismatchingDataelements();
        int allDataelements = getAllDataElements();

        return getPercentage(notCompletelyMismatchingDataelements, allDataelements);

    }


    private int getNotCompletelyMismatchingDataelements (){

        return countAllDateElements(qualityResult -> !qualityResult.isValid(), orConditionsEvaluator);

    }


    @Override
    public double getPercentageOf_CompletelyMismatchingDataelements_outOf_AllDataelements() {

        int completelyMismatchingDataelements = getCompletelyMismatchingDataelements();
        int allDataelements = getAllDataElements();

        return getPercentage(completelyMismatchingDataelements, allDataelements);

    }

    private int getCompletelyMismatchingDataelements (){

        return countAllDateElements(qualityResult -> !qualityResult.isValid(), andConditionsEvaluator);

    }

    private int getCompletelyMatchingDataelements (){

        return countAllDateElements(qualityResult -> qualityResult.isValid(), andConditionsEvaluator);

    }

    @Override
    public double getPercentageOf_NotMappedDataelements_outOf_AllDataelements() {

        int notMappedDataelements = getNotMappedDataelements();
        int allDataelements = getAllDataElements();

        return getPercentage(notMappedDataelements, allDataelements);

    }

    private int getNotMappedDataelements (){

        int result = 0;

        for (MdrIdDatatype mdrId : mdrIgnoredElements){
            result++;
        }

        return result;

    }

    @Override
    public int getTotalNumberOfPatients(){

        if (totalNumberOfPatients == null) {

            Set<String> patientIds = new HashSet<>();

            for (MdrIdDatatype mdrId : qualityResults.getMdrIds()) {
                for (String value : qualityResults.getValues(mdrId)) {

                    QualityResult result = qualityResults.getResult(mdrId, value);
                    patientIds.addAll(result.getPatientLocalIds());

                }
            }

            totalNumberOfPatients = patientIds.size();

        }

        return totalNumberOfPatients;

    }


    @Override
    public double getPercentageOfPatientsOutOfTotalNumberOfPatientsForADataelement (MdrIdDatatype mdrId){

        int numberOf_patientsForId = getNumberOf_PatientsWithMdrId(mdrId);
        int totalNumberOfPatients = getTotalNumberOfPatients();

        return getPercentage(numberOf_patientsForId, totalNumberOfPatients);

    }



}
