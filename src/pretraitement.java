import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.SortedSet;
import java.util.*;

public class pretraitement {
    /*************************************** Normalization ***********************************************************/
    private static double MinMaxNorm (double val, double min, double max){
        return (val - min)/(max-min) ;
    }

    public static double[] MinMaxNormalization (Dataset ds, int instance_index){
        double[] instance = ds.getInstance(instance_index);
        double[] new_instance = new double[instance.length];

        for (int i = 0; i < new_instance.length; i++) {
            new_instance[i] = MinMaxNorm(instance[i], Calcul.min(ds, i), Calcul.max(ds, i));
        }
        return new_instance;
    }

    private static double calcul_S (Dataset ds, int index_att, double moyenne){
        double S = 0;
        double[] column = ds.getColumn(index_att);
        //double moyenne = Calcul.moy(ds, index_att);

        for (int i = 0; i < column.length; i++) {
            S = S + Math.abs( column[i] -  moyenne);
        }
        S = S /  ds.nb_Instances(); // 1/N
        return S;
    }

    public static double[] ZscoreNormalization (Dataset ds, int instance_index){
        double[] instance = ds.getInstance(instance_index);
        double[] new_instance = new double[instance.length];

        for (int i = 0; i < new_instance.length; i++) {
            double moyenne = Calcul.moy(ds, i);
            new_instance[i] = (instance[i] - moyenne) / (calcul_S(ds, i, moyenne)) ;
        }
        return new_instance;
    }

    private static double[] normal_col(Dataset ds, int index_att, int norm){
        //Cette fonction retourne une colonne normalisée
        // norm = 0 -> MinMax normalization  ;  norm = 1 -> Z-Score normalization
        double [] column = ds.getColumn(index_att);
        double [] new_column = new double[column.length];
        if (norm == 0) { // MinMax normalization
            for (int i = 0; i < column.length; i++) {
                new_column[i] = MinMaxNormalization (ds, i)[index_att];
            }
        }
        else{ // Z-Score normalization
            for (int i = 0; i < column.length; i++) {
                new_column[i] = ZscoreNormalization (ds, i)[index_att];
            }
        }
        return  new_column;
    }

    public static double[] bornes(Dataset ds, double[] attribut_col , int Q){
        //fonction qui permet de calculer les bornes des intervalles pour la discretisation en classes d'effectifs egaux
        int nb_inst = (int) Math.ceil(ds.nb_Instances() / Q); //Nombre d'instances par intervalle.

        //double[] attribut_col = ds.getColumn(att_index);
        Arrays.sort(attribut_col);

        double[] bornes_sup = new double[Q]; //Tableau qui va contenir les bornes supérieurs des 'Q' intervalles

        for (int i = 1; i < Q; i++) {
            int indice_quantile = nb_inst * i - 1;
            bornes_sup[i-1] = attribut_col[indice_quantile];
        }
        bornes_sup[Q-1] = attribut_col[attribut_col.length-1]; //la borne sup du dernier intervalle

        return bornes_sup;
    }
    /*************************************** Discretisation ***********************************************************/
    public static String[] Discretisation (Dataset ds, int instance_index,  int Q){
        //Discretisation en classes d'effectifs egaux (Equal-frequency)

        //double [] instance = ds.getInstance(instance_index);
        double [] instance = pretraitement.MinMaxNormalization(ds, instance_index);
        String[] new_instance = new String[instance.length-1];

        for (int i = 0; i < instance.length - 1 ; i++) {
            double[] attribut_col = normal_col(ds, i, 0);
            double[] bornes_sup = bornes(ds, attribut_col, Q); //Recuperer les bornes superieur des intervalles de cette attributs
            double borne_sup = bornes_sup[0];
            int num_intervalle = 1;
            //Pour la valeur : trouver le num de l'intervalle (en comparant la valeur avec la borne sup des intervalles)
            while (borne_sup <= bornes_sup[Q-1]){   // bornes_sup[Q-1] : borne max
                if (instance[i] < borne_sup) {
                    break;
                }
                else if(instance[i] >= borne_sup) {
                    //System.out.print("I'm here");
                    borne_sup = bornes_sup[num_intervalle];
                    num_intervalle = num_intervalle + 1;
                }
            }
            new_instance[i] = "I" + (i+1) + num_intervalle;
        }
        return new_instance;
    }

