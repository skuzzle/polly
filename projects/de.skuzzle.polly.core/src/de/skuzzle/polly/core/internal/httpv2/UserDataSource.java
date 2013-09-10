package de.skuzzle.polly.core.internal.httpv2;

import java.util.ArrayList;
import java.util.List;

import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.httpv2.DataSource;
import de.skuzzle.polly.sdk.util.ColumnFilter;


public class UserDataSource implements DataSource<User> {

    private final UserManager um;
    
    public UserDataSource(UserManager um) {
        this.um = um;
    }
    
    
    
    @Override
    public int totalCount() {
        return this.um.getRegisteredUsers().size();
    }
    
    

    @Override
    public List<User> elements(ColumnFilter<User> filter, String[] filterStrings) {
        final List<User> result = new ArrayList<>();
        for (final User user : this.um.getRegisteredUsers()) {
            if (filter.accept(filterStrings, user)) {
                result.add(user);
            }
        }
        return result;
    }

    @Override
    public List<User> all() {
        return this.um.getRegisteredUsers();
    }

}
