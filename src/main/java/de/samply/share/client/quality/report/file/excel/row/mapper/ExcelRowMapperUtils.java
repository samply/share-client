package de.samply.share.client.quality.report.file.excel.row.mapper;/*
 * Copyright (C) 2018 Medizinische Informatik in der Translationalen Onkologie,
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
import de.samply.common.mdrclient.domain.Definition;
import de.samply.common.mdrclient.domain.Record;
import de.samply.common.mdrclient.domain.Validations;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.client.quality.report.model.searcher.ModelSearcher;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.MdrIdDatatype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ExcelRowMapperUtils {

    private String languageCode;
    private String mdrLinkPrefix; // = "https://mdr.ccp-it.dktk.dkfz.de/detail.xhtml?urn=";
    private ModelSearcher modelSearcher;
    private MdrClient mdrClient;
    private Map<MdrIdDatatype, String> mdrDatenElements = new HashMap<>();

    public ExcelRowMapperUtils(Model model, MdrClient mdrClient) {

        this.mdrLinkPrefix = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_MDR_LINK_PREFIX);

        this.modelSearcher = new ModelSearcher(model);
        this.mdrClient = mdrClient;

        this.languageCode = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_LANGUAGE_CODE);

    }

    public String getMdrLink(MdrIdDatatype mdrId){
        return mdrLinkPrefix + mdrId;
    }

    public String getMdrDatenElement(MdrIdDatatype mdrId) throws ExcelRowMapperException {

        String mdrName = mdrDatenElements.get(mdrId);
        if (mdrName == null){
            mdrName = createMdrDatenElement(mdrId);
            mdrDatenElements.put(mdrId, mdrName);
        }

        return mdrName;

    }

    private String createMdrDatenElement(MdrIdDatatype mdrId) throws ExcelRowMapperException {

        try {

            return createMdrDatenElementWithoutExceptions(mdrId);

        } catch (MdrConnectionException | ExecutionException | MdrInvalidResponseException e) {
            throw new ExcelRowMapperException(e);
        }

    }

    private String createMdrDatenElementWithoutExceptions(MdrIdDatatype mdrId) throws ExecutionException, MdrConnectionException, MdrInvalidResponseException {

        Definition definition = mdrClient.getDataElementDefinition(mdrId.toString(), languageCode);
        ArrayList<Record> designations = definition.getDesignations();
        if (designations != null && designations.size() > 0){
            Record record = designations.get(0);
            return getMdrDatenElement(record);
        }

        return null;

    }

    private String getMdrDatenElement(Record record){
        return (record != null) ? record.getDesignation() : null;
    }

    public String getMdrType (MdrIdDatatype mdrId){

        Validations validations = modelSearcher.getValidations(mdrId);
        return  (validations != null) ? validations.getDatatype(): null;

    }

}
