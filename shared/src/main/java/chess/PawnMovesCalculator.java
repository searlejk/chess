package chess;

import java.util.ArrayList;
import java.util.Collection;

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


    public Collection<ChessMove> legalMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor color = piece.getTeamColor();

        Collection<ChessMove> moveList = new ArrayList<>();
        moveList = this.colorMoves(color, myPosition.getRow(), myPosition);

        int row = myPosition.getRow();
        int col = myPosition.getColumn();


        for (ChessMove move : moveList){
            ChessPosition pos = move.getEndPosition();

            if (pos.getRow()>8 | pos.getRow()<1 | pos.getColumn() <1 | pos.getColumn()>8){
                continue;
            }

            if (board.getPiece(pos)!=null){
                if (board.getPiece(pos).getTeamColor()==color){
                    continue;
                }
                else{
                    if (col!=pos.getColumn()){
                        moves.add(move);
                    }
                    else{
                        continue;
                    }
                }

            }else{
                if (col!=pos.getColumn()){
                    continue;
                }
                if(color==WHITE){
                    if (pos.getRow()-2==row) {
                        if (board.getPiece(new ChessPosition(pos.getRow() - 1, pos.getColumn())) == null && row==2) {
                            moves.add(move);
                            continue;
                        }else{
                            continue;
                        }
                    }
                }
                else if (color==BLACK){
                    if (pos.getRow()+2==row) {
                        if (board.getPiece(new ChessPosition(pos.getRow() + 1, pos.getColumn())) == null && row==7) {
                            moves.add(move);
                            continue;
                        }else{
                            continue;
                        }
                    }
                }
                moves.add(move);
                continue;
            }
        }
        return moves;
    }

    private Collection<ChessMove> colorMoves(ChessGame.TeamColor color, int promoRow, ChessPosition start){
        Collection<ChessMove> moves = new ArrayList<>();
        int row = start.getRow();
        int col = start.getColumn();


        if (color==WHITE && promoRow!=7) {
            moves.add(new ChessMove(start, new ChessPosition(row + 1, col), null));
            moves.add(new ChessMove(start, new ChessPosition(row + 2, col), null));
            moves.add(new ChessMove(start, new ChessPosition(row + 1, col + 1), null));
            moves.add(new ChessMove(start, new ChessPosition(row + 1, col - 1), null));
            return moves;
        }
        else if (color==WHITE && promoRow==7){
            moves.add(new ChessMove(start,new ChessPosition(row+1,col),ROOK));
            moves.add(new ChessMove(start,new ChessPosition(row+1,col),QUEEN));
            moves.add(new ChessMove(start,new ChessPosition(row+1,col),KNIGHT));
            moves.add(new ChessMove(start,new ChessPosition(row+1,col),BISHOP));


            moves.add(new ChessMove(start,new ChessPosition(row+1,col+1),ROOK));
            moves.add(new ChessMove(start,new ChessPosition(row+1,col+1),QUEEN));
            moves.add(new ChessMove(start,new ChessPosition(row+1,col+1),KNIGHT));
            moves.add(new ChessMove(start,new ChessPosition(row+1,col+1),BISHOP));
            moves.add(new ChessMove(start,new ChessPosition(row+1,col-1),ROOK));
            moves.add(new ChessMove(start,new ChessPosition(row+1,col-1),QUEEN));
            moves.add(new ChessMove(start,new ChessPosition(row+1,col-1),KNIGHT));
            moves.add(new ChessMove(start,new ChessPosition(row+1,col-1),BISHOP));
            return moves;

        }else if(color==BLACK && promoRow!=2) {
            moves.add(new ChessMove(start, new ChessPosition(row - 1, col), null));
            moves.add(new ChessMove(start, new ChessPosition(row - 2, col), null));
            moves.add(new ChessMove(start, new ChessPosition(row - 1, col + 1), null));
            moves.add(new ChessMove(start, new ChessPosition(row - 1, col - 1), null));
            return moves;
        }
        else if(color==BLACK && promoRow==2) {
            moves.add(new ChessMove(start, new ChessPosition(row - 1, col), ROOK));
            moves.add(new ChessMove(start, new ChessPosition(row - 1, col), QUEEN));
            moves.add(new ChessMove(start, new ChessPosition(row - 1, col), KNIGHT));
            moves.add(new ChessMove(start, new ChessPosition(row - 1, col), BISHOP));
            moves.add(new ChessMove(start, new ChessPosition(row - 1, col+1), ROOK));
            moves.add(new ChessMove(start, new ChessPosition(row - 1, col+1), QUEEN));
            moves.add(new ChessMove(start, new ChessPosition(row - 1, col+1), KNIGHT));
            moves.add(new ChessMove(start, new ChessPosition(row - 1, col+1), BISHOP));
            moves.add(new ChessMove(start, new ChessPosition(row - 1, col-1), ROOK));
            moves.add(new ChessMove(start, new ChessPosition(row - 1, col-1), QUEEN));
            moves.add(new ChessMove(start, new ChessPosition(row - 1, col-1), KNIGHT));
            moves.add(new ChessMove(start, new ChessPosition(row - 1, col-1), BISHOP));
            return moves;
        }
        System.out.print("You broke colorMoves in PawnMoveCalc");
        return moves;
    }
}

