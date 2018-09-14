$(document).ready(function() {
    bootstrapSwitchOn();

	$('#matches').on('hide.bs.collapse', function(e) {
	    $(e.target)
    		.prev().css({'font-size' : '14px',
    					'font-weight' : 'normal'
    			})
	    	.parent()
	        .find(".expandIcon:first")
	        .removeClass('fa-rotate-45');
	});
	
	$('#matches').on('show.bs.collapse', function(e) {
	    $(e.target)
    	.prev().css({'font-size' : '18px',
					'font-weight' : 'bold'
			})
    	.parent()
        .find(".expandIcon:first")
    	.addClass('fa-rotate-45');
	});

    $(".collapsiblePanel").on("click", ".panel-heading", function() {
        $(this).parents('.panel').find('.collapse').collapse('toggle');
	});


    $('.collapsiblePanel').on({
        'hide.bs.collapse': function () {
            $(this).find('i.fa-chevron-down').removeClass('fa-chevron-down').addClass('fa-chevron-right');
        },
        'show.bs.collapse': function () {
            $(this).find('i.fa-chevron-right').removeClass('fa-chevron-right').addClass('fa-chevron-down');
        }
    });

	if ($('.clockpicker')[0]) {
	    $('.clockpicker').clockpicker({
	        donetext: 'Done'
	    });
	}
	if ($('.inputmask')[0]) {
	    $('.inputmask').inputmask({
	      mask: '9?999'
	    });
	}
    $('.dashboard-panel').click(function(event) {
        event.stopPropagation();
        $(this).siblings('.go-to-page').children('a').trigger("click");
    });

	$('.hasTooltip').tooltip();

    $('.inquiry-table').on({
        mouseenter: function () {
            var $this = $(this); // caching $(this)
            $this.data('initialText', $this.text());
            $this.text($this.attr('title'));
        },
        mouseleave: function () {
            var $this = $(this); // caching $(this)
            $this.text($this.data('initialText'));
        }
    }, ".requested-entity-label-abbr");

    $(".inquiry-table").on("click", "tr a", function() {
        $('#pleaseWait').modal('show');
    });
});

function bootstrapSwitchOn() {
    if ($('.switch')[0]) {
        $('.switch').bootstrapSwitch();
    }
}