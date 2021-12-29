import org.jfree.data.xy.XYSeriesCollection;

import java.util.*;
import java.util.stream.DoubleStream;

public class Calcul {

    //*********************************** MIN / Q0 **************************//
    public static double min (Dataset ds,int X_index) {
        double [] column = ds.getColumn(X_index) ;
        Arrays.sort(column);
        return column[0];
    }

    //*********************************** Q1 ******************************//
    public static double Q1(Dataset ds, int X_index){
        Double q1 ;

        double [] column = ds.getColumn(X_index) ;
        Arrays.sort(column);

        if(column.length%4 != 0){ // la longeur n'est pas div par 4
            q1 = column[(column.length/4)+1];
        }else {
            q1 = column[(column.length/4)-1] + column[(column.length/4)-1] /2;
        }
        return q1;

    }

    //******************************** MEDIANE / Q2 **************************//
    public static double mediane(Dataset ds, int X_index){
        Double median;

        double [] column = ds.getColumn(X_index) ;
        Arrays.sort(column);

        if(column.length%2 != 0){
            median = column[(column.length/2)];
        }
        else{
            median = (column[(column.length/2)] + column[(column.length/2)+1] )/2;
        }

        return median;
    }

    //*********************************** Q3 **************************//
    public static double Q3(Dataset ds, int X_index){
        Double q3 =0.0;
        int position=0;

        double [] column = ds.getColumn(X_index) ;
        Arrays.sort(column);

        if(column.length % 4 != 0){
            if((3*column.length+1 )%4 ==0){
                position = ((3*column.length)/4)+1;
                q3=column[position-1];
            }
            if((3*column.length+2) %4 ==0){
                position = ((3*column.length)/4)+2;
                q3=column[position-1];
            }
        }else {
            double x = column[(3*column.length/4)-1];
            double y = column[(3*column.length/4)];
            q3 = (x + y) / 2;}

        return q3;
    }

    //*********************************** MAX / Q5 **************************//
    public static double max (Dataset ds,int X_index) {

        double [] column = ds.getColumn(X_index) ;
        Arrays.sort(column);

        return column[column.length-1];
    }

    //*********************************** ETENDUE **************************//
    public static double range (Dataset ds, int X_index){
        return max(ds, X_index) - min(ds,X_index);
    }

    //*********************************** MIDRANGE **************************//
    public static double midRange (Dataset ds, int X_index){

        double  midrange = (max(ds, X_index)+min(ds, X_index)) / 2 ;

        return  midrange ;
    }

    //*********************************** MODE **************************//
    public static double mode(Dataset ds, int X_index){
        double mode = 0.0;

        HashMap<Double,Integer> freqs = new HashMap<>();

        double [] column = ds.getColumn(X_index) ;
        Arrays.sort(column);

        ArrayList<Double> vals = new ArrayList<>();

        // creation d'un liste sans repetition
        for(int i=0; i<column.length; i++){
            if(notIn(vals,column[i])) vals.add(column[i]);
            }

        //calculer la freq de chaque nombre

        for(int i=0; i<vals.size();i++){
            int freq = 0;
            for(int j=0; j<column.length;j++){
                if(vals.get(i) == column[j]) freq++;
            }

            freqs.put(vals.get(i),freq);
        }

        //trouver la freq max

        int max = 0;

        for (Map.Entry<Double,Integer> entry : freqs.entrySet()){
            if(entry.getValue() > max) max = entry.getValue();
        }

        for (Map.Entry<Double,Integer> entry : freqs.entrySet()){
            if(entry.getValue() == max) mode = entry.getKey();
        }

        return mode;
    }

    //*********************************** MOYENNE **************************//
    public static double moy(Dataset ds, int X_index){

        double moy ;
        double [] column = ds.getColumn(X_index) ;
        //somme
        double somme = 0.0;

        for (int i=0; i<column.length; i++){ somme += column[i]; }

        moy = somme/column.length;

        return moy;
    }

