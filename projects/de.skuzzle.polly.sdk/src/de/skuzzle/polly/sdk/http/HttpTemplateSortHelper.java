package de.skuzzle.polly.sdk.http;

import de.skuzzle.polly.sdk.util.ReflectionSorter;


/**
 * <p>This class provides a static helper method that modifies a 
 * {@link HttpTemplateContext} so that it can sort a list of objects.</p>
 * 
 * <p>Usage: in your {@link HttpAction} "MyAction"s execute method, call </p>
 * <pre>SortHelper.makeListSortable(c, e, "yourSortKeyName", "yourSortDirectionName", "yourDefaultSortKey")</pre>
 * 
 * <p>In your template you need to specify links that will invoke the sorting for a 
 * certain sort key. For example:
 * <pre>&lt;a href="MyAction?yourSortKeyName=getDate&yourSortDirectionName=$yourSortDirectionName"&gt;Sort for date&lt;/a&gt;</pre>
 * At the beginning of your template specify the following line: 
 * <pre>#set ($yourSortDirectionName = !$yourSortDirectionName)</pre>
 * Now you can invoke the sorter for your list of objects before printing them:
<pre>
#if ($yourSortKeyName)
    $sorter.sort($yourList, $yourSortKeyName, $yourSortDirectionName)
#end
#foreach ($item in $yourList)
    // print your sorted list.
#end
</pre>
 * 
 * <p>All the sorting relies on proper usage of {@link ReflectionSorter}. See its 
 * documentation for further information about sorting using reflection.</p>
 * 
 * @author Simon
 * @since 0.9.1
 * @see ReflectionSorter
 */
public final class HttpTemplateSortHelper {

    
    
    /**
     * <p>Prepares a {@link HttpTemplateContext} so that it can sort a list of 
     * objects.</p>
     * 
     * <p>Note: This method relies on proper usage of the {@link ReflectionSorter}. 
     * Please read the documentation to be aware of the restrictions.</p>
     * 
     * @param c The HttpTemplateContext to prepare.
     * @param e The current HttpEvent.
     * @param sortKeyName The name of the sort key parameter in 
     *          {@link HttpEvent#getProperty(String)}.
     * @param sortDirectionName The name of the sort direction in
     *          {@link HttpEvent#getProperty(String)}.
     * @param defaultSortKey The default sort key if the parameter with the name
     *          <code>sortKeyName</code> was not specified.
     */
    public final static void makeListSortable(
            HttpTemplateContext c, HttpEvent e, 
            String sortKeyName, String sortDirectionName, String defaultSortKey) {
        
        c.put("sorter", ReflectionSorter.class);
        String sortKey = e.getProperty(sortKeyName);
        if (sortKey == null || sortKey.equals("")) {
            sortKey = defaultSortKey;
        }
        
        boolean desc = e.getProperty(sortDirectionName) == null
            ? false
            : e.getProperty(sortDirectionName).equals("true");
        
        c.put(sortDirectionName, desc);
        c.put(sortKeyName, sortKey);
    }
    
    
    
    private HttpTemplateSortHelper() {}
}