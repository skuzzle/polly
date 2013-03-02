package de.skuzzle.polly.console;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import sun.security.krb5.internal.PAData;

import de.skuzzle.polly.core.parser.Evaluator;
import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.DeclarationReader;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ExpASTVisualizer;
import de.skuzzle.polly.process.KillingProcessWatcher;
import de.skuzzle.polly.process.ProcessExecutor;
import de.skuzzle.polly.tools.strings.StringUtils;



public class Main {
    
    private final static String DOT_PATH = 
            "C:\\Program Files (x86)\\Graphviz 2.28\\bin\\dot.exe";
    
    private final static File DECLARATION_FOLDER = new File("decls");
    
    private final static FileFilter DECLARATION_FILTER = new FileFilter() {
        
        @Override
        public boolean accept(File pathname) {
            return pathname.getName().toLowerCase().endsWith(".decl");
        }
    };
    
    
    private final static Comparator<Problem> PROBLEM_COMP = new Comparator<ProblemReporter.Problem>() {

        @Override
        public int compare(Problem o1, Problem o2) {
            int r = o1.getType() - o2.getType();
            if (r == 0) {
                r = o1.getPosition().compareTo(o2.getPosition());
            }
            return r;
        }
    };
    
    
    
    private static void readDeclarations() throws IOException {
        for (final File file : DECLARATION_FOLDER.listFiles(DECLARATION_FILTER)) {
            DeclarationReader dr = null;
            try {
                final String nsName = file.getName().substring(
                        0, file.getName().length() - 5);
                Namespace ns = Namespace.forName(nsName);
                dr = new DeclarationReader(file, "ISO-8859-1", ns);
                dr.readAll();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (dr != null) {
                    dr.close();
                }
            }
        }
    }
    

    public static void main(String[] args) throws IOException, ASTTraversalException {
        
        Namespace.setDeclarationFolder(new File("decls"));
        
        readDeclarations();
        
        final BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        String nsName = "default";
        
        System.out.println("Welcome to PPC - Polly Parser Console");
        System.out.println("Just enter any valid polly expression, or type :q to exit.");
        System.out.println("Type :ns <name> to switch namespaces.");
        System.out.println();
        
        while (true) {
            Namespace ns = Namespace.forName(nsName);
            System.out.print(nsName + " > ");
            final String cmd = r.readLine();
            
            if (cmd.equals(":q")) {
                return;
            } else if (cmd.startsWith(":ns")) {
                final String[] parts = cmd.split(" ");
                if (parts.length != 2) {
                    System.out.println("Invalid parameters for ':ns'");
                } else {
                    nsName = parts[1];
                }
                
                continue;
            } else if (cmd.equals(":cns")) {
                //System.out.println();
                continue;
            } else if (cmd.equals(":vns")) {
                System.out.println("Namespace: " + nsName);
                System.out.println(ns.toString());
                continue;
            }
            
            final MultipleProblemReporter mpr = new MultipleProblemReporter();
            final Evaluator eval = new Evaluator(":result \">\" " + cmd, "ISO-8859-1", 
                mpr);
            
            try {
                eval.evaluate(ns, ns);
            } catch (Exception ad) {
                ad.printStackTrace();
            }
            
            if (eval.errorOccurred()) {
                
                System.out.println("    " + cmd);
                for (String indi : Position.indicatorStrings(mpr.problemPositions(), -12)) {
                    System.out.println("    " + indi);
                }
                
                formatProblems(mpr.getProblems(), -12);
            } else {
                System.out.println(eval.getRoot().toString());
            }
            
            if (eval.getRoot() != null){
                
                final ExpASTVisualizer av = new ExpASTVisualizer();
                av.visualize(eval.getRoot(), new PrintStream("lastAst.dot"), ns);
                
                ProcessExecutor pe = ProcessExecutor.getOsInstance(false);
                pe.addCommand(DOT_PATH);
                pe.addCommandsFromString("-Tpdf -o lastAst.pdf");
                pe.addCommand("lastAST.dot");
                pe.setProcessWatcher(new KillingProcessWatcher(10000, true));
                pe.start();
            }
        }
    }
    
    
    private static void formatProblems(Collection<Problem> problems, int offset) {
        final String[] types = new String[3];
        types[ProblemReporter.LEXICAL] = "Lexical";
        types[ProblemReporter.SYNTACTICAL] = "Syntactical";
        types[ProblemReporter.SEMATICAL] = "Semantical";
        int longestType = "Syntactical".length();
        int longestMsg = 0;
        
        for (final Problem problem : problems) {
            longestMsg = Math.max(longestMsg, problem.getMessage().length());
        }
        
        StringBuilder header = new StringBuilder();
        header.append("Type");
        StringUtils.padSpaces(longestType, header.length(), header);
        header.append(" | Message");
        StringUtils.padSpaces(6 + longestType + longestMsg, header.length(), header);
        header.append(" | Position");
        System.out.println(header.toString());
        for (int i = 0; i < header.length(); ++i) {
            System.out.print("-");
        }
        System.out.println();
        
        List<Problem> probs = new ArrayList<ProblemReporter.Problem>(problems);
        Collections.sort(probs, PROBLEM_COMP);
        for (final Problem problem : probs) {
            final StringBuilder b = new StringBuilder();
            b.append(types[problem.getType()]);
            StringUtils.padSpaces(longestType, b.length(), b);
            b.append(" | ");
            b.append(problem.getMessage());
            StringUtils.padSpaces(6 + longestType + longestMsg, b.length(), b);
            b.append(" | ");
            b.append(problem.getPosition().offset(offset).toString());
            System.out.println(b.toString());
        }
    }
}
