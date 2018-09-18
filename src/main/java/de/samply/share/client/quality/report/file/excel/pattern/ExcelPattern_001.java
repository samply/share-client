package de.samply.share.client.quality.report.file.excel.pattern;/*
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
import de.samply.share.client.quality.report.MdrIgnoredElements;
import de.samply.share.client.quality.report.centraxx.CentraXxMapper;
import de.samply.share.client.quality.report.dktk.DktkId_MdrId_Converter;
import de.samply.share.client.quality.report.file.excel.cell.reference.FirstRowCellReferenceFactoryForOneSheet;
import de.samply.share.client.quality.report.file.excel.instances.patientids.PatientDktkIdsExcelRowContextFactory;
import de.samply.share.client.quality.report.file.excel.instances.patientids.PatientLocalIdsExcelRowContextFactory;
import de.samply.share.client.quality.report.file.excel.instances.statistics.dataelementstats.DataElementStats_ExcelRowContextFactory;
import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContextFactory_002;
import de.samply.share.client.quality.report.file.excel.row.factory.ExcelRowFactory;
import de.samply.share.client.quality.report.file.excel.row.factory.ExcelRowFactoryImpl;
import de.samply.share.client.quality.report.file.excel.row.mapper.ExcelRowMapperUtils;
import de.samply.share.client.quality.report.file.excel.row.mapper.ExcelRowMapper_002;
import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactory;
import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactoryImpl;
import de.samply.share.client.quality.report.file.excel.sheet.ExplanatoryExcelSheetFactory;
import de.samply.share.client.quality.report.file.excel.sheet.wrapper.*;
import de.samply.share.client.quality.report.file.excel.workbook.ExcelWorkbookFactory;
import de.samply.share.client.quality.report.file.excel.workbook.ExcelWorkbookFactoryImpl_002;
import de.samply.share.client.quality.report.file.excel.workbook.ExcelWorkbookFactoryParameters_002;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.client.quality.report.model.searcher.ModelSearcher;

public class ExcelPattern_001 implements ExcelPattern{

    private Model model;
    private MdrClient mdrClient;
    private CentraXxMapper centraXxMapper;
    private DktkId_MdrId_Converter dktkIdManager;
    private MdrIgnoredElements ignoredElements;
    private ExcelRowMapperUtils excelRowMapperUtils;


    public ExcelPattern_001(Model model, MdrClient mdrClient, CentraXxMapper centraXxMapper, DktkId_MdrId_Converter dktkIdManager, MdrIgnoredElements ignoredElements) {

        this.model = model;
        this.mdrClient = mdrClient;
        this.excelRowMapperUtils = new ExcelRowMapperUtils(model, mdrClient);

        this.centraXxMapper = centraXxMapper;
        this.dktkIdManager = dktkIdManager;
        this.ignoredElements = ignoredElements;


    }

    @Override
    public ExcelWorkbookFactory createExcelWorkbookFactory() {

        ExcelWorkbookFactoryParameters_002 excelWorkbookFactoryParameters = createExcelWorkbookFactoryParameters();
        return new ExcelWorkbookFactoryImpl_002(excelWorkbookFactoryParameters);

    }

    private ExcelWorkbookFactoryParameters_002 createExcelWorkbookFactoryParameters (){

        ExcelWorkbookFactoryParameters_002 excelWorkbookFactoryParameters = new ExcelWorkbookFactoryParameters_002();

        ExcelRowFactory excelRowFactory = new ExcelRowFactoryImpl();
        ExcelSheetFactory excelSheetFactory = createExcelSheetFactory(excelRowFactory);

        ExcelRowContextFactory_002 excelRowContextFactory = createExcelRowContextFactory();
        //Table2_ExcelRowContextFactory table2_excelRowContextFactory = new Table2_ExcelRowContextFactory(excelRowMapperUtils, dktkIdManager, centraXxMapper);
        PatientDktkIdsExcelRowContextFactory patientDktkIdsExcelRowContextFactory = new PatientDktkIdsExcelRowContextFactory();
        PatientLocalIdsExcelRowContextFactory patientLocalIdsExcelRowContextFactory = new PatientLocalIdsExcelRowContextFactory();
        ExplanatoryExcelSheetFactory explanatoryExcelSheetFactory = createExplanatoryExcelSheetFactory();
        DataElementStats_ExcelRowContextFactory dataElementStats_excelRowContextFactory = new DataElementStats_ExcelRowContextFactory(excelRowMapperUtils, dktkIdManager, centraXxMapper);


        excelWorkbookFactoryParameters.setExcelSheetFactory(excelSheetFactory);
        excelWorkbookFactoryParameters.setExplanatoryExcelSheetFactory(explanatoryExcelSheetFactory);
        excelWorkbookFactoryParameters.setModelSearcher(new ModelSearcher(model));
        excelWorkbookFactoryParameters.setDktkIdManager(dktkIdManager);
        excelWorkbookFactoryParameters.setPatientLocalIdsExcelRowContextFactory(patientLocalIdsExcelRowContextFactory);
        excelWorkbookFactoryParameters.setPatientDktkIdsExcelRowContextFactory(patientDktkIdsExcelRowContextFactory);
        excelWorkbookFactoryParameters.setMdrIgnoredElements(ignoredElements);
        excelWorkbookFactoryParameters.setExcelRowContextFactory(excelRowContextFactory);
        //excelWorkbookFactoryParameters.setTable2_excelRowContextFactory(table2_excelRowContextFactory);
        excelWorkbookFactoryParameters.setDataElementStats_excelRowContextFactory(dataElementStats_excelRowContextFactory);


        return excelWorkbookFactoryParameters;

    }

    private ExcelSheetFactory createExcelSheetFactory (ExcelRowFactory excelRowFactory){

        ExcelSheetFactory excelSheetFactory = new ExcelSheetFactoryImpl(excelRowFactory);

        excelSheetFactory = new ExcelSheetWithAutoFilterFactory(excelSheetFactory);
        excelSheetFactory = new HighlightMismatchInRed_ExcelSheetFactory_002(excelSheetFactory);
        excelSheetFactory = new ExcelSheetWithAutoSizeColumnFactory(excelSheetFactory);
        excelSheetFactory = new ExcelSheetFreezeFirstRowFactory(excelSheetFactory);

        return excelSheetFactory;

    }

    private ExcelRowContextFactory_002 createExcelRowContextFactory (){

        ExcelRowMapper_002 excelRowMapper = createExcelRowMapper ();
        return new ExcelRowContextFactory_002(excelRowMapper);

    }


    private ExcelRowMapper_002 createExcelRowMapper (){

        FirstRowCellReferenceFactoryForOneSheet firstRowCellReferenceFactoryForOneSheet = new FirstRowCellReferenceFactoryForOneSheet(ExcelWorkbookFactoryImpl_002.PATIENT_LOCAL_IDS_SHEET_TITLE);
        return new ExcelRowMapper_002( centraXxMapper, dktkIdManager, firstRowCellReferenceFactoryForOneSheet, ignoredElements, excelRowMapperUtils);

    }

    private ExplanatoryExcelSheetFactory createExplanatoryExcelSheetFactory(){

        return new ExplanatoryExcelSheetFactory();

    }

}
