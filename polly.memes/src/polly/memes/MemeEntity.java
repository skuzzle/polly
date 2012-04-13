package polly.memes;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
    @NamedQuery(name = "MEME_BY_NAME", 
                query = "SELECT m FROM MemeEntity m WHERE m.name = ?1")
})
public class MemeEntity {
    
    public final static String MEME_BY_NAME = "MEME_BY_NAME";

    @Id@GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    
    private String name;
    
    private String url;
    
    
    public MemeEntity() {}
    
    public MemeEntity(String name, String url) {
        this.name = name;
        this.url = url;
    }
    
    
    
    public int getId() {
        return this.id;
    }
    
    
    
    public String getName() {
        return this.name;
    }
    
    
    
    public String getUrl() {
        return this.url;
    }
}
