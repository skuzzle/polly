package de.skuzzle.polly.sdk.httpv2.html;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.DecimalFormat;
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
import java.util.regex.Pattern;

import de.skuzzle.polly.http.api.AlternativeAnswerException;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.HttpEvent.RequestMode;
import de.skuzzle.polly.http.api.HttpException;
import de.skuzzle.polly.http.api.HttpSession;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
import de.skuzzle.polly.http.api.handler.HttpEventHandler;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.Messages;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.httpv2.GsonHttpAnswer;
import de.skuzzle.polly.sdk.httpv2.SuccessResult;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.util.DirectedComparator;
import de.skuzzle.polly.sdk.util.DirectedComparator.SortOrder;
import de.skuzzle.polly.tools.math.MathUtil;
import de.skuzzle.polly.tools.strings.StringUtils;


public class HTMLTable<T> implements HttpEventHandler {
    
    
    
    public static class BooleanCellEditor implements CellEditor {
        @Override
        public HTMLElement renderEditorCell(Object cellContent, boolean forFilter) {
            
            if (forFilter) {
                // if filter, cellContent is the filter string!
                final String f = (String) cellContent;
                
                final String name = "opt_" + (int) (Math.random() * 1000); //$NON-NLS-1$
                final HTMLElement all = new HTMLElement("input") //$NON-NLS-1$
                    .attr("type", "radio") //$NON-NLS-1$ //$NON-NLS-2$
                    .attr("name", name) //$NON-NLS-1$
                    .attr("class", "filter_input") //$NON-NLS-1$ //$NON-NLS-2$
                    .attr("value", "") //$NON-NLS-1$ //$NON-NLS-2$
                    .content(Messages.tableFilterAll);
                    
                final HTMLElement selected = new HTMLElement("input") //$NON-NLS-1$
                    .attr("type", "radio") //$NON-NLS-1$ //$NON-NLS-2$
                    .attr("name", name) //$NON-NLS-1$
                    .attr("class", "filter_input") //$NON-NLS-1$ //$NON-NLS-2$
                    .attr("value", "true") //$NON-NLS-1$ //$NON-NLS-2$
                    .content(Messages.tableFilterTrue);
                
                final HTMLElement unselected = new HTMLElement("input") //$NON-NLS-1$
                    .attr("type", "radio") //$NON-NLS-1$ //$NON-NLS-2$
                    .attr("name", name) //$NON-NLS-1$
                    .attr("class", "filter_input") //$NON-NLS-1$ //$NON-NLS-2$
                    .attr("value", "false") //$NON-NLS-1$ //$NON-NLS-2$
                    .content(Messages.tableFilterFalse);
                
                final HTMLElement checked;
                if (f.equals("")) { //$NON-NLS-1$
                    checked = all;
                } else if (f.equals("true")) { //$NON-NLS-1$
                    checked = selected;
                } else {
                    checked = unselected;
                }
                checked.attr("checked"); //$NON-NLS-1$
                return new HTMLElementGroup().add(all).add(selected).add(unselected);
            }
            final HTMLElement in = new HTMLElement("input").attr("type", "checkbox"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            if (cellContent != null && (Boolean) cellContent) {
                in.attr("checked"); //$NON-NLS-1$
            }
            return in;
        }
        
        
        
        @Override
        public String getEditIndicator() {
            return ""; //$NON-NLS-1$
        }
    }
    
    
    
    public static class DateCellEditor extends TextCellEditor {
        
        @Override
        public String getEditIndicator() {
            return "<img src=\"/de/skuzzle/polly/sdk/httpv2/html/date_edit.png\" height=\"16\" width=\"16\" style=\"vertical-align:middle\"/>"; //$NON-NLS-1$
        }
    }
    
    
    
