package de.samply.share.client.quality.report.chainlinks.statistics.chainlink;/*
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

import java.util.HashMap;
import java.util.Map;

public enum ChainLinkStatisticKey {

    CONFIGURATION_STARTER ("configuration-starter", "starting configuration..."),
    CONFIGURATION_TERMINATOR ("configuration-terminator", "finalizing configuration"),
    QUALITY_REPORT_CSV_WRITER ("quality-report-csv-writer", "writing quality results in csv file..."),
    QUALITY_REPORT_EXCEL_WRITER ("quality-report-excel-writer", "writing quality results in Excel file..."),
    QUALITY_REPORT_METADATA_WRITER ("quality-report-metadata-writer", "writing metadata file..."),
    IGNORED_ELEMENTS_SETTER ("ignored-elements-setter", "enter MDR elements not mapped in local data management in quality report..."),
    NOT_FOUND_DATA_ELEMENTS_SETTER ("not found elements-setter", "enter all data elements not found in quality report..."),
    LOCAL_DATA_MANAGEMENT_RESULTS_REQUESTER ("localdatamanagement-results-requester", "requesting local data management results..."),
    LOCAL_DATA_MANAGEMENT_STATISTICS_REQUESTER("localdatamanagement-statistics-requester", "requesting local data management statistics..."),
    VALIDATOR ("validator", "validating quality results..."),
    VIEWS_CREATOR ("views-creator", "creating views for local data management..."),
    VIEWS_SENDER ("views-sender", "sending views to local data management..."),
    VIEWS_ONLY_STATISTICS_SENDER ("views-only-statistics-sender", "sending views with option 'only statistics' to local data management...");

    private String message;
    private String fileKey;

    private static Map<String, ChainLinkStatisticKey> mapByFileKey;

    ChainLinkStatisticKey(String fileKey, String message) {

        this.message = message;
        this.fileKey = fileKey;

    }

    public static ChainLinkStatisticKey getChainLinkStatisticKey (String fileKey){
        return getMapByFileKey().get(fileKey);
    }

    private static Map<String, ChainLinkStatisticKey> getMapByFileKey(){

        if (mapByFileKey == null){
            mapByFileKey = new HashMap<>();

            for (ChainLinkStatisticKey chainLinkStatisticKey : values()){
                mapByFileKey.put(chainLinkStatisticKey.getFileKey(), chainLinkStatisticKey);
            }
        }

        return mapByFileKey;

    }

    public String getMessage() {
        return message;
    }

    public String getFileKey() {
        return fileKey;
    }

}
