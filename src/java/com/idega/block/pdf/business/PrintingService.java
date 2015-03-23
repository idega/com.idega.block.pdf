/*
 * $Id: PrintingService.java,v 1.1 2004/11/04 20:32:46 aron Exp $
 * Created on 4.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.pdf.business;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.ujac.print.DocumentHandlerException;
import org.ujac.print.DocumentPrinter;

import com.idega.block.pdf.data.DocumentURIEntity;
import com.idega.business.IBOService;
import com.idega.io.MemoryOutputStream;

/**
 * 
 *  Last modified: $Date: 2004/11/04 20:32:46 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public interface PrintingService extends IBOService {

    public DocumentPrinter printDocument(PrintingContext pcx)
            throws java.rmi.RemoteException;

    public PrintingContext createPrintingContext()
            throws java.rmi.RemoteException;

    /**
     * 
     * @param inputStream is iText format file to be converted, 
     * not <code>null</code>;
     * @param outputStream to write generated document to. When <code>null</code>
     * then {@link MemoryOutputStream} will be used;
     * @param documentResourcesFolder is link to resources folder. When 
     * <code>null</code> then "content/files/public/" will be used;
     * @param expressions are {@link Map} of EL variable and value, which can be 
	 * changed before conversion, skipped if <code>null</code>;
     * @throws IOException when streams given does not work as they should;
     * @throws DocumentHandlerException when something is wrong with resources;
     * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
     */
	void printIText(
			InputStream inputStream, 
			OutputStream outputStream, 
			String documentResourcesFolder, 
			Map<String, String> expressions
			) throws IOException, DocumentHandlerException;

	/**
     * 
     * @param inputStream is iText format file to be converted, 
     * not <code>null</code>;
     * @param documentResourcesFolder is link to resources folder. When 
     * <code>null</code> then "content/files/public/" will be used;
     * @param expressions are {@link Map} of EL variable and value, which can be 
	 * changed before conversion, skipped if <code>null</code>;
     * @return {@link OutputStream} of converted PDF or <code>null</code> on failure;
     * @throws DocumentHandlerException
     * @throws IOException
     * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
     */
    ByteArrayOutputStream printIText(
    		InputStream inputStream,
			String documentResourcesFolder, 
			Map<String, String> expressions
			) throws DocumentHandlerException, IOException;

	/**
	 * 
	 * @param inputStream is XHTML source to be converted, not <code>null</code>;
	 * @param outputStream to place converted PDF, not <code>null</code>;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	void printXHTML(InputStream inputStream, OutputStream outputStream);

	/**
	 * 
	 * @param inputStream is XHTML source to be converted, not <code>null</code>;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 * @return {@link OutputStream} of converted PDF or <code>null</code> on failure;
	 */
	ByteArrayOutputStream printXHTML(InputStream inputStream);

	/**
	 * 
	 * @param source of XHTML document to convert, not <code>null</code>;
	 * @return {@link OutputStream} of converted PDF or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	ByteArrayOutputStream printXHTML(String source);

	/**
	 * 
	 * @param source of XHTML document to convert, not <code>null</code>;
	 * @param properties are {@link Map} of EL variable and value, which can be 
	 * changed before conversion, skipped if <code>null</code>;
	 * @return {@link OutputStream} of converted PDF or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	ByteArrayOutputStream printXHTML(String source,
			Map<String, String> properties);

	/**
	 * 
	 * @param entity to get URL of XHTML source from, not <code>null</code>;
	 * @param properties are {@link Map} of EL variable and value, which can be 
	 * changed before conversion, skipped if <code>null</code>;
	 * @return PDF output stream of converted document or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	ByteArrayOutputStream printXHTML(
			DocumentURIEntity entity,
			Map<String, String> properties);
}
