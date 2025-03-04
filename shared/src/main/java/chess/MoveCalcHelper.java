package chess;

import java.util.ArrayList;
import java.util.Collection;

public class MoveCalcHelper {

    // creates just one instance
    private static final MoveCalcHelper INSTANCE = new MoveCalcHelper();

    // private constructor so it can't be instantiated
    private MoveCalcHelper() { };

    // provides public access to this instance
    public static MoveCalcHelper getInstance() {
        return INSTANCE;
    }

    public void slidingMovesCalc(
            Collection<ChessMove> moves,
            ChessBoard board,
            ChessPosition myPos,
            int incRow,
            int incCol,
            ChessGame.TeamColor color){

        // set first row and col with incRow & incCol
        int row = myPos.getRow() + incRow;
        int col = myPos.getColumn() + incCol;

        while(row>=1 && row <=8 && col>=1 && col <=8){
            ChessPosition newPos = new ChessPosition(row,col);
            ChessPiece pieceAtNewPos = board.getPiece(newPos);
            ChessMove newMove = new ChessMove(myPos,newPos,null);
            // If there is a piece
            if (pieceAtNewPos!=null){
                // If piece is other team
                if (pieceAtNewPos.getTeamColor()!=color){
                    moves.add(newMove);
                }
                // Stop incrementing once you hit a piece
                break;
            } else {
                // Add piece for empty Square
                moves.add(newMove);
            }
            // Increment row & col (depending on incRow and incCol)
            row += incRow;
            col += incCol;
        }

    }

    public Collection<ChessMove> calcLegalMovesFromAllPositions(
            int[] rowOffsets,
            int[] colOffsets,
            ChessBoard board,
            ChessPosition myPos,
            ChessGame.TeamColor color){

        Collection<ChessPosition> allPos = new ArrayList<>();
        Collection<ChessMove> legalMoves = new ArrayList<>();

        // builds allPos - All Positions it could move
        for (int i = 0; i < rowOffsets.length; i++){
            allPos.add(new ChessPosition(
                    myPos.getRow() + rowOffsets[i],
                    myPos.getColumn() + colOffsets[i]));
        }

        // logic for adding moves to legalMoves
        for (ChessPosition pos : allPos){
            int row = pos.getRow();
            int col = pos.getColumn();
            ChessMove move = new ChessMove(myPos,pos,null);

            if (row > 8 | row < 1 | col < 1 | col > 8){
                continue;
            }

            if (board.getPiece(pos)!=null){
                if (board.getPiece(pos).getTeamColor()!=color){
                    legalMoves.add(move);
                }

            }else{
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }
}
