package de.skuzzle.polly.core.internal.httpv2;

import java.util.Date;
import java.util.List;

import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.httpv2.SuccessResult;
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
        case 2: return element.getCurrentNickName();
        case 3: return element.isIdle();
        case 4: return new Date(element.getLastMessageTime());
        case 5: return new Date(element.getLoginTime());
        default: return "";
        }
    }

    @Override
    public SuccessResult setCellValue(int column, int row, String value) {
        final User usr = this.getData().get(row);
        System.out.println("User to edit: " + usr.getName());
        System.out.println("Edit col: " + column);
        System.out.println("Edit row: " + row);
        return new SuccessResult(true, "");
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

    @Override
    public boolean isEditable(int column) {
        return false;
    }
    
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
        case 0:
        case 1: 
        case 2: return String.class;
        case 3: return Boolean.class;
        case 4:
        case 5: return Date.class;
        default: return Object.class;
        }
    }
}