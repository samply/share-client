/*
 * Copyright (c) 2017 Medical Informatics Group (MIG),
 * Universitätsklinikum Frankfurt
 *
 * Contact: www.mig-frankfurt.de
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
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */

package de.samply.share.client.util;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.centralsearch.DateRestriction;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.common.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutionException;

/**
 * Utility class for methods to create Views and Queries
 */
public class UploadUtils {

    private static final Logger logger = LogManager.getLogger(UploadUtils.class);

    private UploadUtils() {

    }

    /**
     * Create a View without any date restrictions
     * @param dktkFlagged
     *          when set to true, only patients WITH explicit DKTK consent are requested
     *          when set to false, only patients WITHOUT explicit DKTK consent are requested
     * @return the view
     */
    public static View createFullUploadView(boolean dktkFlagged) throws MdrConnectionException {
        return createUploadView(null, dktkFlagged);
    }

    /**
     * Create a View with the given date restrictions
     *
     * @param dateRestriction
     *          the date restriction object, specifying upper and lower bounds
     * @param dktkFlagged
     *          when set to true, only patients WITH explicit DKTK consent are requested
     *          when set to false, only patients WITHOUT explicit DKTK consent are requested
     * @return the view
     */
    public static View createUploadView(DateRestriction dateRestriction, boolean dktkFlagged) throws MdrConnectionException {
        try {
            View view = new View();
            view.setQuery(createUploadQuery(dateRestriction, dktkFlagged));
            view.setViewFields(MdrUtils.getViewFields(true));
            return view;
        } catch (MdrConnectionException | ExecutionException e) {
            throw new MdrConnectionException(e.getMessage());
        }
    }

    /**
     * Create a Query without any date restrictions
     * @param dktkFlagged
     *          when set to true, only patients WITH explicit DKTK consent are requested
     *          when set to false, only patients WITHOUT explicit DKTK consent are requested
     * @return the view
     */
    public static Query createFullUploadQuery(boolean dktkFlagged) {
        return createUploadQuery(null, dktkFlagged);
    }

    /**
     * Create a Query with the given date restrictions
     *
     * @param dateRestriction
     *          the date restriction object, specifying upper and lower bounds
     * @param dktkFlagged
     *          when set to true, only patients WITH explicit DKTK consent are requested
     *          when set to false, only patients WITHOUT explicit DKTK consent are requested
     * @return the view
     */
    public static Query createUploadQuery(DateRestriction dateRestriction, boolean dktkFlagged) {
        ObjectFactory objectFactory = new ObjectFactory();
        Query query = new Query();
        Where where = new Where();
        And and = new And();

        Attribute attr_dktkFlag = new Attribute();
        MdrIdDatatype mdrKeyDktkConsent = new MdrIdDatatype(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_KEY_CONSENT_DKTK));
        attr_dktkFlag.setMdrKey(mdrKeyDktkConsent.getLatestCentraxx());
        attr_dktkFlag.setValue(objectFactory.createValue(Boolean.toString(dktkFlagged)));

        Eq equals = new Eq();
        equals.setAttribute(attr_dktkFlag);

        // If the patients without explicit consent are wanted, check for dktkflag is false OR null
        if (dktkFlagged) {
            and.getAndOrEqOrLike().add(equals);
        } else {
            Or or = new Or();
            IsNotNull isNotNull = new IsNotNull();
            isNotNull.setMdrKey(mdrKeyDktkConsent.getLatestCentraxx());

            or.getAndOrEqOrLike().add(equals);
            or.getAndOrEqOrLike().add(isNotNull);
            and.getAndOrEqOrLike().add(or);
        }

        // Set the upper and lower bounds for the query if the date restrictions are set
        if (dateRestriction != null) {
            MdrIdDatatype mdrKeyUploadFrom = new MdrIdDatatype(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_KEY_UPLOAD_FROM));
            MdrIdDatatype mdrKeyUploadTo = new MdrIdDatatype(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_KEY_UPLOAD_TO));
            Attribute attr_from = new Attribute();
            attr_from.setMdrKey(mdrKeyUploadFrom.getLatestCentraxx());
            attr_from.setValue(objectFactory.createValue(dateRestriction.getLastUpload()));

            Gt greaterThan = new Gt();
            greaterThan.setAttribute(attr_from);
            and.getAndOrEqOrLike().add(greaterThan);

            Attribute attr_to = new Attribute();
            attr_to.setMdrKey(mdrKeyUploadTo.getLatestCentraxx());
            attr_to.setValue(objectFactory.createValue(dateRestriction.getServerTime()));

            Leq lessOrEqual = new Leq();
            lessOrEqual.setAttribute(attr_to);
            and.getAndOrEqOrLike().add(lessOrEqual);
        }


        if (!SamplyShareUtils.isNullOrEmpty(and.getAndOrEqOrLike())) {
            where.getAndOrEqOrLike().add(and);
        }
        query.setWhere(where);
        return query;
    }
}
