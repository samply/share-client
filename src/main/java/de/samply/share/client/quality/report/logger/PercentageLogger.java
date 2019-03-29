package de.samply.share.client.quality.report.logger;/*
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

import org.apache.logging.log4j.Logger;

public class PercentageLogger {

    private Logger logger;
    private int numberOfElements;
    private int counter = 0;
    private int lastPercentage = 0;

    public PercentageLogger(Logger logger, int numberOfElements, String description) {

        this.logger = logger;
        this.numberOfElements = numberOfElements;
        if (numberOfElements > 0) {
            logger.debug(description);
        }

    }

    public void incrementCounter(){

        if (numberOfElements > 0) {

            counter++;

            Double percentage = 100.0D * ((double) counter) / ((double) numberOfElements);
            int ipercentage = percentage.intValue();

            if (lastPercentage != ipercentage) {

                lastPercentage = ipercentage;

                if (ipercentage % 10 == 0) {
                    logger.debug(ipercentage + " %");
                }

            }
        }


    }


}
