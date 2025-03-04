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


    public Collection<ChessMove> legalMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor color = piece.getTeamColor();

        Collection<ChessPosition> posList = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        posList.add(new ChessPosition(row+1,col));
        posList.add(new ChessPosition(row-1,col));
        posList.add(new ChessPosition(row,col+1));
        posList.add(new ChessPosition(row,col-1));
        posList.add(new ChessPosition(row+1,col+1));
        posList.add(new ChessPosition(row+1,col-1));
        posList.add(new ChessPosition(row-1,col+1));
        posList.add(new ChessPosition(row-1,col-1));


        for (ChessPosition pos : posList){
            if (pos.getRow()>8 | pos.getRow()<1 | pos.getColumn() <1 | pos.getColumn()>8){
                continue;
            }

            ChessMove move = new ChessMove(myPosition,pos,null);

            if (board.getPiece(pos)!=null){
                if (board.getPiece(pos).getTeamColor()==color){
                    continue;
                }
                else{
                    moves.add(move);
                    continue;
                }
            }else{
                moves.add(move);
                continue;
            }
        }
        return moves;
    }
}
