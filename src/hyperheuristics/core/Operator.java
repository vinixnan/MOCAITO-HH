package hyperheuristics.core;

public class Operator implements Comparable<Operator> {
	private int operatorId;
	private int rankPos;
	private double fitnessImprovement;

	public Operator(int operatorId, int rankPos, double fitnessImprovement) {
		this.operatorId = operatorId;
		this.rankPos = rankPos;
		this.fitnessImprovement = fitnessImprovement;
	}

	public int getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}

	public int getRankPos() {
		return rankPos;
	}

	public void setRankPos(int rankPos) {
		this.rankPos = rankPos;
	}

	public double getFitnessImprovement() {
		return fitnessImprovement;
	}

	public void setFitnessImprovement(double fitness) {
		this.fitnessImprovement = fitness;
	}

	@Override
	public int compareTo(Operator o) {
		if (this.fitnessImprovement > o.getFitnessImprovement()) {
			return 1;
		} else if (this.fitnessImprovement > o.getFitnessImprovement()) {
			return -1;
		} else {
			return 0;
		}
	}

}
