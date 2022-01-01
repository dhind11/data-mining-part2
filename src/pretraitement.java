import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.SortedSet;
import java.util.*;

public class pretraitement {
    /*************************************** Normalization ***********************************************************/
    private static double MinMaxNorm(double val, double min, double max) {
        return (val - min) / (max - min);
    }

    public static double[] MinMaxNormalization(Dataset ds, int instance_index) {
        double[] instance = ds.getInstance(instance_index);
        double[] new_instance = new double[instance.length];

        for (int i = 0; i < new_instance.length; i++) {
            new_instance[i] = MinMaxNorm(instance[i], Calcul.min(ds, i), Calcul.max(ds, i));
        }
        return new_instance;
    }

    private static double calcul_S(Dataset ds, int index_att, double moyenne) {
        double S = 0;
        double[] column = ds.getColumn(index_att);
        //double moyenne = Calcul.moy(ds, index_att);

        for (int i = 0; i < column.length; i++) {
            S = S + Math.abs(column[i] - moyenne);
        }
        S = S / ds.nb_Instances(); // 1/N
        return S;
    }

    public static double[] ZscoreNormalization(Dataset ds, int instance_index) {
        double[] instance = ds.getInstance(instance_index);
        double[] new_instance = new double[instance.length];

        for (int i = 0; i < new_instance.length; i++) {
            double moyenne = Calcul.moy(ds, i);
            new_instance[i] = (instance[i] - moyenne) / (calcul_S(ds, i, moyenne));
        }
        return new_instance;
    }

    private static double[] normal_col(Dataset ds, int index_att, int norm) {
        //Cette fonction retourne une colonne normalisée
        // norm = 0 -> MinMax normalization  ;  norm = 1 -> Z-Score normalization
        double[] column = ds.getColumn(index_att);
        double[] new_column = new double[column.length];
        if (norm == 0) { // MinMax normalization
            for (int i = 0; i < column.length; i++) {
                new_column[i] = MinMaxNormalization(ds, i)[index_att];
            }
        } else { // Z-Score normalization
            for (int i = 0; i < column.length; i++) {
                new_column[i] = ZscoreNormalization(ds, i)[index_att];
            }
        }
        return new_column;
    }

    public static double[] bornes(Dataset ds, double[] attribut_col, int Q) {
        //fonction qui permet de calculer les bornes des intervalles pour la discretisation en classes d'effectifs egaux
        int nb_inst = (int) Math.ceil(ds.nb_Instances() / Q); //Nombre d'instances par intervalle.

        //double[] attribut_col = ds.getColumn(att_index);
        Arrays.sort(attribut_col);

        double[] bornes_sup = new double[Q]; //Tableau qui va contenir les bornes supérieurs des 'Q' intervalles

        for (int i = 1; i < Q; i++) {
            int indice_quantile = nb_inst * i - 1;
            bornes_sup[i - 1] = attribut_col[indice_quantile];
        }
        bornes_sup[Q - 1] = attribut_col[attribut_col.length - 1]; //la borne sup du dernier intervalle

        return bornes_sup;
    }

    /*************************************** Discretisation ***********************************************************/
    public static String[] Discretisation(Dataset ds, int instance_index, int Q) {
        //Discretisation en classes d'effectifs egaux (Equal-frequency)

        //double [] instance = ds.getInstance(instance_index);
        double[] instance = pretraitement.MinMaxNormalization(ds, instance_index);
        String[] new_instance = new String[instance.length - 1];

        for (int i = 0; i < instance.length - 1; i++) {
            double[] attribut_col = normal_col(ds, i, 0);
            double[] bornes_sup = bornes(ds, attribut_col, Q); //Recuperer les bornes superieur des intervalles de cette attributs
            double borne_sup = bornes_sup[0];
            int num_intervalle = 1;
            //Pour la valeur : trouver le num de l'intervalle (en comparant la valeur avec la borne sup des intervalles)
            while (borne_sup <= bornes_sup[Q - 1]) {   // bornes_sup[Q-1] : borne max
                if (instance[i] < borne_sup) {
                    break;
                } else if (instance[i] >= borne_sup) {
                    //System.out.print("I'm here");
                    borne_sup = bornes_sup[num_intervalle];
                    num_intervalle = num_intervalle + 1;
                }
            }
            new_instance[i] = "I" + (i + 1) + num_intervalle;
        }
        return new_instance;
    }

