package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class KnightMovesCalculator {
    private ChessPosition pos;
    private ChessBoard board;

    public KnightMovesCalculator(ChessBoard board, ChessPosition pos) {
        this.board = board;
        this.pos = pos;
    }


    public Collection<ChessMove> legalMoves(ChessBoard board, ChessPosition myPos) {
        int[] knightRowOffsets = { 2, 2, 1, 1, -1, -1, -2, -2 };
        int[] knightColOffsets = { 1, -1, 2, -2, 2, -2, 1, -1 };
        // board
        // myPos
        ChessGame.TeamColor color = board.getPiece(myPos).getTeamColor();

        // Pass everything into MoveCalcHelper
        return MoveCalcHelper.getInstance().calcLegalMovesFromAllPositions(
                knightRowOffsets,
                knightColOffsets,
                board,
                myPos,
                color
        );
    }
}
