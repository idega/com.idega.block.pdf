/**
 * @(#)ITextDocumentURI.java    1.0.0 16:39:21
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

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.event.ValueChangeEvent;

import com.idega.block.pdf.data.ITextDocumentURIEntity;
import com.idega.block.pdf.data.dao.ITextDocumentURIDAO;
import com.idega.presentation.IWContext;
import com.idega.util.CoreUtil;
import com.idega.util.FileUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

/**
 * <p>JSF managed bean for {@link ITextDocumentURIEntity}</p>
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 2015 saus. 29
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
public class ITextDocumentURI implements Serializable {

	private static final long serialVersionUID = 8431943648530058039L;

	public static final String PARAMETER_ID = "prm_id";
	private Long id;

	private String bundleName;

	public static final String PARAMETER_BUNDLE_PATH = "editorForm:bundle";
	private String bundlePath;

	private String bundleURL;

	private String repositoryURI;

	private Long processDefinitionId;

	private Long oldProcessDefinitionId;

	private String processDefinitionName;

	private String editorLink = null;

	private ITextDocumentURIEntity entity;

	public static final String PARAMETER_SUBMITTED = "submitted";
	private boolean submitted = Boolean.FALSE;

	private boolean processDefinitionUpdated = Boolean.FALSE;

	protected ITextDocumentURIDAO getDao() {
		return ELUtil.getInstance().getBean(ITextDocumentURIDAO.BEAN_NAME);
	}

	public ITextDocumentURI() {}

	public ITextDocumentURI(ITextDocumentURIEntity entity) {
		this.entity = entity;
	}

	public boolean isProcessDefinitionUpdated() {
		return processDefinitionUpdated;
	}

	public void setProcessDefinitionUpdated(boolean processDefinitionUpdated) {
		this.processDefinitionUpdated = processDefinitionUpdated;
	}

	public Long getProcessDefinitionId() {
		if (this.processDefinitionId == null && getEntity() != null) {
			this.processDefinitionId = getEntity().getProcessDefinitionId();
		}

		return processDefinitionId;
	}

	public void setProcessDefinitionId(Long processDefinitionId) {
			this.processDefinitionId = processDefinitionId;
	}

	public Long getOldProcessDefinitionId() {
		if (this.oldProcessDefinitionId == null) {
			String parameter = IWContext.getInstance().getParameter(
					"editorForm:oldProcessDefinitionId");
			if (!StringUtil.isEmpty(parameter)) {
				this.oldProcessDefinitionId = Long.valueOf(parameter);
			}
		}

		return oldProcessDefinitionId;
	}
	
	protected boolean isUpdatingProcessDefinition() {
		return getOldProcessDefinitionId() != null 
				&& !getOldProcessDefinitionId().equals(getProcessDefinitionId());
	}

	public void setOldProcessDefinitionId(Long oldProcessDefinitionId) {
		if (!isProcessDefinitionUpdated()) {
			this.oldProcessDefinitionId = oldProcessDefinitionId;
		}
	}
	
	public Long getId() {
		if (this.id == null && getEntity() != null) {
			this.id = getEntity().getId();
		}

		return id;
	}

	public void setId(Long id) {
		if (!isProcessDefinitionUpdated()) {
			this.id = id;
		}
	}

	public String getBundleURL() {
		if (StringUtil.isEmpty(this.bundleURL) && getEntity() != null) {
			this.bundleURL = getEntity().getBundleURL();
		}

		return bundleURL;
	}

	public void setBundleURL(String bundleURL) {
		if (!isProcessDefinitionUpdated()) {
			this.bundleURL = bundleURL;
		}
	}

	public String getBundlePath() {
		if (StringUtil.isEmpty(this.bundlePath) && getEntity() != null) {
			this.bundlePath = getEntity().getBundlePath();
		}

		if (StringUtil.isEmpty(this.bundlePath) && !isProcessDefinitionUpdated()) {
			this.bundlePath = IWContext.getInstance().getParameter(
					PARAMETER_BUNDLE_PATH);
		}

		return bundlePath;
	}

	public void setBundlePath(String bundlePath) {
		if (!isProcessDefinitionUpdated()) {
			this.bundlePath = bundlePath;
		}
	}

	public String getBundleName() {
		if (StringUtil.isEmpty(this.bundleName) 
				&& !StringUtil.isEmpty(getBundlePath())) {
			this.bundleName = getBundlePath().substring(
					getBundlePath().lastIndexOf("/") + 1);
			if (this.bundleName.contains(".bundle")) {
				this.bundleName = this.bundleName.substring(
						0, this.bundleName.indexOf(".bundle"));
			}
		}
		
		return bundleName;
	}

	public void setBundleName(String bundleName) {
		if (!isProcessDefinitionUpdated()) {
			this.bundleName = bundleName;
		}
	}

	public String getBundleURI() {
		if (!StringUtil.isEmpty(getBundlePath()) && !StringUtil.isEmpty(getBundleURL())) {
			return getBundlePath() + getBundleURL();
		}

		return "-";
	}

	public String getRepositoryURI() {
		if (StringUtil.isEmpty(this.repositoryURI) && getEntity() != null) {
			this.repositoryURI = getEntity().getRepositoryURI();
		}

		return repositoryURI;
	}

	public void setRepositoryURI(String repositoryURI) {
		if (!isProcessDefinitionUpdated()) {
			this.repositoryURI = repositoryURI;
		}
	}

	public String getProcessDefinitionName() {
		if (this.processDefinitionName == null && getEntity() != null) {
			this.processDefinitionName = getEntity().getProcessDefinitionName();
		}

		return processDefinitionName;
	}

	public void setProcessDefinitionName(String processDefinitionName) {
		if (!isProcessDefinitionUpdated()) {
			this.processDefinitionName = processDefinitionName;
		}
	}

	public String getEditorLink() {
		return editorLink;
	}

	public void setEditorLink(String editorLink) {
		this.editorLink = editorLink;
	}

	public boolean isSubmitted() {
		String submitted = CoreUtil.getIWContext().getParameter(PARAMETER_SUBMITTED);
		if (Boolean.TRUE.toString().equals(submitted)) {
			this.submitted = Boolean.TRUE;
		}

		return this.submitted;
	}

	public void setSubmitted(boolean submitted) {
		this.submitted = submitted;
	}

	

	public void selectedBundlePathChange(ValueChangeEvent event) {
 		Object value = event.getNewValue();
 		if (!isProcessDefinitionUpdated() && value != null) {
			this.bundlePath = value.toString();
 		}
	}

	public void selectedProcessDefinitionIdChange(ValueChangeEvent event) {
		Object value = event.getNewValue();
		if (value != null) {
			setProcessDefinitionId(Long.valueOf(value.toString()));
			setEntity(getDao().findByProcessDefinition(Long.valueOf(value.toString())));

			if (isUpdatingProcessDefinition()) {
				this.id = null;
				this.processDefinitionName = null;
				this.repositoryURI = null;
				this.bundlePath = null;
				this.bundleURL = null;
				this.bundleName = null;
				this.processDefinitionUpdated = Boolean.TRUE;
			}

			this.oldProcessDefinitionId = Long.valueOf(value.toString());
		}
	}

	public void save() {
		if (getDao().update(null, 
				this.bundlePath,
				this.bundleURL,
				this.repositoryURI, 
				this.processDefinitionId, 
				ITextDocumentURIEntity.class) != null) {
			setSubmitted(Boolean.TRUE);
		}
	}

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

	public ITextDocumentURIEntity getEntity() {
		if (this.entity == null) {
			if (this.id == null) {
				IWContext context = CoreUtil.getIWContext();
				if (context != null) {
					String parameter = context.getParameter(PARAMETER_ID);
					if (!StringUtil.isEmpty(parameter)) {
						this.id = Long.valueOf(parameter);
					}
				}
			}

			if (this.id != null) {
				this.entity = getDao().findById(this.id);
			}
		}

		return entity;
	}

	public void setEntity(ITextDocumentURIEntity entity) {
		this.entity = entity;
	}
}
