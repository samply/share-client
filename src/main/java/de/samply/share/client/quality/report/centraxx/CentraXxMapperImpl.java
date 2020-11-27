package de.samply.share.client.quality.report.centraxx;/*
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
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.util.db.ConfigurationUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CentraXxMapperImpl implements CentraXxMapper{

    private Map<String, String> centraXxDataElements = new HashMap<>();
    private Map<AttributeValueKey, String> centraXxAttributeValues = new HashMap<>();

    public CentraXxMapperImpl() throws CentraXxMapperException {

        loadCentraXxDataelements();
        loadCentraXxValues();

    }

    @Override
    public String getCentraXxAttribute(MdrIdDatatype mdrId) {
        return centraXxDataElements.get(mdrId.toString());
    }

    @Override
    public String getCentraXxValue(MdrIdDatatype mdrId, String mdrValue) {

        AttributeValueKey attributeValueKey = new AttributeValueKey(mdrId, mdrValue);
        return centraXxAttributeValues.get(attributeValueKey);

    }

    private void loadCentraXxDataelements() throws CentraXxMapperException {

        File file = getCentraXxDataelementsFile();
        loadCentraXxDataelements(file);

    }

    private void loadCentraXxDataelements(File file) throws CentraXxMapperException {

        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))){

            loadCentraXxDataelements(bufferedReader);

        } catch (IOException e) {
            throw new CentraXxMapperException(e);
        }

    }

    private void loadCentraXxDataelements(BufferedReader bufferedReader) throws IOException {

        String line = null;

        while ((line = bufferedReader.readLine()) != null){
            loadLineOfCentraXxDataElements(line);
        }

    }

    private void loadLineOfCentraXxDataElements(String line){

        String[] split = line.split("\t");

        if (split.length >= 2){

            String mdrId = split[0];
            String dataElement = split[1];

            putDataElement(mdrId, dataElement);

        }
    }

    private void putDataElement(String mdrId, String dataElement){

        String oldDataElement = centraXxDataElements.get(mdrId);
        if (oldDataElement != null){
            dataElement = addElement(oldDataElement, dataElement);
        }

        centraXxDataElements.put(mdrId, dataElement);
    }

    private String addElement (String oldElement, String newElement){

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(oldElement);
        stringBuilder.append(" | ");
        stringBuilder.append(newElement);

        return stringBuilder.toString();

    }


    private File getCentraXxDataelementsFile() throws CentraXxMapperException {

        String filename = getCentraXxDataelementsFilename();
        return getConfigFile(filename);

    }

    private File getConfigFile (String filename) throws CentraXxMapperException {

        try {

            ClassLoader classLoader = getClass().getClassLoader();
            return new File(classLoader.getResource(filename).toURI());

        } catch (Exception e){
            throw new CentraXxMapperException(e);
        }

    }

    private String getCentraXxDataelementsFilename(){
        return ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_CENTRAXX_DATAELEMENTS_FILE);
    }

    private void loadCentraXxValues() throws CentraXxMapperException {

        File file = getCentraXxValuesFile();
        loadCentraXxValues(file);

    }

    private File getCentraXxValuesFile() throws CentraXxMapperException {

        String filename = getCentraXxValuesFilename();
        return getConfigFile(filename);

    }


    private void loadCentraXxValues(File file) throws CentraXxMapperException {

        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))){

            loadCentraXxValues(bufferedReader);

        } catch (IOException e) {
            throw new CentraXxMapperException(e);
        }

    }

    private void loadCentraXxValues(BufferedReader bufferedReader) throws IOException {

        String line = null;

        while ((line = bufferedReader.readLine()) != null){
            loadLineOfCentraXxValues(line);
        }

    }

    private void loadLineOfCentraXxValues(String line) {

        String[] split = line.split("\t");

        if (split.length >= 3) {

            String mdrId = split[0];
            String mdrValue = split[1];
            String centraXxValue = split[2];

            AttributeValueKey attributeValueKey = new AttributeValueKey(mdrId, mdrValue);

            putValue(attributeValueKey, centraXxValue);

        }

    }

    private void putValue (AttributeValueKey attributeValueKey, String value){

        String oldValue = centraXxAttributeValues.get(attributeValueKey);
        if (oldValue != null){
            value = addElement(oldValue, value);
        }

        centraXxAttributeValues.put(attributeValueKey, value);
    }

    private String getCentraXxValuesFilename(){
        return ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_CENTRAXX_VALUES_FILE);
    }


}
