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
 * Created by michael on 16.03.17.
 */
function initPagination() {
    $('.pagination-md').twbsPagination({
        totalPages: $('#resultPages').val(),
        visiblePages: 15,
        first: '&lt;&lt;',
        prev: '&lt;',
        next: '&gt;',
        last: '&gt;&gt;',
        initiateStartPageClick: false,
        hideOnlyOnePage: true,
        onPageClick: function (event, page) {
            changeResultPage({page: page});
        }
    });
}

function handleGenerateExportResponse(data) {
    if (data.status == "begin") {
        $('#pleaseWait').modal('show');
    } else if (data.status == "success") {
        $('#pleaseWait').modal('hide');
    }
}

function blockElement(selector) {
    if ($(selector)[0]) {
        $(selector).block({
            message: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i><span class="sr-only">Loading...</span>',
            css: {'border': 'none', 'background-color': 'transparent'}
        });
    }
}

function unblockElement(selector) {
    if ($(selector)[0]) {
        $(selector).unblock();
    }
}

$(document).ready(function () {
    if ($('#chartData')[0]) {
        $.jqplot.config.enablePlugins = true;
        var ageData = $('#chartData .age').html();
        var ageDataJson = $.parseJSON(ageData);
        var ageKeys = ["0...9", "10...19", "20...29", "30...39", "40...49", "50...59", "60...69", "70...79", "80...89", "90...99", "100+", "unbekannt"];
        var ageValues = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];


        var genderData = $('#chartData .gender').html();
        var genderDataJson = $.parseJSON(genderData);
        var genderKeys = [];
        var genderValues = [];

        $.each(ageDataJson.data, function (key, value) {
            var keyInt = parseInt(key);
            if (keyInt == -1) {
                ageValues[11] = ageValues[11] + value;
            } else if (keyInt >= 100) {
                ageValues[10] = ageValues[10] + value;
            } else {
                var index = (keyInt / 10 >> 0);
                ageValues[index] = ageValues[index] + value;
            }
        });

        var plot1 = $.jqplot('chart-age', [ageValues], {
            title: 'Alter bei Erstdiagnose',
            animate: !$.jqplot.use_excanvas,
            seriesDefaults: {
                renderer: $.jqplot.BarRenderer,
                pointLabels: {
                    show: true,
                    edgeTolerance: -20
                }
            },
            axesDefaults: {
                labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
                tickRenderer: $.jqplot.CanvasAxisTickRenderer,
                min: 0
            },
            axes: {
                xaxis: {
                    label: "Alter in Jahren",
                    renderer: $.jqplot.CategoryAxisRenderer,
                    ticks: ageKeys,
                    tickOptions: {
                        angle: -30,
                        formatString: '%d'
                    }
                },
                yaxis: {
                    label: "Ergebnisse"
                }
            },
            highlighter: {
                show: false
            }
        });

        $.each(genderDataJson.data, function (key, value) {
            genderKeys.push(key);
            genderValues.push(value);
        });

        var plot2 = $.jqplot('chart-gender', [genderValues], {
            title: 'Geschlecht',
            animate: !$.jqplot.use_excanvas,
            seriesDefaults: {
                renderer: $.jqplot.BarRenderer,
                pointLabels: {
                    show: true,
                    edgeTolerance: -20
                }
            },
            axesDefaults: {
                labelRenderer: $.jqplot.CanvasAxisLabelRenderer
            },
            axes: {
                xaxis: {
                    label: "Geschlecht",
                    renderer: $.jqplot.CategoryAxisRenderer,
                    ticks: genderKeys
                },
                yaxis: {
                    label: "Ergebnisse"
                }
            },
            highlighter: {
                show: false
            }
        });
    }
});