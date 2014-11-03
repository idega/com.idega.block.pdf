package com.idega.block.pdf.downloadwriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.idega.block.pdf.PDFConstants;
import com.idega.block.pdf.business.PdfDownloadComponentProvider;
import com.idega.core.file.util.MimeType;
import com.idega.graphics.generator.business.PDFGenerator;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.io.MediaWritable;
import com.idega.presentation.IWContext;
import com.idega.util.CoreUtil;
import com.idega.util.IOUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

public class UIComponentDownloadWriter  implements MediaWritable {
	
	public static final String PARAMETER_DOWNLOAD_BEAN_NAME = "pdf_download_bean";
	public static final String PARAMETER_DOWNLOAD_COMPONENT_IDENTIFIER = "pdf_download_component";
	public static final String PARAMETER_FILE_NAME = "pdf_file_name"; 
	private String mimeType = null;
	private byte[] bytes;
	@Override
	public void init(HttpServletRequest request, IWContext iwc) {
		try{
			String beanName = iwc.getParameter(PARAMETER_DOWNLOAD_BEAN_NAME);
			PdfDownloadComponentProvider provider = ELUtil.getInstance().getBean(beanName);
			String id = iwc.getParameter(PARAMETER_DOWNLOAD_COMPONENT_IDENTIFIER);
			UIComponent component = provider.getComponentForDownload(iwc,id);
			PDFGenerator generator = ELUtil.getInstance().getBean(PDFGenerator.SPRING_BEAN_NAME_PDF_GENERATOR);
			bytes = generator.getBytesOfGeneratedPDF(iwc, component, false, false);
			String fileName = iwc.getParameter(PARAMETER_FILE_NAME);
			if(StringUtil.isEmpty(fileName)){
				fileName = "pdf.pdf";
			}
			setMimeType(MimeType.pdf.getMimeType());
			
			HttpServletResponse response = iwc.getResponse();
			response.setHeader("Expires", String.valueOf(0));
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
	        response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
			response.setContentLength(bytes.length);
		}catch (Exception e) {
			getLogger().log(Level.WARNING, "Failed to download pdf", e);
			addErrorMessage(iwc, null);
		}
	}
	@Override
	public void writeTo(OutputStream out) throws IOException {
		try {
			out.write(bytes);
		} catch(Exception e) {
			getLogger().log(Level.WARNING, "Error streaming from input to output streams", e);
			addErrorMessage(CoreUtil.getIWContext(), null);
		} finally {
			out.flush();
			IOUtil.closeOutputStream(out);
		}
	}
	@Override
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	private void addErrorMessage(IWContext iwc,String errorMsg){
		setMimeType("text/html");
		HttpServletResponse response = iwc.getResponse();
		try {
			PrintWriter writer = response.getWriter();
			if(StringUtil.isEmpty(errorMsg)){
				errorMsg = getResourceBundle(iwc).getLocalizedString("download_failed", "Download failed");
			}
			writer.write("<h1>"+ errorMsg +"</h1>"); 
		} catch (IOException e) {
			getLogger().log(Level.WARNING, "Failed to add error message", e);
		}
	}
	
	private Logger getLogger(){
		return Logger.getLogger(UIComponentDownloadWriter.class.getName());
	}
	private IWResourceBundle getResourceBundle(IWContext iwc){
		IWMainApplication iwma = iwc.getApplicationContext().getIWMainApplication();
		IWBundle bundle = iwma.getBundle(PDFConstants.IW_BUNDLE_IDENTIFIER);
		return bundle.getResourceBundle(iwc);
	}
}