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

/**
 * Created by michael on 06.03.17.
 */
$(document).ready(function() {

    var base64Credentials = $('#base64CredentialsDiv').text();

    if ($('#logTable').length) {
        var logtable = $('#logTable').DataTable( {
            "ajax": {
                "url": "../rest/inquiries/log",
                "type": "GET",
                "beforeSend": function (request) {
                    request.setRequestHeader('Authorization', 'Basic' + base64Credentials.toString());
                }
            },
            "columns": [
                { "data": "eventTime" },
                { "data": "message" }
            ],
            language: {
                url: "../javax.faces.resource/i18n/datatables_de_DE.json"
            },
            "columnDefs": [
                { type: 'de_datetime', targets: 0 }
            ],
            "order": [[ 0, "desc" ]],
            "pageLength": 50
        } );

        setInterval( function () {
            logtable.ajax.reload( null, false ); // user paging is not reset on reload
        }, 4000);
    }
});