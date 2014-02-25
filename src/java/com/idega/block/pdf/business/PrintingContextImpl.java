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
import java.util.logging.Logger;

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
    private String resourceURL;
    private String fileName;
    private IWBundle bundle;

    private Logger logger = null;

    protected Logger getLogger() {
    	if (logger == null) {
    		logger = Logger.getLogger(getClass().getName());
    	}
    	return logger;
    }

    /* (non-Javadoc)
     * @see com.idega.block.pdf.business.PrintingContext#getDocumentProperties()
     */
    @Override
	public Map getDocumentProperties() {
        return this.properties;
    }

    /* (non-Javadoc)
     * @see com.idega.block.pdf.business.PrintingContext#addDocumentProperties(java.util.Map)
     */
    @Override
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
    @Override
	public InputStream getTemplateStream() {
        return this.templateStream;
    }

    /* (non-Javadoc)
     * @see com.idega.block.pdf.business.PrintingContext#setTemplateStream(java.io.InputStream)
     */
    @Override
	public void setTemplateStream(InputStream stream) {
        this.templateStream = stream;
    }

    /* (non-Javadoc)
     * @see com.idega.block.pdf.business.PrintingContext#getResourceDirectory()
     */
    @Override
	public File getResourceDirectory() {
        return this.resourceDirectory;
    }

    /* (non-Javadoc)
     * @see com.idega.block.pdf.business.PrintingContext#setResourceDirectory(java.io.File)
     */
    @Override
	public void setResourceDirectory(File directory) {
        this.resourceDirectory = directory;
    }

    /* (non-Javadoc)
     * @see com.idega.block.pdf.business.PrintingContext#getResourceURL()
     */
    @Override
	public String getResourceURL() {
        return this.resourceURL;
    }

    /* (non-Javadoc)
     * @see com.idega.block.pdf.business.PrintingContext#setResourceURL(java.lang.String)
     */
    @Override
	public void setResourceURL(String url) {
        this.resourceURL = url;
    }

    /* (non-Javadoc)
     * @see com.idega.block.pdf.business.PrintingContext#getDocumentStream()
     */
    @Override
	public OutputStream getDocumentStream() {
        return this.documentStream;
    }

    /* (non-Javadoc)
     * @see com.idega.block.pdf.business.PrintingContext#setDocumentStream(java.io.OutputStream)
     */
    @Override
	public void setDocumentStream(OutputStream out) {
        this.documentStream = out;
    }


		@Override
		public String getFileName() {
			return this.fileName;
		}


		@Override
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

	@Override
	public IWBundle getBundle() {
		return bundle;
	}

	@Override
	public void setBundle(IWBundle bundle) {
		this.bundle = bundle;
	}

}
