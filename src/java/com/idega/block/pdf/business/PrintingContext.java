/*
 * $Id: PrintingContext.java,v 1.3 2008/10/23 12:25:07 valdas Exp $
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
import java.util.Map;

import com.idega.idegaweb.IWBundle;

/**
 * 
 *  Last modified: $Date: 2008/10/23 12:25:07 $ by $Author: valdas $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.3 $
 */
public interface PrintingContext {

	public static final String IW_BUNDLE_ROPERTY_NAME = "iwb";
	
    /**
     * Gets the document properties
     * @return
     */
    public Map getDocumentProperties();
    
    /**
     * Add document properties
     * @param properties 
     */
    public void addDocumentProperties(Map properties);

    /**
     * Gets a InputStream to the template xml to be proccessed
     * @return
     */
    public InputStream getTemplateStream();

    /**
     * Sets the template Inputstream
     * @param directory
     */
    public void setTemplateStream(InputStream stream);
          
    /**
     * Gets a directory where extra resources will be loaded from
     * @return
     */
    public File getResourceDirectory();

    /**
     * Sets the directory where extra resources can be found
     * @param directory
     */
    public void setResourceDirectory(File directory);
    /**
     * Gets a Outputstream for the document to be written to.
     * @return
     */
    public OutputStream getDocumentStream();
    
    /**
     * Sets the OutputStream the document will be written to
     * @param out
     */
    public void setDocumentStream(OutputStream out);
    
    /**
     * Gets the file name of the document created from the printing context
     * @return
     */
    public String getFileName();

    /**
     * Sets the file name of the document created from the printing context
     * @param fileName
     */
    public void setFileName(String fileName);
    
    public void setBundle(IWBundle bundle);
    
    public IWBundle getBundle();
}