package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotion;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotion = promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotion);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        }
        if (obj == this){
            return true;
        }
        if (this.getClass() != obj.getClass()){
            return false;
        }

        ChessMove c = (ChessMove)obj;
        boolean test = this.startPosition.getRow()==c.startPosition.getRow();
        boolean test1 = this.startPosition.getColumn()==c.startPosition.getColumn();
        boolean test2 = this.endPosition.getRow()==c.endPosition.getRow();
        boolean test3 = this.endPosition.getColumn()==c.endPosition.getColumn();
        boolean test4 = this.promotion == c.promotion;


        return( test && test1 && test2 && test3 && test4 );
    }


    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return this.startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return this.endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return this.promotion;
    }
}
