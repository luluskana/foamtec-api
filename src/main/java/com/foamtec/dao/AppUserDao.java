package com.foamtec.dao;

import com.foamtec.domain.AppUser;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;

@Repository
@Transactional
public class AppUserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public void create(AppUser appUser) {
        Session session = (Session) entityManager.getDelegate();
        String sqlSelect = "SELECT ID FROM APP_USER ORDER BY ID DESC LIMIT 1";
        List lists = session.createSQLQuery(sqlSelect).list();
        if(lists.size() <= 0) {
            appUser.setId(1L);
        } else {
            Long id = ((BigInteger)lists.get(0)).longValue() + 1;
            appUser.setId(id);
        }
        entityManager.persist(appUser);
    }

    public AppUser findByUsername(String username) {
        Criteria c = ((Session) entityManager.getDelegate()).createCriteria(AppUser.class);
        c.add(Restrictions.eq("username", username));
        return (AppUser)c.uniqueResult();
    }

    public AppUser findByEmployeeID(String employeeID) {
        Criteria c = ((Session) entityManager.getDelegate()).createCriteria(AppUser.class);
        c.add(Restrictions.eq("employeeID", employeeID));
        return (AppUser)c.uniqueResult();
    }

    public AppUser findByUsernameAndPassword(String username, String password) {
        Criteria c = ((Session) entityManager.getDelegate()).createCriteria(AppUser.class);
        Criterion case1 = Restrictions.eq("username", username);
        Criterion case2 = Restrictions.eq("password", password);
        Criterion case3 = Restrictions.and(case1, case2);
        c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        c.add(case3);
        return (AppUser)c.uniqueResult();
    }

    public void update(AppUser appUser) {
        entityManager.merge(appUser);
        entityManager.flush();
    }

    public AppUser findById(Long id) {
        return entityManager.find(AppUser.class, id);
    }
}
