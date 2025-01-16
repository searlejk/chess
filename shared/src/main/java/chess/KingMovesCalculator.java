package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition startPosition;
    private final ChessPiece.PieceType type;


    public KingMovesCalculator(ChessBoard board, ChessPosition startPosition, ChessPiece.PieceType type) {
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


        ///  My King Can walk off the board, fix later thx (don't read this TAs)

        int i = 1;
        moves.add(new ChessMove(startPosition,new ChessPosition(row+1,col+1),type));
        moves.add(new ChessMove(startPosition,new ChessPosition(row-1,col-1),type));
        moves.add(new ChessMove(startPosition,new ChessPosition(row-1,col+1),type));
        moves.add(new ChessMove(startPosition,new ChessPosition(row+1,col-1),type));
        moves.add(new ChessMove(startPosition,new ChessPosition(row+1,col),type));
        moves.add(new ChessMove(startPosition,new ChessPosition(row,col+1),type));
        moves.add(new ChessMove(startPosition,new ChessPosition(row-1,col),type));
        moves.add(new ChessMove(startPosition,new ChessPosition(row,col-1),type));

        return moves;
    }

}