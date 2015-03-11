import java.util.*;
import java.awt.*;

public class Cube {

 		/* Constants */
 	
 	final static int NUM_SIDES = 6;

 	final static int 	U = 0,
 						L = 1,
 						F = 2,
 						R = 3,
 						B = 4,
 						D = 5;

 	// Given a face, return the value of that side
 	final static HashMap<String, Integer> sides = new HashMap<String, Integer>() {
		{

			this.put("U", 0);
			this.put("L", 1);
			this.put("F", 2);
			this.put("R", 3);
			this.put("B", 4);
			this.put("D", 5);

		}
	};

	// Given a value of a side, return the face
	final static HashMap<Integer, String> faces = new HashMap<Integer, String>() {
		{

			this.put(0, "U");
			this.put(1, "L");
			this.put(2, "F");
			this.put(3, "R");
			this.put(4, "B");
			this.put(5, "D");

		}
	};

	final static String[][] possibleMoves = new String[][] {


		// 0x0x0
		{},
		
		// 1x1x1
		{},

		// 2x2x2
		{
			"B", "B2", "B'",
			"D", "D2", "D'",
			"F", "F2", "F'",
			"L", "L2", "L'",
			"R", "R2", "R'",
			"U", "U2", "U'"
		},

		// 3x3x3
		{
			"B", "B2", "B'", //"Bw", "Bw2", "Bw'",
			"D", "D2", "D'", //"Dw", "Dw2", "Dw'",
			"F", "F2", "F'", //"Fw", "Fw2", "Fw'",
			"L", "L2", "L'", //"Lw", "Lw2", "Lw'",
			"R", "R2", "R'", //"Rw", "Rw2", "Rw'",
			"U", "U2", "U'"//, "Uw", "Uw2", "Uw'"
		}

	};

	final static Cube solved3x3x3 = new Cube(3, true);	

 		/* Variables */
 	
 	public Sticker[][][] stickers;
 	public Point[][][] stickersOriginOnCanvas;
 	public int n = -1;

 	/* Constructor */
 	public Cube(int n, boolean solved) {

 		// Set instance variables
 		this.n = n;
 		stickers = new Sticker[NUM_SIDES][n][n];
 		stickersOriginOnCanvas = new Point[NUM_SIDES][n][n];

 		// Set initial state of cube (either solved or blank)
 		resetState(solved);

 	}

 	/* Make the cube blank or solved, based on parameter */
 	private void resetState(boolean solved) {

 		for (int i = 0; i < NUM_SIDES; i++)
			for (int j = 0; j < n; j++)
				for (int k = 0; k < n; k++)
					stickers[i][j][k] = Sticker.get(solved ? i : -1);

 	}

 	/* Apply a scramble to the solved state */
 	public String setState(String str) {

 		resetState(true);

 		String[] moves = str.split(" +");

 		String movesApplied = "";

 		for (String move : moves)
 			if (parseMove(move))
 				movesApplied += " " + move; 

 		return movesApplied.trim();

 	}

 	/* Parse a single move in the form of a String and apply it to the cube */
 	public boolean parseMove(String str) {
 		try {

 			String move = new String(str);

 				/* Parse input to determine the number of slices being turned */
 			
 			int nSlices = 1;
 			/**
 			int indexOfW = move.indexOf("w");
 			if (indexOfW != -1) {
 				move = move.substring(0, indexOfW) + move.substring(indexOfW + 1);
 				nSlices = 2;
 			}

 			if (Character.isDigit(move.charAt(0))) {
 				nSlices = Integer.valueOf(move.charAt(0) - '0');
 				move = move.substring(1);
 			}

 			// Check to see if too many slices are being turned
 			if (nSlices > (n % 2 == 0 ? n/2 : n/2 + 1))
 				return false;
 			**/

 				/* Parse input to determine the number of times that side needs to be turns clockwise */

			int rotations = 1;

			if (move.length() == 2) {
				if (move.charAt(1) == '\'')
					rotations = 3;
				else if (move.charAt(1) == '2')
					rotations = 2;
				else
					return false;

			// Invalid move caused by nonsense trailing characters
			} else if (move.length() > 2)
				return false;

				/* Apply move */

			String letter = move.substring(0, 1);
			Integer side = sides.get(letter);

			// Treat command as a move
			if (side != null)
				applyMove(nSlices, rotations, side);

			/**
			// Treat command as a rotation
			else if (letter.equals("x"))
				x(rotations);
			else if (letter.equals("y"))
				y(rotations);
			else if (letter.equals("z"))
				z(rotations);
			**/

			// Report invalid move
			else
				return false;
				
		} catch (StringIndexOutOfBoundsException e) {
			return false;
		}

		return true;

 	}

