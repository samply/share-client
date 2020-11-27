package de.samply.share.client.control;
/*
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


import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.chain.Chain;
import de.samply.share.client.quality.report.chain.finalizer.ChainFinalizer;
import de.samply.share.client.quality.report.chain.factory.ChainFactory;
import de.samply.share.client.quality.report.chain.factory.ChainFactoryException;
import de.samply.share.client.quality.report.chain.factory.QualityReportChainFactory_002;
import de.samply.share.client.quality.report.chainlinks.statistics.chain.ChainStatistics;
import de.samply.share.client.quality.report.chainlinks.statistics.manager.ChainStatisticsManager;
import de.samply.share.client.quality.report.faces.QualityReportFileInfo;
import de.samply.share.client.quality.report.faces.QualityReportFileInfoManager;
import de.samply.share.client.quality.report.faces.QualityReportFileInfoManagerException;
import de.samply.share.client.quality.report.faces.QualityReportFileInfoManagerImpl;
import de.samply.share.client.quality.report.file.id.generator.QualityFileIdGenerator;
import de.samply.share.client.quality.report.file.id.generator.QualityFileIdGeneratorImpl;
import de.samply.share.client.quality.report.file.id.path.IdPathManager_002;
import de.samply.share.client.quality.report.file.manager.QualityReportMetadataFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportMetadataFileManagerImpl;
import de.samply.share.client.quality.report.file.metadata.txtcolumn.MetadataTxtColumnManager_002;

import de.samply.share.client.util.db.ConfigurationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omnifaces.util.Faces;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;


@ManagedBean(name = "qualityReportController")
@SessionScoped
public class QualityReportController implements Serializable {


    private IdPathManager_002 idPathManager = new IdPathManager_002();
    private QualityFileIdGenerator qualityFileIdGenerator = new QualityFileIdGeneratorImpl();
    private ChainFactory qualityReportChainFactory;
    private QualityReportFileInfoManager qualityReportFileInfoManager;
    private static final Logger logger = LogManager.getLogger(QualityReportController.class);
    private ChainStatisticsManager chainStatisticsManager = ApplicationBean.getChainStatisticsManager();
    private ChainFinalizer chainFinalizer = ApplicationBean.getChainFinalizer();
    private boolean isLoading = true;


    public QualityReportController()  {

        MetadataTxtColumnManager_002 metadataTxtColumnManager = new MetadataTxtColumnManager_002();
        QualityReportMetadataFileManager qualityReportMetadataFileManager = new QualityReportMetadataFileManagerImpl<>(metadataTxtColumnManager, idPathManager);
        qualityReportFileInfoManager = new QualityReportFileInfoManagerImpl(qualityReportMetadataFileManager, idPathManager);
        isLoading = false;

    }


    private ChainFactory createQualityReportChainFactory(IdPathManager_002 idPathManager, ChainFinalizer chainFinalizer) throws QualityReportControllerException {

        try {
            return new QualityReportChainFactory_002(idPathManager, chainFinalizer);
        } catch (ChainFactoryException e) {
            throw new QualityReportControllerException(e);
        }

    }

    public void  generate () throws QualityReportControllerException {

        isLoading = true;

        String fileId = qualityFileIdGenerator.generateFileId();
        Chain chain = createQualityReportChain(fileId);
        chainStatisticsManager.setChainStatistics(chain.getChainStatistics());

        chain.run();

        isLoading = false;
    }

    public boolean isTaskRunning(){
        return !isLoading && chainStatisticsManager.getChainStatistics() != null;
    }

    public boolean isLoading(){
        return isLoading;
    }

    public boolean isStatusChanged(){
        return chainStatisticsManager.isStatusChanged();
    }

    private Chain createQualityReportChain(String fileId) throws QualityReportControllerException {

        try {

            return getQualityReportChainFactory().create(fileId);

        } catch (ChainFactoryException e) {
            throw new QualityReportControllerException(e);
        }

    }

    private ChainFactory getQualityReportChainFactory() throws QualityReportControllerException {

        if (qualityReportChainFactory == null){
            qualityReportChainFactory = createQualityReportChainFactory(idPathManager, chainFinalizer);
        }

        return qualityReportChainFactory;
    }


    public List<QualityReportFileInfo> getQualityReportFileInfos() {

        try {

            return qualityReportFileInfoManager.getQualityReportFiles();

        } catch (QualityReportFileInfoManagerException e) {
            logger.error(e);
            return null;
        }

    }

    public void download (String filePath, String filename){

        try {

            Faces.sendFile(new FileInputStream(filePath), filename, true);

        } catch (IOException e) {
            logger.error(e);
        }
    }

    public ChainStatistics getChainStatistics(){
        return chainStatisticsManager.getChainStatistics();
    }

    public void finalizeChain(){
        chainFinalizer.finalizeChain();
    }

    public String getLanguage(){
        return ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_LANGUAGE_CODE);
    }


}
