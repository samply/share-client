package de.samply.share.client.filter;

import com.google.common.base.Joiner;
import de.samply.share.client.control.LoginBean;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omnifaces.filter.HttpFilter;
import org.omnifaces.util.Servlets;

/**
 * This HttpFilter prevents access to the standard user pages without being logged in.
 */
@WebFilter("/user/*")
public class AccessControlFilterUser extends HttpFilter {

  private static final Logger logger = LogManager.getLogger(AccessControlFilterUser.class);
  private static final String SESSION_USER = "user";

  @Override
  public void doFilter(HttpServletRequest request, HttpServletResponse response,
      HttpSession session, FilterChain chain) throws ServletException, IOException {
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
   * Generate a String to put into the url as parameter, so that a redirect to the correct page is
   * possible from login. Escape the parameter separator - & - with "__" so that it will be
   * recognized as one parameter later.
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
      stringBuilder.append(LoginBean.ESCAPED_PARAMETER_SEPARATOR).append(stringListEntry.getKey())
          .append("=").append(Joiner.on(',').join(stringListEntry.getValue()));
    }
    return stringBuilder.toString();
  }
}
