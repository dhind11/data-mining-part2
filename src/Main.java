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
        String [] inst_disc = pretraitement.Discretisation(ds, 0,4);
        System.out.print("\nDiscretisation (methode2) : \t");
        for (int i = 0; i < inst_disc.length ; i++) {
            System.out.print("\t" + inst_disc[i]);
        }

        //************************************************ create dataset_disc ********************************************
        ArrayList<String[]> dataset_disc = new ArrayList<>(); // dataset discretisée
        for(int i = 0; i<ds.Nb_Instances(); i++){
            dataset_disc.add(pretraitement.Discretisation(ds,i,4));
        }

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
        System.out.println("\nmin-sup="+ds.Nb_Instances()*20/100);
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

        //************************************************ Naive Bayesian Classifier********************************************
        double[] naive_bayesian=pretraitement.Naive_Bayesian(train_test.train_disc,4,train_test.test_disc.get(0));
        //String mot="I21";
        //System.out.println(Character.getNumericValue(mot.charAt(2))+1);
    }

}}
