package logic;

import java.util.ArrayList;
import java.util.Arrays;

import static logic.GameUtilitiesAndConstants.*;

public class ChessPiece {
    public ArrayList<int[]> legalMoves;
    public int[] previousPosition;
    public int[] displayPosition;
    // NOTE: captured pieces should be marked with positions (-1, -1), simply so they are out of the way
    public boolean isCaptured;
    public String pieceType;
    // NOTE: positions are formatted [row, col]
    public int[] position;
    public int moveCount;
    public int color;
    public int pieceValue;
    // Used to determine en passant; a pawn that jumps 2 units needs to be the last piece moved to be captured via en passant
    public boolean isLastMovedPiece;

    public boolean containsLegalMove(int[] position) {
        for (int[] legalMove : this.legalMoves) {
            if (Arrays.equals(legalMove, position)) {
                return true;
            }
        }

        return false;
    }

    public void updatePosition(int[] positionArg) {
        this.previousPosition = this.position;
        this.displayPosition = getDisplayCoordinates(positionArg);
        this.position = positionArg;
        this.moveCount++;
    }

    public ChessPiece createCopy() {
        ChessPiece pieceCopy = new ChessPiece(this.pieceType, this.color, this.position, this.pieceValue);

        pieceCopy.isLastMovedPiece = this.isLastMovedPiece;
        pieceCopy.isCaptured       = this.isCaptured;
        pieceCopy.moveCount        = this.moveCount;
        pieceCopy.previousPosition = this.previousPosition;
        pieceCopy.legalMoves       = new ArrayList<>();

        for (int[] position : this.legalMoves) {
            pieceCopy.legalMoves.add(position.clone());
        }

        return pieceCopy;
    }

    ChessPiece(String pieceType, int color, int[] position, int pieceValue) {
        this.position = position;
        this.displayPosition = getDisplayCoordinates(position);
        this.pieceType = pieceType;
        this.isCaptured = false;
        this.color = color;
        this.moveCount = 0;
        this.pieceValue = pieceValue;
        this.previousPosition = new int[]{-1, -1};
        this.legalMoves = new ArrayList<int[]>();
        this.isLastMovedPiece = false;
    }
}
