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
import de.samply.share.client.quality.report.MdrMappedElements;
import de.samply.share.client.util.connector.centraxx.CxxMappingElement;
import de.samply.share.common.utils.MdrIdDatatype;

import java.util.Set;

public class CentraxxMapperImpl_v2 implements CentraxxMapper {

    private final static String CXX_VALUES_SEPARATOR = " | ";
    private MdrMappedElements mdrMappedElements;
    private FileLoader fileLoader = new FileLoader();
    private GeneralRehearsalPriorization generalRehearsalPriorization = new GeneralRehearsalPriorization();


    public CentraxxMapperImpl_v2(MdrMappedElements mdrMappedElements) throws CentraxxMapperException {

        this.mdrMappedElements = mdrMappedElements;
        loadGeneralRehearsalPriorization();

    }

    @Override
    public String getCentraXxAttribute(MdrIdDatatype mdrId) {

        CxxMappingElement cxxMappingElement = mdrMappedElements.getCxxMappingElement(mdrId);
        return (cxxMappingElement != null) ? cxxMappingElement.getCxxName() : null;

    }

    @Override
    public String getCentraXxValue(MdrIdDatatype mdrId, String mdrValue) {

        String result = null;

        CxxMappingElement cxxMappingElement = mdrMappedElements.getCxxMappingElement(mdrId);

        if (cxxMappingElement != null) {

            Set<String> cxxValues = cxxMappingElement.getCxxValues(mdrValue);


            if (cxxValues != null && cxxValues.size() > 0) {

                StringBuilder stringBuilder = new StringBuilder();
                cxxValues.forEach((x) -> stringBuilder.append(x + CXX_VALUES_SEPARATOR));

                int index = stringBuilder.lastIndexOf(CXX_VALUES_SEPARATOR);
                result = stringBuilder.substring(0, index);

            }

        }

        return result;

    }

    @Override
    public String getGeneralRehearsalPriorization(MdrIdDatatype mdrId) {
        return generalRehearsalPriorization.getPriorization(mdrId);
    }

    private void loadGeneralRehearsalPriorization() throws CentraxxMapperException {

        FileLoader.FilenameReader filenameReader = () -> fileLoader.getConfigurationFilename(EnumConfiguration.QUALITY_REPORT_GENERAL_REHEARSAL_PRIORITATION_FILE);
        FileLoader.LineLoader lineLoader = (line) -> loadLineOfRehearsalPriorization(line);

        fileLoader.load (filenameReader, lineLoader);

    }

    private void loadLineOfRehearsalPriorization(String line) {

        String[] split = line.split(";");

        if (split.length >= 2){

            String mdrIdKey = split[0];
            String priorization = split[1];

            generalRehearsalPriorization.setPriorization(mdrIdKey, priorization);

        }
    }




}
