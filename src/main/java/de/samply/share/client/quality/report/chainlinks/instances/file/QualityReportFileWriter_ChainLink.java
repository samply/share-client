package de.samply.share.client.quality.report.chainlinks.instances.file;/*
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


import de.samply.share.client.quality.report.chainlinks.ChainLink;
import de.samply.share.client.quality.report.chainlinks.ChainLinkException;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;
import de.samply.share.client.quality.report.chainlinks.instances.validator.ValidatorContext;
import de.samply.share.client.quality.report.file.manager.QualityReportFileManagerException;
import de.samply.share.client.quality.report.file.manager.QualityReportFileManager;
import de.samply.share.client.quality.report.results.QualityResults;

public class QualityReportFileWriter_ChainLink<I extends ChainLinkItem & FileContext & ValidatorContext> extends ChainLink<I> {

    private QualityReportFileManager qualityReportFileManager;

    public QualityReportFileWriter_ChainLink(QualityReportFileManager qualityReportFileManager) {
        this.qualityReportFileManager = qualityReportFileManager;
    }

    @Override
    protected String getChainLinkId() {
        return "Quality Report File Writer";
    }

    @Override
    protected I process(I item) throws ChainLinkException {

        try {

            writefile(item);
            return item;

        } catch (QualityReportFileManagerException e) {
            e.printStackTrace();
            throw new ChainLinkException(e);
        }

    }

    private void writefile (I item) throws QualityReportFileManagerException {


        logger.info("getting quality results");
        QualityResults qualityResults = item.getQualityResults();

        logger.info("writing quality report");
        qualityReportFileManager.writeFile(qualityResults, item.getFileId());

        logger.info("quality report finished");

    }


}
