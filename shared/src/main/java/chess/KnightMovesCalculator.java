package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition startPosition;
    private final ChessPiece.PieceType type;

    public KnightMovesCalculator(ChessBoard board, ChessPosition startPosition, ChessPiece.PieceType type) {
        this.board = board;
        this.startPosition = startPosition;
        this.type = type;

    }


    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return this.startPosition;
    }

    public Collection<ChessMove> LegalMoves() {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = this.startPosition.getRow();
        int col = this.startPosition.getColumn();

        ChessPosition startPosition = new ChessPosition(row,col);
        System.out.println("("+this.startPosition.getRow() +", "+this.startPosition.getColumn()+ ") This is the start Position of King");




        int i = 1;
        moves.add(new ChessMove(startPosition,new ChessPosition(row+2,col+1),type));
        moves.add(new ChessMove(startPosition,new ChessPosition(row+2,col-1),type));
        moves.add(new ChessMove(startPosition,new ChessPosition(row-2,col+1),type));
        moves.add(new ChessMove(startPosition,new ChessPosition(row-2,col-1),type));
        moves.add(new ChessMove(startPosition,new ChessPosition(row+1,col+2),type));
        moves.add(new ChessMove(startPosition,new ChessPosition(row+1,col-2),type));
        moves.add(new ChessMove(startPosition,new ChessPosition(row-1,col-2),type));
        moves.add(new ChessMove(startPosition,new ChessPosition(row-1,col+2),type));

        return moves;
    }
}

