tinyMCE.init({
	selector:'textarea',
	height : '100%',
	width : '100%',
	setup : function(ed) {
		ed.onChange.add(function(ed, l) {
			var processDefinitionId = jQuery("input[id='editorForm:oldProcessDefinitionId']").val();
			RichTextDocumentService.updateTempolarPDF(processDefinitionId, tinyMCE.activeEditor.getContent(), {
				callback: function(result) {
					jQuery("a.windowLink").attr("href", result);
				}
			});
		});
	}
});

jQuery(document).ready(function() {
	RichTextDocumentService.updateTempolarPDF(null, jQuery('textarea[id = "editorForm:sourceCode"]').val(), {
		callback: function(result) {
			jQuery("a.windowLink").attr("href", result);
		}
	});

	jQuery(".windowLink").fancybox({
		type:		"iframe",
		autoSize:	false,
		beforeShow:	function () {
			var iframe = jQuery(".fancybox-iframe");
			if (iframe != null) {
				this.width = 800;
			}
		}
	});
});