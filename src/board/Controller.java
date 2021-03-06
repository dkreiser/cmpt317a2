package board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cmpt317A2.GameNode;
import cmpt317A2.Tuple;
import gamepiece.gamePiece;
import search.AlphaBeta;
import search.Minimax;
import search.Search;

public class Controller {

	/**
	 * A hard-coded turn limit - a "turn" is considered one move by dragons and
	 * one move by kings
	 */
	final private int turnLimit = 25;
	
	/**
	 * a hard-coded state limit: If a "state" appears 8 times then we will call
	 * the game a draw. This number may have to be increased a bit since we are
	 * only checking board characters, not specific pieces.
	 */
	final private int stateLimit = 5;

	/**
	 * a HashMap that keeps all of the states.
	 */
	HashMap<String,Integer> states;

	/** an instance of the game board, we only ever want one */
	private Board myBoard = new Board();

	/** a scanner created to handle user input */
	private Scanner myScanner = new Scanner(System.in);

	/** we never want to make a duplicate copy of this class. */
	private Controller() {
		states = new HashMap<String,Integer>();
	}
	
	/**
	 * Called when the human player is playing as King to execute their turn
	 * 
	 * @return true if the game is in a draw state, false otherwise.
	 */
	private boolean playerKingTurn() {
		// variables used
		ArrayList<gamePiece> myUnits = myBoard.getTeamOne();
		int xCoordinate;
		int yCoordinate;
		char pieceToMove;
		ArrayList<Tuple> moveList;

		// The input pattern.
		Pattern whichPiece = Pattern.compile("[0-4] [0-4]");

		// Step one: check if the game is a draw or can be continued.
		if (isDraw(myUnits)) {
			return true;
		}

		// Step two: print the board
		myBoard.printGameBoard();

		// Step three: list the units that the player has control over
		printUnitList(myUnits);

		// Step four: ask the player for which piece they would like to move.
		// this bit of code stops the user from entering invalid input and
		// crashing the program
		System.out.println("Enter the x and y coordinates of the unit you would like to move, separated by a space");
		String input = myScanner.nextLine();
		Matcher userInput = whichPiece.matcher(input);

		while (!userInput.matches()) {
			System.out
					.println("Enter the x and y coordinates of the unit you would like to move, separated by a space");
			System.out.println("\tExample: 0 0");
			input = myScanner.nextLine();
			userInput = whichPiece.matcher(input);
		}

		xCoordinate = Character.getNumericValue(input.charAt(0));
		yCoordinate = Character.getNumericValue(input.charAt(2));

		// we will keep asking the player for a valid unit until we get one.
		while (true) {
			pieceToMove = myBoard.getPiece(xCoordinate, yCoordinate);
			if (pieceToMove == 'K') {
				System.out
						.println("You are moving the King at position: " + "(" + xCoordinate + "," + yCoordinate + ")");
				// They've selected a valid unit: verify it can move before
				// preceding.
				moveList = myBoard.availableMoves(Board.actualGameState, xCoordinate, yCoordinate);
				if (moveList.size() != 0) {
					break;
				} else {
					System.out.println("but the King has no available moves, please select another unit!");
				}
			} else if (pieceToMove == 'G') {
				System.out.println(
						"You are moving the Guard at position: " + "(" + xCoordinate + "," + yCoordinate + ")");
				// They've selected a valid unit: verify it can move before
				// preceding.
				moveList = myBoard.availableMoves(Board.actualGameState, xCoordinate, yCoordinate);
				if (moveList.size() != 0) {
					break;
				} else {
					System.out.println("but that Guard has no available moves, please select another unit!");
				}
			} else {
				System.out.println("Invalid input, can't move: " + pieceToMove);
				System.out.println("Please enter another coordinate");
			}
			input = myScanner.nextLine();
			userInput = whichPiece.matcher(input);

			while (!userInput.matches()) {
				System.out.println(
						"Enter the x and y coordinates of the unit you would like to move, separated by a space");
				System.out.println("\tExample: 0 0");
				input = myScanner.nextLine();
				userInput = whichPiece.matcher(input);
			}

			xCoordinate = Character.getNumericValue(input.charAt(0));
			yCoordinate = Character.getNumericValue(input.charAt(2));
		}

		// Step five: ask the player where they would like to move that unit and
		// then move it.
		moveUnit(myUnits, moveList, xCoordinate, yCoordinate, pieceToMove);

		String stateString = Board.actualGameState.stringify();
		
		if(states.containsKey(stateString)){
			states.put(stateString, states.get(stateString) + 1);
		} else {
			states.put(stateString, 0);
		}
		
		if(states.get(stateString) == stateLimit){
			return true;
		}
		
		Board.actualGameState.nextTurn();

		return false;
	}

