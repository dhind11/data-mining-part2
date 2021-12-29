import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Dataset {
    ArrayList<ArrayList<Double>> data = new ArrayList();

    public Dataset(String path) {
        this.ReadDataset(path);
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

    public int nb_Instances() { return data.size(); }

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
        double[] column = new double[this.nb_Instances()];

        for (int i = 0; i < this.nb_Instances(); i++) {
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
        for (int i = 0; i < nb_Instances(); i++) {
            if (this.getClass(i) == classe)  nb_inst++;
        }
        return nb_inst;
    }

    public void saveDataset() throws FileNotFoundException{
        PrintWriter PW = new PrintWriter(new FileOutputStream("dataset\\new_dataset.txt"));
        for (int i = 0; i < this.nb_Instances(); i++) {
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
        for (int i = 0; i < this.nb_Instances(); i++) {
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
