/**
 * @(#)ITextDocumentURIDAOImpl.java    1.0.0 15:34:47
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
package com.idega.block.pdf.data.dao.impl;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.block.pdf.data.ITextDocumentURIEntity;
import com.idega.block.pdf.data.dao.ITextDocumentURIDAO;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.data.SimpleQuerier;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

/**
 * <p>Implementation of {@link ITextDocumentURIDAO}</p>
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 2015 saus. 28
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
@Repository(ITextDocumentURIDAO.BEAN_NAME)
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Transactional(readOnly = false)
public class ITextDocumentURIDAOImpl extends GenericDaoImpl implements
		ITextDocumentURIDAO {

	protected String getProcessDefinitionName(Long processDefinitionId) {
		if (processDefinitionId != null) {
			StringBuilder query = new StringBuilder();
			query.append("SELECT jpd.NAME_ FROM JBPM_PROCESSDEFINITION jpd ");
			query.append("WHERE jpd.ID_ = " + processDefinitionId);

			List<Serializable[]> results = null;
			try {
				results = SimpleQuerier.executeQuery(query.toString(), 1);
				if (!ListUtil.isEmpty(results)) {
					return results.iterator().next()[0].toString();
				}
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "Failed to get results by query: '" + 
						query.toString() + "' cause of: ", e);
			}
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see is.idega.idegaweb.egov.gumbo.dao.FishingLicenseURIDAO#update(is.idega.idegaweb.egov.gumbo.data.FishingLicenseURI)
	 */
	@Override
	public <T extends ITextDocumentURIEntity> T update(T entity) {
		if (entity != null) {
			if (findById(entity.getId()) == null) {
				persist(entity);
				if (entity.getId() != null) {
					getLogger().info("Entity: " + entity + " created!");
					return entity;
				}
			} else {
				entity = merge(entity);
				if (entity != null) {
					getLogger().info("Entity: " + entity + " updated");
					return entity;
				}
			}		
		}

		getLogger().warning("Failed to create/update entity: " + entity);
		return null;
	}

	/* (non-Javadoc)
	 * @see is.idega.idegaweb.egov.gumbo.dao.FishingLicenseURIDAO#update(java.lang.Long, java.lang.Long, java.lang.String, java.lang.Class)
	 */
	@Override
	public <T extends ITextDocumentURIEntity> T update(
			Long id, 
			String bundleURI,
			String repositoryURI, 
			Long processDefinition, 
			String processDefinitionName, 
			Class<T> clazz) {
		T entityToUpdate = null;
		if (id != null) {
			entityToUpdate = findById(id, clazz);
		}

		if (entityToUpdate == null && processDefinition != null) {
			entityToUpdate = findByProcessDefinition(processDefinition, clazz);
		}

		if (entityToUpdate == null) {
			try {
				entityToUpdate = clazz.newInstance();
			} catch (InstantiationException e) {
				getLogger().log(Level.WARNING, 
						"Unable to find constructor for: " + clazz, e);
				return null;
			} catch (IllegalAccessException e) {
				getLogger().log(Level.WARNING, 
						"Constructor of: " + clazz + " can't be accessed: " , e);
				return null;
			}
		}

		if (!StringUtil.isEmpty(bundleURI)) {
			entityToUpdate.setBundleURI(bundleURI);
		}

		if (!StringUtil.isEmpty(repositoryURI)) {
			entityToUpdate.setRepositoryURI(repositoryURI);
		}

		if (processDefinition != null) {
			entityToUpdate.setProcessDefinitionId(processDefinition);
		}

		if (!StringUtil.isEmpty(processDefinitionName)) {
			entityToUpdate.setProcessDefinitionName(processDefinitionName);
		}

		return update(entityToUpdate);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.pdf.data.dao.ITextDocumentURIDAO#update(java.lang.Long, java.lang.String, java.lang.String, java.lang.Long, java.lang.Class)
	 */
	@Override
	public <T extends ITextDocumentURIEntity> T update(
			Long id,
			String bundleURI,
			String repositoryURI,
			Long processDefinition, 
			Class<T> clazz) {
		return update(
				id, 
				bundleURI, 
				repositoryURI, 
				processDefinition, 
				getProcessDefinitionName(processDefinition), 
				clazz);
	}

	/* (non-Javadoc)
	 * @see is.idega.idegaweb.egov.gumbo.dao.FishingLicenseURIDAO#update(java.math.BigDecimal, java.lang.String, java.lang.Class)
	 */
	@Override
	public <T extends ITextDocumentURIEntity> T update(
			Class<T> clazz,
			String bundleURI, 
			String repositoryURI, 
			Long processDefinition, 
			String processDefinitionName) {

		return update(null, bundleURI, repositoryURI, processDefinition, 
				processDefinitionName, clazz);
	}

	/* (non-Javadoc)
	 * @see is.idega.idegaweb.egov.gumbo.dao.FishingLicenseURIDAO#remove(is.idega.idegaweb.egov.gumbo.data.FishingLicenseURI)
	 */
	@Override
	public void remove(ITextDocumentURIEntity entity) {
		if (entity != null) {
			super.remove(entity);
		}
	}

	@Override
	public void remove(Long id) {
		remove(findById(id));	
	}

	/* (non-Javadoc)
	 * @see is.idega.idegaweb.egov.gumbo.dao.FishingLicenseURIDAO#getById(java.lang.Long, java.lang.Class)
	 */
	@Override
	public <T extends ITextDocumentURIEntity> T findById(Long id, Class<T> clazz) {
		if (id != null) {
			return getSingleResult(
					ITextDocumentURIEntity.FIND_BY_ID,
					clazz, 
					new Param(ITextDocumentURIEntity.idProp, id));
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see is.idega.idegaweb.egov.gumbo.dao.FishingLicenseURIDAO#getById(java.lang.Long)
	 */
	@Override
	public ITextDocumentURIEntity findById(Long id) {
		return findById(id, ITextDocumentURIEntity.class);
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.egov.gumbo.dao.FishingLicenseURIDAO#getByProcessDefinition(org.jbpm.graph.def.ProcessDefinition, java.lang.Class)
	 */
	@Override
	public <T extends ITextDocumentURIEntity> T findByProcessDefinition(
			Long processDefinition, Class<T> clazz) {
		if (processDefinition != null) {
			return getSingleResult(
					ITextDocumentURIEntity.FIND_BY_PROCESS_DEFINITION_ID,
					clazz, 
					new Param(ITextDocumentURIEntity.processDefinitionProp, processDefinition));
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.egov.gumbo.dao.FishingLicenseURIDAO#getByProcessDefinition(org.jbpm.graph.def.ProcessDefinition)
	 */
	@Override
	public ITextDocumentURIEntity findByProcessDefinition(
			Long processDefinition) {
		return findByProcessDefinition(processDefinition, ITextDocumentURIEntity.class);
	}

	/* (non-Javadoc)
	 * @see is.idega.idegaweb.egov.gumbo.dao.FishingLicenseURIDAO#getAll()
	 */
	@Override
	public List<ITextDocumentURIEntity> findAll() {
		return getResultList(
				ITextDocumentURIEntity.FIND_ALL,
				ITextDocumentURIEntity.class);
	}
}
