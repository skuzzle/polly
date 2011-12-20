package polly.core.persistence;

import java.io.PrintStream;

public class DatabaseProperties {

    private String password;
    private String user;
    private String driver;
    private String url;
    
    
    
    public DatabaseProperties(String password, String user, String driver,
            String url) {
        this.password = password;
        this.user = user;
        this.driver = driver;
        this.url = url;
    }
    


    public void toString(PrintStream s) {
        s.append("    <properties>\n");
        
        this.appendProperty(s, "javax.persistence.jdbc.password", this.password);
        this.appendProperty(s, "javax.persistence.jdbc.user", this.user);
        this.appendProperty(s, "javax.persistence.jdbc.driver", this.driver);
        this.appendProperty(s, "javax.persistence.jdbc.url", this.url);
        this.appendProperty(s, "eclipselink.ddl-generation", "create-tables");
        this.appendProperty(s, "eclipselink.ddl-generation.output-mode", "database");
        this.appendProperty(s, "eclipselink.logging.level", "OFF");
        
        s.append("    </properties>\n");
    }
    
    
    
    private void appendProperty(PrintStream s, String name, String value) {
        s.append("        ");
        s.append("<property name=\"");
        s.append(name);
        s.append("\" value=\"");
        s.append(value);
        s.append("\"/>\n");
    }
}
