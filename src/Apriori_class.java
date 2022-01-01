import java.util.ArrayList;

public class Apriori_class {

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

    private static ArrayList<String> Common_items(ArrayList<String> items1,ArrayList<String> items2){
        ArrayList<String> common_items=new ArrayList<>();
        for (int i = 0; i < items1.size(); i++) {
            if(items2.contains(items1.get(i))){
                common_items.add(items1.get(i));
            }
        }
        return common_items;
    }

    private static ArrayList<String> Combine_items(ArrayList<String> items1,ArrayList<String> items2,ArrayList<String> common_items){
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

    private static int calcul_supportk(ArrayList<String[]> dataset_disc,ArrayList<String> combined_items) {
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

    private static boolean In_Ck(ArrayList<Itemset_Element> Ck,ArrayList<String> combined_items){
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

    private static ArrayList<Itemset_Element> generate_Ck(ArrayList<String[]> dataset_disc,int min_sup,ArrayList<Itemset_Element> Lk,int k) {
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

    private static ArrayList<Itemset_Element> generate_Lk(ArrayList<Itemset_Element> Ck,int min_sup){
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
}
