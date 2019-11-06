package pack.util;

import java.io.Serializable;

/**
 * Контейнер вакансии
 * @author v4e
 */
public class Vacancy implements Serializable{
    
    private String nameVacancy;
    private String urlVacancy;

    public Vacancy()
    {
    }

    public Vacancy(String nameVacancy, String urlVacancy)
    {
        this.nameVacancy = nameVacancy;
        this.urlVacancy = urlVacancy;
    }

    public String getNameVacancy()
    {
        return nameVacancy;
    }

    public String getUrlVacancy()
    {
        return urlVacancy;
    }
    
}
