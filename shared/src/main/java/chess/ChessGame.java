package chess;

import java.util.ArrayList;
import java.util.Collection;

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

    ///  Sets all castling to true for white
    private boolean wkc = true;
    private boolean wqc = true;

    ///  Sets all castling to true for black
    private boolean bkc = true;
    private boolean bqc = true;

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
        Collection<ChessMove> moves = piece.pieceMoves(board,startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        TeamColor TrueTeamTurn = this.getTeamTurn();

        ChessGame ogGame = new ChessGame();
        ogGame.setBoard(this.board);
        ogGame.setTeamTurn(this.getTeamTurn());
        boolean killedPiece = false;
        ChessPiece deadPiece = new ChessPiece(TrueTeamTurn, ChessPiece.PieceType.BISHOP);

        for (ChessMove move: moves ){
            try{

                /// If there is a piece in the end position save it
                if (ogGame.board.getPiece(move.getEndPosition())!=null) {
                    deadPiece = ogGame.board.getPiece(move.getEndPosition());
                    killedPiece = true;
                }

                ogGame.setTeamTurn(piece.getTeamColor());
                ogGame.makeMove(move);

                ///  If no exception is thrown the code ends here
                ///  Remove moved piece
                this.board.remPiece(new ChessPosition(move.getEndPosition().getRow(),move.getEndPosition().getColumn()));
                if (killedPiece){
                    this.board.addPiece(new ChessPosition(move.getEndPosition().getRow(),move.getEndPosition().getColumn()),deadPiece);
                }

                ///  Place piece back
                this.board.addPiece(startPosition,piece);

                ///  Set turn back so I can check again
                ogGame.setTeamTurn(piece.getTeamColor());

                killedPiece = false;
                validMoves.add(move);

            } catch (InvalidMoveException e) {
                ///  Remove moved piece
                this.board.remPiece(new ChessPosition(move.getEndPosition().getRow(),move.getEndPosition().getColumn()));
                if (killedPiece){
                    this.board.addPiece(new ChessPosition(move.getEndPosition().getRow(),move.getEndPosition().getColumn()),deadPiece);
                }

                ///  Place piece back
                this.board.addPiece(startPosition,piece);

                ///  Set turn back so I can check again
                ogGame.setTeamTurn(piece.getTeamColor());

                ///  Say "MOVE IS BAD"
                killedPiece = false;
                System.out.println("Move is bad: "+move);
            }
        }

        ///  If the team going is not in check
        boolean proceed = false;
        if (TrueTeamTurn== WHITE){
            if (this.wqc | this.wkc){
                proceed = true;
            }
        }
        if (TrueTeamTurn==TeamColor.BLACK){
            if (this.bqc | this.bkc){
                proceed = true;
            }
        }

        if (proceed){
            if (!this.isInCheck(TrueTeamTurn)){
                if (TrueTeamTurn== WHITE){
                    if (this.wqc){
                        ChessPosition empty1 = new ChessPosition(1,3);
                        ChessPosition empty2 = new ChessPosition(1,4);

                        if (this.board.getPiece(empty1)==null && this.board.getPiece(empty2)==null){
                            ///  Make board with extra kings and check for check
                            ChessPiece whiteKing = new ChessPiece(WHITE,KING);

                            this.board.addPiece(empty1,whiteKing);
                            this.board.addPiece(empty2,whiteKing);

                            ogGame.setBoard(this.board);
                            ogGame.setTeamTurn(WHITE);

                            ///  If no check, then add move
                            if(!ogGame.isInCheck(WHITE)){
                                ///  Add move here to valid Moves
                            }

                            this.board.remPiece(empty1);
                            this.board.remPiece(empty2);
                            ///  Add White Queen Side Castle move as option
                        }
                    }
                    if (this.wkc){
                        ChessPosition empty1 = new ChessPosition(1,6);
                        ChessPosition empty2 = new ChessPosition(1,7);

                        if (this.board.getPiece(empty1)==null && this.board.getPiece(empty2)==null) {
                            ///  Make board with extra kings and check for check
                            ChessPiece whiteKing = new ChessPiece(WHITE, KING);

                            this.board.addPiece(empty1, whiteKing);
                            this.board.addPiece(empty2, whiteKing);

                            ogGame.setBoard(this.board);
                            ogGame.setTeamTurn(WHITE);

                            ///  If no check, then add move
                            if (!ogGame.isInCheck(WHITE)) {
                                ///  Add move here to valid Moves
                            }

                            this.board.remPiece(empty1);
                            this.board.remPiece(empty2);

                        }
                    }
                }
                if (TrueTeamTurn==TeamColor.BLACK){
                    if (this.bqc){
                        ChessPosition empty1 = new ChessPosition(8,3);
                        ChessPosition empty2 = new ChessPosition(8,4);

                        if (this.board.getPiece(empty1)==null && this.board.getPiece(empty2)==null){
                            ///  Make board with extra kings and check for check
                            ChessPiece blackKing = new ChessPiece(TeamColor.BLACK,KING);

                            this.board.addPiece(empty1,blackKing);
                            this.board.addPiece(empty2,blackKing);

                            ogGame.setBoard(this.board);
                            ogGame.setTeamTurn(TeamColor.BLACK);

                            ///  If no check, then add move
                            if(!ogGame.isInCheck(TeamColor.BLACK)){
                                ///  Add move here to valid Moves
                            }

                            this.board.remPiece(empty1);
                            this.board.remPiece(empty2);
                            ///  Add White Queen Side Castle move as option
                        }
                    }
                    if (this.bkc){
                        ChessPosition empty1 = new ChessPosition(8,6);
                        ChessPosition empty2 = new ChessPosition(8,7);

                        if (this.board.getPiece(empty1)==null && this.board.getPiece(empty2)==null) {
                            ///  Make board with extra kings and check for check
                            ChessPiece blackKing = new ChessPiece(TeamColor.BLACK, KING);

                            this.board.addPiece(empty1, blackKing);
                            this.board.addPiece(empty2, blackKing);

                            ogGame.setBoard(this.board);
                            ogGame.setTeamTurn(TeamColor.BLACK);

                            ///  If no check, then add move
                            if (!ogGame.isInCheck(TeamColor.BLACK)) {
                                ///  Add move here to valid Moves
                            }

                            this.board.remPiece(empty1);
                            this.board.remPiece(empty2);

                        }
                    }
                }
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

        }else {
            throw new InvalidMoveException("No piece in start Position");
        }

        ///  Get Piece && teamColor
        ChessPiece piece = futureBoard.getPiece(startPosition);
        TeamColor teamColor = piece.getTeamColor();
        ChessPiece.PieceType type = piece.getPieceType();


        ///  Verify if turn is correct
        if (this.getTeamTurn()!=teamColor){
            throw new InvalidMoveException("Its not your turn");
        }


        ///  Get moves
        Collection<ChessMove> moves = piece.pieceMoves(futureBoard,startPosition);

        ///  If NOT in given moves, then throw Exception
        if (!moves.contains(move)){
            throw new InvalidMoveException("Illegal move for this PieceType");
        }

        if(type == ChessPiece.PieceType.PAWN && move.getPromotionPiece()!=null){
            ///  Pass Correct Piece into move function
            ChessPiece promotionalPiece = new ChessPiece(piece.getTeamColor(),move.getPromotionPiece());
            futureBoard.move(promotionalPiece,move);
        }
        else{
            ///  Any other non-Pawn promotional moves
            futureBoard.move(piece,move);
        }

        futureGame.board = futureBoard;
        if (futureGame.isInCheck(teamColor)) {
            throw new InvalidMoveException("You can't place your king in check");
        }

        ///  If not in check, make the move
        else {

            /*

            ***** THIS CHECKS THAT VALID PIECES ARE ON THE BOARD AND HAVEN'T MOVED FOR CASTLING *****

             */

            ///  IF moving a KING set Castling false for that team color
            if (piece.getPieceType()==KING){
                if (piece.getTeamColor() == WHITE){
                    this.wkc = false;
                    this.wqc = false;
                }
                if (piece.getTeamColor() == TeamColor.BLACK){
                    this.bkc = false;
                    this.bqc = false;
                }
            }
            ///  IF moving a left-side ROOK set Castling false
            if (piece.getPieceType()==ROOK){
                /// If left side rook
                if (move.getStartPosition().getColumn()==1){
                    if (piece.getTeamColor()== WHITE){
                        this.wqc = false;
                    }
                    if (piece.getTeamColor()==TeamColor.BLACK){
                        this.bqc = false;
                    }
                }
                /// If right side rook
                if (move.getStartPosition().getColumn()==8){
                    if (piece.getTeamColor()== WHITE){
                        this.wkc = false;
                    }
                    if (piece.getTeamColor()==TeamColor.BLACK){
                        this.bkc = false;
                    }
                }
            }


            Collection<ChessPosition> rookPositions = new ArrayList<>();
            rookPositions.add(new ChessPosition(1,1));
            rookPositions.add(new ChessPosition(1,8));
            rookPositions.add(new ChessPosition(8,1));
            rookPositions.add(new ChessPosition(8,8));


            if (rookPositions.contains(move.getEndPosition())) {
                ChessPiece endPiece = this.board.getPiece(move.getEndPosition());
                if (endPiece.getTeamColor() == WHITE) {
                    if (move.getEndPosition().getColumn() == 8){
                        this.wkc = false;
                    }
                    else if (move.getEndPosition().getColumn()==1){
                        this.wqc = false;
                    }
                }
                if (endPiece.getTeamColor() == TeamColor.BLACK) {
                    if (move.getEndPosition().getColumn() == 8){
                        this.bkc = false;
                    }
                    else if (move.getEndPosition().getColumn()==1){
                        this.bqc = false;
                    }
                }
            }

            this.board = futureBoard;
        }

        TeamColor oppTeamColor;

        if (teamColor== WHITE){
            oppTeamColor = TeamColor.BLACK;
        }
        else{
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

        if (teamColor == WHITE){
            oppColor = TeamColor.BLACK;
        }
        if (teamColor == TeamColor.BLACK){
            oppColor = WHITE;
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
                        ///  if White promotion pawn, change tempMove
                        if (i == 7 && board.getPiece(currPosition).getPieceType() == ChessPiece.PieceType.PAWN && board.getPiece(currPosition).getTeamColor() == WHITE){
                            tempMove = new ChessMove(currPosition,king,QUEEN);
                        }
                        if (i == 2 && board.getPiece(currPosition).getPieceType() == ChessPiece.PieceType.PAWN && board.getPiece(currPosition).getTeamColor() == TeamColor.BLACK){
                            tempMove = new ChessMove(currPosition,king,QUEEN);
                        }
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

        /*

        1) If in check
        2) If in stalemate
        3) Checkmate = true

         */
        boolean InStalemate = true;

        TeamColor trueTeamColor = this.getTeamTurn();

        if (this.getTeamTurn() == WHITE){
            trueTeamColor = WHITE;
        }
        if (this.getTeamTurn() == TeamColor.BLACK){
            trueTeamColor = TeamColor.BLACK;
        }

        while (InStalemate==true) {
            for (int i = 1; i < 9; i++) {
                for (int j = 1; j < 9; j++) {
                    ///  If there is a chess piece && Its team color is the one we are checking
                    if (board.getPiece(new ChessPosition(i, j)) != null && board.getPiece(new ChessPosition(i, j)).getTeamColor() == teamColor) {

                        ///  See if it has any validMoves
                        ChessPosition pos = new ChessPosition(i, j);
                        this.setTeamTurn(trueTeamColor);
                        if (this.validMoves(pos).size() == 0) {

                        } else {
                            InStalemate = false;
                            break;
                        }
                    }
                }
            }
            break;
        }

        boolean check = this.isInCheck(teamColor);


        if (check && InStalemate) {
            return true;
        } else {
            return false;
        }
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

        if (this.getTeamTurn() == WHITE){
            trueTeamColor = WHITE;
        }
        if (this.getTeamTurn() == TeamColor.BLACK){
            trueTeamColor = TeamColor.BLACK;
        }



        ///  Iterate over board to find pieces of teamColor
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ///  If there is a chess piece && Its team color is the one we are checking
                if (board.getPiece(new ChessPosition(i, j))!=null && board.getPiece(new ChessPosition(i, j)).getTeamColor() == teamColor) {

                    ///  See if it has any validMoves
                    ChessPosition pos = new ChessPosition(i,j);
                    this.setTeamTurn(trueTeamColor);
                    if (this.validMoves(pos).size() == 0){

                    }else{
                        return false;
                    }
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