	/**
	 * Called when the human player is playing as dragons to execute their turn
	 * 
	 * @return true if the game is in a draw state, false otherwise.
	 */
	private boolean playerDragonTurn() {
		// variables used
		ArrayList<gamePiece> myUnits = myBoard.getTeamTwo();
		int xCoordinate;
		int yCoordinate;
		char pieceToMove;
		ArrayList<Tuple> moveList;

		// The input pattern.
		Pattern whichPiece = Pattern.compile("[0-4] [0-4]");

		// Step one: Check if the game is a draw or can be continued.
		if (isDraw(myUnits)) {
			return true;
		}

		// Step two: print the board.
		myBoard.printGameBoard();

		// Step three: list the units that the player has control over
		printUnitList(myUnits);

		// Step four: ask the player for which piece they would like to move.
		// this bit of code stops the user from entering invalid input and
		// crashing the program
		System.out.println("Enter the x and y coordinates of the unit you would like to move, separated by a space");
		String input = myScanner.nextLine();
		Matcher userInput = whichPiece.matcher(input);

		while (!userInput.matches()) {
			System.out
					.println("Enter the x and y coordinates of the unit you would like to move, separated by a space");
			System.out.println("\tExample: 0 0");
			input = myScanner.nextLine();
			userInput = whichPiece.matcher(input);
		}

		xCoordinate = Character.getNumericValue(input.charAt(0));
		yCoordinate = Character.getNumericValue(input.charAt(2));

		// we will keep asking the player for a valid unit until we get one.
		while (true) {
			pieceToMove = myBoard.getPiece(xCoordinate, yCoordinate);
			if (pieceToMove == 'D') {
				System.out.println(
						"You are moving the Dragon at position: " + "(" + xCoordinate + "," + yCoordinate + ")");
				// They've selected a valid unit: verify it can move before
				// preceding.
				moveList = myBoard.availableMoves(Board.actualGameState, xCoordinate, yCoordinate);
				if (moveList.size() != 0) {
					break;
				} else {
					System.out.println("but that dragon has no available moves, please select another one!");
				}
			} else {
				System.out.println("Invalid input, can't move: " + pieceToMove);
				System.out.println("Please enter another coordinate");
			}
			input = myScanner.nextLine();
			userInput = whichPiece.matcher(input);

			while (!userInput.matches()) {
				System.out.println(
						"Enter the x and y coordinates of the unit you would like to move, separated by a space");
				System.out.println("\tExample: 0 0");
				input = myScanner.nextLine();
				userInput = whichPiece.matcher(input);
			}

			xCoordinate = Character.getNumericValue(input.charAt(0));
			yCoordinate = Character.getNumericValue(input.charAt(2));
		}

		// Step five: ask the player where they would like to move that unit and
		// then move it.
		moveUnit(myUnits, moveList, xCoordinate, yCoordinate, pieceToMove);

		String stateString = Board.actualGameState.stringify();
		
		if(states.containsKey(stateString)){
			states.put(stateString, states.get(stateString) + 1);
		} else {
			states.put(stateString, 0);
		}
		
		if(states.get(stateString) == stateLimit){
			return true;
		}
		
		Board.actualGameState.nextTurn();

		return false;
	}

