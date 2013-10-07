package polly.rx.parsing;


public class VenadHelper {

    private static String parseName(String name, boolean returnClan) {
        String clan = "";
        int i = name.indexOf("[");
        if (i != -1) {
            clan = name.substring(i + 1, name.length() - 1);
            name = name.substring(0, name.length() - clan.length() - 2);
        }
        
        if (returnClan) {
            return clan;
        } else {
            return name;
        }
    }
    
    
    public static String getClan(String name) {
        return parseName(name, true);
    }
    
    
    
    public static String getName(String name) {
        return parseName(name, false);
    }
}
