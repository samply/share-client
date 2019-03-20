package de.samply.share.client.quality.report.file.excel.workbook;/*
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

import de.samply.share.client.quality.report.MdrMappedElements;
import de.samply.share.client.quality.report.dktk.DktkId_MdrId_Converter;
import de.samply.share.client.quality.report.file.excel.instances.patientids.PatientDktkIdsExcelRowContextFactory;
import de.samply.share.client.quality.report.file.excel.instances.patientids.PatientLocalIdsExcelRowContextFactory;
import de.samply.share.client.quality.report.file.excel.instances.statistics.dataelementstats.DataElementStats_ExcelRowContextFactory;
import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContextFactory_002;
import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactory;
import de.samply.share.client.quality.report.file.excel.sheet.ExplanatoryExcelSheetFactory;
import de.samply.share.client.quality.report.model.searcher.ModelSearcher;

public class ExcelWorkbookFactoryParameters_002 {

    private ExcelSheetFactory excelSheetFactory;
    private ExplanatoryExcelSheetFactory explanatoryExcelSheetFactory;
    private ModelSearcher modelSearcher;
    private DktkId_MdrId_Converter dktkIdManager;
    private PatientLocalIdsExcelRowContextFactory patientLocalIdsExcelRowContextFactory;
    private PatientDktkIdsExcelRowContextFactory patientDktkIdsExcelRowContextFactory;
    private MdrMappedElements mdrMappedElements;
    private ExcelRowContextFactory_002 excelRowContextFactory;
    private DataElementStats_ExcelRowContextFactory dataElementStats_excelRowContextFactory;


    public ExcelSheetFactory getExcelSheetFactory() {
        return excelSheetFactory;
    }

    public void setExcelSheetFactory(ExcelSheetFactory excelSheetFactory) {
        this.excelSheetFactory = excelSheetFactory;
    }

    public ExplanatoryExcelSheetFactory getExplanatoryExcelSheetFactory() {
        return explanatoryExcelSheetFactory;
    }

    public void setExplanatoryExcelSheetFactory(ExplanatoryExcelSheetFactory explanatoryExcelSheetFactory) {
        this.explanatoryExcelSheetFactory = explanatoryExcelSheetFactory;
    }

    public ModelSearcher getModelSearcher() {
        return modelSearcher;
    }

    public void setModelSearcher(ModelSearcher modelSearcher) {
        this.modelSearcher = modelSearcher;
    }

    public DktkId_MdrId_Converter getDktkIdManager() {
        return dktkIdManager;
    }

    public void setDktkIdManager(DktkId_MdrId_Converter dktkIdManager) {
        this.dktkIdManager = dktkIdManager;
    }

    public PatientLocalIdsExcelRowContextFactory getPatientLocalIdsExcelRowContextFactory() {
        return patientLocalIdsExcelRowContextFactory;
    }

    public void setPatientLocalIdsExcelRowContextFactory(PatientLocalIdsExcelRowContextFactory patientLocalIdsExcelRowContextFactory) {
        this.patientLocalIdsExcelRowContextFactory = patientLocalIdsExcelRowContextFactory;
    }

    public PatientDktkIdsExcelRowContextFactory getPatientDktkIdsExcelRowContextFactory() {
        return patientDktkIdsExcelRowContextFactory;
    }

    public void setPatientDktkIdsExcelRowContextFactory(PatientDktkIdsExcelRowContextFactory patientDktkIdsExcelRowContextFactory) {
        this.patientDktkIdsExcelRowContextFactory = patientDktkIdsExcelRowContextFactory;
    }

    public ExcelRowContextFactory_002 getExcelRowContextFactory() {
        return excelRowContextFactory;
    }

    public void setExcelRowContextFactory(ExcelRowContextFactory_002 excelRowContextFactory) {
        this.excelRowContextFactory = excelRowContextFactory;
    }

    public DataElementStats_ExcelRowContextFactory getDataElementStats_excelRowContextFactory() {
        return dataElementStats_excelRowContextFactory;
    }

    public void setDataElementStats_excelRowContextFactory(DataElementStats_ExcelRowContextFactory dataElementStats_excelRowContextFactory) {
        this.dataElementStats_excelRowContextFactory = dataElementStats_excelRowContextFactory;
    }

    public MdrMappedElements getMdrMappedElements() {
        return mdrMappedElements;
    }

    public void setMdrMappedElements(MdrMappedElements mdrMappedElements) {
        this.mdrMappedElements = mdrMappedElements;
    }

}
