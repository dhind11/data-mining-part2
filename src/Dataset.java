import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Dataset {
    ArrayList<ArrayList<Double>> data = new ArrayList();
    ArrayList<double[]> train=new ArrayList<>();
    ArrayList<double[]> test=new ArrayList<>();
    ArrayList<Integer> train_indexes = new ArrayList<Integer>();
    ArrayList<Integer> test_indexes = new ArrayList<Integer>();

    public Dataset(String path) {
        //path= to the dataset file (aka seed.txt)
        this.ReadDataset(path);
    }

    public Dataset(ArrayList<double[]> train) {
        //path= to the dataset file (aka seed.txt)
        for(int i=0;i<train.size();i++){
            double[] line=new double[8];
            ArrayList<Double> instance= new ArrayList<Double>();
            line=train.get(i);
            for (int j=0;j<7;j++) {
                instance.add(line[j]);
            }
            this.data.add(instance);
        }
    }

    public void ReadDataset(String path){
        try {
            BufferedReader BfReader = new BufferedReader(new FileReader(path));
            String line = BfReader.readLine();
            while (line != null){
                //Lire le fichier du dataset ligne par ligne et le mettre dans une 'ArrayList' de tableaux de 'doubles'
                String [] values = line.split("[ \t]+");
                //double [] values_double = new double[8];
                ArrayList instance = new ArrayList();
                for (int i = 0; i<values.length; i++){
                    if (values[i] != "")
                        //instance.set(i, (Double.parseDouble(values[i])));
                        instance.add((Double.parseDouble(values[i])));
                }
                this.data.add(instance);
                line = BfReader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return this.data;
    }

    public int Nb_Instances() { return data.size(); }

    public int nbAttributes() { return data.get(0).size(); }

    //public ArrayList getInstance(int i) { return data.get(i); }
    public double[] getInstance(int index) {
        double[] instance = new double[nbAttributes()];
        for (int i = 0; i < instance.length; i++) {
            instance[i] = (double) (this.data.get(index)).get(i);
        }
        return instance;
    }

    public int getClass(int index) { return (int) ((double)data.get(index).get(this.nbAttributes()-1)); }

    public double[] getFeatures(int index) {
        return Arrays.copyOf(getInstance(index), this.nbAttributes()-1);
    }

    public double[] getColumn (int attribute_index){
        double[] column = new double[this.Nb_Instances()];

        for (int i = 0; i < this.Nb_Instances(); i++) {
            column[i] = data.get(i).get(attribute_index);
        }

        return column;
    }

    public int nbClasses(){
        double[] class_att = this.getColumn(nbAttributes()-1);
        return (int) java.util.Arrays.stream(class_att).distinct().count();
    }

    public double [] getAllClasses (){
        double [] classes;
        classes = (java.util.Arrays.stream(this.getColumn(nbAttributes()-1)).distinct()).toArray();
        //for (double c : classes) { System.out.print("\n class : "+ (int)c); }
        return classes;
    }

    public void addInstance(double [] instance){
        ArrayList inst = (ArrayList) Arrays.stream(instance).boxed().collect(Collectors.toList());
        this.data.add(inst);
    }

    public void addInstance(int index, double [] instance){
        ArrayList inst = (ArrayList) Arrays.stream(instance).boxed().collect(Collectors.toList());
        this.data.add(index, inst);
    }

    public void editInstance(int instance_index, int attribute_index, double new_value){
        this.data.get(instance_index).set(attribute_index, new_value);
    }

    public void removeInstance (int index){
        data.remove(index);
    }

    public int nbInstancePerClass (int classe){       //Nombre d'instances de cette classe
        int nb_inst = 0;
        for (int i = 0; i < Nb_Instances(); i++) {
            if (this.getClass(i) == classe)  nb_inst++;
        }
        return nb_inst;
    }

    public void saveDataset() throws FileNotFoundException{
        PrintWriter PW = new PrintWriter(new FileOutputStream("dataset\\new_dataset.txt"));
        for (int i = 0; i < this.Nb_Instances(); i++) {
            double[] instance = this.getInstance(i);
            String inst = "";
            for (double value : instance) {
                inst = inst + value + "\t";
            }
            PW.println(inst);
        }
        PW.close();
    }

    public void printDataset () {
        for (int i = 0; i < this.Nb_Instances(); i++) {
            double[] instance = this.getInstance(i);
            System.out.print("\n");
            for (int j = 0; j < this.nbAttributes(); j++) {
                System.out.print(instance[j] + "\t");
            }
        }
    }

    public void printInstance (double[] instance) {
        for (int i = 0; i < instance.length; i++) {
            System.out.print(instance[i] + "\t");
        }
        System.out.println("\n");
    }

    public void Train_Test(int train,int Q) {
        int nb_train = 0;
        int classe = 1;
        int k = 0;

        while (k < this.Nb_Instances()) {

            while (nb_train < train && this.getClass(k) == classe) {
                this.train.add(this.getInstance(k));
                this.train_indexes.add(k);
                nb_train++;
                k++;
            }
            nb_train = 0;

            while (k < this.Nb_Instances() && this.getClass(k) == classe) {
                this.test.add(this.getInstance(k));
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

    public void Create_test(int test,int Q){

    }


/*
    public void printDataset () {
        for (int i = 0; i < this.nbInstances(); i++) {
            ArrayList instance = this.getInstance(i);
            System.out.print("\n");
            for (int j = 0; j < this.nbAttributes(); j++) {
                System.out.print(instance.get(j) + "\t");
            }
        }
    }
*/










}
