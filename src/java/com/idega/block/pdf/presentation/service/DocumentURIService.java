/**
 * @(#)DocumentURIService.java    1.0.0 16:32:09
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
package com.idega.block.pdf.presentation.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;

import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.block.pdf.data.DocumentURIEntity;
import com.idega.block.pdf.data.dao.DocumentURIDAO;
import com.idega.block.pdf.presentation.DocumentURIEditor;
import com.idega.block.pdf.presentation.bean.DocumentURI;
import com.idega.block.pdf.presentation.bean.ITextDocument;
import com.idega.block.pdf.presentation.bean.ITextDocumentURI;
import com.idega.faces.presentation.service.ManagedBeanService;
import com.idega.repository.RepositoryService;
import com.idega.repository.bean.RepositoryItem;
import com.idega.util.CoreConstants;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.datastructures.map.MapUtil;
import com.idega.util.expression.ELUtil;

/**
 * <p>Presentation bean for {@link DocumentURIEntity}</p>
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 2015 kov. 3
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
/*
 * Spring
 */
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Service(DocumentURIService.BEAN_NAME)
@EnableAspectJAutoProxy(proxyTargetClass=true)

/*
 * DWR
 */
@RemoteProxy(
		name=DocumentURIService.JAVASCRIPT_CLASS_NAME,
		creator=SpringCreator.class, 
		creatorParams={
			@Param(
					name="beanName", 
					value=DocumentURIService.BEAN_NAME),
			@Param(
					name="javascript", 
					value=DocumentURIService.JAVASCRIPT_CLASS_NAME)
		}
)
public class DocumentURIService extends ManagedBeanService<DocumentURIEditor> {

	public static final String BEAN_NAME = "documentURIService";

	public static final String JAVASCRIPT_CLASS_NAME = "DocumentURIService";

	public static final String REPOSITORY_PATH = CoreConstants.PUBLIC_PATH + "/dynamic_resources/";	

	@Autowired
	private DocumentURIDAO documentURIDAO;

	@Autowired
	private RepositoryService repositoryService;

	protected DocumentURIDAO getDocumentURIDAO() {
		if (this.documentURIDAO == null) {
			ELUtil.getInstance().autowire(this);
		}

		return this.documentURIDAO;
	}

	/**
	 * 
	 * @return service to work with {@link Repository}
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected RepositoryService getRepositoryService() {
		if (this.repositoryService == null) {
			ELUtil.getInstance().autowire(this);
		}

		return this.repositoryService;
	}

	public List<DocumentURI> getDocumentURIs() {
		ArrayList<DocumentURI> types = new ArrayList<DocumentURI>();
		
		List<DocumentURIEntity> typeEntities = getDocumentURIDAO().findAll();
		for (DocumentURIEntity entity : typeEntities) {
			DocumentURI bean = new DocumentURI(entity);
			bean.setEditorLink(getEditorLink(entity.getId()));
			types.add(bean);
		}

		return types;
	}

	public Map<String, Long> getDocumentsMap() {
		TreeMap<String, Long> map = new TreeMap<String, Long>();
		
		List<DocumentURIEntity> entities = getDocumentURIDAO().findAll();
		for (DocumentURIEntity entity : entities) {
			map.put(entity.getRepositoryURI(), entity.getId());
		}

		return map;
	}

	public Map<String, String> getRepositoryURIs() {
		TreeMap<String, String> repositoryURIs = new TreeMap<String, String>();

		Collection<String> nodes = null;
		try {
			nodes = getRepositoryService()
					.getChildNodesAsRootUserRecursively(REPOSITORY_PATH);
		} catch (RepositoryException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, 
					"Failed to get files list from directory: '" 
							+ REPOSITORY_PATH + "';");
		}

		if (!ListUtil.isEmpty(nodes)) {
			for (String node : nodes) {
				repositoryURIs.put(
						"/content" + node, 
						"/content" + node);
			}
		}

		return repositoryURIs.descendingMap();
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

	/**
	 * 
	 * @return all existing filenames with {@link Date}s in 
	 * {@link ITextDocumentURI#getRepositoryURI()} directory or 
	 * {@link Collections#emptyMap()} on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	@RemoteMethod
	public Map<String, String> getRevisions() {
		TreeMap<String, String> revisions = new TreeMap<String, String>();

		Collection<RepositoryItem> nodes = null;
		try {
			nodes = getRepositoryService()
					.getChildNodesAsRootUser(getRepositoryPath());
		} catch (RepositoryException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, 
					"Failed to get files list from directory: '" 
							+ getRepositoryPath() + "';");
		}

		if (!ListUtil.isEmpty(nodes)) {
			for (RepositoryItem node : nodes) {
				String fileName = node.getNodeName();
				String timeString = fileName.substring(0, fileName.indexOf(CoreConstants.DOT));
				Long timeInMillis = Long.valueOf(timeString);
				revisions.put(
						new IWTimestamp(timeInMillis).toSQLString(), 
						"/content" + node.getAbsolutePath());
			}
		}

		return revisions.descendingMap();
	}

	/**
	 * 
	 * @return <code>true</code> if {@link ITextDocument#getRevisions()} is not
	 * empty;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	@RemoteMethod
	public boolean isRevisionExist() {
		return !MapUtil.isEmpty(getRevisions());
	}

	@RemoteMethod
	public void remove(Long id) {
		getDocumentURIDAO().remove(id);
	}
}
