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
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.ExecutionVisitor;
import de.skuzzle.polly.parsing.ast.visitor.TypeResolver;


/**
 * Class to read assignments from a file. The assignments must be valid polly statements
 * and separated by lines. Any empty line or line that starts with a '#' will be skipped.
 * 
 * @author Simon Taddiken
 */
public class DeclarationReader implements Closeable {
    
    public static void main(String[] args) throws IOException {
        DeclarationReader dr = new DeclarationReader(new File("decls/me.decl"), "ISO-8859-1", Namespace.forName("me"));
        
        dr.readAll();
        dr.close();
        
        System.out.println(Namespace.forName("me").toString());
    }
    
    
    /**
     * Parser subclass that changes visibility of the parseExpr method. This is used to
     * parse a single assignment from file.
     * 
     * @author Simon Taddiken
     */
    private final class DeclarationParser extends ExpInputParser {

        public DeclarationParser(String input, String charset) throws UnsupportedEncodingException {
            super(input);
        }
        
        
        
        @Override
        public Expression parseExpr() throws ParseException {
            return super.parseExpr();
        }
    }
    
    

    private final String charset;
    private final LineNumberReader reader;
    private final File file;
    private final TypeResolver typeResolver;
    private final ExecutionVisitor executor;
    
    
    
    public DeclarationReader(File file, String charset, Namespace nspace) 
            throws FileNotFoundException {
        this.charset = charset;
        this.file = file;
        this.reader = new LineNumberReader(new FileReader(file));
        this.typeResolver = new TypeResolver(nspace);
        this.executor = new ExecutionVisitor(nspace);
    }
    
    
    
    /**
     * Reads all declarations from the file and stores them in the namespace.
     * 
     * @throws IOException If an IO error occurs.
     */
    public void readAll() throws IOException {
        while(this.readDeclaration());
    }
    
    
    
    /**
     * <p>Reads the next declaration from the declaration file and stores it to the 
     * namespace that this class was created with. If the read declaration is
     * invalid, it will be skipped. In this case, this method might return 
     * <code>true</code> anyway if there are more declarations to read.</p>
     * 
     * @return <code>true</code> if more declarations are to be read, <code>false</code>
     *          if no more declarations are available.
     * @throws IOException If an IO error occurs.
     */
    public boolean readDeclaration() throws IOException {
        String line = this.reader.readLine();
        if (line == null) {
            return false;
        } else if (line.equals("") || line.startsWith("#")) {
            // line to be skipped
            return true;
        }
        
        final DeclarationParser p = new DeclarationParser(line, this.charset);
        try {
            final Expression exp = p.parseExpr();
            if (!(exp instanceof Assignment)) {
                return true;
            }
            
            final Assignment assign = (Assignment) exp;
            assign.visit(this.typeResolver);
            assign.visit(this.executor);
            return true;
        } catch (ASTTraversalException e) {
            // skip this delcaration because it was invalid.
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
