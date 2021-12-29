import java.util.ArrayList;

public class Train_Test {
    ArrayList<double[]> train=new ArrayList<>();
    ArrayList<double[]> test=new ArrayList<>();
    ArrayList<String[]> train_disc=new ArrayList<>();
    ArrayList<String[]> test_disc=new ArrayList<>();
    ArrayList<Integer> train_indexes = new ArrayList<Integer>();
    ArrayList<Integer> test_indexes = new ArrayList<Integer>();

    //discretizé
    public Train_Test(Dataset ds,int train,int Q){

        ArrayList<Integer> train_indexes= new ArrayList<Integer>();
        ArrayList<Integer> test_indexes=new ArrayList<Integer>();
        int nb_train=0;
        int classe=1;
        int k=0;

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
         for(int i=0;i<train_indexes.size();i++){
             String[] ligne=new String[8];
             String[] instance=new String[7];
             instance=pretraitement.Discretisation(ds,train_indexes.get(i),Q);
             for(int j=0;j<instance.length;j++){
                 ligne[j]=instance[j];
             }
             ligne[7]=Integer.toString(ds.getClass(train_indexes.get(i)));
             this.train_disc.add(ligne);
         }
        for(int i=0;i<test_indexes.size();i++){
            String[] ligne=new String[8];
            String[] instance=new String[7];
            instance=pretraitement.Discretisation(ds,test_indexes.get(i),Q);
            for(int j=0;j<instance.length;j++){
                ligne[j]=instance[j];
            }
            ligne[7]=Integer.toString(ds.getClass(test_indexes.get(i)));
            this.test_disc.add(ligne);
        }


    }
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
