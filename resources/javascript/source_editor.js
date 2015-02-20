editAreaLoader.init({
	id:					"editorForm:sourceCode",
	language:			"en",
	syntax:				"xml",
	toolbar:			"undo, redo",
	allow_toggle:		false,
	start_highlight:	true,
	show_line_colors:	true,
	min_width:			650,
	change_callback:	"SourceEditor.updatePDF"
});

jQuery(document).ready(function() {
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

	SourceEditor.updatePDF();
});

var SourceEditor = {
	updatePDF:	function() {
		var processDefinitionId = jQuery("input[id='editorForm:oldProcessDefinitionId']").val();
		var source = editAreaLoader.getValue("editorForm:sourceCode")
		LazyLoader.loadMultiple(['/dwr/engine.js', '/dwr/interface/ITextDocumentService.js'], function() {
			ITextDocumentService.updateTempolarPDF(processDefinitionId, source, {
				callback: function(result) {
					jQuery("a.windowLink").attr("href", result);
				}
			});
		});
	}
}