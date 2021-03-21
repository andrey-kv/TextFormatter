package com.ankov.textformatter.repositories;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Component
public class SectionTextRepositoryImpl  {

    @PersistenceContext
    EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    private static final String QUERY =
            "select t.id, t.code, t.parentCode, t.item, c.text, c.translate, t.isActive " +
            "from Toc t " +
            "left join TextContent c on (t.code = c.id and t.parentCode > 0) " +
            "where (t.id = 1 or t.parentCode = 1) and t.isActive order by t.id";


    public List getSectionTextDTOById(Integer sId) {

        Query query = getEntityManager().createQuery(QUERY);
        //        .setParameter("sectionId", sId);

        return query.getResultList();
    }

}
