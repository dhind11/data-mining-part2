import org.jfree.data.xy.XYSeriesCollection;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class Main {

    public static void main (String[] args){

        Dataset ds = new Dataset("dataset/seeds_dataset.txt");
        //ds.ReadDataset("dataset/seeds_dataset.txt");


        int num_instance = 0;

        double [] inst_norm = pretraitement.MinMaxNormalization(ds, num_instance);
        //System.out.print("Normalisation (min-max) : \t");
        for (int i = 0; i < inst_norm.length-1 ; i++) {
            //System.out.print("\t" + inst_norm[i]);
        }
        //************************************************Discretization********************************************
        String [] inst_disc = pretraitement.Discretisation2(ds, 0,4);
        System.out.print("\nDiscretisation (methode2) : \t");
        for (int i = 0; i < inst_disc.length ; i++) {
            System.out.print("\t" + inst_disc[i]);
        }

        //************************************************ create dataset_disc ********************************************
        ArrayList<String[]> dataset_disc = new ArrayList<>(); // dataset discretisée
        for(int i = 0; i<ds.nb_Instances(); i++){
            dataset_disc.add(pretraitement.Discretisation2(ds,i,4));
        }
        //afficher dataset_discrétisé
        /*for (String[] instance:dataset_disc) {
            System.out.print("**\t");
            for (String val:instance) {
                System.out.print(val+"/");
            }
            System.out.println("\n");
        }*/

        //************************************************ calculate a support ********************************************
        String A="I21";
        int support=pretraitement.calcul_support(A,dataset_disc);
        System.out.println("\n"+"support("+A+")= "+support);
        //************************************************ create C1 ********************************************
        ArrayList<ElementC1> C1=new ArrayList<>();
        C1=pretraitement.Create_itemset1(ds, 20, 4);
        pretraitement.printC1(C1);
        //************************************************ create L1 ********************************************
        System.out.print("\n");
        ArrayList<String> L1= new ArrayList<>();
        L1=pretraitement.Create_L1(ds,20,C1);
        pretraitement.printL1(L1);
        //************************************************ create C2 ********************************************
        System.out.print("\n"+"C2= ");
        ArrayList<String[]> C2=new ArrayList<>();
        C2=pretraitement.Create_itemset2(ds,L1,4);
        pretraitement.printC2(C2);
        //************************************************ create L2 ********************************************
        System.out.print("\n"+"L2= ");
        ArrayList<String[]> L2=new ArrayList<>();
        L2=pretraitement.Create_L2(ds,20,C2);
        pretraitement.printL2(L2);
        System.out.println("\nmin-sup="+ds.nb_Instances()*20/100);
        //************************************************ Train Test Splitting ********************************************
        //train parameter here is an int that gives you the number of lines per class, if train=20, u take 20 each class
        Train_Test train_test= new Train_Test(ds,50,4);
        /*for (String[] instance:train_test.train_disc) {
            System.out.print("**\t");
            for (String val:instance) {
                System.out.print(val+"/");
            }
            System.out.println("\n");
        }*/
        /*int[] frequences=new int[3];
        for (int element:frequences) {
            element=0;
        }

        for (int i = 0; i < train_test.train_disc.size(); i++) {
            if(train_test.train_disc.get(i)[6].charAt(2)=='4'){
                if(train_test.train_disc.get(i)[7].equals("1")){frequences[0]++;}
                if(train_test.train_disc.get(i)[7].equals("2")){frequences[1]++;}
                if(train_test.train_disc.get(i)[7].equals("3")){frequences[2]++;}
            }

        }

        for (int element:frequences) {
            System.out.println(element);
        }
        System.out.println(frequences[0]+frequences[1]+frequences[2]);
        */
        //************************************************ Naive Bayesian Classifier********************************************
        for (int i = 0; i < train_test.test_disc.size(); i++) {
            int naive_bayesian=pretraitement.predict_naive_bayesian(train_test.train_disc,4,train_test.test_disc.get(i));
            for (String element:train_test.test_disc.get(i)) {
                System.out.print(element+"\t");
            }
            System.out.print("\n");
            System.out.println("Prediction="+naive_bayesian);
        }
        //int naive_bayesian=pretraitement.predict_naive_bayesian(train_test.train_disc,4,train_test.test_disc.get(50));
        /*for (String element:train_test.test_disc.get(50)) {
            System.out.print(element+"\t");
        }
        System.out.print("\n");
        System.out.println("Prediction="+naive_bayesian);*/

        //************************************************ Confusion Matrix ********************************************
        //double[][] confusion_matrix=pretraitement.Confusion_matrix(train_test.train_disc,4,train_test.test_disc);
    }

}
