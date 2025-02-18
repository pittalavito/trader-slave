package app.traderslave.service;

import app.traderslave.factory.BasePersistenceFactory;
import app.traderslave.model.domain.BasePersistentModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public abstract class BaseJpaService<T extends BasePersistentModel, R extends JpaRepository<T, Long>, F extends BasePersistenceFactory<T>> {

    protected final R repository;
    protected final F factory;

    protected T create(T entity) {
        entity.setUid(factory.buildUidForCreate());
        return save(entity);
    }

    protected T update(T entity) {
        entity.setVersion(factory.buildVersionForUpdate(entity));
        return save(entity);
    }

    private T save(T entity) {
        T t;
        try {
            t = repository.save(entity);
        } catch (Exception e) {
            log.error("Error saving entity: uid = {},  id = {}, class name = {}", entity.getUid(), entity.getId(), entity.getClass().toString());
            // TODO: create custom exception
            throw new RuntimeException("Error saving entity");
        }
        log.info("Saved entity: uid = {},  id = {}, class name = {}", t.getUid(), t.getId(), t.getClass().toString());
        return t;
    }
}
