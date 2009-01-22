/*
 * $Id: PrintingContextImpl.java,v 1.5 2009/01/22 17:30:04 anton Exp $
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

import com.idega.idegaweb.IWBundle;

/**
 * 
 *  Last modified: $Date: 2009/01/22 17:30:04 $ by $Author: anton $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.5 $
 */
public class PrintingContextImpl implements PrintingContext {
    
    private Map properties;
    private InputStream templateStream;
    private OutputStream documentStream;
    private File resourceDirectory;
    private String fileName;
    private IWBundle bundle;
    
    /* (non-Javadoc)
     * @see com.idega.block.pdf.business.PrintingContext#getDocumentProperties()
     */
    public Map getDocumentProperties() {
        return this.properties;
    }

    /* (non-Javadoc)
     * @see com.idega.block.pdf.business.PrintingContext#addDocumentProperties(java.util.Map)
     */
    public void addDocumentProperties(Map properties) {
        if(this.properties==null) {
					this.properties = new HashMap(properties);
				}
				else {
					this.properties.putAll(properties);
				}
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

		
		public String getFileName() {
			return this.fileName;
		}

		
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

	public IWBundle getBundle() {
		return bundle;
	}

	public void setBundle(IWBundle bundle) {
		this.bundle = bundle;
	}

}