 	/* Apply move to the puzzle */
 	public void applyMove(int nSlices, int rotations, int side) {

 		// For each slice
 		for (int k = 0; k < nSlices; k++)

			// For each sticker in the layer
			for (int j = 0; j < n; j++) {

				// Left side
				if (side == sides.get("L"))
	 				for (int i = 0 ; i < 4 - rotations; i++) {
	 					Sticker temp = stickers[0][j][k];
	 					stickers[0][j][k] = stickers[2][j][k];
	 					stickers[2][j][k] = stickers[5][j][k];
	 					stickers[5][j][k] = stickers[4][n-j-1][n-k-1];
	 					stickers[4][n-j-1][n-k-1] = temp;
						}

				// Right side
				else if (side == sides.get("R"))
					for (int i = 0 ; i < rotations; i++) {
	 					Sticker temp = stickers[0][j][n-k-1];
	 					stickers[0][j][n-k-1] = stickers[2][j][n-k-1];
	 					stickers[2][j][n-k-1] = stickers[5][j][n-k-1];
	 					stickers[5][j][n-k-1] = stickers[4][n-j-1][k];
	 					stickers[4][n-j-1][k] = temp;
	 				}

	 			// Top side
	 			else if (side == sides.get("U"))
		 			for (int i = 0 ; i < 4 - rotations; i++) {
	 					Sticker temp = stickers[1][k][j];
	 					stickers[1][k][j] = stickers[4][k][j];
	 					stickers[4][k][j] = stickers[3][k][j];
	 					stickers[3][k][j] = stickers[2][k][j];
	 					stickers[2][k][j] = temp;
	 				}

	 			// Bottom side
	 			else if (side == sides.get("D"))
	 				for (int i = 0 ; i < rotations; i++) {
	 					Sticker temp = stickers[1][n-k-1][j];
	 					stickers[1][n-k-1][j] = stickers[4][n-k-1][j];
	 					stickers[4][n-k-1][j] = stickers[3][n-k-1][j];
	 					stickers[3][n-k-1][j] = stickers[2][n-k-1][j];
	 					stickers[2][n-k-1][j] = temp;
	 				}

	 			// Front side
	 			else if (side == sides.get("F"))
		 			for (int i = 0 ; i < rotations; i++) {
		 				Sticker temp = stickers[0][n-k-1][n-j-1];
	 					stickers[0][n-k-1][n-j-1] = stickers[1][j][n-k-1];
	 					stickers[1][j][n-k-1] = stickers[5][k][j];
	 					stickers[5][k][j] = stickers[3][n-j-1][k];
	 					stickers[3][n-j-1][k] = temp;
	 				}

	 			// Back side
	 			else if (side == sides.get("B"))
	 				for (int i = 0 ; i < 4 - rotations; i++) {
	 					Sticker temp = stickers[0][k][n-j-1];
	 					stickers[0][k][n-j-1] = stickers[1][j][k];
	 					stickers[1][j][k] = stickers[5][n-k-1][j];
	 					stickers[5][n-k-1][j] = stickers[3][n-j-1][n-k-1];
	 					stickers[3][n-j-1][n-k-1] = temp;
	 				}

	 		}

 		rotateFace(side, rotations);

 	}

 	/* Rotate the designated face (faces are numbered in the Sticker enum) */
 	public void rotateFace(int face, int rotations) {

 		// Number of rotations
		for (int i = 0 ; i < rotations; i++) {

	 		Sticker[][] newFace = new Sticker[n][n];

				// Each sticker on the side
 				for (int j = 0; j < n; j++)
 					for (int k = 0; k < n; k++)
 						newFace[j][k] = stickers[face][n-k-1][j];
	 				
	 		stickers[face] = newFace;

	 	}
 	}

