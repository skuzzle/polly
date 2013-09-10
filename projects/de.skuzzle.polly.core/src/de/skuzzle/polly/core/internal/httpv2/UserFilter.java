package de.skuzzle.polly.core.internal.httpv2;

import java.util.regex.Pattern;

import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.util.ColumnFilter;


public class UserFilter implements ColumnFilter<User> {

    @Override
    public boolean accept(String[] filterString, User element) {
        boolean idMatch = true;
        if (!filterString[0].equals("")) {
            idMatch = Pattern.compile(".*" + filterString[0] + ".*").matcher(
                "" + element.getId()).matches();
        }
        boolean nameMatch = true;
        if (!filterString[1].equals("")) {
            nameMatch = Pattern.compile(".*" + filterString[1].toLowerCase() + ".*").matcher(
                element.getName().toLowerCase()).matches();
        }
        return idMatch && nameMatch; 
    }
}
