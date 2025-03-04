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
        Collection<ChessPosition> allPos = new ArrayList<>();
        Collection<ChessMove> legalMoves = new ArrayList<>();
        // board
        // myPos
        ChessGame.TeamColor color = board.getPiece(myPos).getTeamColor();

        // add all Knight Positions to allPos
        int[] knightRowOffsets = { 2, 2, 1, 1, -1, -1, -2, -2 };
        int[] knightColOffsets = { 1, -1, 2, -2, 2, -2, 1, -1 };

        for (int i = 0; i < knightRowOffsets.length; i++) {
            allPos.add(new ChessPosition(
                    myPos.getRow() + knightRowOffsets[i],
                    myPos.getColumn() + knightColOffsets[i]));
        }

        MoveCalcHelper.getInstance().calcLegalMovesFromAllPositions(
                allPos,
                legalMoves,
                board,
                myPos,
                color
        );

        return legalMoves;
    }
}
