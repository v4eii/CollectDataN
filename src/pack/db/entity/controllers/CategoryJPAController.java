package pack.db.entity.controllers;

import pack.db.entity.Category;
import pack.db.entity.Skills;
import pack.db.entity.exceptions.NonexistentEntityException;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by v4e on 22.10.2019
 */
public class CategoryJPAController implements Serializable {

    public CategoryJPAController(EntityManagerFactory emf)
    {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager()
    {
        return emf.createEntityManager();
    }

    public void create(Category category)
    {
        if (category.getSkillsCollection() == null)
        {
            category.setSkillsCollection(new ArrayList<Skills>());
        }
        EntityManager em = null;
        try
        {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Skills> attachedSkillsCollection = new ArrayList<Skills>();
            for (Skills skillsCollectionSkillsToAttach : category.getSkillsCollection())
            {
                skillsCollectionSkillsToAttach = em.getReference(skillsCollectionSkillsToAttach.getClass(), skillsCollectionSkillsToAttach.getIdSkill());
                attachedSkillsCollection.add(skillsCollectionSkillsToAttach);
            }
            category.setSkillsCollection(attachedSkillsCollection);
            em.persist(category);
            for (Skills skillsCollectionSkills : category.getSkillsCollection())
            {
                Category oldIdCategoryOfSkillsCollectionSkills = skillsCollectionSkills.getIdCategory();
                skillsCollectionSkills.setIdCategory(category);
                skillsCollectionSkills = em.merge(skillsCollectionSkills);
                if (oldIdCategoryOfSkillsCollectionSkills != null)
                {
                    oldIdCategoryOfSkillsCollectionSkills.getSkillsCollection().remove(skillsCollectionSkills);
                    oldIdCategoryOfSkillsCollectionSkills = em.merge(oldIdCategoryOfSkillsCollectionSkills);
                }
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

    public void edit(Category category) throws NonexistentEntityException, Exception
    {
        EntityManager em = null;
        try
        {
            em = getEntityManager();
            em.getTransaction().begin();
            Category persistentCategory = em.find(Category.class, category.getIdCategory());
            Collection<Skills> skillsCollectionOld = persistentCategory.getSkillsCollection();
            Collection<Skills> skillsCollectionNew = category.getSkillsCollection();
            Collection<Skills> attachedSkillsCollectionNew = new ArrayList<Skills>();
            for (Skills skillsCollectionNewSkillsToAttach : skillsCollectionNew)
            {
                skillsCollectionNewSkillsToAttach = em.getReference(skillsCollectionNewSkillsToAttach.getClass(), skillsCollectionNewSkillsToAttach.getIdSkill());
                attachedSkillsCollectionNew.add(skillsCollectionNewSkillsToAttach);
            }
            skillsCollectionNew = attachedSkillsCollectionNew;
            category.setSkillsCollection(skillsCollectionNew);
            category = em.merge(category);
            for (Skills skillsCollectionOldSkills : skillsCollectionOld)
            {
                if (!skillsCollectionNew.contains(skillsCollectionOldSkills))
                {
                    skillsCollectionOldSkills.setIdCategory(null);
                    skillsCollectionOldSkills = em.merge(skillsCollectionOldSkills);
                }
            }
            for (Skills skillsCollectionNewSkills : skillsCollectionNew)
            {
                if (!skillsCollectionOld.contains(skillsCollectionNewSkills))
                {
                    Category oldIdCategoryOfSkillsCollectionNewSkills = skillsCollectionNewSkills.getIdCategory();
                    skillsCollectionNewSkills.setIdCategory(category);
                    skillsCollectionNewSkills = em.merge(skillsCollectionNewSkills);
                    if (oldIdCategoryOfSkillsCollectionNewSkills != null && !oldIdCategoryOfSkillsCollectionNewSkills.equals(category))
                    {
                        oldIdCategoryOfSkillsCollectionNewSkills.getSkillsCollection().remove(skillsCollectionNewSkills);
                        oldIdCategoryOfSkillsCollectionNewSkills = em.merge(oldIdCategoryOfSkillsCollectionNewSkills);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex)
        {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0)
            {
                Integer id = category.getIdCategory();
                if (findCategory(id) == null)
                {
                    throw new NonexistentEntityException("The category with id " + id + " no longer exists.");
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
            Category category;
            try
            {
                category = em.getReference(Category.class, id);
                category.getIdCategory();
            }
            catch (EntityNotFoundException enfe)
            {
                throw new NonexistentEntityException("The category with id " + id + " no longer exists.", enfe);
            }
            Collection<Skills> skillsCollection = category.getSkillsCollection();
            for (Skills skillsCollectionSkills : skillsCollection)
            {
                skillsCollectionSkills.setIdCategory(null);
                skillsCollectionSkills = em.merge(skillsCollectionSkills);
            }
            em.remove(category);
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

    public List<Category> findCategoryEntities()
    {
        return findCategoryEntities(true, -1, -1);
    }

    public List<Category> findCategoryEntities(int maxResults, int firstResult)
    {
        return findCategoryEntities(false, maxResults, firstResult);
    }

    private List<Category> findCategoryEntities(boolean all, int maxResults, int firstResult)
    {
        EntityManager em = getEntityManager();
        try
        {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Category.class));
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

    public Category findCategory(Integer id)
    {
        EntityManager em = getEntityManager();
        try
        {
            return em.find(Category.class, id);
        }
        finally
        {
            em.close();
        }
    }

    // TODO: Костыль. Толь я тупой, толь лыжи не едут. При попытке создания namedQuery и присваивания параметра q.setParametr("name", name),
    //  что бы ни было в строке name он устанавливает параметр name = "name". Лучше конечно после поправить
    public Category findCategoryByName(String name) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createNativeQuery("SELECT * FROM category WHERE name = \'" + name + "\'");
            Category c = null;
            if (q.getSingleResult() instanceof Object[]) {
                Object[] ar = (Object[]) q.getSingleResult();
                c = new Category((Integer) ar[0], (String) ar[1]);
            }
            return c;
        }
        finally {
            em.close();
        }
    }


    public int getCategoryCount()
    {
        EntityManager em = getEntityManager();
        try
        {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Category> rt = cq.from(Category.class);
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
