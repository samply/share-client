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

import de.samply.share.common.utils.MdrIdDatatype;

public interface QualityResultsStatistics {


    //1
    public double getPercentageOf_PatientsWithValue_outOf_PatientsWithMdrId(MdrIdDatatype mdrId, String value);

    //2
    public double getPercentageOf_PatientsWithValue_outOf_TotalPatients(MdrIdDatatype mdrId, String value);

    public double getPercentageOf_MismatchingPatientsWithValue_outOf_MismatchingPatientsWithMdrId (MdrIdDatatype mdrId, String value);



    public double getPercentageOf_MismatchingPatientsWithMdrId_outOf_PatientsWithMdrId (MdrIdDatatype mdrId);

    public double getPercentageOf_MatchingPatientsWithMdrId_outOf_PatientsWithMdrId (MdrIdDatatype mdrId);

    public int getNumberOf_MismatchingPatientsWithMdrId (MdrIdDatatype mdrId);

    public int getNumberOf_MatchingPatientsWithMdrId (MdrIdDatatype mdrId);

    public int getNumberOf_PatientsForValidation(MdrIdDatatype mdrId);

    // 3
    public int getNumberOf_PatientsWithMdrId(MdrIdDatatype mdrId);

    //4
    public double getPercentageOf_PatientsWithMdrId_outOf_TotalPatients(MdrIdDatatype mdrId);

    //5
    public int getNumberOf_PatientsWithMatchOnlyWithMdrId(MdrIdDatatype mdrId);

    //6
    public double getPercentageOf_PatientsWithMatchOnlyWithMdrId_outOf_PatientsWithMdrId(MdrIdDatatype mdrId);

    //7
    public double getPercentageOf_PatitentsWithMatchOnlyWithMdrId_outOf_TotalPatients(MdrIdDatatype mdrId);

    //8
    public int getNumberOf_PatientsWithAnyMismatchWithMdrId(MdrIdDatatype mdrId);

    //9
    public double getPercentageOf_PatientsWithAnyMismatchWithMdrId_outOf_PatientsWithMdrId(MdrIdDatatype mdrId);

    //10
    public double getPercentageOf_PatientsWithAnyMismatchWithMdrId_outOf_TotalPatients(MdrIdDatatype mdrId);

    public double getPercentageOfPatientsOutOfTotalNumberOfPatientsForADataelement (MdrIdDatatype mdrId);



    public double getPercentageOf_CompletelyMatchingDataelements_outOf_AllDataelements();

    public double getPercentageOf_NotCompletelyMismatchingDataelements_outOf_AllDataelements();

    public double getPercentageOf_CompletelyMismatchingDataelements_outOf_AllDataelements();

    public double getPercentageOf_NotMappedDataelements_outOf_AllDataelements();

    public int getTotalNumberOfPatients();


}
