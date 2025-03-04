package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() {
        String temp = "";
        return temp+"("+this.getRow()+", "+this.getColumn()+")";
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

        ChessPosition c = (ChessPosition)obj;
        return(this.row == c.row && this.col == c.col);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }



    /**
     * @return which column this position is in
     * 1 codes for the left col
     */
    public int getColumn() {
        return col;
    }
}