    public static String[] Discretisation2(Dataset ds, int instance_index, int Q) {
        //Discretisation en classes d'amplitudes égales
        double min = 0;
        double max = 1;
        double width = 1 / (double) Q;  // On considere l'intervalle [0,1]
        //System.out.print("width="+width);
        //double width = (min - max) / (double)Q;

        //double [] instance = ds.getInstance(instance_index);
        double[] instance = pretraitement.MinMaxNormalization(ds, instance_index);
        String[] new_instance = new String[instance.length - 1];

        for (int i = 0; i < instance.length - 1; i++) {
            double borne_sup = min + width;
            int num_intervalle = 1;
            while (borne_sup <= max) {
                if (instance[i] < borne_sup || instance[i] == max) {
                    break;
                } else if (instance[i] >= borne_sup) {
                    borne_sup = borne_sup + width;
                    num_intervalle = num_intervalle + 1;
                }
            }
            //System.out.print("\t " + num_intervalle);
            new_instance[i] = "I" + (i + 1) + num_intervalle;
        }
        return new_instance;
    }

    /*************************************** Itemsets ***********************************************************/
    public static int calcul_support(String A, ArrayList<String[]> dataset_disc) {
        int support = 0;
        for (int i = 0; i < dataset_disc.size(); i++) {
            String[] ligne = dataset_disc.get(i);
            for (int j = 0; j < ligne.length; j++) {
                if (ligne[j].equals(A)) {
                    support++;
                }
            }
        }
        return support;

    }

    public static int item_exist(ArrayList<ElementC1> C1, String element_id) {
        for (int i = 0; i < C1.size(); i++) {
            if (C1.get(i).itemset.equals(element_id)) {
                return i;
            }
        }
        return -1;
    }

    public static ArrayList<ElementC1> Create_itemset1(Dataset ds, int min_sup, int Q) {

        ArrayList<String[]> dataset_disc = new ArrayList<>(); // dataset discretisée
        for (int i = 0; i < ds.nb_Instances(); i++) {
            dataset_disc.add(Discretisation2(ds, i, Q));
        }

        ArrayList<ElementC1> C1 = new ArrayList<>();
        for (int i = 0; i < dataset_disc.size(); i++) {

            for (int j = 0; j < dataset_disc.get(i).length; j++) {

                String element_id = dataset_disc.get(i)[j];
                //ElementC1 element = new ElementC1(element_id,0);

                int position = item_exist(C1, element_id);
                if (position != -1) {      // il existe
                    C1.get(position).freq++;
                } else {
                    ElementC1 element = new ElementC1(element_id, 1);
                    C1.add(element);
                }

            }

        }
        return (C1);
        //printC1(C1);

    }

    public static void printC1(ArrayList<ElementC1> C1) {
        System.out.print("C1=\t");
        for (int i = 0; i < C1.size(); i++) {
            System.out.print("{" + C1.get(i).itemset + "}:" + C1.get(i).freq + ",");
        }
    }

    public static ArrayList<String> Create_L1(Dataset ds, int min_sup, ArrayList<ElementC1> C1) {
        int support_min = (ds.nb_Instances()) * min_sup / 100;
        ArrayList<String> L1 = new ArrayList<>();
        for (int i = 0; i < C1.size(); i++) {
            if (C1.get(i).freq >= support_min) {
                L1.add(C1.get(i).itemset);
            }
        }
        return L1;
    }

    public static void printL1(ArrayList<String> L1) {
        System.out.print("L1=\t");
        for (int j = 0; j < L1.size(); j++) {
            System.out.print("{" + L1.get(j) + "}\t");
        }
    }

    public static int calcul_support2(String[] A, ArrayList<String[]> dataset_disc) {
        int support = 0;
        for (int i = 0; i < dataset_disc.size(); i++) {
            String[] ligne = dataset_disc.get(i);
            boolean found = false;
            int j = 0;
            while (j < (ligne.length) - 1) {
                int k = 1;
                while (k < (ligne.length)) {
                    if (!ligne[j].equals(ligne[k])) {
                        if (A[0].equals(ligne[j]) || A[1].equals(ligne[j])) {
                            if (A[0].equals(ligne[k]) || A[1].equals(ligne[k])) {
                                support++;
                                found = true;
                            }
                        }
                    }
                    if (found) {
                        break;
                    } else {
                        k++;
                    }
                }
                if (found) {
                    break;
                } else {
                    j++;
                }
            }
        }
        return support;
    }

