package com.idega.block.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpException;
import org.apache.webdav.lib.WebdavResource;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.io.DownloadWriter;
import com.idega.presentation.IWContext;
import com.idega.slide.business.IWSlideService;
import com.idega.util.FileUtil;
import com.idega.util.StringUtil;

public class PDFWriter extends DownloadWriter{
	
	public static final String PDF_URL_PARAMETER = "pdf_url_parameter";
	
	private static final Logger logger = Logger.getLogger(PDFWriter.class.getName());
	private WebdavResource pdfDoc;
	
	
	@Override
	public void init(HttpServletRequest req, IWContext iwc) {
		
		
		String pathToPdf = iwc.getParameter(PDF_URL_PARAMETER);
		if (StringUtil.isEmpty(pathToPdf)) {
			logger.log(Level.SEVERE, "PDF from XForm was not generated!");
			return;
		}
		
		IWSlideService slide = null;
		try {
			slide = (IWSlideService) IBOLookup.getServiceInstance(iwc, IWSlideService.class);
		} catch (IBOLookupException e) {
			logger.log(Level.SEVERE, "Error getting IWSlideService!", e);
		}
		if (slide == null) {
			return;
		}
		try{
			pdfDoc = slide.getWebdavResourceAuthenticatedAsRoot(pathToPdf);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (pdfDoc == null || !pdfDoc.exists()) {
			return;
		}
		Long length = Long.valueOf(pdfDoc.getGetContentLength());
		setAsDownload(iwc, pdfDoc.getDisplayName(), length.intValue());
	}
	
	@Override
	public void writeTo(OutputStream streamOut) throws IOException {
		if (pdfDoc == null) {
			logger.log(Level.SEVERE, "Unable to get XForm");
			return;
		}
		
		InputStream streamIn = pdfDoc.getMethodData();
		FileUtil.streamToOutputStream(streamIn, streamOut);
		
		streamOut.flush();
		streamOut.close();
		streamIn.close();
	}

}