 	/* Rotate in the x direction (which follows R) */
 	public void x(int rotations) {

 		for (int i = 0 ; i < rotations; i++) {

			rotateFace(sides.get("R"), 1);
 			rotateFace(sides.get("L"), 3);

	 		for (int j = 0; j < n; j++)
 				for (int k = 0; k < n; k++) {
 					Sticker temp = stickers[0][j][k];
 					stickers[0][j][k] = stickers[2][j][k];
 					stickers[2][j][k] = stickers[5][j][k];
 					stickers[5][j][k] = stickers[4][n-j-1][n-k-1];
 					stickers[4][n-j-1][n-k-1] = temp;
 				}

 		}
 	}

	/* Rotate in the y direction (which follows U) */
 	public void y(int rotations) {

 		for (int i = 0 ; i < rotations; i++) {

 			rotateFace(sides.get("U"), 1);
 			rotateFace(sides.get("D"), 3);
	 		
	 		for (int j = 0; j < n; j++)
 				for (int k = 0; k < n; k++) {
 					Sticker temp = stickers[1][j][k];
 					stickers[1][j][k] = stickers[2][j][k];
 					stickers[2][j][k] = stickers[3][j][k];
 					stickers[3][j][k] = stickers[4][j][k];
 					stickers[4][j][k] = temp;
 				}
 		}
 	}

 	/* Rotate in the z direction (which follows F) */
 	public void z(int rotations) {

 		for (int i = 0 ; i < rotations; i++) {
 			
 			rotateFace(sides.get("F"), 1);
 			rotateFace(sides.get("B"), 3);

	 		for (int j = 0; j < n; j++)
 				for (int k = 0; k < n; k++) {
 					Sticker temp = stickers[0][n-k-1][n-j-1];
 					stickers[0][n-k-1][n-j-1] = stickers[1][j][n-k-1];
 					stickers[1][j][n-k-1] = stickers[5][k][j];
 					stickers[5][k][j] = stickers[3][n-j-1][k];
 					stickers[3][n-j-1][k] = temp;
 				}

 		}
 	}

 	/**

 	public void findError(String[] scramble, int index, int errorsLeft, Cube current, String moveApplied, int lastSideTurned) {

 		if (this.equals(current)) {
 			System.out.println(moveApplied);
 			return;
 		}

 		// +1 to allow for a move to be added to the end
 		if (index == scramble.length + 1)
 			return;

 		if (errorsLeft > 0) {

 			for (String move : possibleMoves[n]) {

 				// We don't want to do "R" followed by "R2" for example
 				int sideTurned = getSideTurned(move);
 				if (lastSideTurned == sideTurned)
 					continue;

 					/ Add new move to scramble /

 				Cube copy1 = (Cube) current.clone();
 				copy1.parseMove(move);
 				findError(scramble, index, errorsLeft - 1, copy1, moveApplied + " " + move, sideTurned);

 					 Replace existing move 

 				if (index < scramble.length && !move.equals(scramble[index])) {
	 				Cube copy2 = (Cube) current.clone();
	 				copy2.parseMove(move);
	 				findError(scramble, index + 1, errorsLeft - 1, copy2, moveApplied + " " + move, sideTurned);
	 			}
 			}


 		}

 		if (index < scramble.length) {
 			
			// We don't want to do "R" followed by "R2" for example
 			int sideTurned = getSideTurned(scramble[index]);
 			if (lastSideTurned != sideTurned) {

	 			current = (Cube) current.clone();
	 			current.parseMove(scramble[index]);
	 			findError(scramble, index + 1, errorsLeft, current, moveApplied + " " + scramble[index], sideTurned);
 			} //else 
 				//System.out.println(sideTurned + " " + lastSideTurned);

 		}

 	}

 	**/

 	/* Ex: "2Rw'" will return value corresponding to R */
 	public int getSideTurned(String move) {

 		Set<String> setOfSides = sides.keySet();
 		Iterator<String> iter = setOfSides.iterator();

 		while (iter.hasNext()) {
 			String side = iter.next();
 			if (move.indexOf(side) != -1)
 				return sides.get(side);
 		}

 		return -1;
 	}

