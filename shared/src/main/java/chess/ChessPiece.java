package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor color;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceType type = board.getPiece(myPosition).getPieceType();
        Collection<ChessMove> moves = new ArrayList<>();

        if (type == PieceType.BISHOP){
            BishopMovesCalculator b_calc = new BishopMovesCalculator(board,myPosition);
            moves = b_calc.legalMoves(board,myPosition);
            return moves;

        }
        if (type == PieceType.ROOK){
            RookMovesCalculator r_calc = new RookMovesCalculator(board,myPosition);
            moves = r_calc.legalMoves(board,myPosition);
            return moves;

        }
        if (type == PieceType.QUEEN){
            RookMovesCalculator r_calc = new RookMovesCalculator(board,myPosition);
            moves = r_calc.legalMoves(board,myPosition);

            BishopMovesCalculator b_calc = new BishopMovesCalculator(board,myPosition);
            for (ChessMove move :b_calc.legalMoves(board,myPosition)){
                moves.add(move);
            }
            return moves;

        }
        if (type == PieceType.KING){
            KingMovesCalculator K_calc = new KingMovesCalculator(board,myPosition);
            moves = K_calc.legalMoves(board,myPosition);
            return moves;

        }
        if (type == PieceType.KNIGHT){
            KnightMovesCalculator Knight_calc = new KnightMovesCalculator(board,myPosition);
            moves = Knight_calc.legalMoves(board,myPosition);
            return moves;

        }
        if (type == PieceType.PAWN){
            PawnMovesCalculator p_calc = new PawnMovesCalculator(board,myPosition);
            moves = p_calc.legalMoves(board,myPosition);
            return moves;

        }


        System.out.print("ChessPiece PieceMoves broke");
        return moves;
    }
}
