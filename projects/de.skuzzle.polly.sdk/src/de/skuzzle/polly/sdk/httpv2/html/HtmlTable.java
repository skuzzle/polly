package de.skuzzle.polly.sdk.httpv2.html;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.HttpException;
import de.skuzzle.polly.http.api.HttpSession;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
import de.skuzzle.polly.http.api.handler.HttpEventHandler;
import de.skuzzle.polly.sdk.httpv2.GsonHttpAnswer;
import de.skuzzle.polly.sdk.util.DirectedComparator;
import de.skuzzle.polly.sdk.util.DirectedComparator.SortOrder;


public class HTMLTable<T> implements HttpEventHandler {
    
    
    
    public static class BooleanCellEditor implements CellEditor {
        @Override
        public HTMLElement renderEditorCell(Object cellContent, boolean forFilter) {
            
            if (forFilter) {
                // if filter, cellContent is the filter string!
                final String f = (String) cellContent;
                
                final String name = "opt_" + (int) (Math.random() * 1000);
                final HTMLElement all = new HTMLElement("input")
                    .attr("type", "radio")
                    .attr("name", name)
                    .attr("class", "filter_input")
                    .attr("value", "")
                    .content("All");
                    
                final HTMLElement selected = new HTMLElement("input")
                    .attr("type", "radio")
                    .attr("name", name)
                    .attr("class", "filter_input")
                    .attr("value", "true")
                    .content("true");
                
                final HTMLElement unselected = new HTMLElement("input")
                    .attr("type", "radio")
                    .attr("name", name)
                    .attr("class", "filter_input")
                    .attr("value", "false")
                    .content("false");
                
                final HTMLElement checked;
                if (f.equals("")) {
                    checked = all;
                } else if (f.equals("true")) {
                    checked = selected;
                } else {
                    checked = unselected;
                }
                checked.attr("checked");
                return new HTMLInputGroup().add(all).add(selected).add(unselected);
            }
            final HTMLElement in = new HTMLElement("input").attr("type", "checkbox");
            if (cellContent != null && (Boolean) cellContent) {
                in.attr("checked");
            }
            return in;
        }
        
        
        
        @Override
        public String getEditIndicator() {
            return "";
        }
    }
    
    
    
    public static class DateCellEditor extends TextCellEditor {
        
        @Override
        public String getEditIndicator() {
            return "<img src=\"/http/view/files/date_edit.png\" height=\"16\" width=\"16\" style=\"vertical-align:middle\"/>";
        }
    }
    
    
    
    public static class TextCellEditor implements CellEditor {
        @Override
        public HTMLElement renderEditorCell(Object cellContent, boolean forFilter) {
            final String cls = forFilter ? "textbox filter_input" : "textbox edit_input";
            final String content = cellContent == null ? "" : cellContent.toString();
            return new HTMLElement("input")
                .attr("type", "text")
                .attr("class", cls)
                .attr("style", "width: 85%")
                .attr("value", content);
        }
        
        
        
        @Override
        public String getEditIndicator() {
            return 
                "<img src=\"/http/view/files/edit.png\" height=\"16\" width=\"16\" style=\"vertical-align:middle\"/>";
        }
    }
    
    
    
    public static class ToStringCellRenderer implements CellRenderer {
        @Override
        public String renderCellContent(int column, Object cellValue) {
            return cellValue == null ? "" : cellValue.toString();
        }
    }
    
    
    
    public static class DateCellRenderer implements CellRenderer {
        @Override
        public String renderCellContent(int column, Object cellValue) {
            final DateFormat df = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
            return df.format((Date) cellValue);
        }
        
        
    }
    
    
    public static class BooleanCellRenderer implements CellRenderer {

        @Override
        public String renderCellContent(int column, Object cellValue) {
            return new BooleanCellEditor()
                .renderEditorCell(cellValue, false).attr("disabled").toString();
        }
        
    }
    
    
    

