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

$(document).ready(function () {
    $(".documentUploadFileInput").fileinput({
        language: 'de',
        browseLabel: "Datei wählen",
        browseIcon: "<i class='fa fa-folder-open-o'></i>&nbsp;",
        removeLabel: "Entfernen",
        removeIcon: "<i class='fa fa-ban'></i>&nbsp;",
        uploadLabel: "Hochladen",
        uploadIcon: "<i class='fa fa-upload'></i>&nbsp;",
        uploadTitle: 'Dokument zur Liste hinzufügen',
        previewFileIcon: ''
    });

    $('.documentBoxForm').submit(function (e) {
        $(this).find('.uploadTheFile').trigger("click");
        e.preventDefault();
    });

    createEventhandlers();
});

function createEventhandlers() {
    $('.hasPopover').popover({
        placement: 'left',
        container: 'body',
        trigger: 'focus'
    });

    $('.hasConfirmPopover').popover({
        placement: 'left',
        trigger: 'focus',
        container: 'body',
        content: function () {
            var elementId = $(this).parent().find('.idDiv :input').val();
            var elementType = $(this).parent().find('.typeDiv :input').val();
            return "Wirklich löschen?<br /><br /><button class='btn btn-success btn-block popover-submit' type='button' onclick='deleteDocument({ elementId: " + elementId + " })' >Löschen</button>";

        },
        html: true
    });

    $('.archivePopover').popover({
        placement: 'bottom',
        trigger: 'focus',
        container: 'body',
        content: function () {
            return "Wirklich archivieren?<br /><br /><button class='btn btn-success btn-block popover-submit' type='button' onclick='$(\".archive-action\").click();' >Archivieren</button>";

        },
        html: true
    });
}