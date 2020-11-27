package de.samply.share.client.quality.report.file.manager;/*
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
import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManager;
import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManagerException;
import de.samply.share.client.quality.report.file.id.path.IdPathManagerImpl;
import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.QualityResultsImpl;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CsvQualityReportFileManager<I extends QualityResultCsvLineManager> extends QualityReportFileManagerImpl {

    private QualityResultCsvLineManager qualityResultsCsvLineManager;


    public CsvQualityReportFileManager(I qualityResultsCsvLineManager, IdPathManagerImpl<I,?,?> idPathManager) {

        super(idPathManager);
        this.qualityResultsCsvLineManager = qualityResultsCsvLineManager;

    }

    @Override
    public void writeFile(QualityResults qualityResults, String fileId) throws QualityReportFileManagerException {

        String filePath = idPathManager.getCsvFilePath(fileId);
        writeQualityResults(qualityResults, filePath);

    }

    private void writeQualityResults (QualityResults qualityResults, String filePath) throws QualityReportFileManagerException {

        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            writeFile(bufferedWriter, qualityResults);

        } catch (IOException | QualityResultCsvLineManagerException e) {
            throw new QualityReportFileManagerException(e);
        }

    }

    private void writeFile(BufferedWriter bufferedWriter, QualityResults qualityResults) throws IOException, QualityResultCsvLineManagerException {

        for (MdrIdDatatype mdrId : qualityResults.getMdrIds()){

            for (String value : qualityResults.getValues(mdrId)){

                QualityResult result = qualityResults.getResult(mdrId, value);
                writeFile(bufferedWriter, mdrId, value, result);

            }
        }

        bufferedWriter.flush();

    }

    private void writeFile(BufferedWriter bufferedWriter, MdrIdDatatype mdrId, String value, QualityResult qualityResult) throws IOException, QualityResultCsvLineManagerException {

        String line = qualityResultsCsvLineManager.createLine(mdrId, value, qualityResult);
        bufferedWriter.write(line);

    }

    @Override
    public QualityResults readFile (String fileId) throws QualityReportFileManagerException {

        String filePath = idPathManager.getCsvFilePath(fileId);
        return readQualityResults(filePath);

    }

    private QualityResults readQualityResults (String filePath) throws QualityReportFileManagerException {

        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))){

            QualityResults qualityResults = createQualityResults();
            String line;

            while ((line = bufferedReader.readLine()) != null){
                qualityResults = qualityResultsCsvLineManager.parseLineAndAddToQualityResults(line, qualityResults);
            }

            return qualityResults;

        } catch (IOException e) {
            throw new QualityReportFileManagerException(e);
        }

    }


    private QualityResults createQualityResults(){
        return new QualityResultsImpl();
    }

}
