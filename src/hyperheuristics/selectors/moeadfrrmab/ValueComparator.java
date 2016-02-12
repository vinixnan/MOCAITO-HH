/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.selectors.moeadfrrmab;

import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author vinicius
 */
public class ValueComparator implements Comparator<String> {

    HashMap<String, Double> base;

    public ValueComparator(HashMap<String, Double> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    @Override
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return 1;
        } else if (base.get(b) >= base.get(a)) {
            return -1;
        }
        return 0;
    }
}
