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

package de.samply.share.client.rest;

import com.google.gson.Gson;
import de.samply.share.client.model.line.EventLogLine;
import de.samply.share.client.model.db.tables.pojos.EventLog;
import de.samply.share.client.util.db.EventLogUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Datasource for the datatables plugin on the event log page.
 */
@Path("/log")
public class EventLogDatasource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLog() {
        Response response;

        StringBuilder stringBuilder = new StringBuilder();
        Gson gson = new Gson();
        String json = gson.toJson(loadEventLogList());

        stringBuilder.append("{\"data\": ");
        stringBuilder.append(json);
        stringBuilder.append("}");

        response = Response.ok(stringBuilder.toString()).build();
        return response;
    }

    private List<EventLogLine> loadEventLogList() {
        List<EventLogLine> eventLogLines = new ArrayList<>();
        List<EventLog> eventLogs = EventLogUtil.fetchEventLogGlobal();
        for (EventLog el : eventLogs) {
            eventLogLines.add(new EventLogLine(el));
        }
        return eventLogLines;
    }
}
