package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;
import static chess.ChessPiece.PieceType.ROOK;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    @Override
    public String toString() {
        int[] list = new int[]{1,2,3,4,5,6,7,8};
        String superString = "\n";

        for (int i : list){
            for (int j : list){
                ChessPosition pos = new ChessPosition(i,j);
                if (this.getPiece(pos)!=null){
                    ChessPiece.PieceType type = this.getPiece(pos).getPieceType();
                    ChessGame.TeamColor color = this.getPiece(pos).getTeamColor();

                    if (color==WHITE){
                        if (type==PAWN){
                            superString+="|P|";
                        }
                        if (type==KING){
                            superString+="|K|";
                        }
                        if (type==QUEEN){
                            superString+="|Q|";
                        }
                        if (type==KNIGHT){
                            superString+="|N|";
                        }
                        if (type==BISHOP){
                            superString+="|B|";
                        }
                        if (type==ROOK){
                            superString+="|R|";
                        }
                    }
                    else if (color==BLACK){
                        if (type==PAWN){
                            superString+="|p|";
                        }
                        if (type==KING){
                            superString+="|k|";
                        }
                        if (type==QUEEN){
                            superString+="|q|";
                        }
                        if (type==KNIGHT){
                            superString+="|n|";
                        }
                        if (type==BISHOP){
                            superString+="|b|";
                        }
                        if (type==ROOK){
                            superString+="|r|";
                        }

                    }

                }
                else{
                    superString+="| |";
                }
            }
            superString+="\n";
        }
        return superString;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null){
            System.out.println("Null compared");
            return false;
        }
        if (obj == this){
            System.out.println("Comparing same object True");
            return true;
        }
        if (this.getClass() != obj.getClass()){
            System.out.println("False not same object compared");
            return false;
        }

        ChessBoard c = (ChessBoard)obj;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if (c.getPiece(new ChessPosition(i, j))!=null && this.getPiece(new ChessPosition(i, j))!=null) {
                    if (c.getPiece(new ChessPosition(i, j)).getPieceType() == this.getPiece(new ChessPosition(i, j)).getPieceType()) {
                        System.out.println("MATCH");
                    } else {
                        System.out.println("This ("+i+", "+j+") doesn't match");
                        return false;
                    }
                }
            }
        }

        return true;
    }


    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */

    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Removes a chess piece from the chessboard
     *
     * @param position where to remove the piece
     */
    public void remPiece(ChessPosition position) {
        squares[position.getRow()-1][position.getColumn()-1] = null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                this.remPiece(new ChessPosition(i, j));
            }
        }


        this.addPiece(new ChessPosition(2,1),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        this.addPiece(new ChessPosition(2,2),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        this.addPiece(new ChessPosition(2,3),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        this.addPiece(new ChessPosition(2,4),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        this.addPiece(new ChessPosition(2,5),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        this.addPiece(new ChessPosition(2,6),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        this.addPiece(new ChessPosition(2,7),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        this.addPiece(new ChessPosition(2,8),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));

        this.addPiece(new ChessPosition(1,1),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        this.addPiece(new ChessPosition(1,2),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(1,3),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(1,4),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        this.addPiece(new ChessPosition(1,5),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        this.addPiece(new ChessPosition(1,6),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(1,7),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(1,8),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));



        this.addPiece(new ChessPosition(7,1),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        this.addPiece(new ChessPosition(7,2),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        this.addPiece(new ChessPosition(7,3),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        this.addPiece(new ChessPosition(7,4),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        this.addPiece(new ChessPosition(7,5),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        this.addPiece(new ChessPosition(7,6),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        this.addPiece(new ChessPosition(7,7),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        this.addPiece(new ChessPosition(7,8),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));

        this.addPiece(new ChessPosition(8,1),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        this.addPiece(new ChessPosition(8,2),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(8,3),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(8,4),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        this.addPiece(new ChessPosition(8,5),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        this.addPiece(new ChessPosition(8,6),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(8,7),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(8,8),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
    }

    /**
     *
     *  *** WITHOUT CHECKING ANYTHING ***
     *
     *  --- Makes move on Board ---
     *  Please check:
     *  1) Piece in start position
     *  2) Move is legal
     *
     */

    public ChessBoard move(ChessPiece piece, ChessMove move){
        ///  If newSpot is full, clear newSpot
        if (this.getPiece(move.getEndPosition())!=null) {
            this.remPiece(move.getEndPosition());
        }

        ///  Add Piece to newSpot
        this.addPiece(move.getEndPosition(), piece);

        ///  Clear oldSpot
        this.remPiece(move.getStartPosition());

        return this;
    }
}