    public static ArrayList<String[]> Create_itemset2(Dataset ds, ArrayList<String> L1, int Q) {

        ArrayList<String[]> dataset_disc = new ArrayList<>(); // dataset discretisée
        for (int i = 0; i < ds.nb_Instances(); i++) {
            dataset_disc.add(Discretisation2(ds, i, Q));
        }

        String I1, I2;
        ArrayList<String[]> C2 = new ArrayList<>();

        for (int i = 0; i < (L1.size()) - 1; i++) {
            I1 = L1.get(i);
            int j = i + 1;
            while (j < L1.size()) {
                I2 = L1.get(j);
                //if(!(I1.equals(I2))){
                String[] temp_itemset2 = new String[3];
                temp_itemset2[0] = I1;
                temp_itemset2[1] = I2;
                String[] A = new String[2];
                A[0] = I1;
                A[1] = I2;
                temp_itemset2[2] = String.valueOf(calcul_support2(A, dataset_disc));
                C2.add(temp_itemset2);
                //}
                j++;
            }
        }
        return (C2);
    }

    public static void printC2(ArrayList<String[]> C2) {
        for (int i = 0; i < C2.size(); i++) {
            System.out.print("{" + C2.get(i)[0] + "," + C2.get(i)[1] + "}:" + C2.get(i)[2] + "\t");
        }
    }

