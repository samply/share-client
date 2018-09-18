package main.java.de.samply.share.client.mapper;/*
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MappingFileReader {

    private String cxxMdrCxxRepresentationsFilename = "CENTRAXX_MDRCXXREPRESENTATION.csv";
    private String cxxMdrRepresentationsFilename = "CENTRAXX_MDRREPRESENTATION.csv";
    private String teilerBaseViewColumnsFilename = "";


    public List<main.java.de.samply.share.client.mapper.CxxMdrCxxRepresentation> readCxxMdrCxxRepresentations() {
        return readFileAndGetResults(cxxMdrCxxRepresentationsFilename, new CxxMdrCxxRepresentationsLineAnalyzer());
    }


    public List<main.java.de.samply.share.client.mapper.CxxMdrRepresentation> readCxxMdrRepresentations (){
        return readFileAndGetResults(cxxMdrRepresentationsFilename, new CxxMdrRepresentationsLineAnalyzer());
    }

    public List<main.java.de.samply.share.client.mapper.TeilerBaseViewColumn> readTeilerBaseViewColumns (){
        return readFileAndGetResults(teilerBaseViewColumnsFilename, new TeilerBaseViewColumnsLineAnalyzer());
    }

    private interface LineAnalyzer{
        public void analyzeLine (String line);
        public List getResults();
    }

    private class CxxMdrCxxRepresentationsLineAnalyzer implements LineAnalyzer {

        private List<main.java.de.samply.share.client.mapper.CxxMdrCxxRepresentation> cxxMdrCxxRepresentations = new ArrayList<>();
        private Integer counter = 1;

        @Override
        public void analyzeLine(String line) {

            String[] split = line.split(";");
            if (split != null && split.length >= 3){

                main.java.de.samply.share.client.mapper.CxxMdrCxxRepresentation cxxMdrCxxRepresentation = new main.java.de.samply.share.client.mapper.CxxMdrCxxRepresentation();

                cxxMdrCxxRepresentation.setOid(counter++);
                cxxMdrCxxRepresentation.setMdrRepresentationOid(getInteger(split[0]));
                cxxMdrCxxRepresentation.setCxxClassName(split[1]);
                cxxMdrCxxRepresentation.setCxxValueName(split[2]);

                cxxMdrCxxRepresentations.add(cxxMdrCxxRepresentation);

            }

        }

        public List<main.java.de.samply.share.client.mapper.CxxMdrCxxRepresentation> getResults() {
            return cxxMdrCxxRepresentations;
        }

    }

    private Integer getInteger(String sNumber){

        try{
            return new Integer(sNumber);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    private class CxxMdrRepresentationsLineAnalyzer implements LineAnalyzer {

        private List<main.java.de.samply.share.client.mapper.CxxMdrRepresentation> cxxMdrRepresentations = new ArrayList<>();

        @Override
        public void analyzeLine(String line) {

            String[] split = line.split(";");
            if (split != null & split.length >= 3){

                main.java.de.samply.share.client.mapper.CxxMdrRepresentation cxxMdrRepresentation = new main.java.de.samply.share.client.mapper.CxxMdrRepresentation();

                cxxMdrRepresentation.setOid(getInteger(split[0]));
                cxxMdrRepresentation.setMdrMappingOid(getInteger(split[1]));
                cxxMdrRepresentation.setMdrPermittedValue(split[2]);
            }

        }

        public List<main.java.de.samply.share.client.mapper.CxxMdrRepresentation> getResults() {
            return cxxMdrRepresentations;
        }

    }

    private class TeilerBaseViewColumnsLineAnalyzer implements LineAnalyzer {

        private List<main.java.de.samply.share.client.mapper.TeilerBaseViewColumn> teilerBaseViewColumns = new ArrayList<>();

        @Override
        public void analyzeLine(String line) {

            String[] split = line.split(";");

            //TODO
        }

        public List<main.java.de.samply.share.client.mapper.TeilerBaseViewColumn> getResults() {
            return teilerBaseViewColumns;
        }

    }

    private List readFileAndGetResults (String filename, LineAnalyzer lineAnalyzer){

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                lineAnalyzer.analyzeLine(sCurrentLine);
            }

            return lineAnalyzer.getResults();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }


}
