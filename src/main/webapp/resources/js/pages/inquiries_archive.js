

/**
 * Created by michael on 06.03.17.
 */
$(document).ready(function () {

  var userId = $('#userIdDiv').text();
  var base64Credentials = $('#base64CredentialsDiv').text();

  if ($('#archived-inquiries-table').length) {
    var archivedInquiriesTable = $('#archived-inquiries-table').DataTable({
      "ajax": {
        "url": "../rest/inquiries/archived",
        "type": "GET",
        "beforeSend": function (request) {
          request.setRequestHeader('userid', parseInt(userId));
          request.setRequestHeader('Authorization',
              'Basic' + base64Credentials.toString());
        }
      },
      "columns": [
        {
          "data": "name",
          "fnCreatedCell": function (nTd, sData, oData, iRow, iCol) {
            if (!oData.seen) {
              $(nTd).html("<a href='show_inquiry.xhtml?inquiryId=" + oData.id
                  + "&faces-redirect=true' style='font-weight: bold;'>"
                  + oData.name
                  + "</a> <i class='fa fa-info-circle fa-fw' title='Source: "
                  + oData.brokerName + "'></i>");
            } else {
              $(nTd).html("<a href='show_inquiry.xhtml?inquiryId=" + oData.id
                  + "&faces-redirect=true'>" + oData.name
                  + "</a> <i class='fa fa-info-circle fa-fw' title='Source: "
                  + oData.brokerName + "'></i>");
            }
          }
        },
        {"data": "searchFor", "defaultContent": "", "defaultContent": ""},
        {"data": "receivedAt", "defaultContent": "", "defaultContent": ""},
        {"data": "archivedAt", "defaultContent": "", "defaultContent": ""}
      ],
      language: {
        url: "javax.faces.resource/i18n/datatables_de_DE.json"
      },
      "columnDefs": [
        {type: 'de_datetime', targets: [2, 3]}
      ],
      "pageLength": 25,
      "order": [[3, "desc"]]
    });

    setInterval(function () {
      archivedInquiriesTable.ajax.reload(null, false); // user paging is not reset on reload
    }, 4000);
  }
});
