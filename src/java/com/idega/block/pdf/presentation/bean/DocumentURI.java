/**
 * @(#)DocumentURI.java    1.0.0 09:31:50
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.event.ValueChangeEvent;

import com.idega.block.pdf.data.DocumentURIEntity;
import com.idega.block.pdf.data.DocumentURITypeEntity;
import com.idega.block.pdf.data.dao.DocumentURIDAO;
import com.idega.block.pdf.presentation.service.DocumentURIGroupService;
import com.idega.faces.presentation.bean.ManagedBean;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.IWContext;
import com.idega.util.CoreUtil;
import com.idega.util.FileUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

/**
 * <p>JSF managed bean for {@link DocumentURIEntity}</p>
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 2015 kov. 3
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
public class DocumentURI extends ManagedBean<DocumentURIEntity> {

	private static final long serialVersionUID = 7357311175475384027L;

	public static final String PARAMETER_BUNDLE_PATH = "editorForm:bundle";
	private String bundlePath;

	private String bundleURL;

	private String repositoryURI;

	public DocumentURI() {}

	public DocumentURI(DocumentURIEntity entity) {
		setEntity(entity);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.faces.presentation.bean.ManagedBean#getDAO()
	 */
	@Override
	public DocumentURIDAO getDAO() {
		return ELUtil.getInstance().getBean(DocumentURIDAO.BEAN_NAME);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.faces.presentation.bean.ManagedBean#getId()
	 */
	@Override
	public Long getId() {
		if (getEntity() != null) {
			return getEntity().getId();
		}

		return null;
	}

	/**
	 * 
	 * @return {@link DocumentURIEntity#getBundleURL()};
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public String getBundleURL() {
		if (getEntity() != null) {
			return getEntity().getBundleURL();
		}

		return null;
	}

	public void setBundleURL(String bundleURL) {
		this.bundleURL = bundleURL;
	}

	/**
	 * 
	 * @return {@link DocumentURIEntity#getBundlePath()};
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public String getBundlePath() {
		if (getEntity() != null) {
			return getEntity().getBundlePath();
		}

		return IWContext.getInstance().getParameter(PARAMETER_BUNDLE_PATH);
	}

	public void setBundlePath(String bundlePath) {
		this.bundlePath = bundlePath;
	}

	public void selectedBundlePathChange(ValueChangeEvent event) {
 		Object value = event.getNewValue();
 		if (value != null) {
			this.bundlePath = value.toString();
 		}
	}

	/**
	 * 
	 * @return extracted {@link IWBundle#getBundleIdentifier()} from
	 * {@link DocumentURIEntity#getBundlePath()} or <code>null</code>
	 * on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public String getBundleName() {
		if (!StringUtil.isEmpty(getBundlePath())) {
			String bundleName = getBundlePath().substring(
					getBundlePath().lastIndexOf("/") + 1);
			if (bundleName.contains(".bundle")) {
				bundleName = bundleName.substring(
						0, bundleName.indexOf(".bundle"));
			}

			return bundleName;
		}
		
		return null;
	}

	/**
	 * 
	 * @return {@link DocumentURIEntity#getBundleURL()} + {@link DocumentURIEntity#getBundlePath()}
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public String getBundleURI() {
		if (!StringUtil.isEmpty(getBundlePath()) && !StringUtil.isEmpty(getBundleURL())) {
			return getBundlePath() + getBundleURL();
		}

		return "-";
	}

	/**
	 * 
	 * @return {@link DocumentURIEntity#getRepositoryURI()};
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public String getRepositoryURI() {
		if (getEntity() != null) {
			return getEntity().getRepositoryURI();
		}

		return null;
	}

	public void setRepositoryURI(String repositoryURI) {
		this.repositoryURI = repositoryURI;
	}
	
	public void selectedRepositoryURIChange(ValueChangeEvent event) {
 		Object value = event.getNewValue();
 		if (value != null) {
			this.repositoryURI = value.toString();
 		}
	}

	/**
	 * 
	 * @return existing path of resources;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public Map<String, String> getBundleURLs() {
		Map<String, String> bundleURLs = new TreeMap<String, String>();

		if (!StringUtil.isEmpty(getBundleName())) {
			List<String> files = FileUtil.getAllFilesRecursively(getBundlePath() + "/resources");
			for (String path : files) {
				path = path.substring(path.indexOf(getBundleName()));
				if (path.contains("/")) {
					path = path.substring(path.indexOf("/"));
				}

				bundleURLs.put(path, path);
			}
		}

		return bundleURLs;
	}

	/*
	 * Types
	 */
	public List<DocumentURIType> getTypes() {
		if (getEntity() != null) {
			List<DocumentURITypeEntity> entities = getEntity().getTypes();
			if (entities != null) {
				ArrayList<DocumentURIType> types = new ArrayList<DocumentURIType>(entities.size());
				for (DocumentURITypeEntity entity: entities) {
					types.add(new DocumentURIType(entity));
				}

				return types;
			}
		}

		return Collections.emptyList();
	}

	/*
	 * Groups
	 */
	protected DocumentURIGroupService getDocumentURIGroupService() {
		return ELUtil.getInstance().getBean(DocumentURIGroupService.BEAN_NAME);
	}

	public List<DocumentURIGroup> getGroups() {
		List<DocumentURIGroup> groups = getDocumentURIGroupService().getDocumentURIGroups();
		for (DocumentURIGroup group : groups) {
			for (DocumentURIType type : getTypes()) {
				group.setSelectedTypeId(type.getId());
			}
		}

		return groups;
	}

	public List<Long> getSelectedTypeIds() {
		List<Long> typeIds = new ArrayList<Long>(getGroups().size());

		IWContext iwc = CoreUtil.getIWContext();
		for (int index = 0; index < getGroups().size(); index++) {
			String typeId = iwc.getParameter("editorForm:groups:" + index + ":typeId");
			if (!StringUtil.isEmpty(typeId)) {
				typeIds.add(Long.valueOf(typeId));
			}
		}

		return typeIds;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.faces.presentation.bean.ManagedBean#save()
	 */
	@Override
	public void save() {
		DocumentURIEntity updatedEntity = getDAO().update(
				null, bundlePath, 
				bundleURL, repositoryURI, 
				getSelectedTypeIds());
		if (updatedEntity != null) {
			setEntity(updatedEntity);
			setSubmitted(Boolean.TRUE);
		}
	}
}
