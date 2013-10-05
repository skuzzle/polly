package de.skuzzle.polly.sdk.httpv2.html;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.skuzzle.polly.http.api.AlternativeAnswerException;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.HttpException;
import de.skuzzle.polly.http.api.HttpSession;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
import de.skuzzle.polly.http.api.handler.HttpEventHandler;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.httpv2.GsonHttpAnswer;
import de.skuzzle.polly.sdk.httpv2.SuccessResult;
import de.skuzzle.polly.sdk.util.DirectedComparator;
import de.skuzzle.polly.sdk.util.DirectedComparator.SortOrder;
import de.skuzzle.polly.tools.math.MathUtil;


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
                return new HTMLElementGroup().add(all).add(selected).add(unselected);
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
            return "<img src=\"/de/skuzzle/polly/sdk/httpv2/html/date_edit.png\" height=\"16\" width=\"16\" style=\"vertical-align:middle\"/>";
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
                "<img src=\"/de/skuzzle/polly/sdk/httpv2/html/edit.png\" height=\"16\" width=\"16\" style=\"vertical-align:middle\"/>";
        }
    }
    
    
    
    public static class ToStringCellRenderer implements CellRenderer {
        private final boolean escape;
        
        
        public ToStringCellRenderer(boolean escape) {
            this.escape = escape;
        }
        
        @Override
        public String renderCellContent(int column, Object cellValue) {
            if (cellValue == null) {
                return "";
            } else {
                return this.escape 
                        ? Escape.html(cellValue.toString()) 
                        : cellValue.toString();
            }
        }
    }
    
    
    
    public static class DateCellRenderer implements CellRenderer {
        @Override
        public String renderCellContent(int column, Object cellValue) {
            final Date date = (Date) cellValue;
            if (date.getTime() == 0) {
                return "";
            } else {
                final DateFormat df = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
                return df.format((Date) cellValue);
            }
        }
    }
    
    
    
    public static class BooleanCellRenderer implements CellRenderer {

        @Override
        public String renderCellContent(int column, Object cellValue) {
            return new BooleanCellEditor()
                .renderEditorCell(cellValue, false).attr("disabled").toString();
        }
    }
    
    
    
    public static class DoubleCellRenderer implements CellRenderer {

        @Override
        public String renderCellContent(int column, Object cellValue) {
            final NumberFormat nf = new DecimalFormat("0.###");
            return nf.format((Double) cellValue);
        }
        
    }
    
    
    
    public static class TypesCellRenderer implements CellRenderer {
        
        private final FormatManager formatter;
        
        
        public TypesCellRenderer(FormatManager formatter) {
            this.formatter = formatter;
        }
        
        
        
        @Override
        public String renderCellContent(int column, Object cellValue) {
            if (cellValue == null || !(cellValue instanceof Types)) {
                return "";
            }
            final Types t = (Types) cellValue;
            return Escape.html(t.valueString(this.formatter));
        }
    }
    
    
    
    
    private final static String makeUrl(String baseUrl, Map<String, String> param) {
        String append = "&";
        if (!baseUrl.contains("?")) {
            append = "?";
        }
        final StringBuilder b = new StringBuilder(baseUrl.length());
        b.append(baseUrl);
        for (final Entry<String, String> e : param.entrySet()) {
            b.append(append);
            b.append(e.getKey());
            b.append("=");
            b.append(e.getValue());
            append = "&";
        }
        return b.toString();
    }
    
    
    

    private final static String SORT_COLUMN = "sort";
    private final static String SET_PAGE = "page";
    private final static String SET_PAGE_SIZE = "pageSize";
    private final static String FILTER_VAL = "filterVal";
    private final static String FILTER_COL = "filterCol";
    private final static String SET_VALUE = "setValue";
    private final static String COLUMN = "col";
    private final static String ROW = "row";
    
    private final static int DEFAULT_PAGE_SIZE = 40;
    
    
    
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
        private final List<T> sided;
        private final List<T> unsided;
        private final int filteredSize;
        private final Map<T, Integer> indexMap;
        public FilterResult(List<T> sided, List<T> unsided, 
                Map<T, Integer> indexMap, int filteredSize) {
            super();
            this.sided = sided;
            this.unsided = unsided;
            this.indexMap = indexMap;
            this.filteredSize = filteredSize;
        }
    }
    
    
    
    private final String tableId;
    private final Map<String, Object> baseContext;
    private HTMLTableModel<T> model;
    private HTMLColumnSorter<T> colSorter;
    private HTMLColumnFilter filter;
    private final Map<Class<?>, CellEditor> editors;
    private final Map<Class<?>, CellRenderer> renderers;
    private final MyPolly myPolly;
    private final List<HTMLModelListener<T>> listeners;
    
    
    
    public HTMLTable(String tableId, HTMLTableModel<T> model, MyPolly myPolly) {
        this.myPolly = myPolly;
        this.tableId = tableId;
        this.baseContext = new HashMap<>();
        this.model = model;
        this.colSorter = new DefaultColumnSorter<>();
        this.filter = new DefaultTypeFilter(model, myPolly);
        this.listeners = new ArrayList<>();
        
        this.editors = new HashMap<>();
        this.editors.put(String.class, new TextCellEditor());
        this.editors.put(Object.class, new TextCellEditor());
        this.editors.put(Boolean.class, new BooleanCellEditor());
        this.editors.put(Date.class, new DateCellEditor());
        
        this.renderers = new HashMap<>();
        this.renderers.put(Object.class, new ToStringCellRenderer(false));
        this.renderers.put(String.class, new ToStringCellRenderer(true));
        this.renderers.put(Date.class, new DateCellRenderer());
        this.renderers.put(Boolean.class, new BooleanCellRenderer());
        this.renderers.put(Double.class, new DoubleCellRenderer());
        this.renderers.put(Types.class, new TypesCellRenderer(myPolly.formatting()));
    }
    
    
    
    private void fireDataProcessed(List<T> result, HttpEvent e) {
        for (final HTMLModelListener<T> listener : this.listeners) {
            listener.onDataProcessed(this.model, result, e);
        }
    }
    
    
    
    public void addModelListener(HTMLModelListener<T> listener) {
        this.listeners.add(listener);
    }
    
    
    
    public void removeModelListener(HTMLModelListener<T> listener) {
        this.listeners.remove(listener);
    }
    
    
    
    public void setColumnSorter(HTMLColumnSorter<T> colModel) {
        this.colSorter = colModel;
    }
    
    
    
    
    public HTMLColumnSorter<T> getColumnSorter() {
        return this.colSorter;
    }
    
    
    
    
    public void setFilter(HTMLColumnFilter filter) {
        this.filter = filter;
    }
    
    
    
    public Map<String, Object> getBaseContext() {
        return this.baseContext;
    }
    
    
    
    @Override
    public synchronized HttpAnswer handleHttpEvent(String registered, HttpEvent e,
                HttpEventHandler next) throws HttpException {
        
        // current settings for requesting user
        final HttpSession s = e.getSession();
        final User user = (User) s.getAttached("user");
        if (!this.myPolly.roles().canAccess(user, this.model)) {
            throw new AlternativeAnswerException(
                HttpAnswers.newStringAnswer("<tr><th colspan=\"" + 
                    this.model.getColumnCount() + "\">Permission denied</th></tr>"));
        }
        
        
        final String settingsKey = "SETTINGS_"+registered;
        final TableSettings settings;
        synchronized (s) {
            if (s.isSet(settingsKey)) {
                settings = (TableSettings) s.getAttached(settingsKey);
            } else {
                settings = new TableSettings();
                settings.sortCol = this.model.getDefaultSortColumn();
                settings.filter = new String[this.model.getColumnCount()];
                settings.order = new SortOrder[this.model.getColumnCount()];
                Arrays.fill(settings.filter, "");
                Arrays.fill(settings.order, SortOrder.UNDEFINED);
                if (settings.sortCol != -1) {
                    settings.order[settings.sortCol] = this.model.getDefaultSortOrder();
                }
                s.set(settingsKey, settings);
            }
        }
        
        
        List<T> allData = null; 
        if (e.get(SORT_COLUMN) != null) {
            // this is a sorting request
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
            
            allData = this.model.getData(e);
            final T element = allData.get(row);
            final SuccessResult r = this.model.setCellValue(col, element, 
                value, user, this.myPolly);
            
            return new GsonHttpAnswer(200, r);
        } else if (e.get(SET_PAGE_SIZE) != null) {
            final int pageSize = Integer.parseInt(e.get(SET_PAGE_SIZE));
            settings.pageSize = pageSize;
        }
        
        
        
        if (this.model.isFilterOnly()) {
            // data should only be loaded if a filter is active
            boolean hasFilter = false;
            for (final String filter : settings.filter) {
                hasFilter |= !filter.equals("");
            }
            if (!hasFilter) {
                // no filter is active and this is no filter request
                final FilterResult fr = new FilterResult(new ArrayList<T>(), 
                        new ArrayList<T>(), new HashMap<T, Integer>(), 0);
                
                final Map<String, Object> c = this.createContext(settings, 
                    registered, fr, e, new ArrayList<T>());
                c.put("noFilter", true);
                return HttpAnswers.newTemplateAnswer(
                        "de/skuzzle/polly/sdk/httpv2/html/table.html", c);
            }
        }
        
        
        
        if (allData == null) {
            allData = this.model.getData(e);
        }
        
        
        
        // get filtered elements
        final FilterResult fr = this.getFilteredElements(settings, allData, e);
        this.fireDataProcessed(fr.unsided, e);
        
        final Map<String, Object> c = this.createContext(settings, 
            registered, fr, e, allData);
        return HttpAnswers.newTemplateAnswer(
                "de/skuzzle/polly/sdk/httpv2/html/table.html", c);
    }
    
    
    
    private Map<String, Object> createContext(TableSettings settings, String registered, 
            FilterResult fr, HttpEvent e, List<T> allData) {
        final int minPage = Math.max(0, settings.page - 3);
        final int maxPage = Math.max(0, Math.min(settings.pageCount - 1, Math.max(settings.page + 3, 6)));
        
        final Map<String, Object> c = new HashMap<>();
        c.put("settings", settings);
        c.put("tableModel", this.model);
        c.put("colSorter", this.colSorter);
        c.put("renderers", this.renderers);
        c.put("filter", this.filter);
        c.put("editors", this.editors);
        c.put("baseUrl", makeUrl(registered, this.model.getRequestParameters(e)));
        c.put("tId", this.tableId);
        c.put("data", fr.sided);
        c.put("indexMap", fr.indexMap);
        c.put("requestParams", this.model.getRequestParameters(e));
        c.put("minPage", minPage);
        c.put("maxPage", maxPage);
        c.put("all", allData.size());
        c.put("filteredSize", fr.filteredSize);
        c.putAll(this.baseContext);
        return c;
    }
    
    
    
    private FilterResult getFilteredElements(TableSettings s, List<T> data, 
            HttpEvent e) {
        
        // filter full data
        final int colCount = this.model.getColumnCount();
        final Map<T, Integer> idxMap = new HashMap<>(data.size());
        final List<T> unsided = new ArrayList<>(data.size());
        
        // preprocess filters: parse each filter string
        final Object[] filters = new Object[s.filter.length];
        for (int i = 0; i < filters.length; ++i) {
            if (this.model.isFilterable(i) && !s.filter[i].equals("")) {
                filters[i] = this.filter.getAcceptor(i).parseFilter(s.filter[i]);
            }
        }
        
        int originalIdx = -1;
        
        outer: 
        for (final T element : data) {
            ++originalIdx; // index of current element in the original data
            
            
            for (int i = 0; i < colCount; ++i) {
                final Object colFilter = filters[i];
                if (colFilter == null) {
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
            // map index within the filtered- to index in the full collection
            idxMap.put(element, originalIdx); 
            unsided.add(element);
        }
        
        if (unsided.isEmpty()) {
            return new FilterResult(unsided, unsided, idxMap, 0);
        }
        
        
        // sort view port
        if (s.sortCol != -1 && this.model.isSortable(s.sortCol)) {
            if (s.order[s.sortCol] != SortOrder.UNDEFINED) {
                final Comparator<? super T> c = this.colSorter.getComparator(
                    s.sortCol, this.model);
                Collections.sort(unsided, new DirectedComparator<>(s.getOrder(), c));
            }
        }
        
        
        int filteredSize = unsided.size();
        
        // get view port based on filtered data
        s.pageCount = (int) Math.ceil(unsided.size() / (double) s.pageSize);
        s.page = MathUtil.limit(s.page, 0, Math.max(s.pageCount - 1, 0));
        final int firstIdx = s.page * s.pageSize;
        final int lastIdx = Math.min(unsided.size(), firstIdx + s.pageSize);
        
        // Always copy the list to save memory. Resulting list is only 'pageSize' 
        // element big
        final List<T> sided = new ArrayList<T>(unsided.subList(firstIdx, lastIdx));

        return new FilterResult(sided, unsided, idxMap, filteredSize);
    }
}
