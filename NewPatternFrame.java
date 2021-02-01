package bayes;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;



public class NewPatternFrame extends JFrame {
    //GUI
    private JButton calculate = null;
    private JLabel info = null;
    private JPanel panel = null, labelPanel = null,listPanel = null, footerPanel = null;
    private JTextField textFields[] = null;
    //For classification
    private int dimension;
    private ArrayList<String> features = null;
    private BayesAlgorithm bayes = null;
    private ArrayList <ArrayList <String>> patterns = null;
    private ArrayList <String> classes = null;
    private ArrayList <ArrayList <String>> featuresOfNewPattern = null;
    private ArrayList <String> characteristics = null;
    
    NewPatternFrame(String title,int dimension,ArrayList <ArrayList <String>> patterns,ArrayList <String> classes,ArrayList <String> characteristics) { 
        //Configuration of display window
        super(title);
        setSize(400,600);
        setResizable(false);
        this.getContentPane().setBackground(Color.CYAN);
        
        //Fields initialization
        bayes = new BayesAlgorithm();
        features = new ArrayList <>();
        featuresOfNewPattern = new ArrayList <>();
        
        //Assigning values ​​to fields for classification
        this.dimension = dimension;
        this.patterns = patterns;
        this.classes = classes;
        this.characteristics = characteristics;
        
        addFeatures();
        
        panel = new JPanel(new GridLayout(3,1));
        add(panel);
        
        
        labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout());
        panel.add(labelPanel);
        
        listPanel = new JPanel();
        listPanel.setLayout(new GridLayout(featuresOfNewPattern.size(),2));
        panel.add(new JScrollPane(listPanel));
        
        footerPanel = new JPanel();
        footerPanel.setLayout(new FlowLayout());
        panel.add(footerPanel);
        
        info = new JLabel("<html><h2 align = 'center'>Enter for each feature of new pattern, at most one of the displayed values.</h2>"+
                "<h2 align = 'center'>Is case-sensitive</h2></html>");
        labelPanel.add(info);
         
        String hints[] = new String[featuresOfNewPattern.size()];
        for (int i=0; i<featuresOfNewPattern.size(); i++) {
            hints[i] = "<html><h4>For <i><u>"+characteristics.get(i)+"</u></i> give values:<br> [";
            for (int j=0; j<featuresOfNewPattern.get(i).size(); j++) {
                if (j != featuresOfNewPattern.get(i).size() - 1)
                    hints[i]+= featuresOfNewPattern.get(i).get(j) + " | ";
                else 
                    hints[i]+= featuresOfNewPattern.get(i).get(j);
            }
            hints[i] += "]</h4></html>";
        }
        
        
        textFields = new JTextField[this.dimension - 1];
        for (int i=0; i<dimension - 1; i++) {
            JPanel small_panel = new JPanel();
            small_panel.setSize( 200, 400 );
            small_panel.setLayout(new GridLayout(1,2));
            JLabel hint = new JLabel(hints[i]);
            small_panel.add(hint);
            textFields[i] = new JTextField("");
            textFields[i].setSize(200,200);
            small_panel.add(textFields[i]); 
            listPanel.add(small_panel);
        }
        
        calculate = new JButton("CLASSIFICATION");
        calculate.setBackground(Color.LIGHT_GRAY);
        footerPanel.add(calculate);
        
        calculate.addActionListener((ActionEvent e) -> {
            calculate(); 
        });
    }
       
    private ArrayList <String> addFeature(String feature, ArrayList <String> features) {
        boolean flag = true;
        for (int i=0; i<features.size(); i++) {
            if(features.get(i).equals(feature)) {
                flag = false;
                break;
            }
        }
        if (flag)
            features.add(feature);
        
        return features;
    }
    
    private void addFeatures() {
        for (int i=0; i<dimension -1; i++) {
            ArrayList <String> f = new ArrayList <>();
            for (int j=0; j<patterns.size();j++) {
                if (j==0)
                    f.add(patterns.get(j).get(i));
                else
                    f = addFeature(patterns.get(j).get(i),f);
            }
            featuresOfNewPattern.add(f);
        }
    }
    
    private void addFeature(String feature) {
        boolean flag = false;
        for (int i=0; i<features.size(); i++) {
            if (features.get(i).equals(feature))
                flag = true;
        }
        
        if (!flag)
            features.add(feature);
    }
    
    //Take the features of pattern that user want to classify
    public void calculate() {  
        boolean canClassify = true;
        features = new ArrayList <>();
        for (int i=0; i<dimension - 1; i++) {
            if (! textFields[i].getText().equals("")) {
                boolean valid = isValidFeature(textFields[i].getText(),i);
                if (valid){
                    System.out.println("Feature: "+textFields[i].getText()+" in dimension --> "+i);
                    bayes.findProbabilityOfCharacteristicInSpecificDimension(textFields[i].getText(),i, this.dimension,this.patterns,this.classes,this.characteristics);
                    addFeature(textFields[i].getText());
                }
                else {
                    canClassify = false;
                    break;
                }
            }
        }
        
        if (canClassify) {
            if (features.isEmpty())
                Frame.setTextLabel("<html><br><h2>Need to give features for a new pattern</h2></html>");
            else {
                System.out.println("Features --> "+features);
                //Update the fields (in BayesClassifier.java) to be ready for classification
                bayes.setFeatureToSearch(features,this.dimension,this.patterns,this.classes,this.characteristics);
                bayes.continueClassification();
            }    
        }else {
            Frame.setTextLabel("<html><br><h2>Wrong valid values for characteristics</h2></html>");
        }  
    }
    
    private boolean isValidFeature(String feature, int index) {
        for (int i=0; i<featuresOfNewPattern.get(index).size(); i++)
            if (featuresOfNewPattern.get(index).get(i).equals(feature)) {
                return true;
            }
        return false;
    }
}
