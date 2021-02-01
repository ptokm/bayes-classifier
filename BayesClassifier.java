package bayes;

import javax.swing.WindowConstants;

public class BayesClassifier {
    public static void main(String[] args) {
        Frame frame = new Frame("Bayes Classifier");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setBounds(600, 200, 750, 600); //.setBounds(int x, int y, int width, int height)
        frame.show();
    } 
}
