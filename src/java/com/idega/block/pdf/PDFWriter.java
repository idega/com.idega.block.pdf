package com.idega.block.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.io.DownloadWriter;
import com.idega.presentation.IWContext;
import com.idega.repository.RepositoryService;
import com.idega.repository.bean.RepositoryItem;
import com.idega.util.FileUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

public class PDFWriter extends DownloadWriter {

	public static final String PDF_URL_PARAMETER = "pdf_url_parameter";

	private static final Logger logger = Logger.getLogger(PDFWriter.class.getName());

	RepositoryItem pdfDoc;

	@Autowired
	private RepositoryService repository;

	@Override
	public void init(HttpServletRequest req, IWContext iwc) {
		ELUtil.getInstance().autowire(this);

		String pathToPdf = iwc.getParameter(PDF_URL_PARAMETER);
		if (StringUtil.isEmpty(pathToPdf)) {
			logger.log(Level.SEVERE, "PDF from XForm was not generated!");
			return;
		}

		try {
			if (!hasPermission(iwc, pathToPdf)) {
				logger.warning("No acces to " + pathToPdf);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error checking if has permission to " + pathToPdf, e);
			return;
		}

		try{
			pdfDoc = repository.getRepositoryItemAsRootUser(pathToPdf);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (pdfDoc == null || !pdfDoc.exists()) {
			return;
		}
		Long length = Long.valueOf(pdfDoc.getLength());
		setAsDownload(iwc, pdfDoc.getName(), length.intValue());
	}

	@Override
	public void writeTo(IWContext iwc, OutputStream streamOut) throws IOException {
		if (pdfDoc == null) {
			logger.log(Level.SEVERE, "Unable to get XForm");
			return;
		}

		InputStream streamIn = pdfDoc.getInputStream();
		FileUtil.streamToOutputStream(streamIn, streamOut);

		streamOut.flush();
		streamOut.close();
		streamIn.close();
	}

}