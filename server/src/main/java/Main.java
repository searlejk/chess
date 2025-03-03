import chess.*;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import server.Server;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);



        Server temp = new Server();
        temp.run(8080);

    }
}