    public static class TextCellEditor implements CellEditor {
        @Override
        public HTMLElement renderEditorCell(Object cellContent, boolean forFilter) {
            final String cls = forFilter ? "textbox filter_input" : "textbox edit_input"; //$NON-NLS-1$ //$NON-NLS-2$
            final String content = cellContent == null ? "" : cellContent.toString(); //$NON-NLS-1$
            return new HTMLElement("input") //$NON-NLS-1$
                .attr("type", "text") //$NON-NLS-1$ //$NON-NLS-2$
                .attr("class", cls) //$NON-NLS-1$
                .attr("style", "width: 85%") //$NON-NLS-1$ //$NON-NLS-2$
                .attr("value", content); //$NON-NLS-1$
        }
        
        
        
        @Override
        public String getEditIndicator() {
            return 
                "<img src=\"/de/skuzzle/polly/sdk/httpv2/html/edit.png\" height=\"16\" width=\"16\" style=\"vertical-align:middle\"/>"; //$NON-NLS-1$
        }
    }
    
    
    
    public static class ToStringCellRenderer implements CellRenderer {
        private final boolean escape;
        
        private final static Pattern URL_PATTERN = Pattern.compile(
                "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"); //$NON-NLS-1$
        
        public ToStringCellRenderer(boolean escape) {
            this.escape = escape;
        }
        
        @Override
        public String renderCellContent(int column, Object cellValue) {
            if (cellValue == null) {
                return ""; //$NON-NLS-1$
            } else {
                String s = cellValue.toString();
                s = this.escape ? HTMLTools.escape(s) : s;
                final java.util.regex.Matcher m = URL_PATTERN.matcher(s);
                final StringBuffer buff = new StringBuffer();
                
                while (m.find()) {
                    m.appendReplacement(buff, "<a href=\"" + m.group() + "\">" + m.group() + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
                m.appendTail(buff);
                s = buff.toString();
                return s;
            }
        }
    }
    
    
    
    public static class DateCellRenderer implements CellRenderer {
        @Override
        public String renderCellContent(int column, Object cellValue) {
            final Date date = (Date) cellValue;
            if (date.getTime() == 0) {
                return ""; //$NON-NLS-1$
            } else {
                final DateFormat df = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy"); //$NON-NLS-1$
                return df.format((Date) cellValue);
            }
        }
    }
    
    
    
    public static class BooleanCellRenderer implements CellRenderer {

        @Override
        public String renderCellContent(int column, Object cellValue) {
            return new BooleanCellEditor()
                .renderEditorCell(cellValue, false).attr("disabled").toString(); //$NON-NLS-1$
        }
    }
    
    
    
    public static class DoubleCellRenderer implements CellRenderer {

        @Override
        public String renderCellContent(int column, Object cellValue) {
            final DecimalFormat nf = new DecimalFormat("0.##"); //$NON-NLS-1$
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
                return ""; //$NON-NLS-1$
            }
            final Types t = (Types) cellValue;
            return HTMLTools.escape(t.valueString(this.formatter));
        }
    }
    
    
    
    
    private final static String makeUrl(String baseUrl, Map<String, String> param) {
        String append = "&"; //$NON-NLS-1$
        if (!baseUrl.contains("?")) { //$NON-NLS-1$
            append = "?"; //$NON-NLS-1$
        }
        final StringBuilder b = new StringBuilder(baseUrl.length());
        b.append(baseUrl);
        for (final Entry<String, String> e : param.entrySet()) {
            b.append(append);
            b.append(e.getKey());
            b.append("="); //$NON-NLS-1$
            b.append(e.getValue());
            append = "&"; //$NON-NLS-1$
        }
        return b.toString();
    }
    
    
    

