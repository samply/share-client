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

package de.samply.share.client.filter;

import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.tables.pojos.EventLog;
import de.samply.share.client.model.db.tables.pojos.User;
import de.samply.share.client.util.db.EventLogUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omnifaces.filter.HttpFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * This HttpFilter prevents users without admin privilege from accessing the admin area
 *
 * Unauthorized access is also logged in the event log
 */
@WebFilter("/admin/*")
public class AccessControlFilterAdmin extends HttpFilter {

    private static final Logger logger = LogManager.getLogger(AccessControlFilterAdmin.class);
    private static final String SESSION_USER = "user";

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, HttpSession session, FilterChain chain) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(SESSION_USER);

        if (user != null && user.getAdminPrivilege() != null && user.getAdminPrivilege()) {
            // User is logged in, so just continue request.
            chain.doFilter(request, response);
        } else {
            // User is not logged in, so redirect to index.
            EventLog logEntry = new EventLog();
            logEntry.setEventType(EventMessageType.E_UNAUTHORIZED_ATTEMPT_TO_ACCESS_ADMIN_AREA);
            try {
                logger.warn("User " + user.getUsername() + " tried to access admin area. Access denied.");
                logEntry.setUserId(user.getId());
            } catch (NullPointerException npe) {
                logger.warn("User NULL tried to access admin area. Access denied.");
            }
            EventLogUtil.insertEventLog(logEntry);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}