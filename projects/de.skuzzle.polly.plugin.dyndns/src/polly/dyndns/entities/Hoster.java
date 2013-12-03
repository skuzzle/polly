package polly.dyndns.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = Hoster.QUERY_ALL_HOSTERS, query = "SELECT h FROM Hoster h")
public class Hoster {
    
    public final static String QUERY_ALL_HOSTERS = "ALL_HOSTERS"; //$NON-NLS-1$
    
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    private String name;
    private String baseUrl;


    
    public Hoster() {
        this("", ""); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    
    
    public Hoster(String name, String baseUrl) {
        this.name = name;
        this.baseUrl = baseUrl;
    }
    

    public String getName() {
        return this.name;
    }



    public void setName(String name) {
        this.name = name;
    }



    public String getBaseUrl() {
        return this.baseUrl;
    }



    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }



    public int getId() {
        return this.id;
    }
}