 	/* Clone the cube */
 	@Override protected Object clone() {

 		Cube copy = new Cube(n, false);

 		for (int i = 0; i < NUM_SIDES; i++)
			for (int j = 0; j < n; j++)
				for (int k = 0; k < n; k++)
					copy.stickers[i][j][k] = stickers[i][j][k];

    	return (Object) copy;
	}

	/* Check to see if two cubes are equal (regardless of orentientation) */
	@Override public boolean equals(Object obj) {

		Cube other = (Cube) obj;

		// Cubes are not the same size
		if (n != other.n)
			return false;

		other = (Cube) other.clone();

		orientations: for (int o = 0; o < 24; o++) {

				/* Preform necessary rotations to bring cube to the next rotation */

			// White face on top
			if (o < 4) {
				other.y(1);
			
			// Green face on top
			} else if (o < 8) {
				if (o == 4)
					other.x(1);
				other.y(1);
			
			// Yellow face on top
			} else if (o < 12) {
				if (o == 8)
					other.x(1);
				other.y(1);
			
			// Blue face on top
			} else if (o < 16) {
				if (o == 12)
					other.x(1);
				other.y(1);

			// Orange face on top
			} else if (o < 20) {
				if (o == 16)
					other.z(1);
				other.y(1);

			// Red face on top
			} else if (o < 24) {
				if (o == 20)
					other.z(2);
				other.y(1);
			}

				/* Look for differences */

			for (int i = 0; i < NUM_SIDES; i++)
				for (int j = 0; j < n; j++)
					for (int k = 0; k < n; k++)
						if (other.stickers[i][j][k] != stickers[i][j][k])
							if (other.stickers[i][j][k] != Sticker.BLANK && stickers[i][j][k] != Sticker.BLANK)
								continue orientations;

			// If we got this far, we found a match!
			return true;

		}

		// If we got this far, the cubes are not identical
		return false;

	}

	/* Turn a move sequence like "R1R3F3B2" to "F' B2" */
	public static String beautify(String str) {

			/* Setup */

		String beautifulString = "";

			/* Add a space between each move */
		
		for (int i = 0; i < str.length(); i += 2)
			beautifulString += " " + str.substring(i, i + 2);

		beautifulString = beautifulString.trim();

			/* Cancel moves */

		int lengthBefore, lengthAfter;
		do {
			lengthBefore = beautifulString.length();
			beautifulString = cancelMoves(beautifulString);
			lengthAfter = beautifulString.length();
		}
		while (lengthBefore != lengthAfter);

			/* Finish up */

		// Replace 1's and 3's to make notation consistent with WCA notation
		beautifulString = beautifulString.replaceAll("1", "");
		beautifulString = beautifulString.replaceAll("3", "'");

		return beautifulString;

	}

	private static String cancelMoves(String str) {

			/* Setup */

		String[] moves = str.split(" +");
		String cancelledString = "";
		char lastMove = '?';
		int count = 0;

			/* Simplifies something like R2 L1 R1 into R3 L1, where same-sided moves are separated by an opposite-sided move */

		for (int i = 0; i < moves.length - 2; i++) {

			// Skip moves already removed
			if (moves[i].length() == 0 || moves[i + 1].length() == 0)
				continue;

			// Parse moves
			char firstMove = moves[i].charAt(0);
			char secondMove = moves[i + 1].charAt(0);
			char thirdMove = moves[i + 2].charAt(0);
			int firstRotations = Integer.valueOf(moves[i].substring(1));
			int secondRotations = Integer.valueOf(moves[i + 1].substring(1));
			int thirdRotations = Integer.valueOf(moves[i + 2].substring(1));

			// Found identical moves
			if (firstMove == thirdMove) {

				if   ( (firstMove == 'D' || firstMove == 'U') && (secondMove == 'D' || secondMove == 'U') 
					|| (firstMove == 'B' || firstMove == 'F') && (secondMove == 'B' || secondMove == 'F') 
					|| (firstMove == 'L' || firstMove == 'R') && (secondMove == 'L' || secondMove == 'R') 
					) {
					if ((firstRotations + secondRotations) % 4 != 0)
						moves[i] = firstMove + "" + (firstRotations + secondRotations);
					else
						moves[i] = "";
					moves[i + 2] = "";
				}

			}

		}

			/* Reassemble move sequence */

		for (String move : moves) {

			// Skip moves that were removed above
			if (move.length() == 0)
				continue;

			// Parse move
			char thisMove = move.charAt(0);
			int nRotations = Integer.valueOf(move.substring(1));

			// Repeated move
			if (thisMove == lastMove) {
				count = (count + nRotations) % 4;

			// New move
			} else {

				if (lastMove != '?' && count != 4 && count != 0)
					cancelledString += " " + lastMove + "" + count;

				lastMove = thisMove;
				count = nRotations;

			}

		}

		if (lastMove != '?' && count != 4 && count != 0)
			cancelledString += " " + lastMove + count;

		return cancelledString.trim();

	}

