package com.idega.block.pdf.business;

import org.jdom2.Document;

public interface PDFChanger {

	public Document getChangedDocument(Document doc);

}