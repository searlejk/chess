package ui;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class DrawChessHelper {
    private ChessBoard board;
    private ChessGame game;

    public DrawChessHelper(ChessGame game){
        this.board = game.getBoard();
        this.game = game;
    }

    public void drawChess(ChessGame game, Collection<ChessPosition> moves, ChessPosition myPos, ChessGame.TeamColor side){
        System.out.print(ERASE_SCREEN);
        ChessBoard board = game.getBoard();
        String unicodePiece;
        String key = "";
        int startRow = 0;
        int endRow = 0;
        int step = 0;
        if (moves==null){ moves=new ArrayList<>();}
        if (myPos==null){ myPos = new ChessPosition(9,9); }
        switch (side){
            case WHITE -> {
                key = "    a  b  c  d  e  f  g  h    ";
                startRow = 8;
                endRow = 0;
                step = -1;
            }
            case BLACK -> {
                key = "    h  g  f  e  d  c  b  a    ";
                startRow = 1;
                endRow = 9;
                step = 1;
            }
        }

        setTopKey(key);
        for (int row = startRow; row != endRow; row+=step){
            ///  left number key
            setKeyColors(" "+row+" ");
            for (int col = 1; col <= 8; col++){
                ChessPosition pos = new ChessPosition(row,col);

                boardBackgroundColor(row,col,board,pos, moves, myPos);

                if (board.getPiece(pos)!=null){
                    ChessPiece piece = board.getPiece(pos);
                    ChessGame.TeamColor color = piece.getTeamColor();
                    unicodePiece = getUnicodePiece(piece);

                    if(color == ChessGame.TeamColor.WHITE){
                        System.out.print(SET_TEXT_COLOR_WHITE);
                    } else{
                        System.out.print(SET_TEXT_COLOR_DARK_GREY);
                    }
                    System.out.print(unicodePiece);
                }
            }
            setKeyColors(" "+row+" ");
            newLine();
        }
        setKeyColors(key);
        resetTextAndBackground();
        System.out.print("\n");
    }

    public void legalMoves(ChessPosition pos, int side){
        int row = pos.getRow();
        int col = pos.getColumn();

        ChessPosition myPos = new ChessPosition(row,col);
        Collection<ChessPosition> legalMoves = new ArrayList<>();

        if (board.getPiece(myPos)!=null){
            ChessPiece piece = board.getPiece(myPos);
            Collection<ChessMove> moves = piece.pieceMoves(board, myPos);
            for (ChessMove move : moves){
                ChessPosition temp = move.getEndPosition();
                legalMoves.add(temp);
            }
        }

        ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
        if (side==2){
            color = ChessGame.TeamColor.BLACK;
        }

        drawChess(game,legalMoves,myPos,color);

    }
    


    private void resetTextAndBackground(){
        System.out.print(RESET_BG_COLOR);
        System.out.print(RESET_TEXT_COLOR);
    }

    private void setKeyColors(String output){
        System.out.print(SET_TEXT_BOLD);
        System.out.print(SET_TEXT_COLOR_LIGHT_GREY);
        System.out.print(SET_BG_COLOR_DARK_GREY);
        System.out.print(output);
        resetTextAndBackground();
    }

    private void setTopKey(String output){
        setKeyColors(output);
        newLine();
    }

    private void newLine(){
        System.out.print("\n");
    }

    private void boardBackgroundColor(int row,
                                      int col,ChessBoard board,
                                      ChessPosition pos,
                                      Collection<ChessPosition> moves,
                                      ChessPosition myPos){


        if ((row + col) % 2 == 0) {
            if (moves.contains(pos)){
                System.out.print(SET_BG_COLOR_DARK_HIGHLIGHT);
            }
            else if(pos.getColumn()==myPos.getColumn() && pos.getRow()==myPos.getRow()){
                System.out.print(SET_BG_COLOR_PIECE_SPOT);
            }else{
                System.out.print(SET_BG_COLOR_DARK_RED);
            }
        } else{
            if (moves.contains(pos)) {
                System.out.print(SET_BG_COLOR_LIGHT_HIGHLIGHT);
            }
            else if(pos.getColumn()==myPos.getColumn() && pos.getRow()==myPos.getRow()){
                System.out.print(SET_BG_COLOR_PIECE_SPOT);
            }else{
                System.out.print(SET_BG_COLOR_LIGHT_GREY);
            }
        }


        if (board.getPiece(pos)==null){
            System.out.print("   ");
        }
    }

    private static String getUnicodePiece(ChessPiece piece){
        String teamColor = piece.getTeamColor().toString();
        switch (piece.getPieceType()){
            case PAWN:
                return teamColor.equals("WHITE") ? WHITE_PAWN : BLACK_PAWN;
            case KING:
                return teamColor.equals("WHITE") ? WHITE_KING : BLACK_KING;
            case KNIGHT:
                return teamColor.equals("WHITE") ? WHITE_KNIGHT : BLACK_KNIGHT;
            case QUEEN:
                return teamColor.equals("WHITE") ? WHITE_QUEEN : BLACK_QUEEN;
            case ROOK:
                return teamColor.equals("WHITE") ? WHITE_ROOK : BLACK_ROOK;
            case BISHOP:
                return teamColor.equals("WHITE") ? WHITE_BISHOP : BLACK_BISHOP;
            default:
                return EMPTY;
        }
    }


}


