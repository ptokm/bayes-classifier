package bayes;

import java.util.ArrayList;
import javax.swing.JFrame;

public class BayesAlgorithm {
    private int dimension;
    private ArrayList <ArrayList <String>> patterns = null;
    private ArrayList <String> featuresToSearch = null; //The features of a new pattern that user want to classify
    private Double standardProbabilities[] = null; //Probabilities of classes in dataset
    private int countClasses[] = null; //Number of occurrences of each class in dataset 
    private ArrayList <String> classes = null; //Set of classes that each pattern belong
    private ArrayList <String> characteristics = null;
    
    BayesAlgorithm() {
        patterns = new ArrayList <>();
        classes = new ArrayList <>();
        characteristics = new ArrayList <>();
        dimension = -1;
    }
    
    private void addClass(String newClass) {
        boolean flag = true;
        for (int i=0; i<classes.size(); i++) {
            if (newClass.equals(classes.get(i))) {
                flag = false;
                break;
            }
        }
        //If class not already exists, add it in list
        if (flag)
            classes.add(newClass);
    }
    
    private void findClasses() {
        for (int i=0; i<patterns.size(); i++) {
            if (i==0)
                classes.add(patterns.get(i).get(dimension -1));
            else{
                addClass(patterns.get(i).get(dimension - 1));
            }
        }
    }
    
    public void prepareClassification() {
        classes = new ArrayList <>();
        findClasses();

        //In this frame, the user fills in the features of a new pattern that he wants to classify.
        //We also send the data we need to classify a new pattern because
        //when we return here from the frame, the data will be null.
        NewPatternFrame newPatternFrame = new NewPatternFrame("Features",this.dimension,this.patterns,this.classes,this.characteristics);
        newPatternFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        newPatternFrame.setBounds(700, 400, 800, 500);
        newPatternFrame.show();
    }
    
    //Check if all features that user give for new pattern
    //correspoding to the dataset that he has uploaded.
    private boolean canClassify() {
        int count[] = new int[featuresToSearch.size()];
        for (int i=0; i<featuresToSearch.size(); i++) {
            count [i] = 0;
            for (int j=0; j<patterns.size(); j++) {
                for (int k=0; k<dimension -1; k++) {
                    if (featuresToSearch.get(i).equals(patterns.get(j).get(k)))
                        count[i]++;
                }
            }
        }
        
        for (int i=0; i<featuresToSearch.size(); i++) {
            if (count[i] == 0)
                return false;
        }
        
        return true;
    }
       
    public void continueClassification() {
        boolean flag = canClassify();
        if (!flag) {
            ResultsFrame output = new ResultsFrame("Results:");
            output.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            output.setBounds(800, 500, 500, 200);
            output.show();
            output.setTextLabel("<html><h2>There are no corresponding features in Dataset!</h2></html>");
            //Frame.setTextLabel("<html><h2>There are no corresponding features in Dataset!</h2></html>");
        }
        else {
            Frame.setTextLabel("<html><h2>Classification...</h2></html>");
            
            calculateUnchangedData(); 

            ArrayList <ArrayList <Double>> probabilities = new ArrayList <>();
            for (int i=0; i<featuresToSearch.size(); i++) {
                ArrayList <Double> list = new ArrayList<>();
                double[] result = calculateProbabilityOfFeature(featuresToSearch.get(i), countClasses);
                
                for (int j=0; j<result.length; j++)
                    list.add(result[j]);
                probabilities.add(list);
            }

            double result[] = new double[classes.size()];
            
           
            for (int i=0; i<classes.size(); i++) {
                result[i] = 1.0;
                for (int j=0; j<probabilities.size(); j++) {
                    result[i] *= probabilities.get(j).get(i);
                }
                result[i] *= standardProbabilities[i];
            }
            
            double paronomastis = 0;
            for (int i=0; i<result.length; i++)
                paronomastis += result[i];
            
            /*String probs = "<html><ol>";
            for (int i=0; i<result.length; i++) {
                String probability = String.format("%.3f", (result[i]/denominator));
                probs += "<h3>p("+classes.get(i)+"|"+featuresToSearch.get(0)+") = "+probability+"</h3>";
            }
            probs+="</ol></html>";*/
            double prob[] = new double[result.length];
            for (int i=0; i<result.length; i++) {
                prob[i]= result[i] / paronomastis;
            }
            
            int maxclassprob = 0;
            double maxprob = prob[0];
            for (short i=0; i<prob.length; i++)
                if (prob[i] > maxprob){
                    maxprob = prob[i];
                    maxclassprob = i;
                }
            
            String max = String.format("%.2f", maxprob);
            String probs = "<html><br/>The pattern with features: <br><ul>";
            for (short i=0; i<featuresToSearch.size(); i++){
                probs += "<li>"+featuresToSearch.get(i)+"</li>";
            }
            probs += "</ul>has more probability to be in <u>"+classes.get(maxclassprob)+"</u> class with "+max+" / 1</h2></html>";
            
            ResultsFrame output = new ResultsFrame("Results:");
            output.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            output.setBounds(800, 500, 500, 200);
            output.show();
            output.setTextLabel(probs);
        }
    }
    
    //Calculate the probability for specific class in dataset
    /*private double probability(String currentClass) {
        int count = 0;
        for (int i=0; i<patterns.size(); i++) {
            if (patterns.get(i).get(dimension -1).equals(currentClass))
                count++;
        }
        return (double)count/patterns.size();
    }*/
    
    private double probability(int counter) {
        return (double)counter / patterns.size();
    }
    
    //Calculate the number of appearances for specific class in dataset
    private int calcCountClass(String currentClass) {
        int count = 0;
        for (int i=0; i<patterns.size(); i++) {
            if (patterns.get(i).get(dimension -1).equals(currentClass))
                count++;
        }
        return count;
    }
    