    private final static String SORT_COLUMN = "sort";
    private final static String SET_PAGE = "page";
    private final static String SET_PAGE_SIZE = "pageSize";
    private final static String FILTER_VAL = "filterVal";
    private final static String FILTER_COL = "filterCol";
    private final static String SET_VALUE = "setValue";
    private final static String COLUMN = "col";
    private final static String ROW = "row";
    
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
        @Override
        public String toString() {
            return "[sortCol: " + this.sortCol + 
                ", page: " + this.page + 
                ", pageCount: " + this.pageCount + 
                ", pageSize: " + this.pageSize + 
                ", sortOrder: " + Arrays.toString(this.order) + 
                ", filter: " + Arrays.toString(this.filter) + "]";
        }
    }
    
    
    
    private class FilterResult {
        private final List<T> data;
        private final Map<T, Integer> indexMap;
        public FilterResult(List<T> data, Map<T, Integer> indexMap) {
            super();
            this.data = data;
            this.indexMap = indexMap;
        }
    }
    
    
    
    private final String tableId;
    private final String template;
    private final Map<String, Object> baseContext;
    private HTMLTableModel<T> model;
    private HTMLColumnSorter<T> colSorter;
    private HTMLColumnFilter filter;
    private final Map<Class<?>, CellEditor> editors;
    private final Map<Class<?>, CellRenderer> renderers;
    
    
    public HTMLTable(String tableId, String template, HTMLTableModel<T> model) {
        this.tableId = tableId;
        this.template = template;
        this.baseContext = new HashMap<>();
        this.model = model;
        this.colSorter = new DefaultColumnSorter<>();
        this.filter = new DefaultColumnFilter();
        
        this.editors = new HashMap<>();
        this.editors.put(String.class, new TextCellEditor());
        this.editors.put(Object.class, new TextCellEditor());
        this.editors.put(Boolean.class, new BooleanCellEditor());
        this.editors.put(Date.class, new DateCellEditor());
        
        this.renderers = new HashMap<>();
        this.renderers.put(Object.class, new ToStringCellRenderer());
        this.renderers.put(String.class, new ToStringCellRenderer());
        this.renderers.put(Date.class, new DateCellRenderer());
        this.renderers.put(Boolean.class, new BooleanCellRenderer());
    }
    
    
    
    public void setColumnModel(HTMLColumnSorter<T> colModel) {
        this.colSorter = colModel;
    }
    
    
    
    
    public HTMLColumnSorter<T> getColumnModel() {
        return this.colSorter;
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
        } else if (e.get(SET_VALUE) != null) {
            final String value = e.get(SET_VALUE);
            final int col = Integer.parseInt(e.get(COLUMN));
            final int row = Integer.parseInt(e.get(ROW));
            
            return new GsonHttpAnswer(200, this.model.setCellValue(col, row, value));
        } else if (e.get(SET_PAGE_SIZE) != null) {
            final int pageSize = Integer.parseInt(e.get(SET_PAGE_SIZE));
            settings.pageSize = pageSize;
        }
        
        // get filtered elements
        final FilterResult fr = this.getFilteredElements(settings);
        
        final Map<String, Object> c = new HashMap<>();
        c.put("settings", settings);
        c.put("tableModel", this.model);
        c.put("colSorter", this.colSorter);
        c.put("renderers", this.renderers);
        c.put("filter", this.filter);
        c.put("editors", this.editors);
        c.put("baseUrl", registered);
        c.put("tId", this.tableId);
        c.put("data", fr.data);
        c.put("indexMap", fr.indexMap);
        c.put("table", this);
        c.putAll(this.baseContext);
        return HttpAnswers.newTemplateAnswer(this.template, c);
    }
    
    
    
    private FilterResult getFilteredElements(TableSettings s) {
        
        // first: filter full data
        final int colCount = this.model.getColumnCount();
        final List<T> data = this.model.getData();
        final Map<T, Integer> idxMap = new HashMap<>(data.size());
        List<T> result = new ArrayList<>(data.size());
        
        int originalIdx = -1;
        outer: for (final T element : data) {
            ++originalIdx; // index of current element in the original data
            
            
            for (int i = 0; i < colCount; ++i) {
                final String colFilter = s.filter[i];
                if (!this.model.isFilterable(i) || colFilter.equals("")) {
                    // skip empty filter or non filterable column
                    continue;
                }
                final Acceptor acceptor = this.filter.getAcceptor(i);
                if (!acceptor.accept(colFilter, this.model.getCellValue(i, element))) {
                    // discard this element
                    continue outer;
                }
            }
            
            // all filters applied, each accepted the element
            // map index witihn the filetered- to index in the full colletion
            idxMap.put(element, originalIdx); 
            result.add(element);
        }
        
        if (result.isEmpty()) {
            return new FilterResult(result, idxMap);
        }
        
        // get view port based on filtered data
        s.pageCount = (int) Math.ceil(result.size() / (double) s.pageSize);
        s.page = Math.max(0, Math.min(s.page, s.pageCount - 1));
        int firstIdx = s.page * s.pageSize;
        int lastIdx = Math.min(result.size(), firstIdx + s.pageSize);
        result = result.subList(firstIdx, lastIdx);
        
        // sort view port
        if (s.sortCol != -1 && this.model.isSortable(s.sortCol)) {
            if (s.order[s.sortCol] != SortOrder.UNDEFINED) {
                final Comparator<? super T> c = this.colSorter.getComparator(
                    s.sortCol, this.model);
                Collections.sort(result, new DirectedComparator<>(s.getOrder(), c));
            }
        }
        return new FilterResult(result, idxMap);
    }
}
