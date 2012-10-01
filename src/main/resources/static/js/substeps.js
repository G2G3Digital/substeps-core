

$(document).ready(function() {

	var treeClick = function (event) {
        event.preventDefault();
        var id = jQuery(this).attr("id");

	// get the iframe and set the inner html to be...
	
//	var url = "" + id + "-details.html";
	
	//alert("set frame to: " + url);	

//	document.getElementById("feature-detail-frame").src = url;

	var detailJSON = detail[id];
	
	var detailhtml = "<p>" + detailJSON.nodetype +
	" - " + detailJSON.filename +" - Result: " + 
	detailJSON.result + "</p>File: " + detailJSON.id + " in <p>Details: " +
	detailJSON.debugstr + "</p>";
	
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
	
	//	var topoffset = $("#affix-marker").position().top;
	
	//	var leftoffset = $("#feature-detail").offset().left;
	//	var marginleft =  $("#feature-detail").css("margin-left");
	
	//	alert ('topoffset: ' + topoffset);
	
	//alert ('parsedleft: ' + parseInt(leftoffset) );
	//alert ('parsedmargin: ' + parseInt(marginleft));
	
	//	var newleftOffest =  parseInt(leftoffset) - parseInt(marginleft);
	//	var newTopoffset = 0 - parseInt(topoffset);
	
	//	alert ('newTopoffset: ' + newTopoffset);
	
	//	$("#feature-detail").css("top", newTopoffset + 'px');
			
	//	var postSet = $("#feature-detail").css("left");
	//	alert('post set' + postSet);	
	
	 //   $('[data-spy="affix"]').each(function () {
	 //   	$(this).affix('refresh');
	 //   });
		
		
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


