package de.skuzzle.polly.sdk.http;

import java.util.List;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;


public abstract class SimpleMultiPageView<T> extends HttpAction {
    
    private final static int DEFAULT_PAGE_SIZE = 20;
    
    
    private final static String PAGE = "page";
    private final static String REL_PAGE_LEFT = "relLeft";
    private final static String REL_PAGE_RIGHT = "relRight";
    private final static String ABS_PAGE = "absPage";
    private final static String PAGE_IDX = "idx";
    private final static String FIRST_PAGE = "firstPage";
    private final static String LAST_PAGE = "lastPage";
    
    private MultiPageDataSource<T> source;
    
    
    
    public SimpleMultiPageView(String name, MyPolly myPolly, 
        MultiPageDataSource<T> source) {
        super(name, myPolly);
        this.source = source;
    }
    
    
    
    protected abstract HttpTemplateContext createContext(HttpEvent e) 
        throws HttpTemplateException, InsufficientRightsException;
    
    

    @Override
    public HttpTemplateContext execute(HttpEvent e) throws HttpTemplateException,
            InsufficientRightsException {
        
        final HttpTemplateContext c = this.createContext(e);
        
        final String pageSizeKey = this.getName() + "$pageSize";
        int pageSize;
        if (e.getSession().get(pageSizeKey) == null) {
            pageSize = DEFAULT_PAGE_SIZE;
        } else {
            pageSize = (Integer) e.getSession().get(pageSizeKey);
        }
        
        // check for page size modifiaction
        final String pageSizeAction = e.getProperty("setPageSize");
        if ("showAll".equals(pageSizeAction)) {
            pageSize = this.source.getDataCount();
        } else if (pageSizeAction != null) {
            pageSize = Integer.parseInt(pageSizeAction);
        }
        
        e.getSession().putDtata(pageSizeKey, pageSize);
        
        
        
        final int size = this.source.getDataCount();
        final int pc = (int) Math.ceil((float) size / pageSize);
        
        final String currentPageKey = this.getName() + "$currentPage";
        
        int currentPage;
        if (e.getSession().get(currentPageKey) == null) {
            e.getSession().putDtata(currentPageKey, new Integer(0));
            currentPage = 0;
        } else {
            currentPage = (Integer) e.getSession().get(currentPageKey);
        }
        
        final String pageAction = e.getProperty(PAGE);
        if (REL_PAGE_LEFT.equals(pageAction) && currentPage > 0) {
            currentPage = Math.max(0, currentPage - 1);
        } else if (REL_PAGE_RIGHT.equals(pageAction) && currentPage < pc - 1) {
            currentPage = Math.min(pc - 1, currentPage + 1);
        } else if (FIRST_PAGE.equals(pageAction)) {
            currentPage = 0;
        } else if (LAST_PAGE.equals(pageAction)) {
            currentPage = pc - 1;
        } else if (ABS_PAGE.equals(pageAction)) {
            final String sIdx = e.getProperty(PAGE_IDX);
            if (sIdx == null) {
                currentPage = 0;
            } else {
                final int idx = Integer.parseInt(sIdx);
                currentPage = Math.max(0, Math.min(idx, pc));
            }
        }
        
        e.getSession().putDtata(currentPageKey, currentPage);
        
        final int firstIdx = currentPage * pageSize;
        final int lastIdx = Math.min(firstIdx + pageSize, size);
        final List<T> sublist = this.source.getSubData(firstIdx, lastIdx);
        
        c.put("prevIdx", Math.max(0, currentPage - 1));
        c.put("pageIdx", currentPage + 1);
        c.put("data", sublist);
        c.put("pageCount", pc);
        c.put("isFirstPage", currentPage == 0);
        c.put("isLastPage", currentPage == pc - 1);
        
        this.postProcess(sublist, c, e);
        return c;
    }
    
    
    
    protected void postProcess(List<T> sublist, HttpTemplateContext c, HttpEvent e) 
        throws HttpTemplateException, InsufficientRightsException {
        
    }

}
