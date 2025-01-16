package chess;

import javax.lang.model.type.NullType;
import java.util.ArrayList;
import java.util.Collection;

public class MoveCalc {
    private final ChessBoard board;
    private final ChessPosition startPosition;
    private final ChessPiece.PieceType type;


    public MoveCalc(ChessBoard board, ChessPosition startPosition,
                    ChessPiece.PieceType type) {
        this.board = board;
        this.startPosition = startPosition;
        this.type = type;

    }

    /**
     * @return ChessPosition of starting location
     */
    public Collection<ChessMove> MoveCall() {
        if (this.type == ChessPiece.PieceType.BISHOP){
            BishopMovesCalculator temp = new BishopMovesCalculator(board,startPosition,type);
            return temp.LegalMoves();
        }

        if (this.type == ChessPiece.PieceType.KING){
            KingMovesCalculator temp = new KingMovesCalculator(board,startPosition,type);
            return temp.LegalMoves();
        }

        if (this.type == ChessPiece.PieceType.KNIGHT){
            KnightMovesCalculator temp = new KnightMovesCalculator(board,startPosition,type);
            return temp.LegalMoves();
        }

        if (this.type == ChessPiece.PieceType.PAWN){
            PawnMovesCalculator temp = new PawnMovesCalculator(board,startPosition,type);
            return temp.LegalMoves();
        }

        if (this.type == ChessPiece.PieceType.QUEEN){
            QueenMovesCalculator temp = new QueenMovesCalculator(board,startPosition,type);
            return temp.LegalMoves();
        }

        if (this.type == ChessPiece.PieceType.ROOK){
            RookMovesCalculator temp = new RookMovesCalculator(board,startPosition,type);
            return temp.LegalMoves();
        }


        else{
            throw new RuntimeException("Not implemented");
        }
    }

}