	/* Returns state of the cube (Solved state is: BU FU BD LD LU UR RD RF RB LF DF LB BLU BUR FUL FLD RUF FDR LBD BRD) */
	public String getState() {

		return ""

			+ faces.get(stickers[U][2][1].ordinal) + faces.get(stickers[F][0][1].ordinal) + " " // UF
			+ faces.get(stickers[U][1][2].ordinal) + faces.get(stickers[R][0][1].ordinal) + " " // UR
			+ faces.get(stickers[U][0][1].ordinal) + faces.get(stickers[B][0][1].ordinal) + " " // UB
			+ faces.get(stickers[U][1][0].ordinal) + faces.get(stickers[L][0][1].ordinal) + " " // UL

			+ faces.get(stickers[D][0][1].ordinal) + faces.get(stickers[F][2][1].ordinal) + " " // DF
			+ faces.get(stickers[D][1][2].ordinal) + faces.get(stickers[R][2][1].ordinal) + " " // DR
			+ faces.get(stickers[D][2][1].ordinal) + faces.get(stickers[B][2][1].ordinal) + " " // DB
			+ faces.get(stickers[D][1][0].ordinal) + faces.get(stickers[L][2][1].ordinal) + " " // DL

			+ faces.get(stickers[F][1][2].ordinal) + faces.get(stickers[R][1][0].ordinal) + " " // FR
			+ faces.get(stickers[F][1][0].ordinal) + faces.get(stickers[L][1][2].ordinal) + " " // FL
			+ faces.get(stickers[B][1][0].ordinal) + faces.get(stickers[R][1][2].ordinal) + " " // BR
			+ faces.get(stickers[B][1][2].ordinal) + faces.get(stickers[L][1][0].ordinal) + " " // BL

			+ faces.get(stickers[U][2][2].ordinal) + faces.get(stickers[F][0][2].ordinal) + faces.get(stickers[R][0][0].ordinal) + " " // UFR
			+ faces.get(stickers[U][0][2].ordinal) + faces.get(stickers[R][0][2].ordinal) + faces.get(stickers[B][0][0].ordinal) + " " // URB
			+ faces.get(stickers[U][0][0].ordinal) + faces.get(stickers[B][0][2].ordinal) + faces.get(stickers[L][0][0].ordinal) + " " // UBL
			+ faces.get(stickers[U][2][0].ordinal) + faces.get(stickers[L][0][2].ordinal) + faces.get(stickers[F][0][0].ordinal) + " " // ULF

			+ faces.get(stickers[D][0][2].ordinal) + faces.get(stickers[R][2][0].ordinal) + faces.get(stickers[F][2][2].ordinal) + " " // DRF
			+ faces.get(stickers[D][0][0].ordinal) + faces.get(stickers[F][2][0].ordinal) + faces.get(stickers[L][2][2].ordinal) + " " // DFL
			+ faces.get(stickers[D][2][0].ordinal) + faces.get(stickers[L][2][0].ordinal) + faces.get(stickers[B][2][2].ordinal) + " " // DLB
			+ faces.get(stickers[D][2][2].ordinal) + faces.get(stickers[B][2][0].ordinal) + faces.get(stickers[R][2][2].ordinal);	   // DBR

	}


 } // Cube Class