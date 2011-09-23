package de.skuzzle.polly.installer.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;


public class ScriptParser {

    private String input;
    
    public ScriptParser(String input) {
        this.input = input;
    }
    
    
    
    public ScriptParser(File input) throws IOException {
        InputStream in = new FileInputStream(input);
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        String line = null;
        this.input = "";
        while ((line = r.readLine()) != null) {
            this.input += line + "\n";
        }
        r.close();
        in.close();
    }
    
    
    public List<ScriptAction> parse() throws ScriptException {
        String[] lines = this.input.split("\n");
        List<ScriptAction> result = new LinkedList<ScriptAction>();
        
        int i = 0;
        for (String line : lines) {
            ++i;
            if (!line.endsWith(";") && !line.startsWith("#")) {
                throw new ScriptException("Missing ';' in line " + i);
            }
            if (!line.startsWith("#")) {
                result.add(this.lineToAction(line.substring(0, line.length() - 1), i));
            }
        }
        return result;
    }
    
    
    private ScriptAction lineToAction(String line, int number) throws ScriptException {
        // COPYF from, to, [override];
        // SETPROP name, value;
        // INSTALL fileName;
        // UNZIP zipFile, destination

        ScriptAction a = null;
        if (line.startsWith("COPYF")) {
            a = new CopyFolderAction();
        } else if (line.startsWith("SETPROP")) {
            a = new SetConfigAction();
        } else if (line.startsWith("INSTALL")) {
            a = new InstallAction();
        } else if (line.startsWith("UNZIP")) {
            a = new UnzipAction();
        } else if (line.startsWith("DELETE")) {
            a = new DeleteFolderAction();
        } else {
            throw new ScriptException("Unknown command on line " + number + ": " + line);
        }
        
        String parts[] = line.split(",?\\s+");
        a.fromLine(parts, number);
        return a;
    }
}