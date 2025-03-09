
package org.thingsboard.server.dao.sql;

import org.thingsboard.server.dao.model.BaseEntity;
import org.thingsboard.server.dao.util.SqlDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SqlDao
public abstract class JpaPartitionedAbstractDao<E extends BaseEntity<D>, D> extends JpaAbstractDao<E, D> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected E doSave(E entity, boolean isNew) {
        createPartition(entity);
        if (isNew) {
            entityManager.persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }
        return entity;
    }

    public abstract void createPartition(E entity);

}
