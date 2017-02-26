package comp2601.carleton.edu.comp2601a2client;

import java.util.ArrayList;

import static comp2601.carleton.edu.comp2601a2client.GameActivity.OMoves;
import static comp2601.carleton.edu.comp2601a2client.GameActivity.XMoves;
import static comp2601.carleton.edu.comp2601a2client.GameActivity.XOMoves;

/**
 * Created by maximkuzmenko on 2017-02-24.
 */

class Game {
    //access elements from mainactivity, make judgement about the move, send back to MainActivity
    private static final int[][] diagonalSet = {{0, 4, 8}, {2, 4, 6}};
    private static final int[][] horizontalSet = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
    private static final int[][] verticalSet = {{0, 3, 6}, {1, 4, 7}, {2, 5, 8}};

    //function checks if the game is over
    //if diagonal/horizontal/vertical matches or if all spots are filled
    public static int checkGameOver() { //(check this every time a move is played)
        if (checkSpots(diagonalSet, XMoves) || checkSpots(horizontalSet, XMoves) || checkSpots(verticalSet, XMoves)) {
            //System.out.println("Player won this round");
            return 1;
        }
        if (checkSpots(diagonalSet, OMoves) || checkSpots(horizontalSet, OMoves) || checkSpots(verticalSet, OMoves)) {
            //System.out.println("Computer won this round");
            return 2;
        }
        //System.out.println("No one won this round");
        if (XOMoves.isEmpty()){
            System.out.println("No more moves to play, stalemate...");
            return 3;
        }
        System.out.println("No one won this round");
        return 0;
    }

    private static boolean checkSpots(int[][] set, ArrayList<Integer> moves) {
        boolean didWin;
        for (int[] aSet : set) {
            didWin = true;
            for (int y = 0; y < aSet.length; y++) {
                if (moves.indexOf(aSet[y]) == -1) {
                    didWin = false;
                }
            }
            if (didWin) {
                return true;
            }
        }
        return false; //no one won yet
    }

}
