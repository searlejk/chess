package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board = new ChessBoard();
    private ChessGame.TeamColor currTurn = WHITE;

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
        BLACK;
        
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
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        TeamColor trueTeamTurn = this.getTeamTurn();

        ChessGame ogGame = new ChessGame();
        ogGame.setBoard(this.board);
        ogGame.setTeamTurn(this.getTeamTurn());
        boolean killedPiece = false;
        ChessPiece deadPiece = new ChessPiece(trueTeamTurn, ChessPiece.PieceType.BISHOP);

        for (ChessMove move : moves) {
            try {

                /// If there is a piece in the end position save it
                if (ogGame.board.getPiece(move.getEndPosition()) != null) {
                    deadPiece = ogGame.board.getPiece(move.getEndPosition());
                    killedPiece = true;
                }

                ogGame.setTeamTurn(piece.getTeamColor());
                ogGame.makeMove(move);

                ///  If no exception is thrown the code ends here
                ///  Remove moved piece
                this.board.remPiece(new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn()));
                if (killedPiece) {
                    this.board.addPiece(new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn()), deadPiece);
                }

                ///  Place piece back
                this.board.addPiece(startPosition, piece);

                ///  Set turn back so I can check again
                ogGame.setTeamTurn(piece.getTeamColor());

                killedPiece = false;
                validMoves.add(move);

            } catch (InvalidMoveException e) {
                ///  Remove moved piece
                this.board.remPiece(new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn()));
                if (killedPiece) {
                    this.board.addPiece(new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn()), deadPiece);
                }

                ///  Place piece back
                this.board.addPiece(startPosition, piece);

                ///  Set turn back so I can check again
                ogGame.setTeamTurn(piece.getTeamColor());

                ///  Say "MOVE IS BAD"
                killedPiece = false;
                System.out.println("Move is bad: " + move);
            }
        }

        return validMoves;
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

        ///  Declare start and end position
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        ///  If there is a piece in the startPosition
        if (futureBoard.getPiece(startPosition) != null) {
            ChessPiece piece = futureBoard.getPiece(startPosition);
            TeamColor startTeamColor = piece.getTeamColor();

            ///  If there is a piece in the end Position
            if (futureBoard.getPiece(endPosition) != null) {
                TeamColor endTeamColor = futureBoard.getPiece(endPosition).getTeamColor();

                if (endTeamColor == startTeamColor) {
                    throw new InvalidMoveException("You can not Capture your own piece");
                }
            }

        } else {
            throw new InvalidMoveException("No piece in start Position");
        }

        ///  Get Piece && teamColor
        ChessPiece piece = futureBoard.getPiece(startPosition);
        TeamColor teamColor = piece.getTeamColor();
        ChessPiece.PieceType type = piece.getPieceType();


        ///  Verify if turn is correct
        if (this.getTeamTurn() != teamColor) {
            throw new InvalidMoveException("Its not your turn");
        }


        ///  Get moves
        Collection<ChessMove> moves = piece.pieceMoves(futureBoard, startPosition);

        ///  If NOT in given moves, then throw Exception
        if (!moves.contains(move)) {
            throw new InvalidMoveException("Illegal move for this PieceType");
        }

        if (type == PAWN && move.getPromotionPiece() != null) {
            ///  Pass Correct Piece into move function
            ChessPiece promotionalPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            futureBoard.move(promotionalPiece, move);
        } else {
            ///  Any other non-Pawn promotional moves
            futureBoard.move(piece, move);
        }

        futureGame.board = futureBoard;
        if (futureGame.isInCheck(teamColor)) {
            throw new InvalidMoveException("You can't place your king in check");
        }

        ///  If not in check, make the move
        else {
            this.board = futureBoard;
        }

        TeamColor oppTeamColor;

        if (teamColor == WHITE) {
            oppTeamColor = BLACK;
        } else {
            oppTeamColor = WHITE;
        }

        this.setTeamTurn(oppTeamColor);
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
        TeamColor oppColor = WHITE;

        if (teamColor == WHITE) {
            oppColor = BLACK;
        }

        ///  Find king of team color:
        ChessPosition kingPos = new ChessPosition(1,1);

        outer:
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition pos = new ChessPosition(row,col);
                ChessPiece piece = board.getPiece(pos);

                if (piece==null) { continue; }
                if (piece.getTeamColor()!=teamColor) { continue; }
                if (piece.getPieceType()!=KING) { continue; }

                kingPos = pos;
                break outer;
            }
        }


        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null) { continue; }

                ChessGame.TeamColor pieceColor = piece.getTeamColor();
                ChessPiece.PieceType pieceType = piece.getPieceType();

                if (pieceColor!=oppColor) { continue; }

                ChessMove move = new ChessMove(pos, kingPos, null);

                // update move for promotional pawns (either WHITE or BLACK)
                if (pieceColor == WHITE && pieceType == PAWN && row == 7){
                    move = new ChessMove(pos, kingPos, QUEEN);
                }

                if (pieceColor == BLACK && pieceType == PAWN && row == 2) {
                    move = new ChessMove(pos, kingPos, QUEEN);
                }
                if (piece.pieceMoves(board, pos).contains(move)) {
                    return true;
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

        /*
        1) If in check
        2) If in stalemate
        3) Checkmate = true
         */
        boolean inStalemate = true;
        TeamColor trueTeamColor = this.getTeamTurn();

        if (this.getTeamTurn() == WHITE) {
            trueTeamColor = WHITE;
        }
        if (this.getTeamTurn() == BLACK) {
            trueTeamColor = BLACK;
        }

        outer:
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition pos = new ChessPosition(row,col);
                ChessPiece piece = board.getPiece(pos);

                if (piece==null) { continue; }
                ChessGame.TeamColor pieceColor = piece.getTeamColor();
                if (pieceColor!=teamColor) { continue; }

                ///  See if it has any validMoves
                this.setTeamTurn(trueTeamColor);
                if (!this.validMoves(pos).isEmpty()) {
                    inStalemate = false;
                    break outer;
                }
            }
        }
        boolean check = this.isInCheck(teamColor);

        return check && inStalemate;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {

        if (this.isInCheckmate(teamColor)){
            return false;
        }

        TeamColor trueTeamColor = this.getTeamTurn();

        ///  Iterate over board to find pieces of teamColor
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition pos = new ChessPosition(row,col);
                ChessPiece piece = board.getPiece(pos);

                if (piece==null) { continue; }
                if (piece.getTeamColor()!=teamColor) { continue; }

                ///  See if it has any validMoves
                this.setTeamTurn(trueTeamColor);
                if (!this.validMoves(pos).isEmpty()){
                    return false;
                }
            }
        }
        return true;
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

