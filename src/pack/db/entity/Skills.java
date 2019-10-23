package pack.db.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by v4e on 22.10.2019
 */
@Entity
@Table(catalog = "science_collector", schema = "public")
@NamedQueries(
        {
                @NamedQuery(name = "Skills.findAll", query = "SELECT s FROM Skills s"),
                @NamedQuery(name = "Skills.findByIdSkill", query = "SELECT s FROM Skills s WHERE s.idSkill = :idSkill"),
                @NamedQuery(name = "Skills.findByName", query = "SELECT s FROM Skills s WHERE s.name = :name"),
                @NamedQuery(name = "Skills.findByHitCount", query = "SELECT s FROM Skills s WHERE s.hitCount = :hitCount")
        })
public class Skills implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_skill")
    private Integer idSkill;
    @Basic(optional = false)
    private String name;
    @Basic(optional = false)
    @Column(name = "hit_count")
    private int hitCount;
    @JoinColumn(name = "id_category", referencedColumnName = "id_category")
    @ManyToOne
    private Category idCategory;

    public Skills()
    {
    }

    public Skills(Integer idSkill)
    {
        this.idSkill = idSkill;
    }

    public Skills(Integer idSkill, String name, int hitCount)
    {
        this.idSkill = idSkill;
        this.name = name;
        this.hitCount = hitCount;
    }

    public Integer getIdSkill()
    {
        return idSkill;
    }

    public void setIdSkill(Integer idSkill)
    {
        this.idSkill = idSkill;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getHitCount()
    {
        return hitCount;
    }

    public void setHitCount(int hitCount)
    {
        this.hitCount = hitCount;
    }

    public Category getIdCategory()
    {
        return idCategory;
    }

    public void setIdCategory(Category idCategory)
    {
        this.idCategory = idCategory;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (idSkill != null ? idSkill.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Skills))
        {
            return false;
        }
        Skills other = (Skills) object;
        if ((this.idSkill == null && other.idSkill != null) || (this.idSkill != null && !this.idSkill.equals(other.idSkill)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "Skills[ idSkill=" + idSkill + " ]";
    }

}

