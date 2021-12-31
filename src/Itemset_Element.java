import java.util.ArrayList;

public class Itemset_Element {
    ArrayList<String> items;
    int support;

    public Itemset_Element(ArrayList<String> items,int support){
        this.items=items;
        this.support=support;
    }
    public boolean unique_items(){
        for (int i = 0; i < (this.items.size()-1); i++) {
            char ch=(items).get(i).charAt(1);
            for (int j = i+1; j < this.items.size(); j++) {
                if(this.items.get(j).charAt(1)==ch){return false;}
            }
        }
        return true;
    }

}
