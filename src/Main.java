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
        /************************************************ Discretization2 ********************************************/
        String [] inst_disc = pretraitement.Discretisation2(ds, 0,4);
        System.out.print("\nDiscretisation (methode2) : \t");
        for (int i = 0; i < inst_disc.length ; i++) {
            System.out.print("\t" + inst_disc[i]);
        }

        /************************************************ create dataset_disc ********************************************/
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

        /************************************************ calculate a support ********************************************/
        String A="I21";
        int support=pretraitement.calcul_support(A,dataset_disc);
        System.out.println("\n"+"support("+A+")= "+support);
        /************************************************ create C1 ********************************************/
        System.out.println("********************************************* C1 *********************************************");
        ArrayList<Itemset_Element> C1=new ArrayList<>();
        C1=pretraitement.Create_C1(dataset_disc);
        for(Itemset_Element element:C1){
            for (String item:element.items) {
                System.out.print(item+"\t");
            }
            System.out.print(":"+element.support+"/\t");
        }
        /************************************************ create L1 ********************************************/
        /*System.out.println("\n********************************************* L1 *********************************************");
        ArrayList<Itemset_Element> L1=new ArrayList<>();
        int min_sup=(ds.nb_Instances()*20)/100;
        L1=pretraitement.generate_Lk(C1,min_sup,1);
        for(Itemset_Element element:L1){
            for (String item:element.items) {
                System.out.print(item+"\t");
            }
            System.out.print(":"+element.support+"/\t");
        }*/
        /************************************************ create C2 ********************************************/
        /*System.out.println("\n********************************************* C2 *********************************************");
        ArrayList<Itemset_Element> C2=new ArrayList<>();
        C2=pretraitement.generate_Ck(dataset_disc,min_sup,L1,2);
        for(Itemset_Element element:C2){
            for (String item:element.items) {
                System.out.print(item+"\t");
            }
            System.out.print(":"+element.support+"/\t");
        }*/
        /************************************************ create L2 ********************************************/
        /*System.out.println("\n********************************************* L2 *********************************************");
        ArrayList<Itemset_Element> L2=new ArrayList<>();
        L2=pretraitement.generate_Lk(C2,min_sup,2);
        for(Itemset_Element element:L2){
            for (String item:element.items) {
                System.out.print(item+"\t");
            }
            System.out.print(":"+element.support+"/\t");
        }*/
        /******************************************************** APRIORI ****************************************************/
        System.out.println("\n********************************************* APRIORI *********************************************");
        ArrayList<Itemset_Element> apriori=new ArrayList<>();
        apriori=pretraitement.Apriori(dataset_disc,20);
        for(Itemset_Element element:apriori){
            for (String item:element.items) {
                System.out.print(item+"\t");
            }
            System.out.print(":"+element.support+"\n");
        }

        /************************************************ Train Test Splitting ********************************************/
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
        /************************************************ Naive Bayesian Classifier********************************************/
        /*for (int i = 0; i < train_test.test_disc.size(); i++) {
            int naive_bayesian=pretraitement.predict_naive_bayesian(train_test.train_disc,4,train_test.test_disc.get(i));
            for (String element:train_test.test_disc.get(i)) {
                System.out.print(element+"\t");
            }
            System.out.print("\n");
            System.out.println("Prediction="+naive_bayesian);
        }*/
        //int naive_bayesian=pretraitement.predict_naive_bayesian(train_test.train_disc,4,train_test.test_disc.get(50));
        /*for (String element:train_test.test_disc.get(50)) {
            System.out.print(element+"\t");
        }
        System.out.print("\n");
        System.out.println("Prediction="+naive_bayesian);*/

        /************************************************ Confusion Matrix ********************************************/
        //double[][] confusion_matrix=pretraitement.Confusion_matrix(train_test.train_disc,4,train_test.test_disc);
    }

}