    private final static String SORT_COLUMN = "sort"; //$NON-NLS-1$
    private final static String UPDATE_ALL = "updateAll"; //$NON-NLS-1$
    private final static String SET_PAGE = "page"; //$NON-NLS-1$
    private final static String SET_PAGE_SIZE = "pageSize"; //$NON-NLS-1$
    private final static String FILTER_VAL = "filterVal"; //$NON-NLS-1$
    private final static String FILTER_COL = "filterCol"; //$NON-NLS-1$
    private final static String FILTER = "filter"; //$NON-NLS-1$
    private final static String FILTER_ALL = "filter_all"; //$NON-NLS-1$
    private final static String SET_VALUE = "setValue"; //$NON-NLS-1$
    private final static String COLUMN = "col"; //$NON-NLS-1$
    private final static String ROW = "row"; //$NON-NLS-1$
    private final static String FILTER_TOGGLE = "filterToggle"; //$NON-NLS-1$
    
    private final static int DEFAULT_PAGE_SIZE = 40;
    
    
    private final class DataHolder {
        private List<T> allData;
        private List<T> filtered;
        private List<T> view;
    }
    
    
    
    public class TableSettings {
        private int sortCol;
        private SortOrder[] order;
        private String[] filter;
        private String filterAll;
        private int pageCount;
        private int pageSize = DEFAULT_PAGE_SIZE;
        private int page;
        private boolean filterRowShown;
        private WeakReference<DataHolder> data = new WeakReference<DataHolder>(null); 
        private Map<T, Integer> indexMap;
        private String lastRefreshKeyValue;
        
