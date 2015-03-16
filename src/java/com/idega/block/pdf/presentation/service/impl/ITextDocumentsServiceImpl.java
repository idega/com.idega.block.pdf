/**
 * @(#)ITextDocumentsServiceImpl.java    1.0.0 23:15:10
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
package com.idega.block.pdf.business.impl;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.block.pdf.PDFConstants;
import com.idega.block.pdf.business.ITextDocumentsService;
import com.idega.block.pdf.business.PrintingService;
import com.idega.block.pdf.data.ITextDocumentURIEntity;
import com.idega.block.pdf.data.dao.ITextDocumentURIDAO;
import com.idega.block.pdf.presentation.ITextDocumentURIEditor;
import com.idega.block.pdf.presentation.bean.ITextDocumentURI;
import com.idega.builder.bean.AdvancedProperty;
import com.idega.builder.business.BuilderLogic;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.business.DefaultSpringBean;
import com.idega.core.file.util.MimeType;
import com.idega.data.SimpleQuerier;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.io.MemoryFileBuffer;
import com.idega.io.MemoryOutputStream;
import com.idega.presentation.IWContext;
import com.idega.repository.RepositoryService;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.datastructures.map.MapUtil;
import com.idega.util.expression.ELUtil;

/**
 * <p>Implementation of {@link ITextDocumentsService}</p>
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 2015 saus. 29
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
@Service(ITextDocumentsService.BEAN_DEFINITION)
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ITextDocumentsServiceImpl extends DefaultSpringBean implements ITextDocumentsService {

	@Autowired
	private ITextDocumentURIDAO iTextDocumentURIDAO;

	@Autowired
	private RepositoryService repositoryService;

	private PrintingService printingService;

	protected PrintingService getPrintingService() {
		try {
			this.printingService = (PrintingService) IBOLookup.getServiceInstance(
					IWMainApplication.getDefaultIWApplicationContext(), 
					PrintingService.class);
		} catch (IBOLookupException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, 
					"Failed to get " + PrintingService.class.getSimpleName() + 
					", cause of:", e);
		}

		return this.printingService;
	}

	protected ITextDocumentURIDAO getITextDocumentURIDAO() {
		if (this.iTextDocumentURIDAO == null) {
			ELUtil.getInstance().autowire(this);
		}

		return this.iTextDocumentURIDAO;
	}

	protected RepositoryService getRepositoryService() {
		if (this.repositoryService == null) {
			ELUtil.getInstance().autowire(this);
		}

		return this.repositoryService;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.pdf.business.ITextDocumentsService#getITextDocumentURIEntities()
	 */
	@Override
	public List<ITextDocumentURIEntity> getITextDocumentURIEntities() {
		return getITextDocumentURIDAO().findAll();
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.pdf.business.ITextDocumentsService#getITextDocumentURIs()
	 */
	@Override
	public List<ITextDocumentURI> getITextDocumentURIs() {
		List<ITextDocumentURI> beans = new ArrayList<ITextDocumentURI>();

		List<ITextDocumentURIEntity> entities = getITextDocumentURIEntities();
		for (ITextDocumentURIEntity entity: entities) {
			ITextDocumentURI bean = new ITextDocumentURI(entity);
			bean.setEditorLink(getEditorLink(entity.getId()));
			beans.add(bean);
		}

		return beans;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.pdf.business.ITextDocumentsService#getHomepageLink()
	 */
	@Override
	public String getHomepageLink() {
		BuilderLogic bl = BuilderLogic.getInstance();
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null || !iwc.isLoggedOn()) {
			return null;
		}

		String uri = CoreConstants.PAGES_URI_PREFIX;
		com.idega.core.builder.data.ICPage homePage = bl.getUsersHomePage(iwc.getCurrentUser());
		if (homePage != null)
			uri = uri + homePage.getDefaultPageURI();

		return uri;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.pdf.business.ITextDocumentsService#getEditorLink(java.lang.Long)
	 */
	@Override
	public String getEditorLink(Long id) {	
		List<AdvancedProperty> parameters = new ArrayList<AdvancedProperty>();
		if (id != null) {
			parameters.add(new AdvancedProperty(
					ITextDocumentURI.PARAMETER_ID, id));
		}

		return BuilderLogic.getInstance().getUriToObject(
				ITextDocumentURIEditor.class, parameters);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.pdf.business.ITextDocumentsService#getEditorLink()
	 */
	@Override
	public String getEditorLink() {
		return getEditorLink(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.pdf.business.ITextDocumentsService#remove(java.lang.String)
	 */
	@Override
	public void remove(String id) {
		if (!StringUtil.isEmpty(id)) {
			getITextDocumentURIDAO().remove(Long.valueOf(id));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.pdf.business.ITextDocumentsService#getProcessDefinitionNames()
	 */
	@Override
	public Map<String, String> getProcessDefinitionNames() {
		StringBuilder query = new StringBuilder();
		query.append("SELECT jpd.ID_, jpd.NAME_ FROM JBPM_PROCESSDEFINITION jpd");

		IWBundle bundle = getBundle(PDFConstants.IW_BUNDLE_IDENTIFIER);
		IWResourceBundle resourceBundle = bundle.getResourceBundle(getCurrentLocale());
		
		List<Serializable[]> results = null;
		try {
			results = SimpleQuerier.executeQuery(query.toString(), 2);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Failed to get results by query: '" + 
					query.toString() + "' cause of: ", e);
		}

		Map<String, String> names = new TreeMap<String, String>();
		if (!ListUtil.isEmpty(results)) {
			for (Serializable[] result : results) {
				String localizedName = resourceBundle.getLocalizedString(
						"process_definition_name." + result[1].toString(), 
						result[1].toString());
				if (!localizedName.equals(result[1].toString()) || CoreUtil.getIWContext().isSuperAdmin()) {
					names.put(localizedName, result[0].toString());
				}
			}
		}

		return names;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.pdf.business.ITextDocumentsService#getProcessDefinitionNamesWithURI()
	 */
	@Override
	public Map<String, String> getProcessDefinitionNamesWithURI() {
		Map<String, String> names = new TreeMap<String, String>();
		
		List<ITextDocumentURIEntity> entities = getITextDocumentURIEntities();
		for (ITextDocumentURIEntity entity : entities) {
			names.put(
					entity.getProcessDefinitionName(), 
					entity.getProcessDefinitionId().toString());
		}

		return names;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.pdf.business.ITextDocumentsService#getBundlePathsAndNames()
	 */
	@Override
	public Map<String, String> getBundlePathsAndNames() {
		Map<String, String> bundleNames = new TreeMap<String, String>();

		IWMainApplication application = IWMainApplication.getIWMainApplication(
				FacesContext.getCurrentInstance());
		if (application != null) {
			Map<String, IWBundle> bundles = application.getLoadedBundles();
			if (!MapUtil.isEmpty(bundles)) {
				for (IWBundle bundle : bundles.values()) {
					bundleNames.put(
							bundle.getBundleName(),
							bundle.getBundleBaseRealPath());
				}
			}
		}

		return bundleNames;
	}

	@Override
	public String updateTempolarPDF(String processDefinitionId, String source) {
		if (!StringUtil.isEmpty(processDefinitionId) && !StringUtil.isEmpty(source)) {
			MemoryOutputStream outputStream = new MemoryOutputStream(new MemoryFileBuffer());
			String filename = "license-" + System.currentTimeMillis() + ".pdf"; 
			try {
				getPrintingService().print(
						IOUtils.toInputStream(source), 
						outputStream, null);
				if (getRepositoryService().uploadFile(
						CoreConstants.SLASH + PDF_RESOURCE_URL, 
						filename, 
						MimeType.pdf.toString(), 
						new ByteArrayInputStream(outputStream.getBuffer().buffer()))) {
					return getApplication().getIWApplicationContext().getDomain().getURL() 
							+ PDF_RESOURCE_URL + filename;
				}
			} catch (Exception e) {
				java.util.logging.Logger.getLogger(getClass().getName()).log(
						Level.WARNING, "Failed to create PDF document, cause of:", e);
			}
		}

		return "";
	}
}
