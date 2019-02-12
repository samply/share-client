package de.samply.share.client.quality.report.model.reader;/*
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
import de.samply.common.mdrclient.domain.Result;
import de.samply.common.mdrclient.domain.Validations;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.MdrIdAndValidations;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.client.quality.report.properties.PropertyUtils;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.web.mdrFaces.MdrContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class QualityReportModelReaderImpl implements ModelReader {

    private static final String DATAELEMENTGROUP = "dataelementgroup";

    private String[] mdrGroups;
    private String[] additionalMdrElements;

    private String languageCode;



    public QualityReportModelReaderImpl() {

        languageCode = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_LANGUAGE_CODE);
        mdrGroups = PropertyUtils.getListOfProperties(EnumConfiguration.QUALITY_REPORT_MDR_GROUPS);
        additionalMdrElements = PropertyUtils.getListOfProperties(EnumConfiguration.QUALITY_REPORT_ADDITIONAL_MDR_DATA_ELEMENTS);

    }


    @Override
    public Model getModel () throws ModelReaderException {

        try {

            return getModelWithoutExceptions();

        } catch (ExecutionException | MdrInvalidResponseException | MdrConnectionException e) {
            throw new ModelReaderException(e);
        }
    }

    private Model getModelWithoutExceptions () throws ExecutionException, MdrConnectionException, MdrInvalidResponseException {


        MdrClient mdrClient = getMdrClient();

        List<MdrIdAndValidations> mdrIdAndValidations = new ArrayList<>();

        mdrIdAndValidations = addMrdGroups(mdrIdAndValidations, mdrClient);
        mdrIdAndValidations = addAdditionalMrdElements(mdrIdAndValidations, mdrClient);

        Model model = new Model();
        model.setMdrIdAndValidations(mdrIdAndValidations);

        return model;

    }

    private List<MdrIdAndValidations> addMrdGroups (List<MdrIdAndValidations> mdrIdAndValidationsList, MdrClient mdrClient) throws ExecutionException, MdrConnectionException, MdrInvalidResponseException {

        for (String mdrGroup : mdrGroups){

            List<MdrIdDatatype> mdrIdDatatypeList = new ArrayList<>();
            mdrIdDatatypeList = getElementsFromGroupAndSubgroups(mdrIdDatatypeList, mdrGroup, mdrClient);

            List<MdrIdAndValidations> temporalMdrIdAndValidations = getMdrIdAndValidationsList(mdrClient, mdrIdDatatypeList);

            mdrIdAndValidationsList.addAll(temporalMdrIdAndValidations);

        }

        return mdrIdAndValidationsList;
    }

    private List<MdrIdAndValidations> addAdditionalMrdElements (List<MdrIdAndValidations> mdrIdAndValidationsList, MdrClient mdrClient) throws ExecutionException, MdrConnectionException, MdrInvalidResponseException {

        for (String mdrElement : additionalMdrElements){

            MdrIdDatatype mdrId = new MdrIdDatatype(mdrElement);
            MdrIdAndValidations mdrIdAndValidations = getMdrIdAndValidations(mdrClient, mdrId);
            mdrIdAndValidationsList.add(mdrIdAndValidations);

        }

        return mdrIdAndValidationsList;
    }


    private MdrClient getMdrClient(){

        return MdrContext.getMdrContext().getMdrClient();

    }

    private List<MdrIdDatatype> getElementsFromGroupAndSubgroups(List<MdrIdDatatype> theList, String groupKey, MdrClient mdrClient)
            throws MdrConnectionException, ExecutionException {

        List<Result> result_l = mdrClient.getMembers(groupKey, languageCode);
        for (Result r : result_l) {
            if (r.getType().equalsIgnoreCase(DATAELEMENTGROUP)) {
                theList = getElementsFromGroupAndSubgroups(theList, r.getId(), mdrClient);
            } else {
                theList.add(new MdrIdDatatype(r.getId()));
            }
        }

        return theList;
    }


    private List<MdrIdAndValidations> getMdrIdAndValidationsList(MdrClient mdrClient, List<MdrIdDatatype> mdrIds) throws ExecutionException, MdrConnectionException, MdrInvalidResponseException {

        List<MdrIdAndValidations> mdrIdAndValidationsList = new ArrayList<>();

        for (MdrIdDatatype mdrId : mdrIds){

            MdrIdAndValidations mdrIdAndValidations = getMdrIdAndValidations(mdrClient, mdrId);
            mdrIdAndValidationsList.add(mdrIdAndValidations);
        }

        return mdrIdAndValidationsList;
    }

    private MdrIdAndValidations getMdrIdAndValidations(MdrClient mdrClient, MdrIdDatatype mdrId) throws ExecutionException, MdrConnectionException, MdrInvalidResponseException {

        Validations validations = getMdrIdAndValidationsList(mdrClient, mdrId.getLatestMdr());
        return new MdrIdAndValidations(mdrId, validations);

    }

    private Validations getMdrIdAndValidationsList(MdrClient mdrClient, String mdrId) throws ExecutionException, MdrConnectionException, MdrInvalidResponseException {
        return mdrClient.getDataElementValidations(mdrId, languageCode);
    }

}
