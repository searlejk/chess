package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class RookMovesCalculator {
    private ChessPosition pos;
    private ChessBoard board;

    public RookMovesCalculator(ChessBoard board, ChessPosition pos) {
        this.board = board;
        this.pos = pos;
    }


    public Collection<ChessMove> legalMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor color = piece.getTeamColor();

        boolean loop = true;
        boolean reset = false;

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int inc1 = 1;
        int inc2 = 0;

        int checkRow = row+inc1;
        int checkCol = col+inc2;

        while(loop){

            if (checkRow<=8 && checkRow>=1 && checkCol<=8 && checkCol >=1 && !reset){
                ChessPosition nextPos = new ChessPosition(checkRow,checkCol);
                ChessMove move = new ChessMove(myPosition,nextPos,null);
                if (board.getPiece(nextPos)!=null){
                    if (board.getPiece(nextPos).getTeamColor()==color){
                        reset=true;
                        checkRow = row+inc1;
                        checkCol = col+inc2;
                    }else{
                        moves.add(move);
                        reset = true;
                        checkRow = row+inc1;
                        checkCol = col+inc2;
                    }

                }else{
                    moves.add(move);
                    int[] bothIncs  = this.incUpdate(inc1,inc2);
                    inc1 += bothIncs[0];
                    inc2 += bothIncs[1];
                    checkRow = row+inc1;
                    checkCol = col+inc2;
                }

            }else{
                int[] bothIncs  = this.incReset(inc1,inc2);
                inc1 = bothIncs[0];
                inc2 = bothIncs[1];
                checkRow = row+inc1;
                checkCol = col+inc2;
                reset = false;

                if (inc1==0 && inc2 == 0){
                    break;
                }
            }
        }
        return moves;
    }


    private int[] incUpdate(int inc1,int inc2){
        if (inc1>0 && inc2==0){
            return new int[]{1,0};
        }
        if (inc1<0 && inc2==0){
            return new int[]{-1,0};
        }
        if (inc1==0 && inc2>0){
            return new int[]{0,1};
        }
        if (inc1==0 && inc2<0){
            return new int[]{0,-1};
        }

        System.out.print("You broke incUpdate Rook");
        return new int[]{9,9};

    }


    private int[] incReset(int inc1,int inc2){
        if (inc1>0 && inc2>0){
            return new int[]{-1,0};
        }
        if (inc1<0 && inc2==0){
            return new int[]{0,1};
        }
        if (inc1==0 && inc2>0){
            return new int[]{0,-1};
        }
        if (inc1==0 && inc2<0){
            return new int[]{0,0};
        }

        System.out.print("You broke incReset Rook");
        return new int[]{9,9};

    }
}
