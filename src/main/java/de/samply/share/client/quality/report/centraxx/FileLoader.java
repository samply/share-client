package de.samply.share.client.quality.report.centraxx;/*
 * Copyright (C) 2019 Medizinische Informatik in der Translationalen Onkologie,
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
import de.samply.share.client.util.db.ConfigurationUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileLoader {

    public void load (FilenameReader filenameReader, LineLoader loader) throws CentraxxMapperException {

        File file = getFile(filenameReader);
        load(file, loader);

    }

    private void load (File file, LineLoader lineLoader)throws CentraxxMapperException {

        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))){

            load(bufferedReader, lineLoader);

        } catch (IOException e) {
            throw new CentraxxMapperException(e);
        }

    }

    private void load (BufferedReader bufferedReader, LineLoader lineLoader) throws IOException {

        String line = null;

        while ((line = bufferedReader.readLine()) != null){
            lineLoader.load(line);
        }

    }


    public interface FilenameReader{
        public String getFilename();
    }

    public String getConfigurationFilename (EnumConfiguration enumConfigurationFilename){
        return ConfigurationUtil.getConfigurationElementValue(enumConfigurationFilename);
    }

    public interface LineLoader {
        public void load(String line);
    }

    private File getFile(FilenameReader filenameReader) throws CentraxxMapperException {

        String filename = filenameReader.getFilename();
        return getConfigFile(filename);

    }

    private File getConfigFile (String filename) throws CentraxxMapperException {

        try {

            ClassLoader classLoader = getClass().getClassLoader();
            return new File(classLoader.getResource(filename).toURI());

        } catch (Exception e){
            throw new CentraxxMapperException(e);
        }

    }

}
