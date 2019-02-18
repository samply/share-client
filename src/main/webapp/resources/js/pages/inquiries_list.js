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

$(document).ready(function() {
    if ($('#active-inquiries-table').length) {

        var activeInquiriesTable = $('#active-inquiries-table').DataTable( {
            "ajax": function (data, callback, settings) {
                callback(
                    JSON.parse($('#activeInquiriesDiv').text())
                );
            },
            "columns": [
                { "data": "name",
                    "fnCreatedCell": function (nTd, sData, oData, iRow, iCol) {
                        if (!oData.seen) {
                            $(nTd).html("<a href='show_inquiry.xhtml?inquiryId="+ oData.id +"&faces-redirect=true' style='font-weight: bold;'>"+oData.name+"</a> <i class='fa fa-info-circle fa-fw' title='Source: "+ oData.brokerName +"'></i>");
                        } else {
                            $(nTd).html("<a href='show_inquiry.xhtml?inquiryId="+ oData.id +"&faces-redirect=true'>"+oData.name+"</a> <i class='fa fa-info-circle fa-fw' title='Source: "+ oData.brokerName +"'></i>");
                        }
                    }
                },
                { "data": "searchFor" },
                { "data": "receivedAt" },
                { "data": "found" },
                { "data": "asOf" }
            ],
            language: {
                url: "javax.faces.resource/i18n/datatables_de_DE.json"
            },
            "columnDefs": [
                { type: 'de_datetime', targets: [2, 4] }
            ],
            "pageLength": 25,
            "order": [[ 2, "desc" ]]
        } );

        setInterval( function () {
            activeInquiriesTable.ajax.reload( null, false ); // user paging is not reset on reload
        }, 4000);
    }
});