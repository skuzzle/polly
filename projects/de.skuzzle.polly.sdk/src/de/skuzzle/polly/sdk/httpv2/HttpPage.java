package de.skuzzle.polly.sdk.httpv2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;


public class HttpPage {
    
    public final static String MAIN_MENU = "mainMenu";
    public final static String SUB_MENU = "subMenu";
    public final static String CONTENT = "content";
    public final static String USER = "user";
    
    

    private final HttpEvent source;
    private final String contentTemplate;
    private final List<MenuEntry> mainMenu;
    private final List<MenuEntry> subMenu;
    
    
    
    public HttpPage(HttpEvent source, String contentTemplate, List<MenuEntry> mainMenu) {
        super();
        this.source = source;
        this.contentTemplate = contentTemplate;
        this.mainMenu = mainMenu;
        this.subMenu = new ArrayList<>();
    }
    
    
    
    public Map<String, Object> toHttpAnswer() {
        final Map<String, Object> c = new HashMap<String, Object>();
        c.put(MAIN_MENU, this.mainMenu);
        c.put(SUB_MENU, this.subMenu);
        c.put(CONTENT, this.contentTemplate);
        c.put(USER, this.source.getSession().getAttached(USER));
        return c;
    }
}