    //*********************************** MOYENNE TRANQUE **************************//
    public static double moy_tranq (Dataset ds, int X_index,int pourcentage){
        double somme =0.0;
        double [] column = ds.getColumn(X_index) ;
        Arrays.sort(column);

        int num = pourcentage * column.length / 100; // le nombre d'element a enlevé

        //System.out.println(column.length);

        double[] temp ;
        temp = new double[column.length - num];
        int j = 0;
        for (int i = num; i < column.length - num; i++) {
            temp[j] = column[i];
            j++;
        }
        for (double val : temp) {
            somme += val;
        }
        return somme / temp.length;
    }

    //*********************************** Variance **************************//
    public static double variance (Dataset ds, int X_index){
        double [] column = ds.getColumn(X_index) ;
        Arrays.sort(column);
        double moy = moy(ds, X_index);
        double somme =0.0;

        for(int i=0; i< column.length; i++){
            somme += Math.pow((column[i] - moy),2);
        }

        return somme /column.length;
    }

    //*********************************** ECART TYPE **************************//
    public static double ecart_type(Dataset ds, int X_index){
        return   Math.sqrt(variance(ds,X_index)) ;
    }

    //*********************************** ECART INTERQUARTILE **************************//
    public static double ecart_inter(Dataset ds, int X_index){
        return Q3(ds, X_index) - Q1(ds, X_index);
    }

    //***********************************Symetrie***********************************//
    public static void symetrique(Dataset ds, int X_index){
        int moy = (int) moy(ds, X_index);
        int med = (int) mediane(ds, X_index);
        int mode = (int) mode(ds, X_index);

        if(moy == med &&moy == mode){
            System.out.println("les données de l'Att: "+X_index+" sont Symetriques");
        }
        else if(moy < med && med <mode){
            System.out.println("les données de l'Att: "+X_index+" sont Asymetriques Négativement");
        }
        else if(moy > med && med > mode){
            System.out.println("les données de l'Att: "+X_index+" sont Asymetriques Positivement");
        }
        else { System.out.println("les données de l'Att: "+X_index+" sont Asymetriques"); }
    }

    //*********************************** Correlation **************************//
    public static double correlation (Dataset ds , int X_index, int Y_index){
        double coef;
        double [] column1 = ds.getColumn(X_index) ;
        double [] column2 = ds.getColumn(Y_index) ;

        double sum = 0;
        for (int i = 0; i < column1.length; i++) {
            sum += column1[i]*column2[i] ;
        }
        coef = sum - ds.nb_Instances()*(moy(ds, X_index))*moy(ds, Y_index) ;
        coef = coef/((ds.nb_Instances() - 1)* ecart_type(ds, X_index) * ecart_type(ds, Y_index));
        return coef;
    }

    public static String correlation_interpretation (double corr_coef){
        String interpretation = "";
        if (corr_coef > 0.1) {
            if (corr_coef > 0.6){ interpretation = "Les deux attributs sont positivement corrélés (forte correlation)";}
            else { interpretation = "Les deux attributs sont positivement corrélés (faible correlation)"; }
        }
        else if (corr_coef < -0.1) {
            if (corr_coef < -0.6){ interpretation = "Les deux attributs sont négativement corrélés (forte correlation)";}
            else { interpretation = "Les deux attributs sont négativement corrélés (faible correlation)"; }
        }
        else {interpretation = "Les deux attributs ne sont pas corrélés";}

        return interpretation;
    }

    //********************************** Les données aberrantes *******************//
    public static ArrayList<Double> aberrant(Dataset ds, int X_index){

        double IQR = ecart_inter(ds, X_index);
        double x = Q1(ds, X_index) - 1.5 * IQR;
        double y = Q3(ds, X_index) + 1.5 * IQR;
        //System.out.println(x);
        //System.out.println(y);
        double [] column = ds.getColumn(X_index) ;
        Arrays.sort(column);

        ArrayList<Double> aberrant = new ArrayList<>();

        for(double val : column){

            if(val < x) aberrant.add(val);
            if(val > y) aberrant.add(val);
        }
        return aberrant;
    }

    public static boolean notIn (ArrayList<Double> x, double y){
        for(int i=0; i<x.size(); i++){
            if(y == x.get(i)) return false;
        }
        return true;
    }

}
