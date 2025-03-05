package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class PawnMovesCalculator {
    private ChessPosition pos;
    private ChessBoard board;

    public PawnMovesCalculator(ChessBoard board, ChessPosition pos) {
        this.board = board;
        this.pos = pos;
    }


    public Collection<ChessMove> legalMoves(ChessBoard board, ChessPosition myPos) {
        ChessPiece piece = board.getPiece(myPos);
        ChessGame.TeamColor color = piece.getTeamColor();
        Collection<ChessMove> movesCopy;

        int row = myPos.getRow();
        int col = myPos.getColumn();
        int[] pawnRowOffsets = new int[0];
        int[] pawnColOffsets = new int[0];

        if (color == WHITE){
            pawnRowOffsets = new int[]{  1, 1, 1, 2 };
            pawnColOffsets = new int[]{ -1, 0, 1, 0 };
        }
        if (color == BLACK){
            pawnRowOffsets = new int[]{ -1, -1, -1, -2 };
            pawnColOffsets = new int[]{ -1,  0,  1,  0 };
        }



        movesCopy = MoveCalcHelper.getInstance().calcLegalMovesFromAllPositions(
                pawnRowOffsets,
                pawnColOffsets,
                board,
                myPos,
                color);

        System.out.println("movesCopy: " + movesCopy);

        Collection<ChessMove> legalMoves = new ArrayList<>(movesCopy);

        for (ChessMove move : movesCopy){
            int endRow = move.getEndPosition().getRow();
            int endCol = move.getEndPosition().getColumn();
            ChessPosition endPos = move.getEndPosition();
            ChessPiece endPiece = board.getPiece(endPos);
            Collection<ChessPiece.PieceType> promoList = List.of(QUEEN, KNIGHT, BISHOP, ROOK);

            // diagonal move with no piece to capture
            if (endCol!=col && endPiece==null){
                legalMoves.remove(move);
                continue;
            }

            // forward single move with piece blocking way
            if (endPiece!=null && endCol==col){
                legalMoves.remove(move);
                continue;
            }

            // forward double illegal start position
            if ((endRow==row+2 && row!=2) ||
                    endRow==row-2 && row!=7){
                legalMoves.remove(move);
                continue;
            }

            // WHITE diagonal promotion update
            if (endRow==8 && color==WHITE && endCol!=col){
                legalMoves.remove(move);
                for (ChessPiece.PieceType promo : promoList ){
                    ChessMove promoMove = new ChessMove(myPos,endPos,promo);
                    legalMoves.add(promoMove);
                }
                continue;
            }

            // WHITE straight promotion update
            if (endRow==8 && color==WHITE){
                legalMoves.remove(move);
                for (ChessPiece.PieceType promo : promoList ){
                    ChessMove promoMove = new ChessMove(myPos,endPos,promo);
                    legalMoves.add(promoMove);
                }
                continue;
            }

            // BLACK diagonal promotion update
            if (endRow==1 && color==BLACK && endCol!=col){
                legalMoves.remove(move);
                for (ChessPiece.PieceType promo : promoList ){
                    ChessMove promoMove = new ChessMove(myPos,endPos,promo);
                    legalMoves.add(promoMove);
                }
                continue;
            }

            // BLACK straight promotion update
            if (endRow==1 && color==BLACK){
                legalMoves.remove(move);
                for (ChessPiece.PieceType promo : promoList ){
                    ChessMove promoMove = new ChessMove(myPos,endPos,promo);
                    legalMoves.add(promoMove);
                }
                continue;
            }


            // forward double WHITE move
            ChessPosition midWhitePos = new ChessPosition(row+1,col);
            ChessPiece midWhitePiece = board.getPiece(midWhitePos);
            if (endRow==row+2 && midWhitePiece!=null){
                legalMoves.remove(move);
            }

            // forward double BLACK move
            ChessPosition midBlackPos = new ChessPosition(row-1,col);
            ChessPiece midBlackPiece = board.getPiece(midBlackPos);
            if (endRow==row-2 && midBlackPiece!=null){
                legalMoves.remove(move);
            }



        }

        return legalMoves;
    }
}

