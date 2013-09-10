package de.skuzzle.polly.sdk.httpv2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.HttpException;
import de.skuzzle.polly.http.api.HttpSession;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
import de.skuzzle.polly.http.api.handler.HttpEventHandler;
import de.skuzzle.polly.sdk.util.ColumnFilter;
import de.skuzzle.polly.sdk.util.DirectedComparator;
import de.skuzzle.polly.sdk.util.DirectedComparator.SortOrder;


public class HtmlTable<T> implements HttpEventHandler {

    private final static String SORT_COLUMN = "sort";
    private final static String FILTER_VAL = "filterVal";
    private final static String FILTER_COL = "filterCol";
    
    
    
    public class Header {
        protected final String content;
        protected final Comparator<T> c;
        protected final Map<String, String> attributes;
        boolean filterable;
        
        public Header(String content, Comparator<T> c) {
            this.content = content;
            this.attributes = new HashMap<>();
            this.c = c;
            this.filterable = true;
        }
        public Header(String content) {
            this(content, null);
        }
        public Header setFilterable(boolean filterable) {
            this.filterable = filterable;
            return this;
        }
        
        public boolean isFilterable() {
            return this.filterable;
        }

        public String getContent() {
            return this.content;
        }
        public boolean isSortable() {
            return this.c != null;
        }
        public Header attr(String name, String value) {
            this.attributes.put(name, value);
            return this;
        }
    }
    
    
    
    public static class TableSettings {
        private int sortCol;
        private SortOrder[] order;
        private String[] filter;
        
        public int getSortCol() {
            return this.sortCol;
        }
        public SortOrder getOrder() {
            if (this.sortCol != -1) {
                return this.order[this.sortCol];
            }
            return SortOrder.UNDEFINED;
        }
        
        public String[] getFilter() {
            return this.filter;
        }
    }
    
    
    
    private final Map<String, String> attributes;
    private final List<Header> tableHead;
    private final DataSource<T> dataSource;
    private final ColumnFilter<T> filter;
    private final String tableId;
    private final String template;
    private final Map<String, Object> baseContext;
    
    
    public HtmlTable(String tableId, String template, DataSource<T> data, 
            ColumnFilter<T> filter) {
        this.tableId = tableId;
        this.template = template;
        this.attributes = new HashMap<>();
        this.tableHead = new ArrayList<>();
        this.dataSource = data;
        this.filter = filter;
        this.baseContext = new HashMap<>();
    }
    
    
    
    public HtmlTable<T> attr(String name, String value) {
        this.attributes.put(name, value);
        return this;
    }
    
    
    
    public HtmlTable<T> addHeader(String content, Comparator<T> c) {
        this.tableHead.add(new Header(content, c));
        return this;
    }
    
    
    
    public HtmlTable<T> addHeader(String content)  {
        return this.addHeader(content, true, null);
    }
    
    
    
    public HtmlTable<T> addHeader(String content, boolean filterable, Comparator<T> c) {
        this.tableHead.add(new Header(content, c).setFilterable(filterable));
        return this;
    }
    
    
    
    public Map<String, Object> getBaseContext() {
        return this.baseContext;
    }
    
    
    @Override
    public synchronized HttpAnswer handleHttpEvent(String registered, HttpEvent e,
                HttpEventHandler next) throws HttpException {
        
        // current settings for requesting user
        final HttpSession s = e.getSession();
        
        final String settingsKey = "SETTINGS_"+registered;
        final TableSettings settings;
        synchronized (s) {
            if (s.isSet(settingsKey)) {
                settings = (TableSettings) s.getAttached(settingsKey);
            } else {
                settings = new TableSettings();
                settings.sortCol = -1;
                settings.filter = new String[this.tableHead.size()];
                settings.order = new SortOrder[this.tableHead.size()];
                Arrays.fill(settings.filter, "");
                Arrays.fill(settings.order, SortOrder.UNDEFINED);
                s.set(settingsKey, settings);
            }
        }
        
        
        // this is a sorting request
        if (e.get(SORT_COLUMN) != null) {
            // column for which to sort
            final int newSortCol = Integer.parseInt(e.get(SORT_COLUMN));
            if (newSortCol < 0 || newSortCol >= this.tableHead.size()) {
                // TODO: error
            }
            // last sort order of this column, reverse
            final SortOrder order = settings.order[newSortCol].reverse();
            
            // update setting
            settings.order[newSortCol] = order;
            settings.sortCol = newSortCol;
        } else if (e.get(FILTER_COL) != null) {
            final int filterCol = Integer.parseInt(e.get(FILTER_COL));
            if (filterCol < 0 || filterCol >= this.tableHead.size()) {
                // TODO: error
            }
            final String filterVal = e.get(FILTER_VAL);
            settings.filter[filterCol] = filterVal == null ? "" : filterVal;
        }
        
        // get filtered elements
        final List<T> elements = this.dataSource.elements(this.filter, settings.filter);

        // and sort if necessary
        final int col = settings.sortCol;
        if (col != -1) {
            final SortOrder order = settings.order[col];
            if (order != SortOrder.UNDEFINED) {
                final Comparator<T> c = new DirectedComparator<T>(
                        order, this.tableHead.get(col).c);
                Collections.sort(elements, c);
            }
        }
        
        final Map<String, Object> c = new HashMap<>();
        c.put("settings", settings);
        c.put("baseUrl", registered);
        c.put("tId", this.tableId);
        c.put("data", elements);
        c.put("head", this.tableHead);
        c.putAll(this.baseContext);
        return HttpAnswers.newTemplateAnswer(this.template, c);
    }
}
