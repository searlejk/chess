package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class PawnMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition startPosition;
    private final ChessPiece.PieceType type;


    public PawnMovesCalculator(ChessBoard board, ChessPosition startPosition, ChessPiece.PieceType type) {
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


        boolean oneStep = false;

        ///  --------- WHITE -----------
        /// One Space
        if (row+1 >0 && row+1<=8) {
            if (board.getPiece(new ChessPosition(row, col)).getTeamColor() == WHITE) {
                int newrow = row + 1;
                int newcol = col;
                if (board.getPiece(new ChessPosition(newrow, newcol)) != null) {
                    ChessGame.TeamColor teamColor = board.getPiece(new ChessPosition(newrow, newcol)).getTeamColor();

                    if (board.getPiece(new ChessPosition(row, col)).getTeamColor() != teamColor) {
                        /// No capture forward, only right or left
                    }
                } else {
                    moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newrow, newcol), null));
                    oneStep = true;
                }

            }
        }
        /// Two Spaces
        if (row+2 >0 && row+2<=8 && oneStep) {
            if (row==2 && board.getPiece(new ChessPosition(row,col)).getTeamColor() == WHITE) {
                int newrow = row+2;
                int newcol = col;
                if (board.getPiece(new ChessPosition(newrow,newcol))!=null){
                    ChessGame.TeamColor teamColor = board.getPiece(new ChessPosition(newrow,newcol)).getTeamColor();

                    if (board.getPiece(new ChessPosition(row,col)).getTeamColor() != teamColor){
                        /// No capture forward, only right or left
                    }
                }
                else{
                    moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),null));
                }
            }
        }

        ///  Right Step
        if (row+1 >0 && col+1 > 0 && row+1<=8 && col+1<=8) {
            if (board.getPiece(new ChessPosition(row, col)).getTeamColor() == WHITE) {
                int newrow = row + 1;
                int newcol = col + 1;
                if (board.getPiece(new ChessPosition(newrow, newcol)) != null) {
                    ChessGame.TeamColor teamColor = board.getPiece(new ChessPosition(newrow, newcol)).getTeamColor();

                    if (board.getPiece(new ChessPosition(row, col)).getTeamColor() != teamColor) {
                        ///  Piece, Movement
                        moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newrow, newcol), null));
                    }
                } else {
                    ///  No piece no movement
                }

            }
        }
        ///  Left Step
        if (row+1 >0 && col-1 > 0 && row+1<=8 && col-1<=8) {
            if (board.getPiece(new ChessPosition(row, col)).getTeamColor() == WHITE) {
                int newrow = row + 1;
                int newcol = col - 1;
                if (board.getPiece(new ChessPosition(newrow, newcol)) != null) {
                    ChessGame.TeamColor teamColor = board.getPiece(new ChessPosition(newrow, newcol)).getTeamColor();

                    if (board.getPiece(new ChessPosition(row, col)).getTeamColor() != teamColor) {
                        ///  Piece, Movement
                        moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newrow, newcol), null));
                    }
                } else {
                    ///  No piece no movement
                }

            }
        }


        ///  --------- BLACK -----------
        /// One Space
        if (row-1 >0 && row-1<=8) {
            if (board.getPiece(new ChessPosition(row, col)).getTeamColor() == BLACK) {
                int newrow = row - 1;
                int newcol = col;
                if (board.getPiece(new ChessPosition(newrow, newcol)) != null) {
                    ChessGame.TeamColor teamColor = board.getPiece(new ChessPosition(newrow, newcol)).getTeamColor();

                    if (board.getPiece(new ChessPosition(row, col)).getTeamColor() != teamColor) {
                        /// No capture forward, only right or left
                    }
                } else {
                    moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newrow, newcol), null));
                    oneStep = true;
                }

            }
        }
        /// Two Spaces
        if (row-2 >0 && row-2<=8 && oneStep) {
            if (row==7 && board.getPiece(new ChessPosition(row,col)).getTeamColor() == BLACK) {
                int newrow = row-2;
                int newcol = col;
                if (board.getPiece(new ChessPosition(newrow,newcol))!=null){
                    ChessGame.TeamColor teamColor = board.getPiece(new ChessPosition(newrow,newcol)).getTeamColor();

                    if (board.getPiece(new ChessPosition(row,col)).getTeamColor() != teamColor){
                        /// No capture forward, only right or left
                    }
                }
                else{
                    moves.add(new ChessMove(new ChessPosition(row,col),new ChessPosition(newrow,newcol),null));
                }
            }
        }

        ///  Right Step
        if (row-1 >0 && col+1 > 0 && row-1<=8 && col+1<=8) {
            if (board.getPiece(new ChessPosition(row, col)).getTeamColor() == BLACK) {
                int newrow = row - 1;
                int newcol = col + 1;
                if (board.getPiece(new ChessPosition(newrow, newcol)) != null) {
                    ChessGame.TeamColor teamColor = board.getPiece(new ChessPosition(newrow, newcol)).getTeamColor();

                    if (board.getPiece(new ChessPosition(row, col)).getTeamColor() != teamColor) {
                        ///  Piece, Movement
                        moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newrow, newcol), null));
                    }
                } else {
                    ///  No piece no movement
                }

            }
        }
        ///  Left Step
        if (row-1 >0 && col-1 > 0 && row-1<=8 && col-1<=8) {
            if (board.getPiece(new ChessPosition(row, col)).getTeamColor() == BLACK) {
                int newrow = row - 1;
                int newcol = col - 1;
                if (board.getPiece(new ChessPosition(newrow, newcol)) != null) {
                    ChessGame.TeamColor teamColor = board.getPiece(new ChessPosition(newrow, newcol)).getTeamColor();

                    if (board.getPiece(new ChessPosition(row, col)).getTeamColor() != teamColor) {
                        ///  Piece, Movement
                        moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newrow, newcol), null));
                    }
                } else {
                    ///  No piece no movement
                }

            }
        }


        return moves;
    }

}