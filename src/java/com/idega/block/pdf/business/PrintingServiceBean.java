/*
 * $Id: PrintingServiceBean.java,v 1.13 2008/10/23 12:28:06 valdas Exp $ Created
 * on 15.10.2004
 * 
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.block.pdf.business;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.ujac.print.DocumentHandlerException;
import org.ujac.print.DocumentPrinter;
import org.ujac.util.io.FileResourceLoader;
import org.ujac.util.io.HttpResourceLoader;

import com.idega.business.IBORuntimeException;
import com.idega.business.IBOServiceBean;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.servlet.filter.IWBundleResourceFilter;


/**
 * 
 * Last modified: $Date: 2008/10/23 12:28:06 $ by $Author: valdas $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.13 $
 */
public class PrintingServiceBean extends IBOServiceBean implements PrintingService {
	
	private static final long serialVersionUID = 5645957534865246451L;

	/*
	 * // defining the document properties, this map is used for dynamical content
	 * evaluation. Map documentProperties = new HashMap(); ... // instantiating
	 * the document printer FileInputStream templateStream = new
	 * FileInputStream("your-template-file.xml"); DocumentPrinter documentPrinter =
	 * new DocumentPrinter(templateStream, documentProperties); // in case you'd
	 * like to use a XML parser different from the default crimson implementation //
	 * you can specify it here (apache xerces in this case).
	 * documentPrinter.setXmlReaderClass("org.apache.xerces.parsers.SAXParser"); //
	 * defining the ResourceLoader: This is necessary if you like to //
	 * dynamically load resources like images during template processing.
	 * documentPrinter.setResourceLoader(new FileResourceLoader("./")); //
	 * generating the document output FileOutputStream pdfStream = new
	 * FileOutputStream("your-output-file.pdf");
	 * documentPrinter.printDocument(pdfStream);
	 */
	
	/**
	 * Creates a pdf by transforming an xml template. The given PrintingContext
	 * supplies the necessary resources for the generation
	 */
	@SuppressWarnings("unchecked")
	public DocumentPrinter printDocument(PrintingContext pcx) {
		try {
			Map documentProperties = pcx.getDocumentProperties();
			if (pcx.getBundle() == null) {
				Object o = documentProperties.get(PrintingContext.IW_BUNDLE_ROPERTY_NAME);
				if (o instanceof IWBundle) {
					pcx.setBundle((IWBundle) o);
				}
			}
			
			InputStream is = pcx.getTemplateStream();
			
			DocumentPrinter documentPrinter = new DocumentPrinter(is, documentProperties);
			
			/*TemplateInterpreterFactory tif = new DefaultTemplateInterpreterFactory();
			TemplateInterpreter expi = tif.createTemplateInterpreter();
			expi.(new IWBundleType(expi));
			documentPrinter.setTemplateInterpreter(expi);*/
			
			File resourceDirectory = pcx.getResourceDirectory();
			if (resourceDirectory != null) {
				documentPrinter.setResourceLoader(new FileResourceLoader(resourceDirectory));
				
				loadAllResources(pcx.getBundle(), resourceDirectory);
			}
			
			String resourceURL = pcx.getResourceURL();
			if (resourceURL != null) {
				documentPrinter.setResourceLoader(new HttpResourceLoader(resourceURL));
			}
			
			OutputStream os = pcx.getDocumentStream();
			documentPrinter.printDocument(os);
			
			return documentPrinter;
		}
		catch (DocumentHandlerException e) {
			e.printStackTrace();
			throw new IBORuntimeException(e);
		}
		catch (IOException e) {
			throw new IBORuntimeException(e);
		}
	}

	/**
	 * Creates an empty PrintingContext to be filled
	 * 
	 * @return
	 */
	public PrintingContext createPrintingContext() {
		return new PrintingContextImpl();
	}
	
	private boolean loadAllResources(IWBundle bundle, File resourceDirectory) {
		if (bundle == null || resourceDirectory == null || !resourceDirectory.exists() || !resourceDirectory.isDirectory()) {
			return false;
		}
		
		String pathInBundle = resourceDirectory.getAbsolutePath();
		int bundleIdentifierIndex = pathInBundle.indexOf(bundle.getBundleIdentifier());
		if (bundleIdentifierIndex == -1) {
			return false;
		}
		pathInBundle = new StringBuilder(pathInBundle.substring(bundleIdentifierIndex + bundle.getBundleIdentifier().length() + 1)).append(File.separator)
						.toString();
		
		IWBundleResourceFilter.copyAllFilesFromJarDirectory(IWMainApplication.getDefaultIWMainApplication(), bundle, pathInBundle);
		
		return true;
	}
}