    //Store the probabilities and counters for each class in dataset
    public void calculateUnchangedData() {
        countClasses = new int[classes.size()];
        for (int i=0; i<classes.size(); i++) {
            countClasses[i] = calcCountClass(classes.get(i));
        }
        
        standardProbabilities = new Double[classes.size()];
        for (int i=0; i<classes.size(); i++) {
            //standardProbabilities[i] = probability(classes.get(i));
            standardProbabilities[i] = probability(countClasses[i]);
        }   
    }
    
    public void findProbabilityOfCharacteristicInSpecificDimension(String feature, int d,int dimension,ArrayList <ArrayList <String>> patterns,ArrayList <String> classes, ArrayList <String> charact) {
        countClasses = new int[classes.size()];
        for (int i=0; i<classes.size(); i++) {
            countClasses[i] = calcCountClass(classes.get(i));
        }
        
        standardProbabilities = new Double[classes.size()];
        for (int i=0; i<classes.size(); i++) {
            //standardProbabilities[i] = probability(classes.get(i));
            standardProbabilities[i] = probability(countClasses[i]);
        }   
        
        int countForClasses[] = new int[classes.size()];
        boolean flagSmoothing = false;
        
        for (int i=0; i<classes.size(); i++) {
            countForClasses[i] = 0;
                for (int k=0; k<patterns.size(); k++) {
                    if (patterns.get(k).get(d).equals(feature)) {
                        //System.out.println("For dimension with name: "+characteristics.get(j));
                        if (patterns.get(k).get(dimension -1).equals(classes.get(i))) {
                            countForClasses[i]++;
                        }
                    }
                }
        }
        
        System.out.println("##$% "+countForClasses[1]);
        int classForSmoothing = -1;
        
        //If there is a zero probability in some class, 
        //it will do smoothing for that class
        for (int i=0; i<classes.size(); i++) {
            if (countForClasses[i] == 0) {
                countForClasses[i] = 1;
                flagSmoothing = true;
                classForSmoothing = i;
            }
        }
        
        System.out.println("Continue with "+countClasses.length+" classses ");
        double result[] = new double[classes.size()];
        for (int i=0; i<classes.size(); i++) {
            if (flagSmoothing && classForSmoothing == i){
               result[i] = ((double)countForClasses[i] / (countClasses[i] + classes.size()));
               flagSmoothing = true;
            }
            else
                result[i] = ((double)countForClasses[i] / countClasses[i]);
        }
        
        System.out.println("NEWWW");
        for (int i=0; i<result.length; i++) {
            System.out.println("Result ["+i+"] = "+result[i]);
        }
    }
    //Calculate the probability of one feature
    public double[] calculateProbabilityOfFeature(String feature, int countClasses[]) {
        int countForClasses[] = new int[classes.size()];
        boolean flagSmoothing = false;
        
        for (int i=0; i<classes.size(); i++) {
            countForClasses[i] = 0;
            for (int j=0; j<dimension -1; j++) {
                for (int k=0; k<patterns.size(); k++) {
                    if (patterns.get(k).get(j).equals(feature)) {
                        //System.out.println("For dimension with name: "+characteristics.get(j));
                        if (patterns.get(k).get(dimension -1).equals(classes.get(i))) {
                            countForClasses[i]++;
                        }
                    }
                }
            }
        }
      
        int classForSmoothing = -1;
        
        //If there is a zero probability in some class, 
        //it will do smoothing for that class
        for (int i=0; i<classes.size(); i++) {
            if (countForClasses[i] == 0) {
                countForClasses[i] = 1;
                flagSmoothing = true;
                classForSmoothing = i;
            }
        }
        
        double result[] = new double[classes.size()];
        for (int i=0; i<classes.size(); i++) {
            if (flagSmoothing && classForSmoothing == i){
               result[i] = ((double)countForClasses[i] / (countClasses[i] + classes.size()));
               flagSmoothing = true;
            }
            else
                result[i] = ((double)countForClasses[i] / countClasses[i]);
        }
        
        System.out.println("Probabilities");
        for (int i=0; i<classes.size(); i++) {
            System.out.println(""+result[i]+"for class "+classes.get(i));
        }
        /*System.out.println("OLDD");
        for (int i=0; i<result.length; i++) {
            System.out.println("Result ["+i+"] = "+result[i]);
        }*/
        return result;
    }
        
    /**
     * @param featuresToSearch
     * @param dimension
     * @param patterns
     * @param classes
     * @param characteristics
     */
    public void setFeatureToSearch(ArrayList <String> featuresToSearch,int dimension,ArrayList <ArrayList <String>> patterns,ArrayList <String> classes, ArrayList <String> characteristics) {
        this.dimension = dimension;
        this.patterns = patterns;
        /*this.featuresToSearch = new ArrayList <>();
        for (int i=0; i<featuresToSearch.size(); i++)
            if (!featuresToSearch.get(i).equals(""))
                featuresToSearch.add(featuresToSearch.get(i));*/
        this.featuresToSearch = featuresToSearch;
        this.classes = classes;
        this.characteristics = characteristics;
    }
    
    public ArrayList <ArrayList <String>> getDataset() {
        return patterns;
    }

    public void setDataset(ArrayList <ArrayList <String>> dataset) {
        this.patterns = dataset;
    }
    
    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public ArrayList <String> getFeatureToSearch() {
        return featuresToSearch;
    }

    /**
     * @param characteristics the characteristics to set
     */
    public void setCharacteristics(ArrayList <String> characteristics) {
        this.characteristics = characteristics;
    }
}
