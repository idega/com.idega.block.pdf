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


import org.ujac.print.DocumentPrinter;

import com.idega.business.IBOService;

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

}
