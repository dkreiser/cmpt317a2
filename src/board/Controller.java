package board;

import java.util.ArrayList;
import java.util.Scanner;

import cmpt317A2.Tuple;
import gamepiece.gamePiece;

public class Controller {

	/** we never want to make a duplicate copy of this class. */
	private Controller() {}

	/** an instance of the game board */
	public Board myBoard = new Board();

	/** a scanner created to handle user input */
	private Scanner myScanner = new Scanner(System.in);

	/**
	 * instructions to make player ones turn.
	 * 
	 * @return true if the game is in a draw state, false otherwise.
	 */
	private boolean playerOneTurn() {
		// variables used
		ArrayList<gamePiece> myUnits = myBoard.getTeamOne();
		int xCoordinate;
		int yCoordinate;
		char pieceToMove;
		ArrayList<Tuple> moveList;

		// Step one: check if the game is a draw or can be continued.
		isDraw(myUnits);

		// Step two: print the board
		myBoard.printGameBoard();

		// Step three: list the units that the player has control over
		printUnitList(myUnits);

		// Step four: ask the player for which piece they would like to move.
		System.out.println("Enter the x and y coordinates of the unit you would like to move, separated by a space");
		xCoordinate = myScanner.nextInt();
		yCoordinate = myScanner.nextInt();
		myScanner.nextLine(); // this line is to force the scanner to ignore any
								// other input

		// we will keep asking the player for a valid unit until we get one.
		while (true) {
			pieceToMove = myBoard.getPiece(xCoordinate, yCoordinate);
			if (pieceToMove == 'K') {
				System.out
						.println("You are moving the King at position: " + "(" + xCoordinate + "," + yCoordinate + ")");
				// They've selected a valid unit: verify it can move before
				// preceding.
				moveList = myBoard.availableMoves(xCoordinate, yCoordinate);
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
				moveList = myBoard.availableMoves(xCoordinate, yCoordinate);
				if (moveList.size() != 0) {
					break;
				} else {
					System.out.println("but that Guard has no available moves, please select another unit!");
				}
			} else {
				System.out.println("Invalid input, can't move: " + pieceToMove);
				System.out.println("Please enter another coordinate");
			}
			xCoordinate = myScanner.nextInt();
			yCoordinate = myScanner.nextInt();
			myScanner.nextLine();
		}

		// Step five: ask the player where they would like to move that unit and
		// then move it.
		moveUnit(myUnits, moveList, xCoordinate, yCoordinate, pieceToMove);

		return false;
	}

	/**
	 * instructions to make player twos turn.
	 * 
	 * @return true if the game is in a draw state, false otherwise.
	 */
	private boolean playerTwoTurn() {
		// variables used
		ArrayList<gamePiece> myUnits = myBoard.getTeamTwo();
		int xCoordinate;
		int yCoordinate;
		char pieceToMove;
		ArrayList<Tuple> moveList;

		// Step one: Check if the game is a draw or can be continued.
		isDraw(myUnits);

		// Step two: print the board.
		myBoard.printGameBoard();

		// Step three: list the units that the player has control over
		printUnitList(myUnits);

		// Step four: ask the player for which piece they would like to move.
		System.out.println("Enter the x and y coordinates of the unit you would like to move, separated by a space");
		xCoordinate = myScanner.nextInt();
		yCoordinate = myScanner.nextInt();
		myScanner.nextLine(); // this line is to force the scanner to ignore any
								// other input

		// we will keep asking the player for a valid unit until we get one.
		while (true) {
			pieceToMove = myBoard.getPiece(xCoordinate, yCoordinate);
			if (pieceToMove == 'D') {
				System.out.println(
						"You are moving the Dragon at position: " + "(" + xCoordinate + "," + yCoordinate + ")");
				// They've selected a valid unit: verify it can move before
				// preceding.
				moveList = myBoard.availableMoves(xCoordinate, yCoordinate);
				if (moveList.size() != 0) {
					break;
				} else {
					System.out.println("but that dragon has no available moves, please select another one!");
				}
			} else {
				System.out.println("Invalid input, can't move: " + pieceToMove);
				System.out.println("Please enter another coordinate");
			}
			xCoordinate = myScanner.nextInt();
			yCoordinate = myScanner.nextInt();
			myScanner.nextLine();
		}

		// Step five: ask the player where they would like to move that unit and
		// then move it.
		moveUnit(myUnits, moveList, xCoordinate, yCoordinate, pieceToMove);

		return false;
	}

	// Helper functions to make the player turns.
	// Assuming that x and y coordinates passed in are always valid
	private void moveUnit(ArrayList<gamePiece> listOfUnits, ArrayList<Tuple> moveList, int xCoordinate, int yCoordinate,
			char piece) {
		int index = -1;
		while ((index < 0) || (index >= moveList.size())) {
			System.out.println("Here are the legal moves: " + moveList);
			System.out.println("Enter the index of the move you would like to make (starting from 0!)");
			index = myScanner.nextInt();
			myScanner.nextLine();
		}
		Tuple nextMove = moveList.get(index);
		gamePiece pieceToMove = null;
		
		// Note, we might have to make this better. Finds the object which represents the piece we are moving
		for (gamePiece pieceToCheck : listOfUnits) {
			if (pieceToCheck.checkPosition(new Tuple(xCoordinate, yCoordinate))) {
				pieceToMove = pieceToCheck;
			}
		}
		
		// Piece capture move (king or guard is capturing dragon) if appropriate
		if (Board.gameBoard.getChar(nextMove.getX(), nextMove.getY()) == 'D') {
			myBoard.killDragon(nextMove.getX(), nextMove.getY());
		}
		
		// Simple move to unoccupied space, overwrites D if dragon was killed
		pieceToMove.changePosition(nextMove);
		

	}

	private boolean isDraw(ArrayList<gamePiece> listToCheck) {
		for (gamePiece currentPiece : listToCheck) {
			Tuple currentTuple = currentPiece.getPosition();
			if (myBoard.availableMoves(currentTuple.getX(), currentTuple.getY()).size() != 0) {
				return false;
			}
		}
		return true;
	}

	private void printUnitList(ArrayList<gamePiece> listToPrint) {
		for (gamePiece currentPiece : listToPrint) {
			if (currentPiece.isAlive()) {
				System.out.println(currentPiece);
			}
		}
	}

	/**
	 * a method to play the game until it results in a win or a draw.
	 */
	public void game() {
		boolean draw = false;
		boolean dragonsWin = false;
		while (true) {
			if (playerTwoTurn()) {
				draw = true;
				break;
			}
			myBoard.checkGuardCapture();
			if (myBoard.dragonsWin()) {
				dragonsWin = true;
				break;
			}
			if (myBoard.kingWins()) {
				break;
			}
			if (playerOneTurn()) {
				draw = true;
				break;
			}
			myBoard.checkGuardCapture();
			if (myBoard.dragonsWin()) {
				dragonsWin = true;
				break;
			}
			if (myBoard.kingWins()) {
				break;
			}
		}
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
	 * where the game is played from for now.
	 * 
	 * @param args
	 *            not used.
	 */
	public static void main(String[] args) {
		Controller testController = new Controller();

		testController.game();
	}
}