var ITextDocumentURIHelper = {
		remove : function (row) {
			if (row == null || row.length <= 0 || row.nodeName != "TR") {
				return;
			}

			/* 
			 * I don't wait for callback. What is the point in waiting? 
			 * If this won't work, then we have a bug to be fixed
			 */
			var id = jQuery("input:hidden", row).val();
			LazyLoader.loadMultiple(['/dwr/engine.js', '/dwr/interface/ITextDocumentService.js'], function() {
				ITextDocumentService.remove(id);
			});

			jQuery(row).remove();
		}
};