	/**
	 * Helper function which executes moves when it is a player's turn
	 * 
	 * @param listOfUnits
	 *            The list of the player's units
	 * @param moveList
	 *            The list of possible moves at xCoordinate, yCoordinate
	 * @param xCoordinate
	 *            The current x position of the unit to be moved
	 * @param yCoordinate
	 *            The current y position of the unit to be moved
	 * @param piece
	 *            The letter of the piece being moved
	 * 
	 *            Assumes that the x and y coordinates passed in are always
	 *            valid
	 */
	private void moveUnit(ArrayList<gamePiece> listOfUnits, ArrayList<Tuple> moveList, int xCoordinate, int yCoordinate,
			char piece) {

		// The input pattern. Since there is already range error checking,
		// this will just make sure that the input isn't going to simply
		// crash the program.
		Pattern whichSpot = Pattern.compile("\\d{1}");
		String input;
		Matcher userInput;

		int index = -1;

		while ((index < 0) || (index >= moveList.size())) {
			do {
				System.out.println("Here are the legal moves: " + moveList);
				System.out.println("Enter the index of the move you would like to make (starting from 0!)");
				input = myScanner.nextLine();
				userInput = whichSpot.matcher(input);
			} while (!userInput.matches());
			index = Integer.parseInt(input);
		}
		Tuple nextMove = moveList.get(index);
		gamePiece pieceToMove = null;

		// Note, we might have to make this better. Finds the object which
		// represents the piece we are moving
		for (gamePiece pieceToCheck : listOfUnits) {
			if (pieceToCheck.checkPosition(new Tuple(xCoordinate, yCoordinate))) {
				pieceToMove = pieceToCheck;
			}
		}

		// Piece capture move (king or guard is capturing dragon) if appropriate
		if (Board.actualGameState.getChar(nextMove.getX(), nextMove.getY()) == 'D') {
			myBoard.killDragon(nextMove.getX(), nextMove.getY());
		}

		// Simple move to unoccupied space, overwrites D if dragon was killed
		pieceToMove.changePosition(nextMove);
	}

