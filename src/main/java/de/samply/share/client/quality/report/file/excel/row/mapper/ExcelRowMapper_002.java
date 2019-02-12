package de.samply.share.client.quality.report.file.excel.row.mapper;/*
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
import de.samply.share.client.quality.report.centraxx.CentraXxMapper;
import de.samply.share.client.quality.report.dktk.DktkId_MdrId_Converter;
import de.samply.share.client.quality.report.file.excel.cell.reference.CellReference;
import de.samply.share.client.quality.report.file.excel.cell.reference.FirstRowCellReferenceFactoryForOneSheet;
import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowParameters_002;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements_002;
import de.samply.share.client.quality.report.file.excel.row.elements.FormattedExcelRowElements_002;
import de.samply.share.common.utils.MdrIdDatatype;

public class ExcelRowMapper_002 {




    private ExcelRowMapperUtils excelRowMapperUtils;
    private CentraXxMapper centraXxMapper;
    private DktkId_MdrId_Converter dktkIdManager;
    private FirstRowCellReferenceFactoryForOneSheet cellReferenceFactory;
    private MdrIgnoredElements ignoredElements;

    public ExcelRowMapper_002( CentraXxMapper centraXxMapper, DktkId_MdrId_Converter dktkIdManager, FirstRowCellReferenceFactoryForOneSheet cellReferenceFactory, MdrIgnoredElements ignoredElements, ExcelRowMapperUtils excelRowMapperUtils) {


        this.excelRowMapperUtils = excelRowMapperUtils;
        this.centraXxMapper = centraXxMapper;
        this.dktkIdManager = dktkIdManager;
        this.cellReferenceFactory = cellReferenceFactory;
        this.ignoredElements = ignoredElements;

    }

    public ExcelRowElements_002 createExcelRowElements(ExcelRowParameters_002 excelRowParameters) throws ExcelRowMapperException {

        ExcelRowElements_002 rowElements = new FormattedExcelRowElements_002();

        MdrIdDatatype mdrId = excelRowParameters.getMdrId();
        boolean isValid = excelRowParameters.getQualityResult().isValid();
        String mdrAttributeValue = isValid ? excelRowParameters.getValue() : null;
        int numberOfPatients = excelRowParameters.getQualityResult().getNumberOfPatients();
        String mdrDatenElement = excelRowMapperUtils.getMdrDatenElement(mdrId);
        String mdrType = excelRowMapperUtils.getMdrType(mdrId);
        String mdrLink = excelRowMapperUtils.getMdrLink(mdrId);
        String cxxDatenElement = centraXxMapper.getCentraXxAttribute(mdrId);
        String cxxAttributeValue = isValid ? centraXxMapper.getCentraXxValue(mdrId, mdrAttributeValue) : excelRowParameters.getValue();
        String dktkId = dktkIdManager.getDktkId(mdrId);
        CellReference numberOfPatientsCellReference = createNumberOfPatientsCellReference(excelRowParameters);
        Double percentageOutOfPatientWithDataElement = excelRowParameters.getPercentageOutOfPatientWithDataElement();
        Double percentageOutOfTotalPatients = excelRowParameters.getPercentageOutOfTotalPatients();


        if (ignoredElements.isIgnored(mdrId)){
            rowElements.setNotMapped();
        }else{
            rowElements.setValid(isValid, numberOfPatients);
        }

        rowElements.setMdrAttributeValue(mdrAttributeValue);
        rowElements.setMdrDatenElement(mdrDatenElement);
        rowElements.setMdrType(mdrType);
        rowElements.setMdrLink(mdrLink, mdrId);
        rowElements.setCxxDatenElement(cxxDatenElement);
        rowElements.setCxxAttributeValue(cxxAttributeValue);
        rowElements.setDktkId(dktkId);
        rowElements.setPercentageOutOfPatientsWithDataElement(percentageOutOfPatientWithDataElement);
        rowElements.setPercentageOutOfTotalPatients(percentageOutOfTotalPatients);

        if (numberOfPatientsCellReference != null){
            rowElements.setNumberOfPatients(numberOfPatientsCellReference, numberOfPatients);
        } else{
            rowElements.setNumberOfPatients(numberOfPatients);
        }


        return rowElements;
    }
    
    private CellReference createNumberOfPatientsCellReference (ExcelRowParameters_002 excelRowParameters){

        Integer mismatchOrdinal = excelRowParameters.getMismatchOrdinal();
        boolean isValid = excelRowParameters.getQualityResult().isValid();

        return (!isValid && mismatchOrdinal != null) ? cellReferenceFactory.createCellReference(mismatchOrdinal) : null;

    }



}
