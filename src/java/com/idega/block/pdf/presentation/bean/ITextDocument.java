/**
 * @(#)ITextDocument.java    1.0.0 19:52:46
 *
 * Idega Software hf. Source Code Licence Agreement x
 *
 * This agreement, made this 10th of February 2006 by and between 
 * Idega Software hf., a business formed and operating under laws 
 * of Iceland, having its principal place of business in Reykjavik, 
 * Iceland, hereinafter after referred to as "Manufacturer" and Agura 
 * IT hereinafter referred to as "Licensee".
 * 1.  License Grant: Upon completion of this agreement, the source 
 *     code that may be made available according to the documentation for 
 *     a particular software product (Software) from Manufacturer 
 *     (Source Code) shall be provided to Licensee, provided that 
 *     (1) funds have been received for payment of the License for Software and 
 *     (2) the appropriate License has been purchased as stated in the 
 *     documentation for Software. As used in this License Agreement, 
 *     Licensee shall also mean the individual using or installing 
 *     the source code together with any individual or entity, including 
 *     but not limited to your employer, on whose behalf you are acting 
 *     in using or installing the Source Code. By completing this agreement, 
 *     Licensee agrees to be bound by the terms and conditions of this Source 
 *     Code License Agreement. This Source Code License Agreement shall 
 *     be an extension of the Software License Agreement for the associated 
 *     product. No additional amendment or modification shall be made 
 *     to this Agreement except in writing signed by Licensee and 
 *     Manufacturer. This Agreement is effective indefinitely and once
 *     completed, cannot be terminated. Manufacturer hereby grants to 
 *     Licensee a non-transferable, worldwide license during the term of 
 *     this Agreement to use the Source Code for the associated product 
 *     purchased. In the event the Software License Agreement to the 
 *     associated product is terminated; (1) Licensee's rights to use 
 *     the Source Code are revoked and (2) Licensee shall destroy all 
 *     copies of the Source Code including any Source Code used in 
 *     Licensee's applications.
 * 2.  License Limitations
 *     2.1 Licensee may not resell, rent, lease or distribute the 
 *         Source Code alone, it shall only be distributed as a 
 *         compiled component of an application.
 *     2.2 Licensee shall protect and keep secure all Source Code 
 *         provided by this this Source Code License Agreement. 
 *         All Source Code provided by this Agreement that is used 
 *         with an application that is distributed or accessible outside
 *         Licensee's organization (including use from the Internet), 
 *         must be protected to the extent that it cannot be easily 
 *         extracted or decompiled.
 *     2.3 The Licensee shall not resell, rent, lease or distribute 
 *         the products created from the Source Code in any way that 
 *         would compete with Idega Software.
 *     2.4 Manufacturer's copyright notices may not be removed from 
 *         the Source Code.
 *     2.5 All modifications on the source code by Licencee must 
 *         be submitted to or provided to Manufacturer.
 * 3.  Copyright: Manufacturer's source code is copyrighted and contains 
 *     proprietary information. Licensee shall not distribute or 
 *     reveal the Source Code to anyone other than the software 
 *     developers of Licensee's organization. Licensee may be held 
 *     legally responsible for any infringement of intellectual property 
 *     rights that is caused or encouraged by Licensee's failure to abide 
 *     by the terms of this Agreement. Licensee may make copies of the 
 *     Source Code provided the copyright and trademark notices are 
 *     reproduced in their entirety on the copy. Manufacturer reserves 
 *     all rights not specifically granted to Licensee.
 *
 * 4.  Warranty & Risks: Although efforts have been made to assure that the 
 *     Source Code is correct, reliable, date compliant, and technically 
 *     accurate, the Source Code is licensed to Licensee as is and without 
 *     warranties as to performance of merchantability, fitness for a 
 *     particular purpose or use, or any other warranties whether 
 *     expressed or implied. Licensee's organization and all users 
 *     of the source code assume all risks when using it. The manufacturers, 
 *     distributors and resellers of the Source Code shall not be liable 
 *     for any consequential, incidental, punitive or special damages 
 *     arising out of the use of or inability to use the source code or 
 *     the provision of or failure to provide support services, even if we 
 *     have been advised of the possibility of such damages. In any case, 
 *     the entire liability under any provision of this agreement shall be 
 *     limited to the greater of the amount actually paid by Licensee for the 
 *     Software or 5.00 USD. No returns will be provided for the associated 
 *     License that was purchased to become eligible to receive the Source 
 *     Code after Licensee receives the source code. 
 */
package com.idega.block.pdf.presentation.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.jcr.Repository;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.idega.block.pdf.business.PrintingService;
import com.idega.block.pdf.data.DocumentURIEntity;
import com.idega.block.pdf.data.DocumentURITypeEntity;
import com.idega.block.pdf.presentation.filter.DocumentURIFilterBean;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.file.util.MimeType;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.repository.RepositoryService;
import com.idega.util.CoreConstants;
import com.idega.util.FileUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

