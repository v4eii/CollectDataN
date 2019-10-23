package pack.db.entity.controllers;

import pack.db.entity.Category;
import pack.db.entity.Skills;
import pack.db.entity.exceptions.NonexistentEntityException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

/**
 * Created by v4e on 22.10.2019
 */
public class SkillsJPAController implements Serializable {

    public SkillsJPAController(EntityManagerFactory emf)
    {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager()
    {
        return emf.createEntityManager();
    }

    public void create(Skills skills)
    {
        EntityManager em = null;
        try
        {
            em = getEntityManager();
            em.getTransaction().begin();
            Category idCategory = skills.getIdCategory();
            if (idCategory != null)
            {
                idCategory = em.getReference(idCategory.getClass(), idCategory.getIdCategory());
                skills.setIdCategory(idCategory);
            }
            em.persist(skills);
            if (idCategory != null)
            {
                idCategory.getSkillsCollection().add(skills);
                idCategory = em.merge(idCategory);
            }
            em.getTransaction().commit();
        }
        finally
        {
            if (em != null)
            {
                em.close();
            }
        }
    }

    public void edit(Skills skills) throws NonexistentEntityException, Exception
    {
        EntityManager em = null;
        try
        {
            em = getEntityManager();
            em.getTransaction().begin();
            Skills persistentSkills = em.find(Skills.class, skills.getIdSkill());
            Category idCategoryOld = persistentSkills.getIdCategory();
            Category idCategoryNew = skills.getIdCategory();
            if (idCategoryNew != null)
            {
                idCategoryNew = em.getReference(idCategoryNew.getClass(), idCategoryNew.getIdCategory());
                skills.setIdCategory(idCategoryNew);
            }
            skills = em.merge(skills);
            if (idCategoryOld != null && !idCategoryOld.equals(idCategoryNew))
            {
                idCategoryOld.getSkillsCollection().remove(skills);
                idCategoryOld = em.merge(idCategoryOld);
            }
            if (idCategoryNew != null && !idCategoryNew.equals(idCategoryOld))
            {
                idCategoryNew.getSkillsCollection().add(skills);
                idCategoryNew = em.merge(idCategoryNew);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex)
        {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0)
            {
                Integer id = skills.getIdSkill();
                if (findSkills(id) == null)
                {
                    throw new NonexistentEntityException("The skills with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
        finally
        {
            if (em != null)
            {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException
    {
        EntityManager em = null;
        try
        {
            em = getEntityManager();
            em.getTransaction().begin();
            Skills skills;
            try
            {
                skills = em.getReference(Skills.class, id);
                skills.getIdSkill();
            }
            catch (EntityNotFoundException enfe)
            {
                throw new NonexistentEntityException("The skills with id " + id + " no longer exists.", enfe);
            }
            Category idCategory = skills.getIdCategory();
            if (idCategory != null)
            {
                idCategory.getSkillsCollection().remove(skills);
                idCategory = em.merge(idCategory);
            }
            em.remove(skills);
            em.getTransaction().commit();
        }
        finally
        {
            if (em != null)
            {
                em.close();
            }
        }
    }

    public List<Skills> findSkillsEntities()
    {
        return findSkillsEntities(true, -1, -1);
    }

    public List<Skills> findSkillsEntities(int maxResults, int firstResult)
    {
        return findSkillsEntities(false, maxResults, firstResult);
    }

    private List<Skills> findSkillsEntities(boolean all, int maxResults, int firstResult)
    {
        EntityManager em = getEntityManager();
        try
        {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Skills.class));
            Query q = em.createQuery(cq);
            if (!all)
            {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        }
        finally
        {
            em.close();
        }
    }

    public Skills findSkills(Integer id)
    {
        EntityManager em = getEntityManager();
        try
        {
            return em.find(Skills.class, id);
        }
        finally
        {
            em.close();
        }
    }

    public int getSkillsCount()
    {
        EntityManager em = getEntityManager();
        try
        {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Skills> rt = cq.from(Skills.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally
        {
            em.close();
        }
    }

}
