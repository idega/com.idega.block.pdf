package com.idega.block.pdf.business;

import javax.faces.component.UIComponent;

import com.idega.presentation.IWContext;

public interface PdfDownloadComponentProvider {
	public UIComponent getComponentForDownload(IWContext iwc,String id);
}
