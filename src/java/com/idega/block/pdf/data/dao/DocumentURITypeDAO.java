package com.idega.block.pdf.data.dao;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.idega.block.pdf.data.DocumentURIGroupEntity;
import com.idega.block.pdf.data.DocumentURITypeEntity;
import com.idega.core.persistence.GenericDao;

/**
 * 
 * <p>Data access object for {@link DocumentURITypeEntity}</p>
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 2015 vas. 26
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
public interface DocumentURITypeDAO extends GenericDao {

	static final String BEAN_NAME = "documentURITypeDAO";

	static final String JAVASCRIPT_CLASS_NAME = "DocumentURITypeDAO";

	/**
	 *
	 * @param id is {@link DocumentURITypeEntity#getId()}, not <code>null</code>.
	 * @param clazz is {@link Type} or sub-type of {@link DocumentURITypeEntity},
	 * not <code>null</code>;
	 * @return {@link DocumentURITypeEntity} by given id or <code>null</code>
	 * on failure.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	<T extends DocumentURITypeEntity> T findById(Long id, Class<T> clazz);

	/**
	 *
	 * @param id is {@link DocumentURITypeEntity#getId()}, not <code>null</code>.
	 * @return {@link DocumentURITypeEntity} by given id or <code>null</code>
	 * on failure.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	DocumentURITypeEntity findById(Long id);

	/**
	 * 
	 * @param ids is {@link Collection} of {@link DocumentURITypeEntity#getId()}, 
	 * not <code>null</code>.
	 * @return entities or {@link Collections#emptyList()} on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	List<DocumentURITypeEntity> findByIds(Collection<Long> ids);

	/**
	 * 
	 * @param externalIds is {@link Collection} of id's defined in remote system, 
	 * not <code>null</code>
	 * @return entities or {@link Collections#emptyList()} on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	List<DocumentURITypeEntity> findByExternalIds(Collection<String> externalIds);

	/**
	 * 
	 * @param externalId is {@link DocumentURITypeEntity#getExternalId()}, 
	 * not <code>null</code>;
	 * @param groupIdentifier is {@link DocumentURIGroupEntity#getIdentifier()},
	 * not <code>null</code>;
	 * @return entity by criteria or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	DocumentURITypeEntity findByExternalId(String externalId, String groupIdentifier);

	/**
	 * 
	 * @param name is {@link DocumentURITypeEntity#getName()}, not <code>null</code>;
	 * @param clazz is {@link Type} or sub-type of {@link DocumentURITypeEntity},
	 * not <code>null</code>;
	 * @return {@link DocumentURITypeEntity} by given name or <code>null</code>
	 * on failure.
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	<T extends DocumentURITypeEntity> T findByName(String name, Class<T> clazz);
	
	/**
	 * 
	 * @param name is {@link DocumentURITypeEntity#getName()}, not <code>null</code>;
	 * @return {@link DocumentURITypeEntity} by given name or <code>null</code>
	 * on failure.
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	DocumentURITypeEntity findByName(String name);

	/**
	 *
	 * @return all {@link DocumentURITypeEntity}s, existing in database.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	List<DocumentURITypeEntity> findAll();

	/**
	 * 
	 * @param entity to update, not <code>null</code>;
	 * @return created/updated entity or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	<T extends DocumentURITypeEntity> T update(T entity);

	/**
	 * 
	 * @param id is {@link DocumentURITypeEntity#getId()} of entity to update,
	 * new entity is created when <code>null</code>;
	 * @param name is {@link DocumentURITypeEntity#getName()}, 
	 * skipped if <code>null</code>;
	 * @param externalId is defined in remote system, skipped if <code>null</code>;
	 * @param group
	 * @param clazz is {@link Type} or sub-type of {@link DocumentURITypeEntity},
	 * not <code>null</code>;
	 * @return created/updated entity or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	<T extends DocumentURITypeEntity> T update(
			Long id, 
			String name, 
			String externalId, 
			DocumentURIGroupEntity group, 
			Class<T> clazz);

	/**
	 * 
	 * @param id is {@link DocumentURITypeEntity#getId()} of entity to update,
	 * new entity is created when <code>null</code>;
	 * @param name is {@link DocumentURITypeEntity#getName()}, 
	 * skipped if <code>null</code>;
	 * @param externalId is defined in remote system, skipped if <code>null</code>
	 * @param groupId is id of {@link DocumentURIGroupEntity}, 
	 * skipped if <code>null</code>;
	 * @return created/updated entity or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	DocumentURITypeEntity update(Long id, String name, 
			String externalId, Long groupId);

	/**
	 * 
	 * @param entity to remove, not <code>null</code>;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	void removeEntity(DocumentURITypeEntity entity);

	/**
	 * 
	 * <p>Removes entity</p>
	 * @param id is {@link DocumentURITypeEntity#getId()}, not <code>null</code>
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	void remove(Long id);
}
