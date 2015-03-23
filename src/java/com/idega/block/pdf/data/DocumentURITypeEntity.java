/**
 * @(#)DocumentURIType.java    1.0.0 10:15:37
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
package com.idega.block.pdf.data;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;
import org.directwebremoting.convert.BeanConverter;


/**
 * <p>Saves custom type of {@link DocumentURIEntity}</p>
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 2015 vas. 26
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
/*
 * JPA
 */
@Entity
@Table(name=DocumentURITypeEntity.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(
			name = DocumentURITypeEntity.FIND_BY_ID, 
			query = "FROM DocumentURITypeEntity a WHERE a.id = :" + 
					DocumentURITypeEntity.idProp),
	@NamedQuery(
			name = DocumentURITypeEntity.FIND_BY_IDS, 
			query = "FROM DocumentURITypeEntity a WHERE a.id IN (:" + 
					DocumentURITypeEntity.idProp + ")"),
	@NamedQuery(
			name = DocumentURITypeEntity.FIND_BY_EXTERNAL_IDS, 
			query = "FROM DocumentURITypeEntity a WHERE a.externalId IN (:" + 
					DocumentURITypeEntity.externalIdProp + ")"),
	@NamedQuery(
			name = DocumentURITypeEntity.FIND_BY_NAME, 
			query = "FROM DocumentURITypeEntity a WHERE a.name = :" + 
					DocumentURITypeEntity.nameProp),
	@NamedQuery(
			name = DocumentURITypeEntity.FIND_ALL, 
			query = "FROM DocumentURITypeEntity a")
})

/*
 *  DWR
 */
@DataTransferObject(converter = BeanConverter.class)
public class DocumentURITypeEntity implements Serializable {

	private static final long serialVersionUID = 4440164349396089653L;

	public static final String ENTITY_NAME = "repository_document_uri_type";

	public static final String FIND_BY_ID = "documentURITypeEntity.findById";
	public static final String FIND_BY_IDS = "documentURITypeEntity.findByIds";
	public static final String FIND_BY_EXTERNAL_IDS = "documentURITypeEntity.findByExternalIds";
	public static final String FIND_BY_NAME = "documentURITypeEntity.findByName";
	public static final String FIND_ALL = "documentURITypeEntity.findAll";

	public static final String COLUMN_ID = "id";
	public static final String idProp = "id";
	@RemoteProperty
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	public static final String COLUMN_NAME= "name";
	public static final String nameProp = "name";
	@RemoteProperty
	@Column(name = COLUMN_NAME, nullable = false, unique = true)
	private String name;

	public static final String COLUMN_EXTERNAL_ID = "external_id";
	public static final String externalIdProp = "externalId";
	@RemoteProperty
	@Column(name = COLUMN_EXTERNAL_ID)
	private String externalId;

	public static final String COLUMN_GROUP = "group_id";
	public static final String groupProp = "group";
	@ManyToOne
	@JoinColumn(name = COLUMN_GROUP)
	private DocumentURIGroupEntity group;

	@ManyToMany(mappedBy = DocumentURIEntity.typesProp)
	private List<DocumentURIEntity> documentURIs;

	public DocumentURITypeEntity() {}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public DocumentURIGroupEntity getGroup() {
		return group;
	}

	public void setGroup(DocumentURIGroupEntity group) {
		this.group = group;
	}

	public List<DocumentURIEntity> getDocumentURIs() {
		return documentURIs;
	}

	public void setDocumentURIs(List<DocumentURIEntity> documentURIs) {
		this.documentURIs = documentURIs;
	}

	@Override
	public String toString() {
		return "getExternalId(): " + getExternalId() + ", getName() :" + getName();
	}
}
