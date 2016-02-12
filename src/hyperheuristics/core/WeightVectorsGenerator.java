/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.core;

/**
 *
 * @author vinicius
 */
// WeightVectorsGenerator.java

import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

public class WeightVectorsGenerator {

	private	int h, m;
	private double t, d;
        private int pos;

	public WeightVectorsGenerator (int m, int h, double d, double t) {
		this.m = m;
		this.h = h;
		this.t = t;
		this.d = d;
                pos=0;
	}

	protected void generate(){
		Vector<Integer> count = new Vector<Integer>();
		for (int i = 0; i < m; ++i){
			count.add(0);
		}
		for (int j = 1; j < Math.pow(h+1, m); ++j) {
			count = update(count);
			int accumulate = 0;
			for (int i: count ) {
				accumulate+=i;
			}
			if (h == accumulate )
				print(count);
		}
	}

	protected void print(Vector<Integer> v){
            System.out.print(pos+" ");pos++;
		for (int i : v) {
                   
			System.out.print((((1.0 - t)/ ((double) m)) + ( t * ((double) i)  * d)) + "\t");
		}
		System.out.println();
	}

	protected Vector<Integer> update (Vector<Integer> v) {
		for (int i = 0; i< v.size(); i = ((v.get(i) != 0) ? v.size() : i+1)) {
			v.set(i, v.get(i)+1);
			v.set(i, v.get(i)%(h+1));
		}
		return v;
	}

	public static void main(String[] args) {
		int h=11, m = 4;
		double t = 1.0, d;

		Map <String, String> arguments = new HashMap<String, String>();

		for (int i=0; i<args.length; i+=2) {
			arguments.put(args[i], args[i+1]);
		}

		if (arguments.get("-h") != null )
			h = Integer.parseInt(arguments.get("-h"));
		if (arguments.get("-m") != null )
			m = Integer.parseInt(arguments.get("-m"));
		if (arguments.get("-t") != null )
			t = Double.parseDouble(arguments.get("-t"));

		d = 1.0 / (double) h;
		(new WeightVectorsGenerator(m, h, d, t)).generate();
	}

}