package search;

import java.util.Iterator;
import java.util.LinkedList;

import board.Board;
import board.State;
import gameTree.GameNode;

public class Minimax {
	
	private Board gameBoard;
	
	public Minimax(Board b) {
		this.gameBoard = b;
	}

	protected GameNode MinimaxValue(GameNode s, boolean MaxStarts) {

		if (MaxStarts) {
			return MaxValue(s);
		} else {
			return MinValue(s);
		}
	}

	// the two mutually recursive functions
	// only difference is the way the "best" successor
	// is determined

	private GameNode MaxValue(GameNode s) {

		//m_countNodesVisited++;
		if (gameBoard.terminalState(s.getState())) {
			s.setValue(gameBoard.utility(s.getState()));
			return s;
		}

		LinkedList<State> successors = gameBoard.successors(s.getState());
		GameNode best = null;
		double bestValue = Double.NEGATIVE_INFINITY;
		Iterator<State> it = successors.iterator();

		while (it.hasNext()) {
			State current = it.next();
			GameNode curr = new GameNode(current, 0, s.getDepth() + 1);
			GameNode n = MinValue(curr);
			if (n.getValue() > bestValue) {
				bestValue = n.getValue();
				best = curr;
				best.setValue(bestValue);
			}
		}
		return best;
	}

	private GameNode MinValue(GameNode s) {

		//m_countNodesVisited++;
		if (gameBoard.terminalState(s.getState())) {
			s.setValue(gameBoard.utility(s.getState()));
			return s;
		}

		LinkedList<State> successors = gameBoard.successors(s.getState());
		GameNode best = null;
		double bestValue = Double.POSITIVE_INFINITY;
		Iterator<State> it = successors.iterator();

		while (it.hasNext()) {
			State current = it.next();
			GameNode curr = new GameNode(current, 0, s.getDepth() + 1);
			GameNode n = MaxValue(curr);
			if (n.getValue() < bestValue) {
				bestValue = n.getValue();
				best = curr;
				best.setValue(bestValue);
			}
		}
		return best;
	}

}