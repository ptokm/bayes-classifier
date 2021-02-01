package bayes;

import java.awt.Color;
import java.awt.Event;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Frame extends JFrame {
    //Fields for GUI
    private MenuBar menuBar = null;
    private Menu menu = null, dataset = null, classify = null;
    private MenuItem[] menuItems = null, datasetItems = null, classifyItems = null;
    private static JLabel label = null;
    private final String information, about; 
    //Fields for classification
    private BayesAlgorithm bayes = null;
    private boolean canClassify, hasLoadDataset;
    
    Frame(String title) {
        //Configuration of display window
        super(title);
        setResizable(false);
        this.getContentPane().setBackground(Color.lightGray);
        //A flow layout arranges components in a left-to-right flow, much like lines of text in a paragraph
        setLayout(new FlowLayout());
        
        //Menu configuration
        menuBar = new MenuBar();
        menu = new Menu("MENU");
        dataset = new Menu("DATASETS");
        classify = new Menu("CLASSIFY");
        
        menuItems = new MenuItem[2];
        menuItems[0] = new MenuItem("Home");
        menuItems[1] = new MenuItem("About");
        for (short i=0; i<menuItems.length; i++) {
            menu.add(menuItems[i]);
        }
        
        datasetItems = new MenuItem[4];
        datasetItems[0] = new MenuItem("Buy PC or not");
        datasetItems[1] = new MenuItem("Male or Female");
        datasetItems[2] = new MenuItem("Play football or not");
        datasetItems[3] = new MenuItem("Load dataset");
        for (short i=0; i<datasetItems.length; i++)
            dataset.add(datasetItems[i]);
        
        classifyItems = new MenuItem[1];
        classifyItems[0] = new MenuItem("Classify");
        classify.add(classifyItems[0]);
        
        menuBar.add(menu);
        menuBar.add(dataset);
        menuBar.add(classify);
        setMenuBar(menuBar);
        
        information = "<html><br/><br/><br/><h1 align = 'center'><u>STEPS</u></h1>"+
                "<h3 align = 'center'>First, you need to load dataset from your system or "+
                "pick a standard dataset</h3><h3 align = 'center'>After, classify a new pattern which corresponds to that dataset</h3>"+
                "<br/><br/><br/><br/><br/><h1 align = 'center'><u>RULES FOR DATASET</u></h1><br/>"+
                "<ol><li>The first line will describe the features that each pattern will have</li>"+
                "<li>Each pattern must be only in one line</li>"+
                "<li>Each pattern must have the same number of features</li>"+
                "<li>Features of the pattern must be separated by comma</li>"+
                "<li>Blank features of patterns not allowed</li>"+
                "<li>The last feature of patterns should refer to the classes that new pattern will need to be classified</li>"+
                "<li>Each feature of pattern must have unique valid values</li>"+
                "</ol></html>";
        
        about = "<html><br/><br/><br/><br/><br/><br/><br/><br/>"+
                "<h2 align = 'center'>This application was implemented <br>"+
                "from Paraskevi Tokmakidou <br/>within the course Pattern Recognition,<br/>"+
                "in department of Informatics and Telecommunications<br>of the University of Ioannina<br/>"+
                "with responsible teacher Mr. Stavros Adam</h2></html>";
        
        //Instructions for users
        label = new JLabel();
        setTextLabel(information);
        this.add(label);
        
        //Fields initialization
        bayes = new BayesAlgorithm();
        this.canClassify = false;
        this.hasLoadDataset = false;
    }
    
    private void loadDataset() {
        bayes = new BayesAlgorithm();
        ArrayList <ArrayList <String>> patterns = new ArrayList <>();
        int dimension = -1, countPatterns = -1;
        ArrayList <String> characteristics = new ArrayList <>();
        
        setTextLabel("<html><h2>Make sure the first line of the file describes the features of the patterns</h2></html>");
        
        //Permisions for MAC devices to see files in Download folder
        System.setProperty("apple.awt.fileDialogForDirectories", "true");
        
        //Prompt the user to choose a .txt file from his system
        JFileChooser chooser=new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String filename=chooser.getSelectedFile().getAbsolutePath();
            try {
                FileReader file = new FileReader(filename);
                try (Scanner in = new Scanner(file)) {
                    //Read the file line-by-line
                    while(in.hasNextLine()) {
                        countPatterns++; 
                        
                        //Read the line and separate the features for the pattern
                        String line=in.nextLine();
                        String[] features = line.split(",");
                        
                        //Take the dimension from the first pattern 
                        //For the rest check if all other patterns have the same dimension with the first
                        if (countPatterns == 0) 
                            dimension = features.length;
                        else {
                            if (dimension != features.length) {
                                setTextLabel("<html><h2>Incorrect file formatting</h2></html>");
                                canClassify = false;
                                break;
                            }
                        }
                        
                        //After that, means that the file has correct formatting
                        //So we update the dimension field (in BayesAlgorithm.java)
                        //in order to be ready to classify a new pattern 
                        bayes.setDimension(dimension);
                        
                       //We save a description for each pattern attribute
                       if (countPatterns == 0)
                            for (int  i=0; i<dimension; i++)
                                characteristics.add(features[i]);
                        else {
                            //In each iteration it saves in list the corresponding pattern
                            ArrayList <String> list = new ArrayList <>();
                            for(int i=0; i<dimension; i++) {
                                list.add(features[i]);
                            }
                            patterns.add(list);
                        } 
                    }
                }
                if (canClassify) {
                    //Now dataset has all patterns
                    //So we update the dataset field (in BayesAlgorithm.java) in order to be ready to classify a new pattern
                    bayes.setDataset(patterns);
                    bayes.setCharacteristics(characteristics);
                    
                    //Classification can begin               
                    hasLoadDataset = true;
                    setTextLabel("<html><h2 align = 'center'>The data was uploaded correctly!"+
                                "<br><br><h3 align = 'center'>Now you can classificate a new pattern</h3></html>");
                
                    String text = "Want to classify now?";
                    String title = "You successfully load dataset";
                    int optionType = JOptionPane.OK_CANCEL_OPTION;
                    int result = JOptionPane.showConfirmDialog(null, text, title, optionType);
                    if (result == JOptionPane.OK_OPTION) {
                        setTextLabel("<html><h2 align = 'center'>Classification..</h2></html>");
                        bayes.prepareClassification();     
                    }
                }else {
                    String text = "Want to try load file again?";
                    String title = "Incorrect file formatting";
                    int optionType = JOptionPane.OK_CANCEL_OPTION;
                    int result = JOptionPane.showConfirmDialog(null, text, title, optionType);
                    if (result == JOptionPane.OK_OPTION) {
                        setTextLabel("<html><h2>Load dataset...</h2></html>");
                        loadDataset();     
                    }
                }
            }catch (FileNotFoundException | NumberFormatException ex) {
                    setTextLabel("<html><h2 align = 'center'>Something went wrong</h2></html>");
                    canClassify = false;
                    Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else {
            //The user clicks on Cancel button
            //when we suggest him to select a file from his system
            setTextLabel("<html><br/><br/><h2 align = 'center'>Data upload canceled</h2></html>");
            
            String text = "Want to load file now?";
            String title = "You cancelled loading..";
            int optionType = JOptionPane.OK_CANCEL_OPTION;
            int result = JOptionPane.showConfirmDialog(null, text, title, optionType);
            if (result == JOptionPane.OK_OPTION) {
                setTextLabel("<html><h2>Load dataset...</h2></html>");
                loadDataset();     
            }
        }
    }
    
    private void loadMaleOrFemale() {
        ArrayList <ArrayList <String>> patterns = new ArrayList <>();
        ArrayList <String> characteristics = new ArrayList <>();
        int dimension = 5;
        
        ArrayList <String> list0 = new ArrayList <>();
        list0.add("Drew");  list0.add("no"); list0.add("blue"); list0.add("short"); list0.add("male");
        
        ArrayList <String> list1 = new ArrayList <>();
        list1.add("Claudia"); list1.add("yes"); list1.add("brown"); list1.add("long");  list1.add("female");
       
        ArrayList <String> list2 = new ArrayList <>();
        list2.add("Drew");  list2.add("no");    list2.add("blue");  list2.add("long");  list2.add("female");
        
        ArrayList <String> list3 = new ArrayList <>();
        list3.add("Drew");  list3.add("no");    list3.add("blue");  list3.add("long");  list3.add("female");
        
        ArrayList <String> list4 = new ArrayList <>();
        list4.add("Alberto");  list4.add("yes");    list4.add("brown");  list4.add("short");  list4.add("male");
        
        ArrayList <String> list5 = new ArrayList <>();
        list5.add("Karin");  list5.add("no");    list5.add("blue");  list5.add("long");  list5.add("female");

        ArrayList <String> list6 = new ArrayList <>();
        list6.add("Nina");  list6.add("yes");    list6.add("brown");  list6.add("short");  list6.add("female");
        
        ArrayList <String> list7 = new ArrayList <>();
        list7.add("Sergio");  list7.add("yes");    list7.add("blue");  list7.add("long");  list7.add("male");
        
        patterns.add(list0);    patterns.add(list1);  patterns.add(list2);  patterns.add(list3); patterns.add(list4); patterns.add(list5);  patterns.add(list6);    patterns.add(list7);
        characteristics.add("name"); characteristics.add("over_170_cm");    characteristics.add("eye"); characteristics.add("hair_length"); characteristics.add("sex");
        
        //After that, means that the file has correct formatting
        //So we update the dimension field (in Bayes.java)
        //in order to be ready to classify a new pattern 
        bayes.setDimension(dimension);
        bayes.setDataset(patterns);
        bayes.setCharacteristics(characteristics);
        
        canClassify = true;
        hasLoadDataset = true;
                
        setTextLabel("<html><h2 align = 'center'>The data was uploaded correctly!"+
                                "<br><br><h3 align = 'center'>Now you can classify a new pattern</h3></html>");
        
        String text = "Want to classify now?";
        String title = "You successfully load dataset";
        int optionType = JOptionPane.OK_CANCEL_OPTION;
        int result = JOptionPane.showConfirmDialog(null, text, title, optionType);
        if (result == JOptionPane.OK_OPTION) {
            bayes.prepareClassification();     
        }else
            setTextLabel("<html><h2 align = 'center'>The data was uploaded correctly!"+
                                "<br><br><h3 align = 'center'>Now you can classify a new pattern</h3></html>");
    }
    
    private void loadFootball() {
        ArrayList <ArrayList <String>> patterns = new ArrayList <>();
        ArrayList <String> characteristics = new ArrayList <>();
        int dimension = 5;
        
        characteristics.add("outlook");  characteristics.add("temperature"); characteristics.add("humidity"); characteristics.add("windy"); characteristics.add("play");
        
        ArrayList <String> list0 = new ArrayList <>();
        list0.add("sunny"); list0.add("hot"); list0.add("high"); list0.add("false");  list0.add("no");
       
        ArrayList <String> list1 = new ArrayList <>();
        list1.add("sunny"); list1.add("hot"); list1.add("high"); list1.add("true");  list1.add("no");
        
        ArrayList <String> list2 = new ArrayList <>();
        list2.add("overcast");  list2.add("hot");    list2.add("high");  list2.add("false");  list2.add("yes");
        
        ArrayList <String> list3 = new ArrayList <>();
        list3.add("rainy");  list3.add("mild");    list3.add("high");  list3.add("false");  list3.add("yes");
        
        ArrayList <String> list4 = new ArrayList <>();
        list4.add("rainy");  list4.add("cool");    list4.add("normal");  list4.add("false");  list4.add("yes");
        
        ArrayList <String> list5 = new ArrayList <>();
        list5.add("rainy");  list5.add("cool");    list5.add("normal");  list5.add("true");  list5.add("no");

        ArrayList <String> list6 = new ArrayList <>();
        list6.add("overcast");  list6.add("cool");    list6.add("normal");  list6.add("true");  list6.add("yes");
        
        ArrayList <String> list7 = new ArrayList <>();
        list7.add("sunny");  list7.add("mild");    list7.add("high");  list7.add("false");  list7.add("no");
        
        ArrayList <String> list8 = new ArrayList <>();
        list8.add("sunny"); list8.add("cool"); list8.add("normal"); list8.add("false");  list8.add("yes");
       
        ArrayList <String> list9 = new ArrayList <>();
        list9.add("rainy"); list9.add("mild"); list9.add("normal"); list9.add("false");  list9.add("yes");
        
        ArrayList <String> list10 = new ArrayList <>();
        list10.add("sunny");  list10.add("mild");    list10.add("normal");  list10.add("true");  list10.add("yes");
        
        ArrayList <String> list11 = new ArrayList <>();
        list11.add("overcast");  list11.add("mild");    list11.add("high");  list11.add("true");  list11.add("yes");
        
        ArrayList <String> list12 = new ArrayList <>();
        list12.add("overcast");  list12.add("hot");    list12.add("normal");  list12.add("false");  list12.add("yes");
        
        ArrayList <String> list13 = new ArrayList <>();
        list13.add("rainy");  list13.add("mild");    list13.add("high");  list13.add("true");  list13.add("no");
        
        patterns.add(list0);    patterns.add(list1);    patterns.add(list2);  patterns.add(list3); patterns.add(list4); patterns.add(list5);  patterns.add(list6);    patterns.add(list7);
        patterns.add(list8);    patterns.add(list9);    patterns.add(list10); patterns.add(list11);    patterns.add(list12);    patterns.add(list13); 
                        
        //After that, means that the file has correct formatting
        //So we update the dimension field (in Bayes.java)
        //in order to be ready to classify a new pattern 
        bayes.setDimension(dimension);
        bayes.setDataset(patterns);
        bayes.setCharacteristics(characteristics);
        
        canClassify = true;
        hasLoadDataset = true;
                
        setTextLabel("<html><h2 align = 'center'>The data was uploaded correctly!"+
                                "<br><br><h3 align = 'center'>Now you can classificate a new pattern</h3></html>");
                
        String text = "Want to classify now?";
        String title = "You successfully load dataset";
        int optionType = JOptionPane.OK_CANCEL_OPTION;
        int result = JOptionPane.showConfirmDialog(null, text, title, optionType);
        if (result == JOptionPane.OK_OPTION) {
            bayes.prepareClassification();     
        }
    }
    
    private void loadBuyPC() {
        ArrayList <ArrayList <String>> patterns = new ArrayList <>();
        ArrayList <String> characteristics = new ArrayList <>();
        int dimension = 5;
        
        characteristics.add("age");  characteristics.add("income"); characteristics.add("student_status"); characteristics.add("creditworthiness"); characteristics.add("buy_PC");
        
        ArrayList <String> list0 = new ArrayList <>();
        list0.add("young"); list0.add("high"); list0.add("false"); list0.add("good");  list0.add("no");
       
        ArrayList <String> list1 = new ArrayList <>();
        list1.add("young"); list1.add("high"); list1.add("false"); list1.add("exceptional");  list1.add("no");
        
        ArrayList <String> list2 = new ArrayList <>();
        list2.add("mature");  list2.add("high");    list2.add("false");  list2.add("good");  list2.add("yes");
        
        ArrayList <String> list3 = new ArrayList <>();
        list3.add("adult");  list3.add("medium");    list3.add("false");  list3.add("good");  list3.add("yes");
        
        ArrayList <String> list4 = new ArrayList <>();
        list4.add("adult");  list4.add("low");    list4.add("true");  list4.add("good");  list4.add("yes");
        
        ArrayList <String> list5 = new ArrayList <>();
        list5.add("adult");  list5.add("low");    list5.add("true");  list5.add("exceptional");  list5.add("no");

        ArrayList <String> list6 = new ArrayList <>();
        list6.add("mature");  list6.add("low");    list6.add("true");  list6.add("exceptional");  list6.add("yes");
        
        ArrayList <String> list7 = new ArrayList <>();
        list7.add("young");  list7.add("medium");    list7.add("false");  list7.add("good");  list7.add("no");
        
        ArrayList <String> list8 = new ArrayList <>();
        list8.add("young"); list8.add("low"); list8.add("true"); list8.add("good");  list8.add("yes");
       
        ArrayList <String> list9 = new ArrayList <>();
        list9.add("adult"); list9.add("medium"); list9.add("true"); list9.add("good");  list9.add("yes");
        
        ArrayList <String> list10 = new ArrayList <>();
        list10.add("young");  list10.add("medium");    list10.add("true");  list10.add("exceptional");  list10.add("yes");
        
        ArrayList <String> list11 = new ArrayList <>();
        list11.add("mature");  list11.add("medium");    list11.add("false");  list11.add("exceptional");  list11.add("yes");
        
        ArrayList <String> list12 = new ArrayList <>();
        list12.add("mature");  list12.add("high");    list12.add("true");  list12.add("good");  list12.add("yes");
        
        ArrayList <String> list13 = new ArrayList <>();
        list13.add("adult");  list13.add("medium");    list13.add("false");  list13.add("exceptional");  list13.add("no");
        
        patterns.add(list0);    patterns.add(list1);    patterns.add(list2);  patterns.add(list3); patterns.add(list4); patterns.add(list5);  patterns.add(list6);    patterns.add(list7);
        patterns.add(list8);    patterns.add(list9);    patterns.add(list10); patterns.add(list11);    patterns.add(list12);    patterns.add(list13); 
                        
        //After that, means that the file has correct formatting
        //So we update the dimension field (in Bayes.java)
        //in order to be ready to classify a new pattern 
        bayes.setDimension(dimension);
        bayes.setDataset(patterns);
        bayes.setCharacteristics(characteristics);
        
        canClassify = true;
        hasLoadDataset = true;
                
        setTextLabel("<html><h2 align = 'center'>The data was uploaded correctly!"+
                                "<br><br><h3 align = 'center'>Now you can classificate a new pattern</h3></html>");
                
        String text = "Want to classify now?";
        String title = "You successfully load dataset";
        int optionType = JOptionPane.OK_CANCEL_OPTION;
        int result = JOptionPane.showConfirmDialog(null, text, title, optionType);
        if (result == JOptionPane.OK_OPTION) {
            bayes.prepareClassification();     
        }     
    }
    
    //Action depending on the user's choice from the menu
    @Override
    public boolean action(Event event, Object obj) {
        if (event.target instanceof MenuItem) {
            String choice = (String)obj;
            switch (choice) {
                case "Home" -> {
                    setTextLabel(information);
                }
                case "Load dataset" -> {
                    if (hasLoadDataset) {
                        //promptUserLoadDataset("You have already load a file!", "Want to load another file?");
                        String text = "Want to load another file?";
                        String title = "You have already load a file!";
                        int optionType = JOptionPane.OK_CANCEL_OPTION;
                        int result = JOptionPane.showConfirmDialog(null, text, title, optionType);
                        if (result == JOptionPane.OK_OPTION) {
                            loadDataset();
                        }else 
                            setTextLabel("<html><h2>Cancel loading</h2></html>");
                    }else {
                        loadDataset();
                    }    
                }
                case "Male or Female" -> {
                   if (hasLoadDataset) {
                        String text = "Want to continue?";
                        String title = "You have already load a file!";
                        int optionType = JOptionPane.OK_CANCEL_OPTION;
                        int result = JOptionPane.showConfirmDialog(null, text, title, optionType);
                        if (result == JOptionPane.OK_OPTION) {
                            setTextLabel("<html><h2>Load dataset...</h2></html>");
                            loadMaleOrFemale();
                        }
                    }else {
                        setTextLabel("<html><h2>Load dataset...</h2></html>");
                        loadMaleOrFemale();
                    }  
                }
                case "Play football or not" -> {
                    if (hasLoadDataset) {
                        String text = "Want to continue?";
                        String title = "You have already load a file!";
                        int optionType = JOptionPane.OK_CANCEL_OPTION;
                        int result = JOptionPane.showConfirmDialog(null, text, title, optionType);
                        if (result == JOptionPane.OK_OPTION) {
                            setTextLabel("<html><h2>Load dataset...</h2></html>");
                            loadFootball();
                        }
                    }else {
                        setTextLabel("<html><h2>Load dataset...</h2></html>");
                        loadFootball();
                    }    
                }
                case "Buy PC or not" -> {
                    if (hasLoadDataset) {
                        String text = "Want to continue?";
                        String title = "You have already load a file!";
                        int optionType = JOptionPane.OK_CANCEL_OPTION;
                        int result = JOptionPane.showConfirmDialog(null, text, title, optionType);
                        if (result == JOptionPane.OK_OPTION) {
                            setTextLabel("<html><h2>Load dataset...</h2></html>");
                            loadBuyPC();
                        }
                    }else {
                        setTextLabel("<html><h2>Load dataset...</h2></html>");
                        loadBuyPC();
                    }    
                }
                case "Classify" -> {
                    if (canClassify) {
                        bayes.prepareClassification();
                    }else {
                        //promptUserLoadDataset("Need to load dataset", "Want to load file now?");
                        String text = "Want to load a file now?";
                        String title = "Need to load a dataset";
                        int optionType = JOptionPane.OK_CANCEL_OPTION;
                        int result = JOptionPane.showConfirmDialog(null, text, title, optionType);
                        if (result == JOptionPane.OK_OPTION) {
                            setTextLabel("<html><h2>Load dataset...</h2></html>");
                            loadDataset();     
                        }
                    }
                }
                case "About" -> {
                    setTextLabel(about);
                }
            }
        }
        else
            super.action(event,obj);
        return true;
    }
    
    public static void setTextLabel(String text){
        label.setText(text);
    }
    
    public void promptUserLoadDataset(String title,String message) {
        int optionType = JOptionPane.OK_CANCEL_OPTION;
        int result = JOptionPane.showConfirmDialog(null, message, title, optionType);
        if (result == JOptionPane.OK_OPTION) {
            setTextLabel("<html><h2>Load dataset...</h2></html>");
            loadDataset();     
        }
    }
    
}
