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

import com.google.common.base.Joiner;
import de.samply.share.client.control.LoginBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omnifaces.filter.HttpFilter;
import org.omnifaces.util.Servlets;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This HttpFilter prevents access to the standard user pages without being logged in
 */
@WebFilter("/user/*")
public class AccessControlFilterUser extends HttpFilter {

    private static final Logger logger = LogManager.getLogger(AccessControlFilterUser.class);
    private static final String SESSION_USER = "user";

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, HttpSession session, FilterChain chain) throws ServletException, IOException {
        String loginUrl = request.getContextPath() + "/login.xhtml";
        boolean resourceRequest = Servlets.isFacesResourceRequest(request);
        boolean loggedIn = (session != null) && (session.getAttribute(SESSION_USER) != null);

        if (loggedIn) {
            if (!resourceRequest) {
                Servlets.setNoCacheHeaders(response);
            }
            // User is logged in, so just continue request.
            chain.doFilter(request, response);
        } else {
            // User is not logged in, so redirect to index.
            try {
                logger.debug("Unauthorized access attempt. Redirect to login.");
            } catch (NullPointerException npe) {
                logger.warn("User NULL tried to access user page. Access denied.");
            }

            Servlets.facesRedirect(request,
                    response,
                    loginUrl + createRedirectString(request));
        }
    }

    /**
     * Generate a String to put into the url as parameter, so that a redirect to the correct page is possible from login
     *
     * Escape the parameter separator - & - with "__" so that it will be recognized as one parameter later
     *
     * @param request the servlet request
     * @return an escaped parameter string to pass to the login page
     */
    private String createRedirectString(HttpServletRequest request) {
        Map<String, List<String>> requestQueryStringMap = Servlets.getRequestQueryStringMap(request);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("?")
                .append(LoginBean.REQUESTED_PAGE_PARAMETER)
                .append("=")
                .append(Servlets.getRequestRelativeURI(request));
        for (Map.Entry<String, List<String>> stringListEntry : requestQueryStringMap.entrySet()) {
            stringBuilder.append(LoginBean.ESCAPED_PARAMETER_SEPARATOR).append(stringListEntry.getKey()).append("=").append(Joiner.on(',').join(stringListEntry.getValue()));
        }
        return stringBuilder.toString();
    }
}