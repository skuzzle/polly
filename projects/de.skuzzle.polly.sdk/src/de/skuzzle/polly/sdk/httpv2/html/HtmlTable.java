package de.skuzzle.polly.sdk.httpv2.html;

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
import de.skuzzle.polly.sdk.util.DirectedComparator;
import de.skuzzle.polly.sdk.util.DirectedComparator.SortOrder;


public class HtmlTable<T> implements HttpEventHandler {

    private final static String SORT_COLUMN = "sort";
    private final static String SET_PAGE = "page";
    private final static String FILTER_VAL = "filterVal";
    private final static String FILTER_COL = "filterCol";
    
    private final static int DEFAULT_PAGE_SIZE = 10;
    
    public static class TableSettings {
        private int sortCol;
        private SortOrder[] order;
        private String[] filter;
        private int pageCount;
        private int pageSize = DEFAULT_PAGE_SIZE;
        private int page;
        
        public int getSortCol() {
            return this.sortCol;
        }
        public int getPageCount() {
            return this.pageCount;
        }
        public int getPage() {
            return this.page;
        }
        public int getPageSize() {
            return this.pageSize;
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
    
    
    
    private final String tableId;
    private final String template;
    private final Map<String, Object> baseContext;
    private HTMLTableModel<T> model;
    private HTMLColumnModel<T> colModel;
    
    
    
    public HtmlTable(String tableId, String template, HTMLTableModel<T> model) {
        this.tableId = tableId;
        this.template = template;
        this.baseContext = new HashMap<>();
        this.model = model;
        this.colModel = new StringColumnModel<>(this.model);
    }
    
    
    
    public void setColumnModel(HTMLColumnModel<T> colModel) {
        this.colModel = colModel;
    }
    
    
    
    
    public HTMLColumnModel<T> getColumnModel() {
        return this.colModel;
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
                settings.filter = new String[this.model.getColumnCount()];
                settings.order = new SortOrder[this.model.getColumnCount()];
                Arrays.fill(settings.filter, "");
                Arrays.fill(settings.order, SortOrder.UNDEFINED);
                s.set(settingsKey, settings);
            }
        }
        
        
        // this is a sorting request
        if (e.get(SORT_COLUMN) != null) {
            // column for which to sort
            final int newSortCol = Integer.parseInt(e.get(SORT_COLUMN));
            if (newSortCol < 0 || newSortCol >= this.model.getColumnCount()) {
                // TODO: error
            }
            // last sort order of this column, reverse
            final SortOrder order = settings.order[newSortCol].reverse();
            
            // update setting
            settings.order[newSortCol] = order;
            settings.sortCol = newSortCol;
        } else if (e.get(FILTER_COL) != null) {
            final int filterCol = Integer.parseInt(e.get(FILTER_COL));
            if (filterCol < 0 || filterCol >= this.model.getColumnCount()) {
                // TODO: error
            }
            final String filterVal = e.get(FILTER_VAL);
            settings.filter[filterCol] = filterVal == null ? "" : filterVal;
        } else if (e.get(SET_PAGE) != null) {
            final int page = Integer.parseInt(e.get(SET_PAGE));
            settings.page = page;
        }
        
        // get filtered elements
        final List<T> elements = this.getFilteredElements(settings);
        
        final Map<String, Object> c = new HashMap<>();
        c.put("settings", settings);
        c.put("tableModel", this.model);
        c.put("columnModel", this.colModel);
        c.put("baseUrl", registered);
        c.put("tId", this.tableId);
        c.put("data", elements);
        c.put("table", this);
        c.putAll(this.baseContext);
        return HttpAnswers.newTemplateAnswer(this.template, c);
    }
    
    
    
    private List<T> getFilteredElements(TableSettings s) {
        
        // first: filter full data
        final int colCount = this.model.getColumnCount();
        final List<T> data = this.model.getData();
        List<T> result = new ArrayList<>(data.size());
        outer: for (final T element : data) {
            for (int i = 0; i < colCount; ++i) {
                final String colFilter = s.filter[i];
                if (!this.model.isFilterable(i) || colFilter.equals("")) {
                    // skip empty filter or non filterable column
                    continue;
                }
                final Acceptor acceptor = this.colModel.getAcceptor(i);
                if (!acceptor.accept(colFilter, this.model.getCellValue(i, element))) {
                    // discard this element
                    continue outer;
                }
            }
            // all filters applied, each accepted the element
            result.add(element);
        }
        
        // get view port based on filtered data
        s.pageCount = (int) Math.ceil(result.size() / (double) s.pageSize);
        s.page = Math.min(s.page, s.pageCount - 1);
        int firstIdx = s.page * s.pageSize;
        int lastIdx = Math.min(result.size(), firstIdx + s.pageSize);
        result = result.subList(firstIdx, lastIdx);
        
        // sort view port
        if (s.sortCol != -1 && this.model.isSortable(s.sortCol)) {
            if (s.order[s.sortCol] != SortOrder.UNDEFINED) {
                final Comparator<? super T> c = this.colModel.getComparator(s.sortCol);
                Collections.sort(result, new DirectedComparator<>(s.getOrder(), c));
            }
        }
        return result;
    }
}