	/**
	 * Checks if the current game is a draw, i.e. all the pieces of the current
	 * player have no available moves
	 * 
	 * @param listToCheck
	 *            List of pieces to check
	 * @return True if the game is a draw, false otherwise
	 */
	private boolean isDraw(ArrayList<gamePiece> listToCheck) {
		for (gamePiece currentPiece : listToCheck) {
			Tuple currentTuple = currentPiece.getPosition();
			if (myBoard.availableMoves(Board.actualGameState, currentTuple.getX(), currentTuple.getY()).size() != 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Prints all of a team's units to the console in a readable way
	 * 
	 * @param listToPrint
	 *            List of pieces to print
	 */
	private void printUnitList(ArrayList<gamePiece> listToPrint) {
		for (gamePiece currentPiece : listToPrint) {
			if (currentPiece.isAlive()) {
				System.out.println(currentPiece);
			}
		}
	}
	
	/**
	 * Called to execute the computer's turn using the appropriate AI algorithm
	 * 
	 * @param AI
	 *            The initialized AI instance
	 * @param AIisDragon
	 *            True if the AI is playing as Dragons, false otherwise
	 * 
	 * @return true if the game is in a draw state, false otherwise.
	 */
	private boolean playAITurn(Search AI, boolean AIisDragon) {

		// Check for draw
		if (AIisDragon && isDraw(myBoard.getTeamTwo())) {
			return true;
		} else if ((!AIisDragon) && isDraw(myBoard.getTeamOne())) {
			return true;
		}

		// Print board
		myBoard.printGameBoard();

		// Perform AI search
		GameNode n = null;
		
		if (AlphaBeta.class.isInstance(AI)) {
			n = ((AlphaBeta)AI).alphaBeta(new GameNode(Board.actualGameState.clone(), 0, 0), true, AIisDragon);
		} else if (Minimax.class.isInstance(AI)) {
			n = ((Minimax)AI).MinimaxValue(new GameNode(Board.actualGameState.clone(), 0, 0), true, AIisDragon);
		}
		
		if (n == null) {
			throw new IllegalStateException("The AI method did not return a state!");
		}
		
		// Apply the chosen state to our board.
		// Note that this flips the dragonsJustMoved boolean in the state
		// without us having to explicitly call nextTurn()
		myBoard.applyState(n.getState());
		
		String stateString = n.getState().stringify();
		
		if(states.containsKey(stateString)){
			states.put(stateString, states.get(stateString) + 1);
		} else {
			states.put(stateString, 0);
		}
		
		if(states.get(stateString) == stateLimit){
			return true;
		}
		
		return false;
	}

	/**
	 * a method to play the game until it results in a win or a draw.
	 */
	public void game() {
		boolean draw = false;
		boolean dragonsWin = false;

		System.out.println("~~~~~~~~~~~~~~~");
		System.out.println("~THE MAD KING!~");
		System.out.println("~~~~~~~~~~~~~~~");
		
		/* Let the player pick alphabeta or minimax */
		Pattern whichTeam1 = Pattern.compile("[AM]");
		System.out.print("Please enter 'A' to play with Alpha-Beta AI or 'M' to play with Minimax AI: ");
		String input1 = myScanner.nextLine();
		Matcher userInput1 = whichTeam1.matcher(input1);

		while (!userInput1.matches()) {
			System.out.print("Please enter 'A' to play with Alpha-Beta AI or 'M' to play with Minimax AI: ");
			input1 = myScanner.nextLine();
			userInput1 = whichTeam1.matcher(input1);
		}
		
		// Set team value based on user input
		char AIchoice = input1.charAt(0);
		
		/* Let the player select two or one AI*/
		Pattern howManyAI = Pattern.compile("[1-2]");
		System.out.print("Would you like to play with 1 AI or watch 2 AI play?: ");
		String inputHowMany = myScanner.nextLine();
		Matcher howMany = howManyAI.matcher(inputHowMany);

		while (!howMany.matches()) {
			System.out.print("Would you like to play with 1 AI or watch 2 AI play?: ");
			inputHowMany = myScanner.nextLine();
			howMany = howManyAI.matcher(inputHowMany);
		}
		
		char numAI = inputHowMany.charAt(0);
		
		/* Initialize the AI */
		Search AI1;
		
		/* Initialize the playerTeam */
		char playerTeam = '_';
		
		if(numAI == '1'){
			if (AIchoice == 'M') {
				AI1 = new Minimax(myBoard);
			} else {
				AI1 = new AlphaBeta(myBoard);
			}
			/* Let the player pick a team, AI will be the other team */
			Pattern whichTeam = Pattern.compile("[DK]");
			System.out.print("Please enter 'D' to play as dragons or 'K' to play as the Mad King: ");
			String input = myScanner.nextLine();
			Matcher userInput = whichTeam.matcher(input);

			while (!userInput.matches()) {
				System.out.print("Please enter 'D' to play as dragons or 'K' to play as the Mad King: ");
				input = myScanner.nextLine();
				userInput = whichTeam.matcher(input);
			}
			// Set team value based on user input
			playerTeam = input.charAt(0);
		} else {
			if (AIchoice == 'M') {
				AI1 = new Minimax(myBoard);
			} else {
				AI1 = new AlphaBeta(myBoard);
			}
		}

		/* Primary Game Loop */
		int moves = 0;
		for (; moves < turnLimit; moves++) {
			// Dragons always move first
			if (playerTeam == 'D') {
				if (playerDragonTurn()) {
					draw = true;
					break;
				}
			} else {
				if (playAITurn(AI1, true)) {
					draw = true;
					break;
				}
			}
	
			// Do post-move checks to see if the game is over or if pieces need
			// to be captured
			myBoard.checkGuardCapture();
			if (myBoard.dragonsWin(Board.actualGameState, myBoard.getKing().getPosition())) {
				dragonsWin = true;
				break;
			}
			if (myBoard.kingWins(myBoard.getKing().getPosition())) {
				break;
			}
			
			//If there is two AI playing, then we add a "pause"
			if(numAI == '2'){
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					//Won't happen
				}
			}
	
			// King always move second
			if (playerTeam == 'K') {
				if (playerKingTurn()) {
					draw = true;
					break;
				}
			} else {
				if (playAITurn(AI1, false)) {
					draw = true;
					break;
				}
			}
			
			// Do post-move checks to see if the game is over or if pieces need
			// to be captured
			myBoard.checkGuardCapture();
			if (myBoard.dragonsWin(Board.actualGameState, myBoard.getKing().getPosition())) {
				dragonsWin = true;
				break;
			}
			if (myBoard.kingWins(myBoard.getKing().getPosition())) {
				break;
			}
			//If there is two AI playing, then we add a "pause"
			if(numAI == '2'){
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					//Won't happen
				}
			}
		}
		
		/*
		 * If the primary loop above exited because we exceeded 50 moves, the
		 * game is a draw
		 */
		if (moves >= turnLimit) {
			draw = true;
		}

		/* Print result of game */
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		myBoard.printGameBoard();
		if (draw) {
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println("~Nobody wins, it's a draw!~");
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		} else if (dragonsWin) {
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println("~The Dragons Win! Congratulations!~");
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		} else {
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println("~The King Wins! Congratulations!~");
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		}
	}

	/**
	 * where the game is launched from
	 * 
	 * @param args
	 *            not used.
	 */
	public static void main(String[] args) {
		Controller gameController = new Controller();

		gameController.game();
	}
}
