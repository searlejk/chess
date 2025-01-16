package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        System.out.println("("+this.startPosition.getRow() +", "+this.startPosition.getColumn()+ ") This is the start Position");



        ///  --------- (2,1) and (2,-1) -----------
        if (row+2 >0 && col+1 > 0 && row+2<=8 && col+1<=8) {
            int newrow = row+2;
            int newcol = col+1;
            if (board.getPiece(new ChessPosition(newrow,newcol))!=null){
                ChessGame.TeamColor teamColor = board.getPiece(new ChessPosition(newrow,newcol)).getTeamColor();

                if (board.getPiece(new ChessPosition(row,col)).getTeamColor() != teamColor){
                    moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
                }
            }
            else{
                moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
            }

        }

        if (row+2 >0 && col-1 > 0 && row+2<=8 && col-1<=8){
            int newrow = row+2;
            int newcol = col-1;
            if (board.getPiece(new ChessPosition(newrow,newcol))!=null){
                ChessGame.TeamColor teamColor = board.getPiece(new ChessPosition(newrow,newcol)).getTeamColor();

                if (board.getPiece(new ChessPosition(row,col)).getTeamColor() != teamColor){
                    moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
                }
            }
            else{
                moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
            }

        }

        ///  --------- (-2,1) and (-2,-1) -----------
        if (row-2 >0 && col+1 > 0 && row-2<=8 && col+1<=8) {
            int newrow = row-2;
            int newcol = col+1;
            if (board.getPiece(new ChessPosition(newrow,newcol))!=null){
                ChessGame.TeamColor teamColor = board.getPiece(new ChessPosition(newrow,newcol)).getTeamColor();

                if (board.getPiece(new ChessPosition(row,col)).getTeamColor() != teamColor){
                    moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
                }
            }
            else{
                moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
            }

        }

        if (row-2 >0 && col-1 > 0 && row-2<=8 && col-1<=8){
            int newrow = row-2;
            int newcol = col-1;
            if (board.getPiece(new ChessPosition(newrow,newcol))!=null){
                ChessGame.TeamColor teamColor = board.getPiece(new ChessPosition(newrow,newcol)).getTeamColor();

                if (board.getPiece(new ChessPosition(row,col)).getTeamColor() != teamColor){
                    moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
                }
            }
            else{
                moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
            }

        }

        ///  --------- (1,2) and (1,-2) -----------

        if (row+1 >0 && col+2 > 0 && row+1<=8 && col+2<=8){
            int newrow = row+1;
            int newcol = col+2;
            if (board.getPiece(new ChessPosition(newrow,newcol))!=null){
                ChessGame.TeamColor teamColor = board.getPiece(new ChessPosition(newrow,newcol)).getTeamColor();

                if (board.getPiece(new ChessPosition(row,col)).getTeamColor() != teamColor){
                    moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
                }
            }
            else{
                moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
            }

        }

        if (row+1 >0 && col-2 > 0 && row+1<=8 && col-2<=8){
            int newrow = row+1;
            int newcol = col-2;
            if (board.getPiece(new ChessPosition(newrow,newcol))!=null){
                ChessGame.TeamColor teamColor = board.getPiece(new ChessPosition(newrow,newcol)).getTeamColor();

                if (board.getPiece(new ChessPosition(row,col)).getTeamColor() != teamColor){
                    moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
                }
            }
            else{
                moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
            }

        }

        ///  --------- (-1,2) and (-1,-2) -----------

        if (row-1 >0 && col+2 > 0 && row-1<=8 && col+2<=8){
            int newrow = row-1;
            int newcol = col+2;
            if (board.getPiece(new ChessPosition(newrow,newcol))!=null){
                ChessGame.TeamColor teamColor = board.getPiece(new ChessPosition(newrow,newcol)).getTeamColor();

                if (board.getPiece(new ChessPosition(row,col)).getTeamColor() != teamColor){
                    moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
                }
            }
            else{
                moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
            }

        }

        if (row-1 >0 && col-2 > 0 && row-1<=8 && col-2<=8){
            int newrow = row-1;
            int newcol = col-2;
            if (board.getPiece(new ChessPosition(newrow,newcol))!=null){
                ChessGame.TeamColor teamColor = board.getPiece(new ChessPosition(newrow,newcol)).getTeamColor();

                if (board.getPiece(new ChessPosition(row,col)).getTeamColor() != teamColor){
                    moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
                }
            }
            else{
                moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
            }

        }

        System.out.println(moves);
        return moves;
    }

}