    public static ArrayList<String[]> sort_L2(ArrayList<String[]> L2) {
        String item;
        ArrayList<String[]> new_L2 = new ArrayList<>();
        ArrayList<String> items1 = new ArrayList<>();
        for (int i = 0; i < L2.size(); i++) {
            item = L2.get(i)[0];
            if (!items1.contains(item)) {
                items1.add(item);
            }
        }
        Collections.sort(items1); //L2 is a set of elements like {x,y} sort the x parts and put them in items1
        SortedSet<String> items2 = new TreeSet<>(); //will be used temporarely to sort the y in {x,y}
        //now items1 is sorted

        for (int i = 0; i < items1.size(); i++) { //parcourir les x et les y
            for (int j = 0; j < L2.size(); j++) {
                if (L2.get(j)[0].equals(items1.get(i))) {
                    item = L2.get(j)[1];
                    items2.add(item);
                    //j++;
                } else {
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
        return (new_L2);
    }

    public static ArrayList<String[]> Create_L2(Dataset ds, int min_sup, ArrayList<String[]> C2) {
        int support_min = (ds.nb_Instances()) * min_sup / 100;
        ArrayList<String[]> L2 = new ArrayList<>();
        for (int i = 0; i < C2.size(); i++) {

            if (Integer.valueOf(C2.get(i)[2]) >= support_min) {
                L2.add(C2.get(i));
                //System.out.println("items:"+C2.get(i)[0]+C2.get(i)[1]+" Support="+Integer.valueOf(C2.get(i)[2]));

            }
        }
        L2 = sort_L2(L2);
        return (L2);
    }

    public static void printL2(ArrayList<String[]> L2) {
        for (int i = 0; i < L2.size(); i++) {
            //System.out.print("{"+L2.get(i)[0]+","+L2.get(i)[1]+"}:"+L2.get(i)[2]+"\t");
            System.out.print("{" + L2.get(i)[0] + "," + L2.get(i)[1] + "}" + "\t");
        }
    }

    /*************************************** frequent items ***********************************************************/
    public static ArrayList<Itemset_Element> Create_C1(ArrayList<String[]> dataset_disc) {

        ArrayList<Itemset_Element> C1 = new ArrayList();

        for (int i = 0; i < dataset_disc.size(); i++) {
            for (int j = 0; j < dataset_disc.get(i).length; j++) {
                ArrayList<String> element_id = new ArrayList();//example:I21
                element_id.add(dataset_disc.get(i)[j]);//like getting an element with ligne colonne from matrice
                int position=-1;//On considere initialement que element_id n'existe pas dans C1

                if(C1.isEmpty()) {
                    Itemset_Element element = new Itemset_Element(element_id, 1);
                    C1.add(element);
                }
                else{
                        for (int k = 0; k < C1.size(); k++) {
                            for (int l = 0; l < C1.get(k).items.size(); l++) {
                                if(C1.get(k).items.get(l).equals(element_id.get(0))){
                                    position=k;
                                    break;
                                }
                            }

                        }
                        if (position!=-1) { // il existe
                            C1.get(position).support++;
                        }
                        else {
                            Itemset_Element element = new Itemset_Element(element_id, 1);
                            C1.add(element);
                        }
                    }
                //element_id.clear();
                }
            }
        return (C1);
    }

    public static ArrayList<String> Common_items(ArrayList<String> items1,ArrayList<String> items2){
        ArrayList<String> common_items=new ArrayList<>();
        for (int i = 0; i < items1.size(); i++) {
            if(items2.contains(items1.get(i))){
                common_items.add(items1.get(i));
            }
        }
        return common_items;
    }

    public static ArrayList<String> Combine_items(ArrayList<String> items1,ArrayList<String> items2,ArrayList<String> common_items){
        //ArrayList<String> combined_itemsets=new ArrayList();
        ArrayList<String> temp_items=new ArrayList<>();
        boolean exists;

        //remplir temp_items avec common_items
        for (int i = 0; i < common_items.size(); i++) {
            temp_items.add(common_items.get(i));
        }
        //remplir temp_items avec itesm1 qui ne sont pas dans common items
        for (int i = 0; i < items1.size(); i++) {
            exists=false;
            for (int j = 0; j < common_items.size(); j++) {
                if(items1.get(i).equals(common_items.get(j))){
                    exists=true;
                    break;
                }
            }
            if(!exists){
                temp_items.add(items1.get(i));
            }
        }
        //remplir temp_items avec itesm2 qui ne sont pas dans common items
        for (int i = 0; i < items1.size(); i++) {
            exists=false;
            for (int j = 0; j < common_items.size(); j++) {
                if(items2.get(i).equals(common_items.get(j))){
                    exists=true;
                    break;
                }
            }
            if(!exists){
                temp_items.add(items2.get(i));
            }
        }

        return temp_items;
    }

    public static int calcul_supportk(ArrayList<String[]> dataset_disc,ArrayList<String> combined_items) {
        int support=0;
        int nb_exist=0;
        //ArrayList<String> instance=new ArrayList<>();

        for (int i = 0; i < dataset_disc.size(); i++) {
            ArrayList<String> instance=new ArrayList<>();
            //convert instance of dataset_disc, from [] to arraylist
            for (String element:dataset_disc.get(i)) {
                instance.add(element);
            }
            //compare if instance elements CONTAINS combined_items elements
            for (int j = 0; j < combined_items.size(); j++) {
                if (instance.contains(combined_items.get(j))) {
                    nb_exist++;
                }
            }
            if(nb_exist==combined_items.size()){support++;}
            nb_exist=0;
        }
        return support;
    }

    public static boolean In_Ck(ArrayList<Itemset_Element> Ck,ArrayList<String> combined_items){
        boolean contained=false;
        for (int i = 0; i < Ck.size(); i++) {
            for (int j = 0; j < combined_items.size(); j++) {
                if(Ck.get(i).items.contains(combined_items.get(j))){
                    contained=true;
                }
                else{
                    contained=false;
                    break;
                }
            }
            if (contained){
                return true;
            }
        }
        return false;
    }

    public static ArrayList<Itemset_Element> generate_Ck(ArrayList<String[]> dataset_disc,int min_sup,ArrayList<Itemset_Element> Lk,int k) {
        ArrayList<Itemset_Element> Ck = new ArrayList();
        int support;

        if (k > 1) {
            for (int i = 0; i < (Lk.size()-1); i++) {
                ArrayList<String> items1=new ArrayList<>();
                items1=Lk.get(i).items;
                for (int j = (i+1); j < Lk.size(); j++) {
                    ArrayList<String> items2=new ArrayList<>();
                    items2=Lk.get(j).items;
                    if(k==2){
                        ArrayList<String> combined_items = new ArrayList<>();
                        combined_items.add(Lk.get(i).items.get(0));
                        combined_items.add(Lk.get(j).items.get(0));
                        support = calcul_supportk(dataset_disc, combined_items);
                        Itemset_Element temp_Ck_element = new Itemset_Element(combined_items, support);
                        Ck.add(temp_Ck_element);
                    }
                    else {
                        ArrayList<String> common_items = new ArrayList<>();
                        common_items = Common_items(items1, items2);
                        if (common_items.size() == (k - 2)) {
                            ArrayList<String> combined_items = new ArrayList<>();
                            combined_items = Combine_items(items1, items2, common_items);
                            support = calcul_supportk(dataset_disc, combined_items);
                            Itemset_Element temp_Ck_element = new Itemset_Element(combined_items, support);
                            if(!In_Ck(Ck,combined_items)){
                                Ck.add(temp_Ck_element);
                            }
                        }
                    }
                }
            }
        }
        else{
            ArrayList<Itemset_Element> C1=Create_C1(dataset_disc);
            return C1;
        }
    return Ck;
    }

    public static ArrayList<Itemset_Element> generate_Lk(ArrayList<Itemset_Element> Ck,int min_sup){
        ArrayList<Itemset_Element> Lk=new ArrayList();
        //Itemset_Element element_Lk=new ArrayList<>();

        for (int i = 0; i < Ck.size(); i++) {
            if(Ck.get(i).support>=min_sup){
                Itemset_Element element_Lk=new Itemset_Element(Ck.get(i).items,Ck.get(i).support);
                Lk.add(element_Lk);
            }
        }
        return(Lk);
    }

    public static ArrayList<Itemset_Element> Apriori(ArrayList<String[]> dataset_disc,int pourcentage_min_sup){
        ArrayList<Itemset_Element> frequent_items=new ArrayList<>();

        ArrayList<Itemset_Element> Ck=new ArrayList<>();
        ArrayList<Itemset_Element> Lk=new ArrayList<>();
        int min_sup=(dataset_disc.size()*pourcentage_min_sup)/100;//add lower
        int k=1;//to reference L1 L2 L3.. & C1 C2 C3....

        //Create C1 and L1
        Ck=Create_C1(dataset_disc);
        Lk=generate_Lk(Ck,min_sup);
        //add L1 to frequent items
        for (Itemset_Element element:Lk) {
            frequent_items.add(element);
        }
        k++;
        while(Lk.size()>2){
            Ck.clear();
            Ck=generate_Ck(dataset_disc,min_sup,Lk,k);
            Lk.clear();
            Lk=generate_Lk(Ck,min_sup);
            //add Lk to frequent items
            for (Itemset_Element element:Lk) {
                frequent_items.add(element);
            }
            k++;
        }
        return (frequent_items);
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
    public static ArrayList<double[]> probas_per_attribute(ArrayList<String[]> train_disc,int Q,int attribute_index){
        ArrayList<double[]> probas=new ArrayList<>();//Q=4, probas de [(C1,C1,C1,C1),(C2,C2,C2,C2),(C3,C3,C3,C3)]

        for (int i = 0; i < 3; i++) {
            double[] proba_class=new double[Q];//(C1,C1,C1,C1)
            for (int j = 0; j < Q; j++) {
                proba_class[j]=0;
            }
            probas.add(proba_class);
        }

        for(int i=0;i<train_disc.size();i++){
            //System.out.println(i);
            String value=train_disc.get(i)[attribute_index];
            int classe=Integer.valueOf(train_disc.get(i)[7]);
            //System.out.println("classe="+classe);
            char ch=value.charAt(2);
            int intervalle=Integer.parseInt(String.valueOf(ch));
            //System.out.print(value+":"+intervalle+",line:"+(int)(i+1)+"\t");
            double[] proba_class2=new double[Q];
            proba_class2=probas.get(classe-1);
            proba_class2[intervalle-1]++;
            probas.set(classe-1,proba_class2);
        }
        //to verify frequences of intervalls in each class for a given attribute (CHECKED)
        /*System.out.println("Attribut:"+(attribute_index+1));
        for (double[] liste:probas) {
            for (double value:liste) {
                System.out.print(value+"\t");
            }
            System.out.print("\n");
        }*/

        for (int i = 0; i < probas.size(); i++) {
            for (int j = 0; j < probas.get(i).length; j++) {
                probas.get(i)[j]=probas.get(i)[j]/(double)((train_disc.size())/3.0);
            }
        }
        return probas;
    }

    //conditional probas
    public static ArrayList<ArrayList<double[]>> Cond_probas(ArrayList<String[]> train_disc, int Q){
        ArrayList<ArrayList<double[]>> cond_probas=new ArrayList();

        for(int i=0;i<train_disc.get(0).length-1;i++){
            cond_probas.add(probas_per_attribute(train_disc,Q,i));
        }
        //to verify final probas for each attribute (CHECKED)
        /*for (int i = 0; i < cond_probas.size(); i++) {
            System.out.println("Attribut:"+(i+1));
            for (int j = 0; j < cond_probas.get(i).size(); j++) {
                System.out.println("Classe:"+(j+1));
                for (double val:cond_probas.get(i).get(j)) {
                    System.out.print(val+"\t");
                }
                System.out.print("\n");
            }
        }*/
        return cond_probas;
    }

    //Naive bayesian obtained probas for each class
    public static double[] Naive_Bayesian(ArrayList<String[]> train_disc,int Q,String[] instance){
        double[] naive_bayesian=new double[3];
        double[] probas_classes=proba_per_classes(train_disc);//verified! 70 instance for each class
        ArrayList<ArrayList<double[]>> cond_probas=Cond_probas(train_disc,Q);// 3 probabilities(3 classes) for each attribute(7 attributes)

        int attribut=1;
        int label=1;
        for (ArrayList<double[]> probas_array:cond_probas) {
            //System.out.println("Attribut:"+attribut);
            for (double[] probas_list:probas_array) {
                //System.out.println("Classe:"+label);
                for (double value:probas_list) {
                    //System.out.print(value+"\t");
                }
                label++;
                //System.out.print("\n");
            }
            attribut++;
            label=1;
        }
        for (int i = 0; i < 3; i++) {
            naive_bayesian[i]=probas_classes[i];
            //System.out.println("Naive bayesian list at index:"+i+"="+naive_bayesian[i]);
            for (int j = 0; j < instance.length-1; j++) {
                int intervalle=Integer.parseInt(String.valueOf(instance[j].charAt(2)));
                //System.out.println("i="+i+"/j="+j+"/intervalle="+intervalle);
                //System.out.println("cond proba at index:"+j+"="+cond_probas.get(j).get(i)[intervalle-1]);
                naive_bayesian[i]=(double)naive_bayesian[i]*(double)((cond_probas.get(j).get(i))[intervalle-1]);
            }

        }
        //print calculated probas for a given test instance
        /*for(double element:naive_bayesian){
            System.out.println(element);
        }*/
        return naive_bayesian;
    }

    //Prediction de la classe avec naive bayesian
    public static int predict_naive_bayesian(ArrayList<String[]> train_disc,int Q,String[] instance){
        double[] naive_bayesian=new double[3];
        naive_bayesian=Naive_Bayesian(train_disc,Q,instance);
        int classification=0;
        for (int i = 1; i < naive_bayesian.length; i++) {
            if(naive_bayesian[classification]<naive_bayesian[i]){
                classification=i;
            }
        }
        return (classification+1);
    }

    //Confusion Matrix
    public static double[][] Confusion_matrix(ArrayList<String[]> train_disc,int Q,ArrayList<String[]> test_disc){
        //ArrayList<double[][]> confusion_matrix=new ArrayList();
        double[][] confusion_matrix=new double[3][3];

        //Initialize confusion_matrix
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                confusion_matrix[i][j]=0;
            }
        }
        int predicted,real;
        for (int i = 0; i < test_disc.size(); i++) {
            //System.out.println(i);
            predicted=predict_naive_bayesian(train_disc,Q,test_disc.get(i));
            real=Integer.parseInt(test_disc.get(i)[7]);
            confusion_matrix[real-1][predicted-1]++;
        }
        //afficher matrice de confusion
        /*for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(confusion_matrix[i][j]+"\t\t");
            }
            System.out.print("\n");
        }*/
        return confusion_matrix;
    }

    //Mini matrices
    public static ArrayList<int[][]> Mini_confusion_matrix(ArrayList<String[]> train_disc,int Q,ArrayList<String[]> test_disc){
        ArrayList<int[][]> mini_confusion_matrix=new ArrayList();
        int predicted,real;

        for(int i = 0; i<3; i++) {
            for (int j = 0; j < test_disc.size(); j++) {
                //System.out.println(i);
                predicted = predict_naive_bayesian(train_disc, Q, test_disc.get(i));
                real = Integer.parseInt(test_disc.get(i)[7]);

                mini_confusion_matrix.get(i)[real - 1][predicted - 1]++;
            }
        }
        return mini_confusion_matrix;
    }
}
