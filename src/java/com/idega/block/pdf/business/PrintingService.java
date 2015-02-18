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


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ujac.print.DocumentHandlerException;
import org.ujac.print.DocumentPrinter;

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
    /**
     * @see com.idega.block.pdf.business.PrintingServiceBean#printDocument
     */
    public DocumentPrinter printDocument(PrintingContext pcx)
            throws java.rmi.RemoteException;

    /**
     * @see com.idega.block.pdf.business.PrintingServiceBean#createPrintingContext
     */
    public PrintingContext createPrintingContext()
            throws java.rmi.RemoteException;

    /**
     * 
     * <p>Writes generated PDF document to {@link OutputStream}</p>
     * @param inputStream is iText format file to be converted, 
     * not <code>null</code>;
     * @param outputStream to write generated document to. When <code>null</code>
     * then {@link MemoryOutputStream} will be used;
     * @param documentResourcesFolder is link to resources folder. When 
     * <code>null</code> then "content/files/public/" will be used;
     * @throws IOException when streams given does not work as they should;
     * @throws DocumentHandlerException when something is wrong with resources;
     * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
     */
	void print(
			InputStream inputStream, 
			OutputStream outputStream, 
			String documentResourcesFolder
			) throws IOException, DocumentHandlerException;
}
