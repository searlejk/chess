package chess;

import java.util.Collection;

public class SlidingMovesCalc {

    // creates just one instance
    private static final SlidingMovesCalc instance = new SlidingMovesCalc();

    // private constructor so it can't be instantiated
    private SlidingMovesCalc() { };

    // provides public access to this instance
    public static SlidingMovesCalc getInstance() {
        return instance;
    }

    public void slidingMovesCalc(
            Collection<ChessMove> moves,
            ChessBoard board,
            ChessPosition myPos,
            int incRow,
            int incCol,
            ChessGame.TeamColor color){

        // set first row and col with incRow & incCol
        int row = myPos.getRow() + incRow;
        int col = myPos.getColumn() + incCol;

        while(row>=1 && row <=8 && col>=1 && col <=8){
            ChessPosition newPos = new ChessPosition(row,col);
            ChessPiece pieceAtNewPos = board.getPiece(newPos);
            ChessMove newMove = new ChessMove(myPos,newPos,null);
            // If there is a piece
            if (pieceAtNewPos!=null){
                // If piece is other team
                if (pieceAtNewPos.getTeamColor()!=color){
                    moves.add(newMove);
                }
                // Stop incrementing once you hit a piece
                break;
            } else {
                // Add piece for empty Square
                moves.add(newMove);
            }
            // Increment row & col (depending on incRow and incCol)
            row += incRow;
            col += incCol;
        }

    }
}
