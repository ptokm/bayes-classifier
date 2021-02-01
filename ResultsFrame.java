package bayes;

import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ResultsFrame extends JFrame {
    //GUI
    private static JLabel info = null;
    ResultsFrame(String title) {
        //Configuration of display window
        super(title);
        setSize(650,550); //setSize(width,height)
        setResizable(false);
        this.getContentPane().setBackground(Color.lightGray);
        //A flow layout arranges components in a left-to-right flow, much like lines of text in a paragraph
        setLayout(new FlowLayout());
       
        //Instructions for users
        info = new JLabel();
        this.add(info);
        
    }
    
    public void setTextLabel(String text){
        info.setText(text);
    }
}
