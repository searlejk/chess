package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class KingMovesCalculator {
    private ChessPosition pos;
    private ChessBoard board;

    public KingMovesCalculator(ChessBoard board, ChessPosition pos) {
        this.board = board;
        this.pos = pos;
    }


    public Collection<ChessMove> legalMoves(ChessBoard board, ChessPosition myPos) {
        int[] kingRowOffsets = { 1, 1, 1, 0, 0, -1, -1, -1 };
        int[] kingColOffsets = { 1, 0, -1, 1, -1, 1, 0, -1 };
        Collection<ChessMove> legalMoves = new ArrayList<>();
        // board
        // myPos
        ChessGame.TeamColor color = board.getPiece(myPos).getTeamColor();

        // Pass everything into MoveCalcHelper
        MoveCalcHelper.getInstance().calcLegalMovesFromAllPositions(
                kingRowOffsets,
                kingColOffsets,
                legalMoves,
                board,
                myPos,
                color
        );

        return legalMoves;
    }
}
