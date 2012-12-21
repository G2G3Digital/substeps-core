$(document).ready(function() {

	$('#feature-stats-div').html( '<table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered" id="feature-stats-table"></table>' );
	
	$('#feature-stats-table').dataTable( {
			"bPaginate": true,
			"bFilter": false,
			"bSort": true,
			"aaSorting": [[ 4, "desc" ]],
			"aaData": featureStatsData,
			
			"fnCreatedRow": function( nRow, aData, iDisplayIndex ) {
				/* Append the grade to the default row class name */
				if ( aData[4] > "0" )
				{
					$(nRow).addClass( "error_row");
				}
				return nRow;
			},
			
			"aoColumns": [
				{ "sTitle": "Tag" },
				{ "sTitle": "Features" },
				{ "sTitle": "Features run" },
				{ "sTitle": "Features passed" },
				{ "sTitle": "Features failed" },
				{ "sTitle": "Features success" }
			],
			"aoColumnDefs": [ 
			     			{
			     				"fnRender": function ( oObj ) {
			     					return oObj.aData[5] +' %';
			     				},
			     				"aTargets": [ 5 ]
			     			}]
		} );
	
	
	$('#scenario-stats-div').html( '<table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered" id="scenario-stats-table"></table>' );
	
	$('#scenario-stats-table').dataTable( {
			"bPaginate": true,
			"bFilter": false,
			"bSort": true,
			"aaSorting": [[ 4, "desc" ]],
			"aaData": scenarioStatsData,
			
			"fnCreatedRow": function( nRow, aData, iDisplayIndex ) {
				/* Append the grade to the default row class name */
				if ( aData[4] > "0" )
				{
					$(nRow).addClass( "error_row");
				}
				return nRow;
			},
			
			"aoColumns": [
				{ "sTitle": "Tag" },
				{ "sTitle": "Scenarios" },
				{ "sTitle": "Scenarios run" },
				{ "sTitle": "Scenarios passed" },
				{ "sTitle": "Scenarios failed" },
				{ "sTitle": "Scenarios success" }
			],
			"aoColumnDefs": [ 
			     			{
			     				"fnRender": function ( oObj ) {
			     					return oObj.aData[5] +' %';
			     				},
			     				"aTargets": [ 5 ]
			     			}]
		} );		
	
	
	var treeClick = function (event) {
        event.preventDefault();
        var id = jQuery(this).attr("id");

		var detailJSON = detail[id];
		if (detailJSON ) {
		
			var detailhtml = "<p>" + detailJSON.result +"</p>"
		
			if (detailJSON.filename.length >0){
				detailhtml += "<p>File: " +  detailJSON.filename + "</p>";
			}
		 
			detailhtml += "<p>" + detailJSON.nodetype + ": " + detailJSON.description + "</p>";
		
			if (detailJSON.method > 0){
				detailhtml = detailhtml + "<p>Method: " + detailJSON.method + "</p>";
			}

			if(detailJSON.screenshot) {
				detailhtml = detailhtml + "<p><a href='" + detailJSON.screenshot + "'><img style='border: 2px solid red;' width='400px;' src='" + detailJSON.screenshot + "' alt='screenshot of failure' /></a>";
			}
			
			if (detailJSON.emessage.length > 0){
				detailhtml = detailhtml + "<p>" + detailJSON.emessage + "</p><div class=\"stacktrace\"><pre class=\"stacktracepre\">" +
				detailJSON.stacktrace + "</div></pre>";
			}
			
			detailhtml = detailhtml + "<p>Duration: " + detailJSON.runningDurationString + "</p>"
			
			if (detailJSON.children && detailJSON.children.length > 0){
				
				detailhtml = detailhtml + '<table class="table table-bordered table-condensed"><tbody>';
				
				for (i=0;i<detailJSON.children.length;i++){
				
					detailhtml = detailhtml + "<tr";
					if (detailJSON.children[i].result == 'PASSED'){
						detailhtml = detailhtml +' class="success"'
					}
					else if (detailJSON.children[i].result == 'FAILED'){
						detailhtml = detailhtml +' class="error"'
					}
					
					detailhtml = detailhtml + "><td>" + detailJSON.children[i].description + "</td></tr>";
				 }
				detailhtml = detailhtml + "</tbody></table>";
			}
	
			$("#feature-detail").html(detailhtml);
	
			// get the offset of where we should be?
			var topOffsetShouldbe = $("#detail-div-container").offset().top;
	
			// get the offset of the affixed div
			var affixOffset = $("#affix-marker").offset().top;
	
			// so the absolute position position, relative to the parent should be affixOffset - topOffsetShouldbe
			var absPosition = affixOffset - topOffsetShouldbe;
			
			if (absPosition < 0){
				absPosition = 0;
			}
	
			$("#feature-detail").css("top", absPosition + 'px');	
	
		    $('[data-spy="affix"]').each(function () {
		    	$(this).affix('refresh');
		    });
	
		}
		
    };

    jQuery("#feature-tree").jstree({
        "json_data":{
            "data":treeData,
            "progressive_render":true
        },
        "plugins":[ "themes", "json_data", "ui" ]
    })
    .delegate("a", "click", treeClick);

 });


