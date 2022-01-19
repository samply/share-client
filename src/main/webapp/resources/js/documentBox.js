

$(document).ready(function () {
  $(".documentUploadFileInput").fileinput({
    language: 'de',
    browseLabel: "Datei wählen",
    browseIcon: "<i class='fas fa-folder-open-o'></i>&nbsp;",
    removeLabel: "Entfernen",
    removeIcon: "<i class='fas fa-ban'></i>&nbsp;",
    uploadLabel: "Hochladen",
    uploadIcon: "<i class='fas fa-upload'></i>&nbsp;",
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
      return "Wirklich löschen?<br /><br /><button class='btn btn-success btn-block popover-submit' type='button' onclick='deleteDocument({ elementId: "
          + elementId + " })' >Löschen</button>";

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
