package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class RookMovesCalculator {
    private ChessPosition pos;
    private ChessBoard board;

    public RookMovesCalculator(ChessBoard board, ChessPosition pos) {
        this.board = board;
        this.pos = pos;
    }


    public Collection<ChessMove> legalMoves(ChessBoard board, ChessPosition myPos) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPos);
        ChessGame.TeamColor color = piece.getTeamColor();

        // calculate sliding moves:
        SlidingMovesCalc.getInstance().slidingMovesCalc(
                moves,board,myPos,1,0,color);

        SlidingMovesCalc.getInstance().slidingMovesCalc(
                moves,board,myPos,-1,0,color);

        SlidingMovesCalc.getInstance().slidingMovesCalc(
                moves,board,myPos,0,1,color);

        SlidingMovesCalc.getInstance().slidingMovesCalc(
                moves,board,myPos,0,-1,color);

        return moves;
    }
}
