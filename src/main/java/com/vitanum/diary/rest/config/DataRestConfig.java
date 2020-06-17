package com.vitanum.diary.rest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class DataRestConfig implements RepositoryRestConfigurer {

    private final EntityManager entityManager;

    @Autowired
    public DataRestConfig(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        exposeIds(config);
    }

    private void exposeIds(RepositoryRestConfiguration config) {
        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();

        List<Class> entityClasses = new ArrayList<>();

        entities.forEach(entityType -> entityClasses.add(entityType.getJavaType()));

        config.exposeIdsFor(entityClasses.toArray(new Class[0]));
    }
}
