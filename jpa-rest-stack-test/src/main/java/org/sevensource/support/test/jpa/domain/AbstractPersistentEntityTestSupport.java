package org.sevensource.support.test.jpa.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.junit.Test;
import org.sevensource.support.jpa.domain.PersistentEntity;
import org.sevensource.support.test.jpa.AbstractJpaTestSupport;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.transaction.BeforeTransaction;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierApi;
import nl.jqno.equalsverifier.Warning;


@DataJpaTest
public abstract class AbstractPersistentEntityTestSupport<ID extends Serializable, E extends PersistentEntity<ID>> extends AbstractJpaTestSupport<E> {

	private final Class<E> domainClass;

	protected AbstractPersistentEntityTestSupport(Class<E> domainClass) {
		super(domainClass);
		this.domainClass = domainClass;
	}

	protected abstract ID getNewId();

	@BeforeTransaction
	public void beforeTransaction() {
		deleteAll();
	}

	protected final EqualsVerifierApi<E> defaultEqualsVerifier() {

		return
			    EqualsVerifier
		    	.forClass(domainClass)
		    	.withRedefinedSuperclass()						// disable check, since domainClass cannot equal to AbstractUUIDEntity$$DynamicSubclass@0
		        .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)

		        // config for IMPL where id is always set and hash of id is returned
		        //.withNonnullFields("id")						// IF we're selfAssigning id upon access: hashCode relies on id, but equals does not.

		        // config for IMPL where id is not always set and hashcode always returns the same
		        //.suppress(Warning.STRICT_INHERITANCE)			// Subclass: equals is not final
		        //.suppress(Warning.STRICT_HASHCODE)				// IF we're always returning the same hashCode: hashCode relies on id, but equals does not.


//		        //config for IMPL in which only id is compared and always the same hashcode is returned
//		        .suppress(Warning.STRICT_INHERITANCE)			// Subclass: equals is not final
//		        .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY) //object does not equal an identical copy of itself
//		        .suppress(Warning.STRICT_HASHCODE)				// IF we're always returning the same hashCode: hashCode relies on id, but equals does not.

		        //config for IMPL in which only id is compared and always the hashcode of id is returned
		        .withNonnullFields("id")						// IF we're selfAssigning id upon access: hashCode relies on id, but equals does not.
		        .suppress(Warning.STRICT_INHERITANCE)			// Subclass: equals is not final
		        .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY) //object does not equal an identical copy of itself
		        ;
	}

	@Test
	public void equalsContract() {
		defaultEqualsVerifier().verify();
	}

	@Test
	public void test_equality_with_empty_objects() {
		E entity1 = null;
		E entity2 = null;
		try {
			entity1 = domainClass.newInstance();
			entity2 = domainClass.newInstance();
		} catch (Exception e) {
			// do nothing, cannot instantiante without default constructor
			return;
		}

		Set<E> set = new HashSet<>();
		set.add(entity1);
		set.add(entity2);

		int expectedSize;

		if(entity1.getId() == null) {
			expectedSize = 2;
		} else if(entity1.getId().equals(entity2.getId())) {
			expectedSize = 1;
		} else {
			expectedSize = 2;
		}

		assertThat(set).hasSize(expectedSize);
	}

	@Test
	public void test_equality_with_populated_objects() {
		E entity1 = populate();
		E entity2 = populate();

		Set<E> set = new HashSet<>();
		set.add(entity1);
		set.add(entity2);
		assertThat(set).hasSize(2);
	}

	@Test
	public void equals_works() {
		ensureEmpty();

		E e1 = populate();
		E e2 = populate();

		assertThat(e1).isEqualTo(e1);
		assertThat(e2).isEqualTo(e2);
		assertThat(e1).isNotEqualTo(e2);

		getEntityManager().persist(e1);
		getEntityManager().flush();

		assertThat(e1).isEqualTo(e1);
		assertThat(e1).isNotEqualTo(e2);

		assertThat(e1).isNotEqualTo(null);
		assertThat(e1).isNotEqualTo(new Object());

		e2.setId(null);
		assertThat(e1).isNotEqualTo(e2);

		e1.setId(null);
		assertThat(e1).isNotEqualTo(e2);
	}

	@Test
	public void persist_creates_an_ID() {
		ensureEmpty();

		E e = populate();
		getEntityManager().persist(e);
		getEntityManager().flush();
		assertThat(e.getId()).isNotNull();
	}

	@Test
	public void persist_updates_auditing() {
		ensureEmpty();

		E e = populate();

		Instant BEGIN = Instant.now();
		try {
			Thread.sleep(101);
			getEntityManager().persist(e);
			getEntityManager().flush();
			Thread.sleep(101);
		} catch (InterruptedException e1) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException(e1);
		}
		Instant END = Instant.now();

		assertThat(e.getCreatedBy()).isNotEmpty();
		assertThat(e.getLastModifiedBy()).isNotEmpty();

		assertThat(e.getCreatedDate()).isBetween(BEGIN, END);
		assertThat(e.getLastModifiedDate()).isBetween(BEGIN, END);

		assertThat(e.getVersion()).isEqualTo(0);
	}

	@Test
	public void persist_allows_existing_id() {
		ensureEmpty();

		ID id = getNewId();
		E e = populate();
		e.setId(id);
		getEntityManager().persist(e);
		getEntityManager().flush();

		assertThat(e.getId()).isEqualTo(id);

		E ee = getEntityManager().find(domainClass, id);
		assertThat(ee.getId()).isEqualTo(id);
	}

	@Test
	public void isNew_works_for_instantiated_entity() {
		E e = populate();
		assertThat(e.isNew()).isTrue();
	}
	
	@Test
	public void isNew_works_for_persisted_entity() {
		E e = populate();
		assertThat(e.isNew()).isTrue();
		getEntityManager().persist(e);
		getEntityManager().flush();
		assertThat(e.isNew()).isFalse();
	}
	
	@Test
	public void isNew_works_with_null_id() {
		E e = populate();
		e.setId(null);
		assertThat(e.isNew()).isTrue();
		getEntityManager().persist(e);
		getEntityManager().flush();
		assertThat(e.isNew()).isFalse();
	}

	@Test
	public void toString_works() {
		E e = populate();
		assertThat(e.toString()).isNotBlank();
	}

	@Test
	@Transactional(value=TxType.NOT_SUPPORTED)
	// We need NOT_SUPPORTED here to have full control over the transactions
	public void assert_equality_constraints() {

		ensureEmpty();
		E entity = populate();
		JpaEqualityAndHashCodeVerifier<E> verifier = new JpaEqualityAndHashCodeVerifier<>(entity, getEntityManagerFactory(), entityChangesHashCodeAfterPersist());
		verifier.verify();
	}

	protected abstract boolean entityChangesHashCodeAfterPersist();
}
