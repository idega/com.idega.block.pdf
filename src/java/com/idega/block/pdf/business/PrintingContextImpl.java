/*
 * $Id: PrintingContextImpl.java,v 1.1 2004/11/04 20:32:46 aron Exp $
 * Created on 15.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.pdf.business;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 *  Last modified: $Date: 2004/11/04 20:32:46 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class PrintingContextImpl implements PrintingContext {
    
    private Map properties;
    private InputStream templateStream;
    private OutputStream documentStream;
    private File resourceDirectory;

    /* (non-Javadoc)
     * @see com.idega.block.pdf.business.PrintingContext#getDocumentProperties()
     */
    public Map getDocumentProperties() {
        return properties;
    }

    /* (non-Javadoc)
     * @see com.idega.block.pdf.business.PrintingContext#addDocumentProperties(java.util.Map)
     */
    public void addDocumentProperties(Map properties) {
        if(this.properties==null)
            this.properties = new HashMap(properties);
        else
            this.properties.putAll(properties);
    }

    /* (non-Javadoc)
     * @see com.idega.block.pdf.business.PrintingContext#getTemplateStream()
     */
    public InputStream getTemplateStream() {
        return this.templateStream;
    }

    /* (non-Javadoc)
     * @see com.idega.block.pdf.business.PrintingContext#setTemplateStream(java.io.InputStream)
     */
    public void setTemplateStream(InputStream stream) {
        this.templateStream = stream;
    }

    /* (non-Javadoc)
     * @see com.idega.block.pdf.business.PrintingContext#getResourceDirectory()
     */
    public File getResourceDirectory() {
        return this.resourceDirectory;
    }

    /* (non-Javadoc)
     * @see com.idega.block.pdf.business.PrintingContext#setResourceDirectory(java.io.File)
     */
    public void setResourceDirectory(File directory) {
        this.resourceDirectory = directory;
    }

    /* (non-Javadoc)
     * @see com.idega.block.pdf.business.PrintingContext#getDocumentStream()
     */
    public OutputStream getDocumentStream() {
        return this.documentStream;
    }

    /* (non-Javadoc)
     * @see com.idega.block.pdf.business.PrintingContext#setDocumentStream(java.io.OutputStream)
     */
    public void setDocumentStream(OutputStream out) {
        this.documentStream = out;
    }

}
