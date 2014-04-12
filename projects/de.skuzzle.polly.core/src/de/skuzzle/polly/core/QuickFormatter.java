package de.skuzzle.polly.core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class QuickFormatter {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new QuickFormatter();
            }
        });
    }
    

    private final JFrame frame;
    private final TextArea input;
    
    
    public QuickFormatter() {
        this.frame = new JFrame();
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(new Dimension(400, 400));
        
        final JPanel p = new JPanel(new BorderLayout());
        this.input = new TextArea();
        p.add(this.input, BorderLayout.CENTER);
        this.frame.setContentPane(p);
        
        final JButton button = new JButton("Convert");
        p.add(button, BorderLayout.SOUTH);
        button.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                final String inp = input.getText();
                final StringBuilder b = new StringBuilder();
                try (final BufferedReader r = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(inp.getBytes())))) { 
                    String line = null;
                    
                    while ((line = r.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("#")) {
                            line = line.replaceAll("#+", "//");
                            b.append(line);
                            b.append("\n");
                        } else {
                            final int i = line.indexOf(" ");
                            line = line.substring(0, i);
                            b.append("public static String ");
                            b.append(line);
                            b.append(";\n");
                        }
                    }
                    
                    input.setText(b.toString());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        
        this.frame.setVisible(true);
        this.frame.pack();
    }
}
