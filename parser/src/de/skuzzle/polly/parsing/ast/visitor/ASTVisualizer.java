package de.skuzzle.polly.parsing.ast.visitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Delete;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.Native;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.parameters.FunctionParameter;
import de.skuzzle.polly.parsing.ast.expressions.parameters.ListParameter;
import de.skuzzle.polly.parsing.ast.expressions.parameters.Parameter;
import de.skuzzle.polly.process.KillingProcessWatcher;
import de.skuzzle.polly.process.ProcessExecutor;



public class ASTVisualizer extends DepthFirstVisitor {

    private int preorder_number;
    private FileWriter outputStream;
    private Stack<Integer> stack;
    
    

    public void toFile(String filename, Node root) throws IOException, 
            ASTTraversalException {
        try {
            this.outputStream = new FileWriter(filename);
            this.preorder_number = 0;
            this.stack = new Stack<Integer>();
            printBanner();
            root.visit(this);
            printFooter();
        } finally {
            if (this.outputStream != null) {
                this.outputStream.close();
            }
        }
        
        String dotPath = "C:\\Program Files (x86)\\Graphviz 2.28\\bin\\dot.exe";
        ProcessExecutor pe = ProcessExecutor.getOsInstance(false);
        pe.setExecuteIn(new File("C:\\Users\\Simon\\Documents\\Java\\polly\\parser"));
        pe.addCommand(dotPath);
        pe.addCommandsFromString("-Tpdf -o ast.pdf");
        pe.addCommand("datAST.dot");
        pe.setProcessWatcher(new KillingProcessWatcher(10000, true));
        pe.start();
    }
    
    

    private void printBanner() {
        this.println("graph \"\"");
        this.println("{");
        this.println("node [shape=box]");
        this.println("graph[bgcolor=white, ordering=out]");

    }



    // prints the file footer (end of the dot file)
    private void printFooter() {
        this.println("}");
    }



    // prints the node label
    private void printLabel(String name, String type, String attr, Position pos) {
        name = name.equals("") ? "" : name + "|";
        attr = attr.equals("") ? "" : attr + "|";
        type = type.equals("") ? "" : "Type: " + type + "|";
        println("n" + this.preorder_number + "[shape=record, label=\"{" + name + 
            type + attr + pos.toString() + "}\"]");
    }



    // prints text to output stream
    protected void println(String text) {
        try {
            this.outputStream.write(text + "\n");
        } catch (IOException e) {
        }
    }



    // increases the peorder number, pushes this number onto the
    // stack, and emits the node including an edge from its
    // parent
    private void printNode(String name, String attr, Node n) {
        this.preorder_number++;
        this.printLabel(name, "", attr, n.getPosition());
        // emit edge
        if (has_elements()) {
            this.println("n" + top() + " -- " + "n" + this.preorder_number);
        }
        this.push(this.preorder_number);
    }
    
    
    
    private void printNode(String name, String attr, Expression n) {
        this.preorder_number++;
        this.printLabel(name, n.getUnique().toString(), attr, n.getPosition());
        // emit edge
        if (has_elements()) {
            this.println("n" + top() + " -- " + "n" + this.preorder_number);
        }
        this.push(this.preorder_number);
    }



    protected boolean has_elements() {
        return !this.stack.empty();
    }



    private void pop() {
        this.stack.pop();
    }



    protected void push(int i) {
        this.stack.push(i);
    }



    protected int top() {
        return stack.lastElement();
    }

    
    
    @Override
    public void visitOperatorCall(OperatorCall call) throws ASTTraversalException {
        this.beforeOperatorCall(call);
        
        for (final Expression exp : call.getParameters()) {
            exp.visit(this);
        }
        
        this.afterOperatorCall(call);
    }
    


    @Override
    public void beforeOperatorCall(OperatorCall call) throws ASTTraversalException {
        this.printNode(call.getOperator().getId(), "", call);
    }



    @Override
    public void beforeCall(Call call) throws ASTTraversalException {
        this.printNode("Call", "", call);
    }



    @Override
    public void beforeLiteral(Literal literal) throws ASTTraversalException {
        this.printNode(literal.toString(), "", literal);
    }
    
    
    
