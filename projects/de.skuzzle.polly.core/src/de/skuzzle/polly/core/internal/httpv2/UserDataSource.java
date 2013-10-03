package de.skuzzle.polly.core.internal.httpv2;

import java.util.Date;
import java.util.List;

import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.httpv2.html.AbstractHTMLTableModel;
import de.skuzzle.polly.sdk.httpv2.html.HTMLElement;
import de.skuzzle.polly.sdk.httpv2.html.HTMLElementGroup;


public class UserDataSource extends AbstractHTMLTableModel<User> {

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
        case 6: return new HTMLElementGroup().add(
            new HTMLElement("a")
            .attr("href", "/pages/editUser?userId="+element.getId())
            .content( new HTMLElement("img")
                .attr("src", "/files/imgv2/user_edit.png")
                .attr("width", "20")
                .attr("height", "20").toString()
            ).attr("title", "Edit " + element.getName()))
            .add(
                new HTMLElement("a")
                .attr("href", "#")
                .attr("onclick", "deleteUser(" + element.getId() + ", '" + element.getName() + "')")
                .content( new HTMLElement("img")
                    .attr("src", "/files/imgv2/user_delete.png")
                    .attr("width", "20")
                    .attr("height", "20").toString()
                ).attr("title", "Delete " + element.getName())
            );
            
        default: return "";
        }
    }
    
    @Override
    public List<User> getData(HttpEvent e) {
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