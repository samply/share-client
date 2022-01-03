package de.samply.share.client.filter;

import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.tables.pojos.EventLog;
import de.samply.share.client.model.db.tables.pojos.User;
import de.samply.share.client.util.db.EventLogUtil;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.omnifaces.filter.HttpFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This HttpFilter prevents users without admin privilege from accessing the admin area.
 * Unauthorized access is also logged in the event log.
 */
@WebFilter("/admin/*")
public class AccessControlFilterAdmin extends HttpFilter {

  private static final Logger logger = LoggerFactory.getLogger(AccessControlFilterAdmin.class);
  private static final String SESSION_USER = "user";

  @Override
  public void doFilter(HttpServletRequest request, HttpServletResponse response,
      HttpSession session, FilterChain chain) throws ServletException, IOException {
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
