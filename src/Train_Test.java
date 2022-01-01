import java.util.ArrayList;

public class Train_Test {
    ArrayList<double[]> train=new ArrayList<>();
    ArrayList<double[]> test=new ArrayList<>();
    ArrayList<String[]> train_disc=new ArrayList<>();
    ArrayList<String[]> test_disc=new ArrayList<>();
    ArrayList<Integer> train_indexes = new ArrayList<Integer>();
    ArrayList<Integer> test_indexes = new ArrayList<Integer>();

    //discretizé using train
    /*public Train_Test(Dataset ds,int train,int Q){
        ArrayList<Integer> train_indexes= new ArrayList<Integer>();
        ArrayList<Integer> test_indexes=new ArrayList<Integer>();
        int nb_train=0;
        int classe=1;
        int k=0;
        // discretiser le dataset
        ArrayList<String[]> dataset_disc = new ArrayList<>(); // dataset discretisée
        for(int i = 0; i<ds.nb_Instances(); i++){
            dataset_disc.add(pretraitement.Discretisation2(ds,i,Q));
        }
        //trouver les index de train dataset et test dataset
        while(k<ds.nb_Instances()){
            while(nb_train<train && ds.getClass(k)==classe){
                train_indexes.add(k);
                nb_train++;
                k++;
            }
            nb_train=0;
            while(k<ds.nb_Instances() && ds.getClass(k)==classe){
                test_indexes.add(k);
                k++;
            }
            if(classe<4){
                classe++;
            }
            else{
                break;
            }
        }
        //remplir train dataset
        for(int i=0;i<train_indexes.size();i++){
            String[] ligne=new String[8];
            String[] instance=new String[7];
            instance=dataset_disc.get(i);
            //System.out.println("\ni="+train_indexes.get(i)+",att1 val="+instance[0]);
            for(int j=0;j<instance.length;j++){
                ligne[j]=instance[j];
            }
            ligne[7]=Integer.toString(ds.getClass(train_indexes.get(i)));
            this.train_disc.add(ligne);
        }
        //remplir test dataset
        for(int i=0;i<test_indexes.size();i++){
            String[] ligne=new String[8];
            String[] instance=new String[7];
            instance=dataset_disc.get(i);
            //System.out.println("\ni="+train_indexes.get(i)+",att1 val="+instance[0]);
            for(int j=0;j<instance.length;j++){
                ligne[j]=instance[j];
            }
            ligne[7]=Integer.toString(ds.getClass(test_indexes.get(i)));
            this.test_disc.add(ligne);
        }
    }*/

    //discretizé using test
    public Train_Test(Dataset ds,int test,int Q){
        ArrayList<Integer> train_indexes= new ArrayList<Integer>();
        ArrayList<Integer> test_indexes=new ArrayList<Integer>();
        int nb_test=0;
        int classe=1;
        int k=0;
        // discretiser le dataset
        ArrayList<String[]> dataset_disc = new ArrayList<>(); // dataset discretisée
        for(int i = 0; i<ds.nb_Instances(); i++){
            dataset_disc.add(pretraitement.Discretisation2(ds,i,Q));
        }
        //trouver les index de test dataset et train dataset
        while(k<ds.nb_Instances()){
            while(nb_test<test && ds.getClass(k)==classe){
                test_indexes.add(k);
                nb_test++;
                k++;
            }
            nb_test=0;
            while(k<ds.nb_Instances() && ds.getClass(k)==classe){
                train_indexes.add(k);
                k++;
            }
            if(classe<4){
                classe++;
            }
            else{
                break;
            }
        }
        //remplir train dataset
        for(int i=0;i<train_indexes.size();i++){
            String[] ligne=new String[8];
            String[] instance=new String[7];
            instance=dataset_disc.get(i);
            //System.out.println("\ni="+train_indexes.get(i)+",att1 val="+instance[0]);
            for(int j=0;j<instance.length;j++){
                ligne[j]=instance[j];
            }
            ligne[7]=Integer.toString(ds.getClass(train_indexes.get(i)));
            this.train_disc.add(ligne);
        }
        //remplir test dataset
        for(int i=0;i<test_indexes.size();i++){
            String[] ligne=new String[8];
            String[] instance=new String[7];
            instance=dataset_disc.get(i);
            //System.out.println("\ni="+train_indexes.get(i)+",att1 val="+instance[0]);
            for(int j=0;j<instance.length;j++){
                ligne[j]=instance[j];
            }
            ligne[7]=Integer.toString(ds.getClass(test_indexes.get(i)));
            this.test_disc.add(ligne);
        }
    }

    //non discrétisé
    public Train_Test(Dataset ds,int train) {


        int nb_train = 0;
        int classe = 1;
        int k = 0;

        while (k < ds.nb_Instances()) {

            while (nb_train < train && ds.getClass(k) == classe) {
                this.train.add(ds.getInstance(k));
                this.train_indexes.add(k);
                nb_train++;
                k++;
            }
            nb_train = 0;

            while (k < ds.nb_Instances() && ds.getClass(k) == classe) {
                this.test.add(ds.getInstance(k));
                this.test_indexes.add(k);
                k++;
            }

            if (classe < 4) {
                classe++;
            } else {
                break;
            }
        }
    }


}