/**
 * <p>JSF managed bean for iText document editing</p>
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 2015 vas. 2
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
public class ITextDocument extends DocumentURI {

	public static final String REPOSITORY_PATH = CoreConstants.PUBLIC_PATH + "/dynamic_resources/";	

	private static final long serialVersionUID = -1964054087009829433L;

	private String documentSource;

	private DocumentURIFilterBean documentURIFilterBean;

	public ITextDocument() {}

	public ITextDocument(DocumentURIEntity entity) {
		super(entity);
	}

	public DocumentURIFilterBean getDocumentURIFilterBean() {
		return documentURIFilterBean;
	}

	public void setDocumentURIFilterBean(DocumentURIFilterBean documentURIFilterBean) {
		this.documentURIFilterBean = documentURIFilterBean;
	}

	protected List<Long> getEntityTypeIDs(DocumentURIEntity entity) {
		if (entity != null) {
			ArrayList<Long> ids = new ArrayList<Long>();

			List<DocumentURITypeEntity> types = entity.getTypes();
			for (DocumentURITypeEntity type: types) {
				ids.add(type.getId());
			}

			return ids;
		}

		return Collections.emptyList();
	}

	public List<DocumentURIEntity> getEntities() {
		List<Long> types = getDocumentURIFilterBean().getSelectedTypes();
		if (!ListUtil.isEmpty(types)) {
			List<DocumentURIEntity> result = new ArrayList<DocumentURIEntity>();

			List<DocumentURIEntity> entitiesContainingTypes = getDAO().findByTypeIds(types);
			for (DocumentURIEntity duri : entitiesContainingTypes) {
				List<Long> entityTypesIds = getEntityTypeIDs(duri);
				if (entityTypesIds.containsAll(types)) {
					result.add(duri);
				}
			}

			return result;
		}

		return Collections.emptyList();
	}

	public Map<String, Long> getEntitiesMap() {
		TreeMap<String, Long> map = new TreeMap<String, Long>();

		for (DocumentURIEntity entity : getEntities()) {
			String uri = entity.getRepositoryURI();
			if (!StringUtil.isEmpty(uri)) {
				
				try {
					uri = uri.substring(uri.lastIndexOf('/') + 1, uri.lastIndexOf('.'));
					Long time = Long.valueOf(uri);
					map.put(new Date(time).toLocaleString(), entity.getId());
				} catch (Exception e) {
					map.put(uri, entity.getId());
				}
			}
		}

		return map.descendingMap();
	}

	public boolean isMultiple() {
		List<DocumentURIEntity> entities = getEntities();
		if (!ListUtil.isEmpty(entities)) {
			return entities.size() > 1;
		}

		return false;
	}

	@Override
	public DocumentURIEntity getEntity() {
		DocumentURIEntity entity = super.getEntity();
		if (entity == null) {
			List<DocumentURIEntity> entities = getEntities();
			if (entities != null && entities.size() == 1) {
				entity = entities.iterator().next();
			}
		}

		List<Long> selectedTypes = getDocumentURIFilterBean().getSelectedTypes();
		List<Long> existingtypes = getEntityTypeIDs(entity);
		if (existingtypes.containsAll(selectedTypes)) {
			return entity;
		} else {
			return null;
		}
	}

	@Override
	public String getIdClass() {
		return "editorForm:prm_id";
	}

	/**
	 * 
	 * @return service to work with {@link Repository}
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected RepositoryService getRepositoryService() {
		return ELUtil.getInstance().getBean(RepositoryService.BEAN_NAME);
	}

	/**
	 * 
	 * @return service to convert iText file to PDF file;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected PrintingService getPrintingService() {
		try {
			return (PrintingService) IBOLookup.getServiceInstance(
					IWMainApplication.getDefaultIWApplicationContext(), 
					PrintingService.class);
		} catch (IBOLookupException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, 
					"Failed to get " + PrintingService.class.getSimpleName() + 
					", cause of:", e);
		}

		return null;
	}

	/**
	 * 
	 * @return iText {@link File} placed in {@link IWBundle} or 
	 * <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public File getBundleDocument() {
		if (!StringUtil.isEmpty(getBundleURI()) && !getBundleURI().equals("-")) {
			try {
				return FileUtil.getFileFromWorkspace(getBundleURI());
			} catch (IOException e) {
				java.util.logging.Logger.getLogger(getClass().getName()).log(
						Level.WARNING, "Failed to get document from repository "
								+ "by path: '" + getRepositoryURI() 
								+ "' cause of:", e);
			}
		}

		return null;
	}

	/**
	 * 
	 * @return {@link InputStream} from document placed in {@link Repository} by
	 * {@link ITextDocumentURI#getRepositoryURI()}. If no {@link File} found,
	 * then returns stream from {@link ITextDocument#getBundleDocument()} or
	 * <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public InputStream getDocumentStream() {
		InputStream documentStream = null;
		if (documentStream == null) {
			try {
				documentStream = getRepositoryService()
						.getInputStreamAsRoot(getRepositoryURI());
			} catch (Exception e) {
				java.util.logging.Logger.getLogger(getClass().getName()).log(
						Level.WARNING, "Failed to get input stream by path: '" + 
								getRepositoryURI() + "' cause of: ", e);
			}
		}

		if (documentStream == null && getBundleDocument() != null) {
			try {
				documentStream = new FileInputStream(getBundleDocument());
			} catch (Exception e) {
				java.util.logging.Logger.getLogger(getClass().getName()).log(
						Level.WARNING, "Failed to create input stream, "
								+ "cause of:", e);
			}
		}

		return documentStream;
	}

	/**
	 * 
	 * @return {@link InputStream} form {@link ITextDocument#getDocumentStream()}
	 * in {@link String} format or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public String getDocumentSource() {
		InputStream documentStream = getDocumentStream();
		if (documentStream != null) {
			String code = null;
			try {
				code = IOUtils.toString(getDocumentStream());
			} catch (Exception e) {
				Logger.getLogger(getClass().getName()).log(Level.WARNING, 
						"Failed to read stream:", e);
			}

			try {
				documentStream.close();
				Logger.getLogger(getClass().getName()).log(Level.INFO, 
						"Stream was closed after using.");
			} catch (Exception e) {
				Logger.getLogger(getClass().getName()).log(Level.WARNING, 
						"Failed to close stream, cause of: " + e.getMessage());
			}

			return code;
		}

		return null;
	}

	public void setDocumentSource(String documentSource) {
		this.documentSource = documentSource;
	}

	/**
	 * 
	 * @return filename of .xml file with {@link System#currentTimeMillis()}
	 * as name;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public String getNewFilename() {
		return System.currentTimeMillis() + ".xml";
	}

	/**
	 * 
	 * @return PDF filename with formatted {@link Date} from exiting iText 
	 * document filename or new one with current {@link Date};
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public String getPDFName() {
		return IWTimestamp.RightNow().toSQLDateString() + ".pdf";
	}

	/**
	 * 
	 * @return something like /content/files/public/dynamic_resources/itext/{@link ITextDocumentURI#getProcessDefinitionId()}
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public String getRepositoryPath() {
		String webdavServerURL = getRepositoryService().getWebdavServerURL();
		if (!StringUtil.isEmpty(webdavServerURL)) {
			return webdavServerURL + REPOSITORY_PATH;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.pdf.presentation.bean.DocumentURI#save()
	 */
	@Override
	public void save() {
		String fileName = getNewFilename();

		try {
			if (getRepositoryService().uploadXMLFileAndCreateFoldersFromStringAsRoot(
					getRepositoryPath(), 
					fileName, 
					this.documentSource)) {
				setRepositoryURI(getRepositoryPath() + fileName);
				super.save();
			}
		} catch (Exception e) {
			java.util.logging.Logger.getLogger(getClass().getName()).log(
					Level.WARNING, 
					"Failed to save file by path: " + getRepositoryPath() + "/" +
							fileName + " Cause of:", e);
		}
	}

	/**
	 * 
	 * <p>Returns {@link OutputStream} of generated PDF file to 
	 * {@link HttpServletResponse}</p>
	 * @throws IOException
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public void download() throws IOException {
		save();
		setSubmitted(false);
		
	    FacesContext fc = FacesContext.getCurrentInstance();
	    HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();

	    /* 
	     * Some JSF component library or some Filter might have set some headers 
	     * in the buffer beforehand. We want to get rid of them, else it may 
	     * collide. 
	     */
	    response.reset();

	    /* 
	     * Check http://www.iana.org/assignments/media-types for all types. Use 
	     * if necessary ServletContext#getMimeType() for auto-detection based on 
	     * filename. 
	     */
	    response.setContentType(MimeType.pdf.getMimeType()); 

	    /* 
	     * Set it with the file size. This header is optional. It will work if 
	     * it's omitted, but the download progress will be unknown.
	     */
	    response.setContentLength(getDocumentStream().available());

	    /* 
	     * The Save As popup magic is done here. You can give it any file name 
	     * you want, this only won't work in MSIE, it will use current request 
	     * URL as file name instead.
	     */
	    response.setHeader("Content-Disposition", "attachment; filename=\"" + getPDFName() + "\"");

	    /* 
	     * Now you can write the InputStream of the file to the above 
	     * OutputStream the usual way.
	     */
	    try {
			getPrintingService().printIText(getDocumentStream(), response.getOutputStream(), null, null);
		} catch (Exception e) {
			java.util.logging.Logger.getLogger(getClass().getName()).log(
					Level.WARNING, "Failed to create PDF document, cause of:", e);
		}

	    /* 
	     * Important! Otherwise JSF will attempt to render the response which 
	     * obviously will fail since it's already written with a file and closed.
	     */
	    fc.responseComplete();
	}
}
