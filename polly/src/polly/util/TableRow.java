package polly.util;

public interface TableRow<T> {

    public int columnCount();
    
    public String getColumnName(int column);
    
    public String getValue(int column);
    
    public T getValue();
}
