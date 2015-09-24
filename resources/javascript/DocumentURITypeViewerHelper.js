jQuery(document).ready(function() {
	LazyLoader.loadMultiple(['/idegaweb/bundles/com.idega.block.web2.0/resources/javascript/tablesorter/jquery.tablesorter.min.js'], function() {
		$("#editorTable").tablesorter(); 
	}, null);
});

var DocumentURITypeViewerHelper = {
		remove : function (row) {
			if (row == null || row.length <= 0 || row.nodeName != "TR") {
				return;
			}

			/* 
			 * I don't wait for callback. What is the point in waiting? 
			 * If this won't work, then we have a bug to be fixed
			 */
			var id = jQuery("input:hidden", row).val();
			DocumentURITypeDAO.remove(id);
			jQuery(row).remove();
		}
}