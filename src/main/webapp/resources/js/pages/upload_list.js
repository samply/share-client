

/**
 * Created by michael on 06.03.17.
 */
$(document).ready(function () {
  if ($('#upload-table').length) {
    var uploadTable = $('#upload-table').DataTable({
      language: {
        url: "../javax.faces.resource/i18n/datatables_de_DE.json"
      },
      "columnDefs": [
        {type: 'de_datetime', targets: 0}
      ],
      "pageLength": 10,
      "order": [[0, "desc"]]
    });

    setInterval(function () {
      updateButtonBar();
    }, 5000);
  }
});
