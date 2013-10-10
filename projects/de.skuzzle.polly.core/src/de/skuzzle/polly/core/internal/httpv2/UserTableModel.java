package de.skuzzle.polly.core.internal.httpv2;

import java.util.Date;
import java.util.List;

import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.httpv2.html.AbstractHTMLTableModel;
import de.skuzzle.polly.sdk.httpv2.html.HTMLElement;
import de.skuzzle.polly.sdk.httpv2.html.HTMLElementGroup;


public class UserTableModel extends AbstractHTMLTableModel<User> {

    private final static String[] COLUMNS = {
        MSG.userTableColId, 
        MSG.userTableColName, 
        MSG.userTableColNick, 
        MSG.userTableColIdle, 
        MSG.userTableColLastAction, 
        MSG.userTableColLogin, 
        MSG.userTableColAction
    };
    
    
    private final UserManager um;
    
    public UserTableModel(UserManager um) {
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
            new HTMLElement("a") //$NON-NLS-1$
            .attr("href", UserController.PAGE_EDIT_USER + "?userId="+element.getId()) //$NON-NLS-1$ //$NON-NLS-2$
            .content( new HTMLElement("img") //$NON-NLS-1$
                .attr("src", "/files/imgv2/user_edit.png") //$NON-NLS-1$ //$NON-NLS-2$
                .attr("width", "20") //$NON-NLS-1$ //$NON-NLS-2$
                .attr("height", "20").toString() //$NON-NLS-1$ //$NON-NLS-2$
            ).attr("title", MSG.bind(MSG.userTableEditTitle, element.getName()))) //$NON-NLS-1$
            .add(
                new HTMLElement("a") //$NON-NLS-1$
                .attr("href", "#") //$NON-NLS-1$ //$NON-NLS-2$
                .attr("onclick", "deleteUser(" + element.getId() + ", '" + element.getName() + "')") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                .content( new HTMLElement("img") //$NON-NLS-1$
                    .attr("src", "/files/imgv2/user_delete.png") //$NON-NLS-1$ //$NON-NLS-2$
                    .attr("width", "20") //$NON-NLS-1$ //$NON-NLS-2$
                    .attr("height", "20").toString() //$NON-NLS-1$ //$NON-NLS-2$
                ).attr("title", MSG.bind(MSG.userTableDeleteTitle, element.getName())) //$NON-NLS-1$
            );
            
        default: return ""; //$NON-NLS-1$
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