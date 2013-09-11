package de.skuzzle.polly.core.internal.httpv2;

import java.util.List;

import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.httpv2.html.HTMLTableModel;


public class UserDataSource implements HTMLTableModel<User> {

    private final static String[] COLUMNS = {
        "Id", "Name", "Nickname", "Is Idle", "Last IRC action", "IRC login", "Action"};
    
    private final UserManager um;
    
    public UserDataSource(UserManager um) {
        this.um = um;
    }

    @Override
    public String getHeader(int column) {
        return COLUMNS[column];
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public Object getCellValue(int column, User element) {
        switch (column) {
        case 0: return element.getId();
        case 1: return element.getName();
        case 2: return element.getCurrentNickName() == null ? "" : element.getCurrentNickName();
        case 3: return element.isIdle();
        case 4: return element.getLastMessageTime();
        case 5: return element.getLoginTime();
        default: return "";
        }
    }

    @Override
    public List<User> getData() {
        return this.um.getRegisteredUsers();
    }

    @Override
    public boolean isFilterable(int column) {
        return column < 6;
    }

    @Override
    public boolean isSortable(int column) {
        return column < 6;
    }
}