    public static String[] Discretisation2 (Dataset ds, int instance_index,  int Q){
        //Discretisation en classes d'amplitudes égales
        double min = 0;
        double max = 1;
        double width = 1 / (double)Q;  // On considere l'intervalle [0,1]
        //System.out.print("width="+width);
        //double width = (min - max) / (double)Q;

        //double [] instance = ds.getInstance(instance_index);
        double [] instance = pretraitement.MinMaxNormalization(ds, instance_index);
        String[] new_instance = new String[instance.length-1];

        for (int i = 0; i < instance.length - 1 ; i++) {
            double borne_sup = min + width;
            int num_intervalle = 1;
            while (borne_sup <= max) {
                if (instance[i] < borne_sup || instance[i]==max) {
                    break;
                }
                else if(instance[i] >= borne_sup) {
                    borne_sup = borne_sup + width;
                    num_intervalle = num_intervalle + 1;
                }
            }
            //System.out.print("\t " + num_intervalle);
            new_instance[i] = "I" + (i+1) + num_intervalle;
        }
        return new_instance;
    }
    /*************************************** Itemsets ***********************************************************/
    public static int calcul_support (String A,ArrayList<String[]> dataset_disc){
        int support = 0;
        for(int i=0;i<dataset_disc.size();i++){
            String[] ligne=dataset_disc.get(i);
            for(int j=0;j<ligne.length;j++){
                if(ligne[j].equals(A)){support++;}
            }
        }
        return support;

    }

    public static int item_exist (ArrayList<ElementC1> C1, String element_id){
        for (int i = 0; i< C1.size(); i++) {
            if (C1.get(i).itemset.equals(element_id)){
                return i;
            }
        }
        return -1;
    }

    public static ArrayList<ElementC1> Create_itemset1 (Dataset ds, int min_sup, int Q){

        ArrayList<String[]> dataset_disc = new ArrayList<>(); // dataset discretisée
        for(int i = 0; i<ds.nb_Instances(); i++){
            dataset_disc.add(Discretisation2(ds,i,Q));
        }

        ArrayList<ElementC1> C1 = new ArrayList<>();
        for (int i = 0; i< dataset_disc.size(); i++){

            for(int j=0; j<dataset_disc.get(i).length; j++ ){

                String element_id = dataset_disc.get(i)[j];
                //ElementC1 element = new ElementC1(element_id,0);

                int position = item_exist(C1,element_id);
                if (position != -1){      // il existe
                    C1.get(position).freq ++;
                }
                else {
                    ElementC1 element = new ElementC1(element_id,1);
                    C1.add(element);
                }

            }

        }
        return(C1);
        //printC1(C1);

    }

    public static void printC1 (ArrayList<ElementC1> C1){
        System.out.print("C1=\t");
        for (int i = 0; i< C1.size(); i++){
            System.out.print("{" + C1.get(i).itemset + "}:"+ C1.get(i).freq +",");
        }
    }

    public static ArrayList<String> Create_L1(Dataset ds, int min_sup,ArrayList<ElementC1> C1){
        int support_min=(ds.nb_Instances())*min_sup/100;
        ArrayList<String> L1=new ArrayList<>();
        for(int i=0;i<C1.size();i++){
            if(C1.get(i).freq>=support_min){
                L1.add(C1.get(i).itemset);
            }
        }
        return L1;
    }

    public static void printL1 (ArrayList<String> L1){
        System.out.print("L1=\t");
        for(int j=0;j<L1.size();j++){
            System.out.print("{"+L1.get(j)+"}\t");
        }
    }

    public static int calcul_support2 (String[] A,ArrayList<String[]> dataset_disc){
        int support = 0;
        for(int i=0;i<dataset_disc.size();i++){
            String[] ligne=dataset_disc.get(i);
            boolean found=false;
            int j=0;
            while(j<(ligne.length)-1){
                int k=1;
                while(k<(ligne.length)) {
                    if (!ligne[j].equals(ligne[k])) {
                        if (A[0].equals(ligne[j]) || A[1].equals(ligne[j])) {
                            if (A[0].equals(ligne[k]) || A[1].equals(ligne[k])) {
                                support++;
                                found=true;
                            }
                        }
                    }
                    if(found){break;}
                    else{k++;}
                }
                if(found){break;}
                else{j++;}
            }
        }
        return support;
    }