    @Override
    public void beforeAccess(NamespaceAccess access) throws ASTTraversalException {
        this.printNode("Access", "", access);
    }
    
    
    
    @Override
    public void beforeAssignment(Assignment assign) throws ASTTraversalException {
        this.printNode("Assignment", "", assign);
    }
    
    
    
    @Override
    public void beforeFunctionLiteral(FunctionLiteral func) throws ASTTraversalException {
        this.printNode("Function", "", func);
    }
    
    
    
    @Override
    public void beforeNative(Native hc) throws ASTTraversalException {
        this.printNode("Hardcoded", "", hc);
    }
    
    
    
    @Override
    public void beforeIdentifier(Identifier identifier) throws ASTTraversalException {
        this.printNode(identifier.getId(), "", identifier);
    }
    
    
    
    @Override
    public void beforeListLiteral(ListLiteral list) throws ASTTraversalException {
        this.printNode("ListLiteral", "", list);
    }
    
    
    
    @Override
    public void beforeResolvable(ResolvableIdentifier id) throws ASTTraversalException {
        this.printNode(id.getId(), "", id);
    }
    
    
    
    @Override
    public void beforeParameter(Parameter param) throws ASTTraversalException {
        this.printNode(param.getName().getId(), "", param);
    }
    
    
    
    @Override
    public void beforeListParameter(ListParameter param) throws ASTTraversalException {
        this.beforeParameter(param);
    }
    
    
    
    @Override
    public void beforeFunctionParameter(FunctionParameter param)
            throws ASTTraversalException {
        this.beforeParameter(param);
    }
    
    
    
    @Override
    public void beforeDelete(Delete delete) throws ASTTraversalException {
        this.printNode("Delete", "", delete);
    }
    
    
    
    @Override
    public void beforeVarDecl(VarDeclaration decl) throws ASTTraversalException {
        this.printNode(decl.getName().getId(), "", decl.getExpression());
    }
    
    
    
    @Override
    public void beforeRoot(Root root) throws ASTTraversalException {
        this.printNode("Root", "", root);
    }
    
    
    
    @Override
    public void visitVarAccess(VarAccess access) throws ASTTraversalException {
        this.beforeVarAccess(access);
        this.printNode("Var " + access.getIdentifier(), "", access);
        this.afterVarAccess(access);
    }
    
    
    
    @Override
    public void afterAccess(NamespaceAccess access) throws ASTTraversalException {
        this.pop();
    }



    @Override
    public void afterAssignment(Assignment assign) throws ASTTraversalException {
        this.pop();
    }



    @Override
    public void afterCall(Call call) throws ASTTraversalException {
        this.pop();
    }
    
    

    @Override
    public void afterFunctionLiteral(FunctionLiteral func) throws ASTTraversalException {
        this.pop();
    }



    @Override
    public void afterNative(Native hc) throws ASTTraversalException {
        this.pop();
    }



    @Override
    public void afterIdentifier(Identifier identifier) throws ASTTraversalException {
        this.pop();
    }



    @Override
    public void afterListLiteral(ListLiteral list) throws ASTTraversalException {
        this.pop();
    }



    @Override
    public void afterLiteral(Literal literal) throws ASTTraversalException {
        this.pop();
    }



    @Override
    public void afterOperatorCall(OperatorCall call) throws ASTTraversalException {
        this.pop();
    }



    @Override
    public void afterParameter(Parameter param) throws ASTTraversalException {
        this.pop();
    }



    @Override
    public void afterResolvable(ResolvableIdentifier id) throws ASTTraversalException {
        this.pop();
    }



    @Override
    public void afterRoot(Root root) throws ASTTraversalException {
        this.pop();
    }



    @Override
    public void afterVarAccess(VarAccess access) throws ASTTraversalException {
        this.pop();
    }



    @Override
    public void afterVarDecl(VarDeclaration decl) throws ASTTraversalException {
        this.pop();
    }
    
    
    
    @Override
    public void afterDelete(Delete delete) throws ASTTraversalException {
        this.pop();
    }
    
    
    @Override
    public void afterListParameter(ListParameter param) throws ASTTraversalException {
        this.pop();
    }
    
    
    
    @Override
    public void afterFunctionParameter(FunctionParameter param)
            throws ASTTraversalException {
        this.pop();
    }
}
