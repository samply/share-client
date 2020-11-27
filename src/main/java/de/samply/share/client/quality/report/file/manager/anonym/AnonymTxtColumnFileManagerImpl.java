package de.samply.share.client.quality.report.file.manager.anonym;/*
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

import de.samply.share.client.quality.report.file.txtcolumn.AnonymTxtColumn;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class AnonymTxtColumnFileManagerImpl implements AnonymTxtColumnFileManager {

    String filePath;

    public AnonymTxtColumnFileManagerImpl(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void write(AnonymTxtColumn anonymTxtColumn) throws AnonymTxtColumnFileManagerException {

        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            bufferedWriter.write(anonymTxtColumn.createColumn());

        } catch (IOException e) {
            throw new AnonymTxtColumnFileManagerException(e);
        }

    }

    @Override
    public AnonymTxtColumn read() throws AnonymTxtColumnFileManagerException {

        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))){

            return read(bufferedReader);

        } catch (IOException e) {
            throw new AnonymTxtColumnFileManagerException(e);
        }

    }

    private AnonymTxtColumn read (BufferedReader bufferedReader) throws IOException {

        String column = IOUtils.toString (bufferedReader);

        AnonymTxtColumn anonymTxtColumn = new AnonymTxtColumn();
        anonymTxtColumn.parseValuesOfColumn(column);

        return anonymTxtColumn;

    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
