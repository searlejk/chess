package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition startPosition;
    private final ChessPiece.PieceType type;


    public BishopMovesCalculator(ChessBoard board, ChessPosition startPosition, ChessPiece.PieceType type) {
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


        ///  --------- FIRST -----------

        int i = 1;
        while (row+i >0 && col+i > 0 && row+i<=8 && col+i<=8){
            int newrow = row+i;
            int newcol = col+i;

            if (board.getPiece(new ChessPosition(newrow,newcol))!=null){
                ChessGame.TeamColor teamColor = board.getPiece(new ChessPosition(newrow,newcol)).getTeamColor();
                System.out.println(teamColor+" piece"+board.getPiece(new ChessPosition(newrow,newcol))+" at "+"("+newrow+", "+newcol+")");

                if (board.getPiece(new ChessPosition(row,col)).getTeamColor() != teamColor){
                    moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
                    System.out.println("("+newrow+", "+newcol+") added");
                    break;
                }
                else{
                    break;
                }
            }

            moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
            System.out.println("("+newrow+", "+newcol+") added");
            i+=1;
        }

        ///  ----------- SECOND -----------

        i=1;
        while (row-i >0 && col+i > 0 && row-i<=8 && col+i<=8){
            int newrow = row-i;
            int newcol = col+i;

            if (board.getPiece(new ChessPosition(newrow,newcol))!=null){
                ChessGame.TeamColor teamColor = board.getPiece(new ChessPosition(newrow,newcol)).getTeamColor();
                System.out.println(teamColor+" piece"+board.getPiece(new ChessPosition(newrow,newcol))+" at "+"("+newrow+", "+newcol+")");

                if (board.getPiece(new ChessPosition(row,col)).getTeamColor() != teamColor){
                    moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
                    System.out.println("("+newrow+", "+newcol+") added");
                    break;
                }
                else{
                    break;
                }
            }

            moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
            System.out.println("("+newrow+", "+newcol+") added");
            i+=1;
        }

        ///  ----------------- THIRD -------------

        i=1;
        while (row-i >0 && col-i > 0 && row-i<=8 && col-i<=8){
            int newrow = row-i;
            int newcol = col-i;

            if (board.getPiece(new ChessPosition(newrow,newcol))!=null){
                ChessGame.TeamColor teamColor = board.getPiece(new ChessPosition(newrow,newcol)).getTeamColor();
                System.out.println(teamColor+" piece"+board.getPiece(new ChessPosition(newrow,newcol))+" at "+"("+newrow+", "+newcol+")");

                if (board.getPiece(new ChessPosition(row,col)).getTeamColor() != teamColor){
                    moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
                    System.out.println("("+newrow+", "+newcol+") added");
                    break;
                }
                else{
                    break;
                }
            }

            moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
            System.out.println("("+newrow+", "+newcol+") added");
            i+=1;
        }


        ///  ------------- FOURTH -------------


        i=1;
        while (row+i >0 && col-i > 0 && row+i<=8 && col-i<=8){
            int newrow = row+i;
            int newcol = col-i;

            if (board.getPiece(new ChessPosition(newrow,newcol))!=null){
                ChessGame.TeamColor teamColor = board.getPiece(new ChessPosition(newrow,newcol)).getTeamColor();
                System.out.println(teamColor+" piece"+board.getPiece(new ChessPosition(newrow,newcol))+" at "+"("+newrow+", "+newcol+")");

                if (board.getPiece(new ChessPosition(row,col)).getTeamColor() != teamColor){
                    moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
                    System.out.println("("+newrow+", "+newcol+") added");
                    break;
                }
                else{
                    break;
                }
            }

            moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),type));
            System.out.println("("+newrow+", "+newcol+") added");
            i+=1;
        }

        System.out.println(moves);
        return moves;
    }

}