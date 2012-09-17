(function () {

    var treeClick = function (event) {
        event.preventDefault();
        var id = jQuery(this).attr("id");
        jQuery.ajax({
            url:id + "-details.html",
            type:"GET",
            dataType:"text html"
        })
            .done(function (data) {
                jQuery("#feature-detail").html(data);
            })
            .fail(function (jqXHR, textStatus, errorThrown) {
                alert("Loading test details failed: " + textStatus + " : [" + errorThrown + "]");
            });
    };

    var treeLoad = function (data) {
        jQuery("#feature-tree").jstree({
            "json_data":{
                "data":data,
                "progressive_render":true
            },
            "plugins":[ "themes", "json_data", "ui" ]
        })
            .delegate("a", "click", treeClick);
    };

    $(function () {

        jQuery.ajax({
            url:"tree.json?callback=?",
            type:"GET",
            dataType:"text json"
        })
            .done(treeLoad)
            .fail(function (jqXHR, textStatus, errorThrown) {
                alert("Loading test json data failed: " + textStatus + " : [" + errorThrown + "]");
            });


    });
})();