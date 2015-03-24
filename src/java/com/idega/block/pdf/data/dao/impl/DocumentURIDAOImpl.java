/**
 * @(#)DocumentURIDAOImpl.java    1.0.0 12:17:47
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;

import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.block.pdf.data.DocumentURIEntity;
import com.idega.block.pdf.data.DocumentURITypeEntity;
import com.idega.block.pdf.data.dao.DocumentURIDAO;
import com.idega.block.pdf.data.dao.DocumentURITypeDAO;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.data.SimpleQuerier;
import com.idega.util.ArrayUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

/**
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 2015 vas. 26
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
/*
 * Spring
 */
@Repository(DocumentURIDAO.BEAN_NAME)
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Transactional(readOnly = false)
@EnableAspectJAutoProxy(proxyTargetClass=true)

/*
 * DWR
 */
@RemoteProxy(
		name=DocumentURIDAO.JAVASCRIPT_CLASS_NAME,
		creator=SpringCreator.class, 
		creatorParams={
			@org.directwebremoting.annotations.Param(
					name="beanName", 
					value=DocumentURIDAO.BEAN_NAME),
			@org.directwebremoting.annotations.Param(
					name="javascript", 
					value=DocumentURIDAO.JAVASCRIPT_CLASS_NAME)
		}
)
public class DocumentURIDAOImpl extends GenericDaoImpl implements
		DocumentURIDAO {

	@Autowired
	private DocumentURITypeDAO documentURITypeDAO;

	protected DocumentURITypeDAO getDocumentURITypeDAO() {
		if (this.documentURITypeDAO == null) {
			ELUtil.getInstance().autowire(this);
		}

		return this.documentURITypeDAO;
	}

	/* (non-Javadoc) 
	 * @see com.idega.block.pdf.data.dao.DocumentURIDAO#findById(java.lang.Long, java.lang.Class)
	 */
	@Override
	public <T extends DocumentURIEntity> T findById(Long id, Class<T> clazz) {
		if (id != null) {
			return getSingleResult(
					DocumentURIEntity.FIND_BY_ID,
					clazz, 
					new Param(DocumentURIEntity.idProp, id));
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.block.pdf.data.dao.DocumentURIDAO#findById(java.lang.Long)
	 */
	@Override
	public DocumentURIEntity findById(Long id) {
		return findById(id, DocumentURIEntity.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.pdf.data.dao.DocumentURIDAO#findByIds(java.util.Collection)
	 */
	@Override
	public List<DocumentURIEntity> findByIds(Collection<Long> ids) {
		if (!ListUtil.isEmpty(ids)) {
			return getResultList(
					DocumentURIEntity.FIND_BY_IDS,
					DocumentURIEntity.class, 
					new com.idega.core.persistence.Param(DocumentURIEntity.idProp, ids));
		}

		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.pdf.data.dao.DocumentURIDAO#findByTypeIds(java.util.Collection)
	 */
	@Override
	public List<DocumentURIEntity> findByTypeIds(Collection<Long> typeIds) {
		if (!ListUtil.isEmpty(typeIds)) {
			StringBuilder sb = new StringBuilder("SELECT duri.id FROM ");
			sb.append(DocumentURIEntity.ENTITY_NAME).append(" duri ");
			sb.append("JOIN ").append(DocumentURIEntity.JOINED_ENTITY_NAME).append(" middle ");
			sb.append("ON duri.").append(DocumentURIEntity.COLUMN_ID).append(" = ").append("middle.").append(DocumentURIEntity.COLUMN_URI_ID);
			sb.append(" AND middle.").append(DocumentURIEntity.COLUMN_TYPE_ID).append(" IN (");

			for (Iterator<Long> iterator = typeIds.iterator(); iterator.hasNext();) {
				sb.append(iterator.next());
				if (iterator.hasNext()) {
					sb.append(", ");
				}
			}

			sb.append(")");

			String[] resultset = null;
			try {
				resultset = SimpleQuerier.executeStringQuery(sb.toString());
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "", e);
			}

			ArrayList<Long> ids = new ArrayList<Long>();

			if (!ArrayUtil.isEmpty(resultset)) {
				for (String result: resultset) {
					ids.add(Long.valueOf(result));
				}
			}

			return findByIds(ids);
		}

		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.pdf.data.dao.DocumentURIDAO#findByTypes(java.util.Collection)
	 */
	@Override
	public List<DocumentURIEntity> findByTypes(Collection<DocumentURITypeEntity> types) {
		if (!ListUtil.isEmpty(types)) {
			ArrayList<Long> ids = new ArrayList<Long>();
			for (DocumentURITypeEntity type : types) {
				ids.add(type.getId());
			}

			return findByTypeIds(ids);
		}

		return Collections.emptyList();
	}

	/**
	 * 
	 * <p>Avoiding {@link Collection#containsAll(Collection)} trap</p>
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected boolean containsAll(
			Collection<DocumentURITypeEntity> container, 
			Collection<DocumentURITypeEntity> elements) {
		if (!ListUtil.isEmpty(container) && !ListUtil.isEmpty(elements)) {
			ArrayList<Long> containerIds = new ArrayList<Long>();
			for (DocumentURITypeEntity type : container) {
				containerIds.add(type.getId());
			}

			ArrayList<Long> elementsIds = new ArrayList<Long>();
			for (DocumentURITypeEntity type : elements) {
				elementsIds.add(type.getId());
			}

			return containerIds.containsAll(elementsIds);
		}

		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.idega.block.pdf.data.dao.DocumentURIDAO#findByTypesStrict(java.util.Collection)
	 */
	@Override
	public List<DocumentURIEntity> findByTypesStrict(
			Collection<DocumentURITypeEntity> types) {
		List<DocumentURIEntity> filteredEntities = new ArrayList<DocumentURIEntity>();

		if (!ListUtil.isEmpty(types)) {
			List<DocumentURIEntity> entities = findByTypes(types);
			for (DocumentURIEntity entity : entities) {
				if (containsAll(entity.getTypes(), types)) {
					filteredEntities.add(entity);
				}
			}
		}

		return filteredEntities;
	}

	/**
	 * 
	 * @param entities to filter, not <code>null</code>;
	 * @param types to filter by, not <code>null</code>;
	 * @return filtered entities with exact given types;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected List<DocumentURIEntity> getEntitiesByExactTypes(
			Collection<DocumentURIEntity> entities, 
			Collection<DocumentURITypeEntity> types) {
		ArrayList<DocumentURIEntity> filteredEntities = new ArrayList<DocumentURIEntity>();

		if (!ListUtil.isEmpty(types) && !ListUtil.isEmpty(entities)) {
			for (DocumentURIEntity entity: entities) {
				if (containsAll(entity.getTypes(), types) && 
						containsAll(types, entity.getTypes())) {
					filteredEntities.add(entity);
				}
			}
		}

		return filteredEntities;
	}

	/**
	 * 
	 * @param entities to sort, not <code>null</code>;
	 * @return entities sorted by time in milliseconds descending, when
	 * {@link DocumentURIEntity#getRepositoryURI()} has name of creation date;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected Map<Long, DocumentURIEntity> sortByTime(
			Collection<DocumentURIEntity> entities) {
		TreeMap<Long, DocumentURIEntity> map = new TreeMap<Long, DocumentURIEntity>();

		if (!ListUtil.isEmpty(entities)) {
			for (DocumentURIEntity entity: entities) {
				String uri = entity.getRepositoryURI();
				
				try {
					uri = uri.substring(uri.lastIndexOf('/') + 1, uri.lastIndexOf('.'));
					Long time = Long.valueOf(uri);
					map.put(time, entity);
				} catch (Exception e) {
					getLogger().info("Can't convert word: '" + uri + "' to long");
				}
			}
		}

		return map.descendingMap();
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.pdf.data.dao.DocumentURIDAO#findNewestByTypes(java.util.Collection)
	 */
	@Override
	public Collection<DocumentURIEntity> findNewestByTypes(
			Collection<DocumentURITypeEntity> types) {
		HashMap<Long, DocumentURIEntity> filteredEntities = new HashMap<Long, DocumentURIEntity>();

		List<DocumentURIEntity> entities = findByTypesStrict(types);
		for (DocumentURIEntity entity : entities) {
			List<DocumentURIEntity> entitiesByTypes = getEntitiesByExactTypes(
					entities, entity.getTypes());
			Map<Long, DocumentURIEntity> sortedEntities = sortByTime(entitiesByTypes);
			Entry<Long, DocumentURIEntity> entry = sortedEntities.entrySet().iterator().next();
			filteredEntities.put(entry.getKey(), entry.getValue());
		}

		return filteredEntities.values();
	}

	/* (non-Javadoc)
	 * @see com.idega.block.pdf.data.dao.DocumentURIDAO#findAll()
	 */
	@Override
	public List<DocumentURIEntity> findAll() {
		return getResultList(
				DocumentURIEntity.FIND_ALL,
				DocumentURIEntity.class);
	}

	/* (non-Javadoc)
	 * @see com.idega.block.pdf.data.dao.DocumentURIDAO#update(com.idega.block.pdf.data.DocumentURIEntity)
	 */
	@Override
	public <T extends DocumentURIEntity> T update(T entity) {
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

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.pdf.data.dao.DocumentURIDAO#update(java.lang.Long, java.lang.String, java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public <T extends DocumentURIEntity> T update(
			Long id, 
			String bundlePath,
			String bundleURL, 
			String repositoryURI, 
			List<DocumentURITypeEntity> types, 
			Class<T> clazz) {
		T entityToUpdate = null;
		if (id != null) {
			entityToUpdate = findById(id, clazz);
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

		if (!StringUtil.isEmpty(bundleURL)) {
			entityToUpdate.setBundleURL(bundleURL);
		}

		if (!StringUtil.isEmpty(bundlePath)) {
			entityToUpdate.setBundlePath(bundlePath);
		}

		if (!StringUtil.isEmpty(repositoryURI)) {
			entityToUpdate.setRepositoryURI(repositoryURI);
		}

		if (types != null) {
			entityToUpdate.setTypes(types);
		}

		return update(entityToUpdate);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.pdf.data.dao.DocumentURIDAO#update(java.lang.Long, java.lang.String, java.lang.String, java.lang.String, java.lang.Long, java.lang.Long)
	 */
	@Override
	public DocumentURIEntity update(Long id, String bundlePath,
			String bundleURL, String repositoryURI, List<Long> types) {
		return update(id, bundlePath, bundleURL, repositoryURI, 
				getDocumentURITypeDAO().findByIds(types), 
				DocumentURIEntity.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.idega.block.pdf.data.dao.DocumentURIDAO#remove(com.idega.block.pdf.data.DocumentURIEntity)
	 */
	@Override
	public void remove(DocumentURIEntity entity) {
		if (entity != null) {
			super.remove(entity);
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.block.pdf.data.dao.DocumentURIDAO#remove(java.lang.Long)
	 */
	@Override
	public void remove(Long id) {
		remove(findById(id));
	}
}
