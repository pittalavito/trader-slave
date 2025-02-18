package app.traderslave.factory;

import app.traderslave.model.domain.BasePersistentModel;

import java.util.UUID;

public abstract class BasePersistenceFactory<T extends BasePersistentModel> {

    public String buildUidForCreate() {
        return UUID.randomUUID().toString();
    }

    public Integer buildVersionForUpdate(T entity) {
        return entity.getVersion() + 1;
    }
}
