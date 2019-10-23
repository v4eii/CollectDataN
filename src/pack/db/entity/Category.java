package pack.db.entity;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by v4e on 22.10.2019
 */
@Entity
@Table(catalog = "science_collector", schema = "public")
//@XmlRootElement
@NamedQueries(
        {
                @NamedQuery(name = "Category.findAll", query = "SELECT c FROM Category c"),
                @NamedQuery(name = "Category.findByIdCategory", query = "SELECT c FROM Category c WHERE c.idCategory = :idCategory"),
                @NamedQuery(name = "Category.findByName", query = "SELECT c FROM Category c WHERE c.name = :name")
        })
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_category")
    private Integer idCategory;
    @Basic(optional = false)
    private String name;
    @OneToMany(mappedBy = "idCategory")
    private Collection<Skills> skillsCollection;

    public Category()
    {
    }

    public Category(Integer idCategory)
    {
        this.idCategory = idCategory;
    }

    public Category(Integer idCategory, String name)
    {
        this.idCategory = idCategory;
        this.name = name;
    }

    public Integer getIdCategory()
    {
        return idCategory;
    }

    public void setIdCategory(Integer idCategory)
    {
        this.idCategory = idCategory;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

//    @XmlTransient
    public Collection<Skills> getSkillsCollection()
    {
        return skillsCollection;
    }

    public void setSkillsCollection(Collection<Skills> skillsCollection)
    {
        this.skillsCollection = skillsCollection;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (idCategory != null ? idCategory.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof pack.db.entity.Category))
        {
            return false;
        }
        pack.db.entity.Category other = (pack.db.entity.Category) object;
        if ((this.idCategory == null && other.idCategory != null) || (this.idCategory != null && !this.idCategory.equals(other.idCategory)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "Category[ idCategory=" + idCategory + " ]";
    }

}
