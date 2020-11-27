

/**
 * Created by michael on 06.03.17.
 */
$(document).ready(function () {

  var base64Credentials = $('#base64CredentialsDiv').text();

  if ($('#logTable').length) {
    var logtable = $('#logTable').DataTable({
      "ajax": {
        "url": "../rest/inquiries/log",
        "type": "GET",
        "beforeSend": function (request) {
          request.setRequestHeader('Authorization',
              'Basic' + base64Credentials.toString());
        }
      },
      "columns": [
        {"data": "eventTime"},
        {"data": "message"}
      ],
      language: {
        url: "../javax.faces.resource/i18n/datatables_de_DE.json"
      },
      "columnDefs": [
        {type: 'de_datetime', targets: 0}
      ],
      "order": [[0, "desc"]],
      "pageLength": 50
    });

    setInterval(function () {
      logtable.ajax.reload(null, false); // user paging is not reset on reload
    }, 4000);
  }
});
