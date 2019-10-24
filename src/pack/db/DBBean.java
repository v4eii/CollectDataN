package pack.db;

import pack.db.entity.controllers.CategoryJPAController;
import pack.db.entity.controllers.SkillsJPAController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

/**
 * Created by v4e on 22.10.2019
 */

/**
 * Класс отвечающий за работу с БД
 * @author v4e
 */
public class DBBean {

    private CategoryJPAController categoryJPAController;
    private SkillsJPAController skillsJPAController;
    private static DBBean instance = new DBBean();

    @PersistenceUnit(name = "CollPU")
    private EntityManagerFactory emf = null;

    public static DBBean getInstance()
    {
        synchronized (DBBean.class)
        {
            if (instance == null)
                instance = new DBBean();
        }
        return instance;
    }

    public EntityManagerFactory getEmf() {
        return emf == null ? emf = Persistence.createEntityManagerFactory("CollPU") : emf;
    }

    public CategoryJPAController getCategoryJPAController() {
        return categoryJPAController == null ? categoryJPAController = new CategoryJPAController(getEmf()) : categoryJPAController;
    }

    public SkillsJPAController getSkillsJPAController() {
        return skillsJPAController == null ? skillsJPAController = new SkillsJPAController(getEmf()) : skillsJPAController;
    }
}
