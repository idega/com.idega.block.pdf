tinyMCE.init({
	selector:'textarea',
	height : '100%',
	width : '100%',
	mode : "textareas",
    theme : "advanced",
    plugins : "autolink,lists,spellchecker,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template",
	theme_advanced_buttons1 : "bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,formatselect,fontselect,fontsizeselect",
    theme_advanced_buttons2 : "bullist,numlist,|,outdent,indent,blockquote,|,undo,redo,|,link,unlink,anchor,image,cleanup,help,code,|,forecolor,backcolor",
    theme_advanced_buttons3 : "tablecontrols,|,hr,removeformat,visualaid,|,sub,sup,|,charmap,emotions,iespell,media,advhr",
    theme_advanced_buttons4 : "insertlayer,moveforward,movebackward,absolute,|,styleprops,spellchecker,|,cite,abbr,acronym,del,ins,attribs,|,visualchars,nonbreaking,template,blockquote,pagebreak,|,insertfile,insertimage",
    theme_advanced_toolbar_location : "top",
    theme_advanced_toolbar_align : "left",
    theme_advanced_statusbar_location : "bottom",
    theme_advanced_resizing : true,
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

jQuery(window).load(function() {
	jQuery(window).on('beforeunload', function(event) {
		if (RichTextDocumentEditorHelper.SUBMITTED) {
			RichTextDocumentEditorHelper.SUBMITTED = false;
			return;
		}

		RichTextDocumentService.resetFilterBean();
	});
});

var RichTextDocumentEditorHelper = {};
RichTextDocumentEditorHelper.SUBMITTED = false;
RichTextDocumentEditorHelper.submit = function(loadingMessage) {
	showLoadingMessage(loadingMessage);
	RichTextDocumentEditorHelper.SUBMITTED = true;
	 jQuery('#editorForm').submit();
}