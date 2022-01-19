

$(document).ready(function () {
  if ($('#active-inquiries-table').length) {

    var userId = $('#userIdDiv').text();
    var base64Credentials = $('#base64CredentialsDiv').text();

    var activeInquiriesTable = $('#active-inquiries-table').DataTable({
      "ajax": {
        "url": "../rest/inquiries/active",
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
                  + "</a> <i class='fas fa-info-circle fa-fw' title='Source: "
                  + oData.brokerName + "'></i>");
            } else {
              $(nTd).html("<a href='show_inquiry.xhtml?inquiryId=" + oData.id
                  + "&faces-redirect=true'>" + oData.name
                  + "</a> <i class='fas fa-info-circle fa-fw' title='Source: "
                  + oData.brokerName + "'></i>");
            }
          }
        },
        {"data": "searchFor", "defaultContent": ""},
        {"data": "receivedAt", "defaultContent": ""},
        {"data": "found", "defaultContent": ""},
        {"data": "asOf", "defaultContent": ""}
      ],
      language: {
        url: "javax.faces.resource/i18n/datatables_de_DE.json"
      },
      "columnDefs": [
        {type: 'de_datetime', targets: [2, 4]}
      ],
      "pageLength": 25,
      "order": [[2, "desc"]]
    });

    setInterval(function () {
      activeInquiriesTable.ajax.reload(null, false); // user paging is not reset on reload
    }, 4000);
  }
});
