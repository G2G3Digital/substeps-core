// image variables used in the tree
var imgP = "img/PASSED.png";
var imgNR = "img/NOT_RUN.png";
var imgPF = "img/PARSE_FAILURE.png";
var imgF = "img/FAILED.png";




$(document).ready(function() {

	$('#datatable-div').html( '<table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered" id="example"></table>' );
	
$('#example').dataTable( {
		"bPaginate": false,
		"bFilter": false,
		"bSort": true,

		"aaData": statsData,
		"aoColumns": [
			{ "sTitle": "Tag" },
			{ "sTitle": "Features" },
			{ "sTitle": "Features run" },
			{ "sTitle": "Features passed" },
			{ "sTitle": "Features failed" },
			{ "sTitle": "Features success" },
			
			{ "sTitle": "Scenarios" },
			{ "sTitle": "Scenarios run" },
			{ "sTitle": "Scenarios passed" },
			{ "sTitle": "Scenarios failed" },
			{ "sTitle": "Scenarios success" }
				
			
		]
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
		 
		 detailhtml += "<p>" + detailJSON.nodetype + ": " + detailJSON.desc + "</p>";
		
		if (detailJSON.method.length > 0){
			
			detailhtml = detailhtml + "<p>Method: " + detailJSON.method + "</p>";
		}
 		
//	var detailhtml = "<p>" + detailJSON.nodetype + ": " + detailJSON.result +"</p> File: " +  
//	 detailJSON.filename + "<br/>" + 
//	" in <p>Details: " +
//	detailJSON.desc + "</p>";
	
	if (detailJSON.emessage.length > 0){
		detailhtml = detailhtml + "<p>" + detailJSON.emessage + "</p><div class=\"stacktrace\"><pre class=\"stacktracepre\">" +
		detailJSON.stacktrace + "</div></pre>";
	}

	if (detailJSON.children.length > 0){
		
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
	
	//alert ('topOffsetShouldbe: ' + topOffsetShouldbe);
	
	// get the offset of the affixed div
	var affixOffset = $("#affix-marker").offset().top;
	
	// so the absolute position position, relative to the parent should be affixOffset - topOffsetShouldbe
	var absPosition = affixOffset - topOffsetShouldbe;
	
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


