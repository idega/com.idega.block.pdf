package com.idega.block.pdf.business;

import java.io.Serializable;

public interface PDFWriterProvider extends Serializable {

	public Class<?> getPDFWriterClass();
	
}
