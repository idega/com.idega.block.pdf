/*
 * $Id: PrintingServiceBean.java,v 1.1 2004/11/04 20:32:46 aron Exp $
 * Created on 15.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.pdf.business;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.ujac.print.DocumentHandlerException;
import org.ujac.print.DocumentPrinter;
import org.ujac.util.FileResourceLoader;
import org.ujac.util.exi.ExpressionInterpreter;

import com.idega.business.IBORuntimeException;
import com.idega.business.IBOServiceBean;
;

/**
 * 
 *  Last modified: $Date: 2004/11/04 20:32:46 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class PrintingServiceBean extends IBOServiceBean  implements PrintingService{
    
    /* 
//  defining the document properties, this map is used for dynamical content evaluation.
    Map documentProperties = new HashMap();
    ...
//     instantiating the document printer
    FileInputStream templateStream = new FileInputStream("your-template-file.xml");
    DocumentPrinter documentPrinter = new DocumentPrinter(templateStream, documentProperties);
//     in case you'd like to use a XML parser different from the default crimson implementation
//     you can specify it here (apache xerces in this case).
    documentPrinter.setXmlReaderClass("org.apache.xerces.parsers.SAXParser");
//     defining the ResourceLoader: This is necessary if you like to 
//     dynamically load resources like images during template processing.
    documentPrinter.setResourceLoader(new FileResourceLoader("./"));
//     generating the document output
    FileOutputStream pdfStream = new FileOutputStream("your-output-file.pdf");
    documentPrinter.printDocument(pdfStream);
    */
  
   /**
    * Creates a pdf by transforming an xml template.
    * The given PrintingContext supplies the necessary resources for the generation 
    */ 
   public DocumentPrinter printDocument(PrintingContext pcx) {
       try {
        Map documentProperties = pcx.getDocumentProperties();
           InputStream is = pcx.getTemplateStream();
           DocumentPrinter documentPrinter = new DocumentPrinter(is,documentProperties);
           ExpressionInterpreter expi = ExpressionInterpreter.createInstance();
           expi.registerTypeHandler(new IWBundleType( expi));
           documentPrinter.setExpressionInterpeter(expi);
           
           File resourceDirectory = pcx.getResourceDirectory();  
           if(resourceDirectory!=null)
               documentPrinter.setResourceLoader(new FileResourceLoader(resourceDirectory));
           OutputStream os = pcx.getDocumentStream();
           documentPrinter.printDocument(os);
           return documentPrinter;
    } catch (DocumentHandlerException e) {
        throw new IBORuntimeException(e);
    } catch (IOException e) {
        throw new IBORuntimeException(e);
    }
       
   }
   
 
   
   
   /**
    * Creates an empty PrintingContext to be filled
    * @return
    */
   public PrintingContext createPrintingContext(){
       return new PrintingContextImpl();
   }
   
}
