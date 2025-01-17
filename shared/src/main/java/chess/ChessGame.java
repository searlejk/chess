package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board = new ChessBoard();
    private ChessGame.TeamColor currTurn = TeamColor.WHITE;

    public ChessGame() {
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.currTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        return piece.pieceMoves(board,startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        ///  Create new Chess Game
        ChessGame futureGame = new ChessGame();
        ChessBoard futureBoard = this.board;

        ///  If there is a piece in the startPosition
        if (futureBoard.getPiece(move.getStartPosition()) != null) {
            ChessPiece piece = futureBoard.getPiece(move.getStartPosition());
            TeamColor startTeamColor = piece.getTeamColor();

            ///  If there is a piece in the end Position
            if (futureBoard.getPiece(move.getEndPosition()) != null) {
                TeamColor endTeamColor = futureBoard.getPiece(move.getStartPosition()).getTeamColor();

                if (endTeamColor == startTeamColor) {
                    throw new InvalidMoveException("You can not Capture your own piece");
                }
            }

        }else {
            throw new InvalidMoveException("No piece in start Position");
        }

        ///  Get Piece && teamColor && type
        ChessPiece piece = futureBoard.getPiece(move.getStartPosition());
        TeamColor teamColor = piece.getTeamColor();
        ChessPiece.PieceType type = piece.getPieceType();


        ///  Brute Force Move
        futureBoard.move(piece,move);

        futureGame.board = futureBoard;
        if (futureGame.isInCheck(teamColor)) {
            throw new InvalidMoveException("You can't place your king in check");
        }

        ///  If not in check, make the move
        else {
            this.board = futureBoard;
        }
    }



    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        /*
        1)  Iterate through the board to find all pieces of the opposite color, put them in a list
        2)  for each Piece check their moves to see if they can reach the king's square
        3)  if they can, the king is in check

         */
        TeamColor oppColor = TeamColor.WHITE;

        if (teamColor == TeamColor.WHITE){
            oppColor = TeamColor.BLACK;
        }
        if (teamColor == TeamColor.BLACK){
            oppColor = teamColor.WHITE;
        }

        ///  Find king of team color:
        ChessPosition king = new ChessPosition(1,1);

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if (board.getPiece(new ChessPosition(i, j))!=null) {
                    if(board.getPiece(new ChessPosition(i, j)).getTeamColor()==teamColor ) {
                        if (board.getPiece(new ChessPosition(i, j)).getPieceType() == ChessPiece.PieceType.KING) {
                            king = new ChessPosition(i, j);
                            System.out.println("Found " + board.getPiece(new ChessPosition(i, j)).getPieceType() + " at: (" + i + ", " + j + ")");
                        }
                    }
                }
            }
        }

        System.out.println("king location: "+king);

        ///  List of Opposing pieces:
        Collection<ChessPiece> oppPieces = new ArrayList<>();

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currPosition = new ChessPosition(i,j);
                ///  If there is a piece
                if (board.getPiece(currPosition)!=null) {
                    ///  If that piece matches Opposing team's color
                    if(board.getPiece(currPosition).getTeamColor()==oppColor){
                        /// Create ChessMove Var for that chess piece
                        /// Check if that piece can move from: Its location, to the king's location
                        ChessMove tempMove = new ChessMove(currPosition,king,null);
                        if (board.getPiece(currPosition).pieceMoves(board,currPosition).contains(tempMove)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
