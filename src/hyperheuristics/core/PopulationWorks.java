/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.core;

import jmetal.base.SolutionSet;
import jmetal.util.Ranking;

/**
 *
 * @author vinicius
 */
public class PopulationWorks {

    //  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
    public static SolutionSet removeDominadas(SolutionSet result) {
        Ranking ranking = new Ranking(result);
        result = ranking.getSubfront(0);

        boolean dominador, dominado;
        double valor1 = 0;
        double valor2 = 0;

        for (int i = 0; i < (result.size() - 1); i++) {
            for (int j = (i + 1); j < result.size(); j++) {
                dominador = true;
                dominado = true;

                for (int k = 0; k < result.get(i).numberOfObjectives(); k++) {
                    valor1 = result.get(i).getObjective(k);
                    valor2 = result.get(j).getObjective(k);

                    if (valor1 > valor2 || dominador == false) {
                        dominador = false;
                    } else if (valor1 <= valor2) {
                        dominador = true;
                    }

                    if (valor2 > valor1 || dominado == false) {
                        dominado = false;
                    } else if (valor2 < valor1) {
                        dominado = true;
                    }
                }

                if (dominador) {
//                    System.out.println("--------------------------------------------");
//                    System.out.println("Solucao [" + i + "] domina a Solucao [" + j + "]");
//                    System.out.println("[" + i + "] " + result.get(i).toString());
//                    System.out.println("[" + j + "] " + result.get(j).toString());

                    result.remove(j);
                    j = j - 1;
                } else if (dominado) {
//                    System.out.println("--------------------------------------------");
//                    System.out.println("Solucao [" + j + "] domina a Solucao [" + i + "]");
//                    System.out.println("[" + i + "] " + result.get(i).toString());
//                    System.out.println("[" + j + "] " + result.get(j).toString());

                    result.remove(i);
                    j = i;
                }
            }
        }

        return result;
    }

    //  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
    public static SolutionSet removeRepetidas(SolutionSet result) {
        String solucao;

        for (int i = 0; i < result.size() - 1; i++) {
            solucao = result.get(i).getDecisionVariables()[0].toString();
            for (int j = i + 1; j < result.size(); j++) {
                if (solucao.equals(result.get(j).getDecisionVariables()[0].toString())) {
//                    System.out.println("--------------------------------------------");
//                    System.out.println("Solucao [" + i + "] e igual [" + j + "]");
//                    System.out.println(result.get(i).getDecisionVariables()[0].toString());
//                    System.out.println(result.get(j).getDecisionVariables()[0].toString());

                    result.remove(j);
                }
            }
        }

        return result;
    }
}