    public static ArrayList<String[]> Create_itemset2(Dataset ds,ArrayList<String> L1,int Q){

        ArrayList<String[]> dataset_disc = new ArrayList<>(); // dataset discretisée
        for(int i = 0; i<ds.nb_Instances(); i++){
            dataset_disc.add(Discretisation2(ds,i,Q));
        }

        String I1,I2;
        ArrayList<String[]> C2=new ArrayList<>();

        for(int i=0;i<(L1.size())-1;i++){
            I1=L1.get(i);
            int j=i+1;
            while(j<L1.size()){
                I2=L1.get(j);
                //if(!(I1.equals(I2))){
                    String[] temp_itemset2=new String[3];
                    temp_itemset2[0]=I1;
                    temp_itemset2[1]=I2;
                    String[] A=new String[2];
                    A[0]=I1;
                    A[1]=I2;
                    temp_itemset2[2]=String.valueOf(calcul_support2(A,dataset_disc));
                    C2.add(temp_itemset2);
                //}
                j++;
            }
        }
        return (C2);
    }

    public static void printC2(ArrayList<String[]> C2){
        for(int i=0;i<C2.size();i++){
            System.out.print("{"+C2.get(i)[0]+","+C2.get(i)[1]+"}:"+C2.get(i)[2]+"\t");
        }
    }

    public static  ArrayList<String[]> sort_L2(ArrayList<String[]> L2){
        String item;
        ArrayList<String[]> new_L2=new ArrayList<>();
        ArrayList<String> items1=new ArrayList<>();
        for(int i=0;i<L2.size();i++) {
            item = L2.get(i)[0];
            if (!items1.contains(item)) {
                items1.add(item);
            }
        }
        Collections.sort(items1); //L2 is a set of elements like {x,y} sort the x parts and put them in items1
        SortedSet<String> items2= new TreeSet<>(); //will be used temporarely to sort the y in {x,y}
        //now items1 is sorted

        for(int i=0;i<items1.size();i++) { //parcourir les x et les y
            for (int j = 0; j < L2.size(); j++) {
                if (L2.get(j)[0].equals(items1.get(i))) {
                    item = L2.get(j)[1];
                    items2.add(item);
                    //j++;
                }
                else {
                    //items2 is automatically sorted
                    Iterator<String> iter = items2.iterator();
                    while (iter.hasNext()) {
                        String[] xy = new String[3];
                        xy[0] = items1.get(i);
                        xy[1] = iter.next();
                        new_L2.add(xy);
                    }
                    items2.clear();
                }

            }
        }
        return(new_L2);
    }

    public static ArrayList<String[]> Create_L2(Dataset ds, int min_sup,ArrayList<String[]> C2){
        int support_min=(ds.nb_Instances())*min_sup/100;
        ArrayList<String[]> L2=new ArrayList<>();
        for(int i=0;i<C2.size();i++){

            if(Integer.valueOf(C2.get(i)[2])>=support_min ){
                L2.add(C2.get(i));
                //System.out.println("items:"+C2.get(i)[0]+C2.get(i)[1]+" Support="+Integer.valueOf(C2.get(i)[2]));

            }
        }
        L2=sort_L2(L2);
        return (L2);
    }

    public static void printL2(ArrayList<String[]> L2){
        for(int i=0;i<L2.size();i++){
            //System.out.print("{"+L2.get(i)[0]+","+L2.get(i)[1]+"}:"+L2.get(i)[2]+"\t");
            System.out.print("{"+L2.get(i)[0]+","+L2.get(i)[1]+"}"+"\t");
        }
    }
    /*************************************** Naive Bayesian ***********************************************************/
    /*public static void proba_bayes(Dataset train,Dataset test){
        ArrayList<String[]> train_disc=new ArrayList<>();
        ArrayList<String[]> test_disc=new ArrayList<>();
        //DISCRETIZATON
        for(int i=0;i<train.nb_Instances()-10;i++){
            String[] instance = pretraitement.Discretisation(train, i,4);
            train_disc.add(instance);
        }
        for(int i=0;i<test.nb_Instances()-10;i++){
            String[] instance = pretraitement.Discretisation(test, i,4);
            test_disc.add(instance);
        }
        //calcul bayes
        //TEST: C1=20 C2=20 C3=20
        //TRAIN: C1=50 C2=50 C3=50
        int train_c=20;
        int test_c=50;
        float train_proba=50/150;//proba de la classe (C1 C2 C3) same prob bcs they are equal to 20
        for(int i=1;i<4;i++){
            System.out.println("P(C"+i+")= "+train_proba);
        }
    }*/

