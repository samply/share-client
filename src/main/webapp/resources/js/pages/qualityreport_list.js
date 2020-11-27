

/**
 * Created by michael on 06.03.17.
 */
$(document).ready(function () {

  if ($('#quality-report-list').length) {

    var qualityReportTable = $('#quality-report-list').DataTable({
      language: {
        url: "../javax.faces.resource/i18n/datatables_de_DE.json"
      },
      "bSort": false,
      "columnDefs": [
        {type: 'de_datetime', targets: 1}
      ],
      "pageLength": 25,
      "order": [[0, "desc"]]
    });

    setInterval(function () {

      updateQualityReportConditions();
      updateQualityReportProgressbar();
      updateTimeout();

      if ($('#isQualityReportLoading').val() == "false") {
        updateQualityReportButton();

        if ($('#isQualityReportStatusChanged').val() == "true") {
          updateQualityReportFileListGroup();
        }

      }

    }, 2000);

  }

});
