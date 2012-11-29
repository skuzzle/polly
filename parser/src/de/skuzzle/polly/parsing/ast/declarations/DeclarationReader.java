package de.skuzzle.polly.parsing.ast.declarations;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;

import de.skuzzle.polly.parsing.ExpInputParser;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.visitor.TypeResolver;


public class DeclarationReader implements Closeable {
    
    public static void main(String[] args) throws IOException {
        DeclarationReader dr = new DeclarationReader(new File("decls/me.decl"), Namespace.forName("me"));
        
        dr.readAll();
        dr.close();
    }
    
    
    private final class DeclarationParser extends ExpInputParser {

        public DeclarationParser(String input) throws UnsupportedEncodingException {
            super(input);
        }
        
        
        
        @Override
        public Expression parseExpr() throws ParseException {
            return super.parseExpr();
        }
    }
    
    

    private final LineNumberReader reader;
    private final File file;
    private final TypeResolver typeResolver;
    
    
    
    public DeclarationReader(File file, Namespace nspace) throws FileNotFoundException {
        this.file = file;
        this.reader = new LineNumberReader(new FileReader(file));
        this.typeResolver = new TypeResolver(nspace);
    }
    
    
    
    /**
     * Reads all declaration from the file and stores them in the namespace.
     * 
     * @throws IOException If parsing fails or an IO error occurs.
     */
    public void readAll() throws IOException {
        while(this.readDeclaration());
    }
    
    
    
    /**
     * Reads the next declaration from the declaration file and stores it to the 
     * namespace.
     * 
     * @return <code>true</code> if declaration was read successfully, <code>false</code>
     *          if no more declarations are available.
     * @throws IOException If parsing fails or an IO error occurs.
     */
    public boolean readDeclaration() throws IOException {
        final String line = this.reader.readLine();
        if (line == null) {
            return false;
        }
        
        final DeclarationParser p = new DeclarationParser(line);
        try {
            final Expression exp = p.parseExpr();
            if (!(exp instanceof Assignment)) {
                throw new IOException("corrupt declaration file");
            }
            final Assignment assign = (Assignment) exp;
            assign.visit(this.typeResolver);
            return true;
        } catch (Exception e) {
            throw new IOException("In file: " + 
                this.file.getName() + ", in line: " + this.reader.getLineNumber() +
                ": " + e.getMessage(), e);
        }
    }
    
    
    
    @Override
    public void close() throws IOException {
        if (this.reader != null) {
            this.reader.close();
        }
    }
}