        public boolean isFilterRowShown() {
            return this.filterRowShown;
        }
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
        public String getFilterAll() {
            return this.filterAll;
        }
        public boolean isFilterSet() {
            if (StringUtils.isValid(this.filterAll)) {
                return true;
            }
            for (final String s : this.filter) {
                if (StringUtils.isValid(s)) {
                    return true;
                }
            }
            return false;
        }
        @Override
        public String toString() {
            final DataHolder data = this.data.get();
            return "[sortCol: " + this.sortCol +  //$NON-NLS-1$
                ", filterRowShown: " + this.filterRowShown +  //$NON-NLS-1$
                ", page: " + this.page +  //$NON-NLS-1$
                ", pageCount: " + this.pageCount +  //$NON-NLS-1$
                ", pageSize: " + this.pageSize +  //$NON-NLS-1$
                ", sortOrder: " + Arrays.toString(this.order) +  //$NON-NLS-1$
                ", filter: " + Arrays.toString(this.filter) + "]" +  //$NON-NLS-1$ //$NON-NLS-2$
                ", all elements: " + (data != null ? data.allData.size() : "0") +  //$NON-NLS-1$ //$NON-NLS-2$
                ", filtered elements: " + (data != null ? data.filtered.size() : "0"); //$NON-NLS-1$ //$NON-NLS-2$
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
    @SuppressWarnings("unchecked")
    public synchronized HttpAnswer handleHttpEvent(String registered, HttpEvent e,
                HttpEventHandler next) throws HttpException {
        
        final Map<String, String> params = e.parameterMap(RequestMode.GET);
        
        // current settings for requesting user
        final HttpSession s = e.getSession();
        final User user = (User) s.get(WebinterfaceManager.USER);
        if (!this.myPolly.roles().canAccess(user, this.model)) {
            final String permDenied = Messages.htmlTablePermDenied;
            throw new AlternativeAnswerException(
                HttpAnswers.newStringAnswer("<tr><th colspan=\"" +  //$NON-NLS-1$
                    this.model.getColumnCount() + "\">" + permDenied + "</th></tr>")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        
        final String settingsKey = "SETTINGS_"+registered; //$NON-NLS-1$
        final TableSettings settings;
        synchronized (s) {
            if (s.isSet(settingsKey)) {
                settings = (TableSettings) s.get(settingsKey);
            } else {
                settings = new TableSettings();
                settings.filterRowShown = true;
                settings.sortCol = this.model.getDefaultSortColumn();
                settings.filter = new String[this.model.getColumnCount()];
                settings.order = new SortOrder[this.model.getColumnCount()];
                settings.filterAll = ""; //$NON-NLS-1$
                Arrays.fill(settings.filter, ""); //$NON-NLS-1$
                Arrays.fill(settings.order, SortOrder.UNDEFINED);
                if (settings.sortCol != -1) {
                    settings.order[settings.sortCol] = this.model.getDefaultSortOrder();
                }
                s.set(settingsKey, settings);
            }
        }
        
        // check filter row toggle first because it requires no more data processing
        if (params.get(FILTER_TOGGLE) != null) {
            settings.filterRowShown = !settings.filterRowShown;
            // just return junk answer
            return HttpAnswers.newStringAnswer("ok"); //$NON-NLS-1$
        }
        
        
        boolean forceRefresh = false;
        if (this.model.getRefreshKey() != null) {
            final String refreshValue = params.get(this.model.getRefreshKey());
            forceRefresh = refreshValue != settings.lastRefreshKeyValue;
            settings.lastRefreshKeyValue = refreshValue;
        }
        
        DataHolder data = settings.data.get();
        boolean mustRefilter = true;
        if (data == null || params.get(UPDATE_ALL) != null || forceRefresh) {
            data = new DataHolder();
            data.allData = this.model.getData(e);
            data.view = data.allData;
            data.filtered = data.view;
            
            settings.indexMap = new HashMap<>(data.allData.size());
            settings.pageCount = (int) Math.ceil(data.allData.size() / (double) settings.pageSize);
            int i = 0;
            for (final T t : data.allData) {
                settings.indexMap.put(t, i++);
            }
            
            settings.data = new WeakReference<HTMLTable<T>.DataHolder>(data);
            this.updateFilter(settings, data, e);
            this.updateSorting(settings, data);
            this.updateViewPort(settings, data);
            
            mustRefilter = false;
        }
        
        if (params.get(SORT_COLUMN) != null) {
            // this is a sorting request
            // column for which to sort
            final int newSortCol = Integer.parseInt(params.get(SORT_COLUMN));
            if (newSortCol < 0 || newSortCol >= this.model.getColumnCount()) {
                // TODO: error
            }
            // last sort order of this column, reverse
            final SortOrder order = settings.order[newSortCol].reverse();
            
            // update setting
            settings.order[newSortCol] = order;
            settings.sortCol = newSortCol;
            
            // sort data
            this.updateSorting(settings, data);
            this.updateViewPort(settings, data);
            
        } else if (params.get(FILTER_COL) != null) {
            final int filterCol = Integer.parseInt(params.get(FILTER_COL));
            if (filterCol < 0 || filterCol >= this.model.getColumnCount()) {
                // TODO: error
            }
            final String filterVal = params.get(FILTER_VAL);
            settings.filter[filterCol] = filterVal == null ? "" : filterVal; //$NON-NLS-1$
            
            this.updateFilter(settings, data, e);
            this.updateSorting(settings, data);
            this.updateViewPort(settings, data);
            
        } else if (params.get(FILTER) != null && Boolean.parseBoolean(params.get(FILTER))) { 
            for (int i = 0; i < this.model.getColumnCount(); ++i) {
                if (params.get(Integer.toString(i)) != null) { 
                    settings.filter[i] = params.get(Integer.toString(i));
                } else {
                    settings.filter[i] = ""; //$NON-NLS-1$
                }
            }
            final String all = params.get(FILTER_ALL) == null 
                    ? ""  //$NON-NLS-1$
                    : params.get(FILTER_ALL); 
            settings.filterAll = all;
            
            this.updateFilter(settings, data, e);
            this.updateSorting(settings, data);
            this.updateViewPort(settings, data);
            
        } else if (params.get(SET_PAGE) != null) {
            final int page = Integer.parseInt(params.get(SET_PAGE));
            settings.page = MathUtil.limit(page, 0, Math.max(settings.pageCount - 1, 0));
            
            this.updateViewPort(settings, data);
            
        } else if (params.get(SET_VALUE) != null) {
            final String value = params.get(SET_VALUE);
            final int col = Integer.parseInt(params.get(COLUMN));
            final int row = Integer.parseInt(params.get(ROW));
            
            final T element = this.model.getData(e).get(row);
            final SuccessResult r = this.model.setCellValue(col, element, 
                value, user, this.myPolly);
            
            return new GsonHttpAnswer(200, r);
        } else if (e.get(SET_PAGE_SIZE) != null) {
            final int pageSize = Integer.parseInt(e.get(SET_PAGE_SIZE));
            settings.pageSize = pageSize;
            
            this.updateViewPort(settings, data);
        } else if (mustRefilter) {
            this.updateFilter(settings, data, e);
            this.updateSorting(settings, data);
            this.updateViewPort(settings, data);
        }
        
        
        if (this.model.isFilterOnly()) {
            // data should only be loaded if a filter is active
            boolean hasFilter = !settings.filterAll.equals(""); //$NON-NLS-1$
            for (final String filter : settings.filter) {
                hasFilter |= !filter.equals(""); //$NON-NLS-1$
            }
            if (!hasFilter) {
                // no filter is active and this is no filter request
                final Map<String, Object> c = this.createContext(settings, registered, e);
                c.put("noFilter", true); //$NON-NLS-1$
                return HttpAnswers.newTemplateAnswer(
                        "de/skuzzle/polly/sdk/httpv2/html/table.html", c); //$NON-NLS-1$
            }
        }
        
        final Map<String, Object> c = this.createContext(settings, registered, e);
        return HttpAnswers.newTemplateAnswer(
                "de/skuzzle/polly/sdk/httpv2/html/table.html", c); //$NON-NLS-1$
    }
    
    
    
    private void updateSorting(TableSettings settings, DataHolder data) {
        if (settings.sortCol != -1 && this.model.isSortable(settings.sortCol)) {
            if (settings.order[settings.sortCol] != SortOrder.UNDEFINED) {
                final Comparator<? super T> c = 
                        this.colSorter.getComparator(settings.sortCol, this.model);
                Collections.sort(data.filtered, new DirectedComparator<>(settings.getOrder(), c));
            }
        }
    }
    
    
    
    private void updateViewPort(TableSettings settings, DataHolder data) {
        settings.pageCount = (int) Math.ceil(data.filtered.size() / (double) settings.pageSize);
        final int firstIdx = MathUtil.limit(settings.page * settings.pageSize, 0, data.filtered.size()) ;
        final int lastIdx = Math.min(data.filtered.size(), firstIdx + settings.pageSize);

        data.view = data.filtered.subList(firstIdx, lastIdx);
    }
    
    
    
    private void updateFilter(TableSettings s, DataHolder data, HttpEvent e) {
        // filter full data
        final int colCount = this.model.getColumnCount();
        data.filtered = new ArrayList<>(data.allData.size());
        
        // preprocess filters: parse each filter string
        final Object[] filters = new Object[s.filter.length];
        final boolean[] negate = new boolean[s.filter.length];
        for (int i = 0; i < filters.length; ++i) {
            if (this.model.isFilterable(i) && !s.filter[i].equals("")) { //$NON-NLS-1$
                negate[i] = s.filter[i].startsWith("!"); //$NON-NLS-1$
                String f = s.filter[i];
                if (negate[i]) {
                    f = f.substring(1); // strip ! off the beginning
                }
                filters[i] = this.filter.getAcceptor(i).parseFilter(f);
            }
        }
        
        Pattern FILTER_ALL;
        final boolean negateFilterAll = s.filterAll.startsWith("!"); //$NON-NLS-1$
        final String f = negateFilterAll ? s.filterAll.substring(1) : s.filterAll;
        final String filterAllString = ".*" + f + ".*"; //$NON-NLS-1$ //$NON-NLS-2$
        final boolean doFilterAll = !s.filterAll.equals(""); //$NON-NLS-1$
        
        try {
            FILTER_ALL = Pattern.compile(filterAllString, Pattern.CASE_INSENSITIVE);
        } catch (Exception e1) {
            FILTER_ALL = Pattern.compile(".*", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
        }
        int originalIdx = -1;
        
        for (final T element : data.allData) {
            ++originalIdx; // index of current element in the original data
            
            boolean acceptedByAll = false;
            if (doFilterAll) {
                for (int i = 0; i < colCount; ++i) {
                    if (model.isFilterable(i)) {
                        final Object cellContent = this.model.getCellValue(i, element);
                        boolean all = FILTER_ALL.matcher(cellContent.toString()).find();
                        if (negateFilterAll) {
                            all = !all;
                        }
                        acceptedByAll |=  all;
                        if (acceptedByAll) {
                            break;
                        }
                    }
                }
            }
            
            boolean acceptRow = true;
            for (int i = 0; i < colCount; ++i) {
                boolean acceptCol = true;
                if (!this.model.isFilterable(i)) {
                    // column is not filterable
                    acceptCol = true;
                } else {
                    final Object cellContent = this.model.getCellValue(i, element);
                    final Object colFilter = filters[i];
                    final boolean doFilterCol = colFilter != null;
                    
                    if (doFilterCol) {
                        final Acceptor acceptor = this.filter.getAcceptor(i);
                        final boolean acceptedByCol = acceptor.accept(colFilter, cellContent);
                        
                        acceptCol = doFilterAll 
                                ? acceptedByAll && acceptedByCol
                                : acceptedByCol;
                        acceptCol = negate[i] ? !acceptCol : acceptCol;
                    } else {
                        acceptCol = doFilterAll ? acceptedByAll : true;
                    }
                }
                acceptRow &= acceptCol;
            }
            // all filters applied, each accepted the element
            // map index within the filtered- to index in the full collection
            if (acceptRow) {
                s.indexMap.put(element, originalIdx); 
                data.filtered.add(element);
            }
        }
        this.fireDataProcessed(data.filtered, e);
    }
    
    
    
    private Map<String, Object> createContext(TableSettings settings, String registered, 
            HttpEvent e) {
        final int minPage = Math.max(0, settings.page - 3);
        final int maxPage = Math.max(0, Math.min(settings.pageCount - 1, Math.max(settings.page + 3, 6)));
        
        final Map<String, Object> c = new HashMap<>();
        HTMLTools.gainFieldAccess(c, Messages.class, "MSG"); //$NON-NLS-1$
        c.put("Messages", Constants.class); //$NON-NLS-1$
        c.put("settings", settings); //$NON-NLS-1$
        c.put("tableModel", this.model); //$NON-NLS-1$
        c.put("colSorter", this.colSorter); //$NON-NLS-1$
        c.put("renderers", this.renderers); //$NON-NLS-1$
        c.put("filter", this.filter); //$NON-NLS-1$
        c.put("editors", this.editors); //$NON-NLS-1$
        c.put("baseUrl", makeUrl(registered, this.model.getRequestParameters(e))); //$NON-NLS-1$
        c.put("tId", this.tableId); //$NON-NLS-1$
        c.put("data", settings.data.get().view); //$NON-NLS-1$
        c.put("indexMap", settings.indexMap); //$NON-NLS-1$
        c.put("requestParams", this.model.getRequestParameters(e)); //$NON-NLS-1$
        c.put("minPage", minPage); //$NON-NLS-1$
        c.put("maxPage", maxPage); //$NON-NLS-1$
        c.put("all", settings.data.get().allData.size()); //$NON-NLS-1$
        c.put("filteredSize", settings.data.get().filtered.size()); //$NON-NLS-1$
        c.putAll(this.baseContext);
        return c;
    }
}
