package com.foamtec.dao;

import com.foamtec.domain.MaterialPlanning;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;

@Repository
@Transactional
public class MaterialPlanningDao {

    @PersistenceContext
    private EntityManager entityManager;

    public void create(MaterialPlanning materialPlanning) {
        Session session = (Session) entityManager.getDelegate();
        String sqlSelect = "SELECT ID FROM MaterialPlanning ORDER BY ID DESC LIMIT 1";
        List lists = session.createSQLQuery(sqlSelect).list();
        if(lists.size() <= 0) {
            materialPlanning.setId(1L);
        } else {
            Long id = ((BigInteger)lists.get(0)).longValue() + 1;
            materialPlanning.setId(id);
        }
        entityManager.persist(materialPlanning);
    }

    public MaterialPlanning findByCustomerPart(String materialCustomer) {
        Criteria c = ((Session) entityManager.getDelegate()).createCriteria(MaterialPlanning.class);
        c.add(Restrictions.eq("materialCustomer", materialCustomer));
        List<MaterialPlanning> materialPlannings = c.list();
        if(materialPlannings.size() > 1) {
            MaterialPlanning materialPlanning = null;
            for(MaterialPlanning m : materialPlannings) {
                String lastCharm = m.getMaterialFoamtec();
                String s = lastCharm.substring(lastCharm.length() - 1);
                if(s.equals("B")) {
                    materialPlanning = m;
                }
            }
            return materialPlanning;
        } else {
            return (MaterialPlanning)c.uniqueResult();
        }
    }
}