    public static int Nb_instance_per_classe(ArrayList<String[]> dataset_disc,int classe){
        int cpt=0;
        for(int i=0;i<dataset_disc.size();i++){
            int val;
            val=Integer.valueOf(dataset_disc.get(i)[7]);
            if(val==classe){cpt++;}
        }
        return cpt;
    }

    //proba P=|C(i,d)|/|D|   (d=D)
    public static double[] proba_per_classes(ArrayList<String[]> dataset_disc){
        //|Ci,d|
        double[] nb_instance_class = new double[3];
        for(int i=0;i<3;i++){
            nb_instance_class[i]=Nb_instance_per_classe(dataset_disc,i+1);
        }
        //|D|
        double D=dataset_disc.size();
        //|(Ci,d)|/|D|
        double[] classes_probas=new double[3];
        for(int i=0;i<3;i++){
            classes_probas[i]=(nb_instance_class[i]/D);
        }
        return(classes_probas);

    }

    //calcul probas per attribute
    public static ArrayList<double[]> probas_per_attribute(ArrayList<String[]> dataset_disc,int Q,int attribute_index){
        ArrayList<double[]> probas=new ArrayList<>();//Q=4, probas de [(C1,C1,C1,C1),(C2,C2,C2,C2),(C3,C3,C3,C3)]

        for (int i = 0; i < 3; i++) {
            double[] proba_class=new double[Q];//(C1,C1,C1,C1)
            for (int j = 0; j < Q; j++) {
                proba_class[j]=0;
            }
            probas.add(proba_class);
        }

        for(int i=0;i<dataset_disc.size();i++){
            //System.out.println(i);
            String value=dataset_disc.get(i)[attribute_index];
            int classe=Integer.valueOf(dataset_disc.get(i)[7]);
            //System.out.println("classe="+classe);
            char ch=value.charAt(2);
            int intervalle=Integer.parseInt(String.valueOf(ch));
            //System.out.print(value+":"+intervalle+",line:"+(int)(i+1)+"\t");
            double[] proba_class2=new double[Q];
            proba_class2=probas.get(classe-1);
            proba_class2[intervalle-1]++;
            probas.set(classe-1,proba_class2);
        }

        for (int i = 0; i < probas.size(); i++) {
            for (int j = 0; j < probas.get(i).length; j++) {
                probas.get(i)[j]=probas.get(i)[j]/(double)dataset_disc.size();
            }
        }
        return probas;
    }

    //conditional probas
    public static ArrayList<ArrayList<double[]>> Cond_probas(ArrayList<String[]> dataset_disc, int Q){
        ArrayList<ArrayList<double[]>> cond_probas=new ArrayList();

        for(int i=0;i<dataset_disc.get(0).length-1;i++){
            cond_probas.add(probas_per_attribute(dataset_disc,Q,i));
        }
        return cond_probas;
    }

    //Naive bayesian
    public static int Naive_Bayesian(ArrayList<String[]> dataset_disc,int Q,String[] instance){
        double[] naive_bayesian=new double[3];
        double[] probas_classes=proba_per_classes(dataset_disc);//verified! 70 instance for each class
        ArrayList<ArrayList<double[]>> cond_probas=Cond_probas(dataset_disc,Q);// 3 probabilities(3 classes) for each attribute(7 attributes)

        int attribut=1;
        int label=1;
        for (ArrayList<double[]> probas_array:cond_probas) {
            System.out.println("Attribut:"+attribut);
            for (double[] probas_list:probas_array) {
                System.out.println("Classe:"+label);
                for (double value:probas_list) {
                    System.out.print(value+"\t");
                }
                label++;
                System.out.print("\n");
            }
            attribut++;
            label=1;
        }
        for (int i = 0; i < 3; i++) {
            naive_bayesian[i]=probas_classes[i];
            System.out.println("Naive bayesian list at index:"+i+"="+naive_bayesian[i]);
            for (int j = 0; j < instance.length-1; j++) {
                int intervalle=Integer.parseInt(String.valueOf(instance[j].charAt(2)));
                //System.out.println("i="+i+"/j="+j+"/intervalle="+intervalle);
                System.out.println("cond proba at index:"+j+"="+cond_probas.get(j).get(i)[intervalle-1]);
                naive_bayesian[i]=(double)naive_bayesian[i]*(double)((cond_probas.get(j).get(i))[intervalle-1]);
            }

        }
        //return naive_bayesian;
        int classification=0;
        for (int i = 1; i < naive_bayesian.length; i++) {
            if(naive_bayesian[classification]<naive_bayesian[i]){
                classification=i;
            }
        }
        return (classification+1);
    }
}
