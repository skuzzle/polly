package polly.util;

import java.io.Writer;


public class StringBuilderWriter extends Writer {

    private StringBuilder stringBuilder;
    
    public StringBuilderWriter(StringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }
    
    
    @Override
    public void close() {}

    @Override
    public void flush() {}

    @Override
    public void write(char[] cbuf, int off, int len) {
        this.stringBuilder.append(cbuf, off, len);
    }

}
