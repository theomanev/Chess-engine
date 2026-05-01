package logic;

import ui.RenderBoard;

import java.util.Arrays;

import static logic.GameUtilitiesAndConstants.*;

public class GameController {
    private GenerateMoves moveGenerator;
    private RenderBoard renderBoard;
    public ChessPiece[] pieceList;
    public boolean isCastlingMove;
    public boolean isEngineMove;
    private int engineDepth;
    public int playerToMove;

    public int[][] positionsSelected;

    public boolean isOutsideBounds(int[] position) {
        return (position[0] > 7 || position[1] > 7 || position[0] < 0 || position[1] < 0);
    }

    public ChessPiece getPieceAtSquare(int[] position) {
        for (ChessPiece piece : pieceList) {
            if (Arrays.equals(piece.position, position)) {
                return piece;
            }
        }

        return null;
    }

    int countLegalMoves(int selectedColor) {
        int legalMoveCount = 0;

        for (ChessPiece piece : this.pieceList) {
            if (piece.color != selectedColor) {
                continue;
            }

            legalMoveCount += piece.legalMoves.size();
        }

        return legalMoveCount;
    }

    boolean isCheckmated(int color) {
        if (playerToMove != color) {
            return false;
        }

        ChessPiece kingPiece = null;
        for (ChessPiece piece : pieceList) {
            if ((piece.color == color) && piece.pieceType.equals("king")) {
                kingPiece = piece;
            }
        }

        int legalMoveCount = countLegalMoves(color);

        return (legalMoveCount == 0) && isSquareTargetedByColor(kingPiece.position, kingPiece.color ^ 1);
    }

    boolean isStalemated(int color) {
        if (playerToMove != color) {
            return false;
        }

        ChessPiece kingPiece = null;
        for (ChessPiece piece : pieceList) {
            if ((piece.color == color) && piece.pieceType.equals("king")) {
                kingPiece = piece;
            }
        }

        int legalMoveCount = countLegalMoves(color);

        return (legalMoveCount == 0) && !(isSquareTargetedByColor(kingPiece.position, kingPiece.color ^ 1));
    }

    public void mouseClicked(int[] position) {
        position = getCoordinatesFromDisplay(position);
        ChessPiece pieceAtSquare = getPieceAtSquare(position);

        // Ban the following: not trying to select your colored piece initially, not ending a move with a legal move
        if (((positionsSelected[0] == null) && ((pieceAtSquare == null) || (pieceAtSquare.color != playerToMove))) ||
            ((positionsSelected[0] != null) && (!getPieceAtSquare(positionsSelected[0]).containsLegalMove(position)))) {

            positionsSelected[0] = null;
            positionsSelected[1] = null;

            System.out.println("CONSOLE: Invalid square selected!");

            return;
        }

        // If player is playing against engine, enforce player only playing his color
        if (playerToMove != playerStartingColor) {
            System.out.println("CONSOLE: Please wait another turn before playing!");

            positionsSelected[0] = null;
            positionsSelected[1] = null;

            return;
        }

        System.out.println("CONSOLE: Valid square selected!");

        // If this is the first square being selected...
        if (positionsSelected[0] == null) {
            positionsSelected[0] = position;
        } else {
            positionsSelected[1] = position;
            applyMove();
        }
    }

    public void applyMove() {
        ChessPiece pieceToMove = getPieceAtSquare(positionsSelected[0]);
        ChessPiece pieceTargeted = getPieceAtSquare(positionsSelected[1]);

        int[] squareToMoveTo = positionsSelected[1];

        // Check for special en passant case and promotion
        if (pieceToMove.pieceType.equals("pawn")) {
            int i = (pieceToMove.color == BLACK) ? 1 : -1;

            ChessPiece pieceAtEnPassant = getPieceAtSquare(new int[] {positionsSelected[1][0] - i, positionsSelected[1][1]});

            // If en passant (diagonal capture where there's no piece directly being captured)
            // Avoid case where pawn normally moves forward one unit
            if ((pieceTargeted == null) && (pieceAtEnPassant != null) && (pieceAtEnPassant.color != playerToMove)) {
                pieceAtEnPassant.isCaptured = true;

                pieceAtEnPassant.previousPosition = new int[] {-1, -1};
                pieceAtEnPassant.displayPosition  = new int[] {-1, -1};
                pieceAtEnPassant.position         = new int[] {-1, -1};
            }

            // In the case of promotion (a pawn is at either ends of the board)
            // Automatically promote to queen is enabled in this mode of chess
            if ((positionsSelected[1][0] == 0) || (positionsSelected[1][0] == 7)) {
                pieceToMove.pieceType = "queen";
                pieceToMove.pieceValue = 9;
            }
        }

        // Check for special castling
        if (pieceToMove.pieceType.equals("king")) {
            if (Math.abs(positionsSelected[1][1] - positionsSelected[0][1]) == 2) {
                // Get whether the king is castling left or right (dir = -1 or 1, respectively)
                int dir = Integer.signum(positionsSelected[1][1] - positionsSelected[0][1]);

                // Horizontal offset of rook from king depending on which way the king is castling
                int rookInc = (dir > 0) ? (3 * dir) : (4 * dir);
                ChessPiece castledRook = getPieceAtSquare(new int[] {positionsSelected[0][0], positionsSelected[0][1] + rookInc});

                castledRook.updatePosition(new int[] {positionsSelected[0][0], positionsSelected[0][1] + dir});
            }
        }

        // If there is a piece being captured; there is a piece on the square where our piece is going
        if (pieceTargeted != null) {
            pieceTargeted.isCaptured = true;

            pieceTargeted.previousPosition = new int[] {-1, -1};
            pieceTargeted.displayPosition  = new int[] {-1, -1};
            pieceTargeted.position         = new int[] {-1, -1};
        }

        pieceToMove.updatePosition(squareToMoveTo);

        updateLastMoved(pieceToMove);

        // Apply XOR gate to toggle playerToMove
        this.playerToMove ^= 1;

        updateAllPieceMoves();

        // We only want the main gameController to update the board; alternative gameControllers used should not update
        if (renderBoard != null) {
            renderBoard.clearBoardIcons();
            renderBoard.renderPieces(pieceList);
        }

        // Check for stalemate or checkmate
        if (isCheckmated(BLACK) && (!isEngineMove && !isCastlingMove)) {
            System.out.println("CONSOLE: Game over! White wins by checkmate.");
            System.exit(0);
        }

        if (isCheckmated(WHITE) && (!isEngineMove && !isCastlingMove)) {
            System.out.println("CONSOLE: Game over! Black wins by checkmate.");
            System.exit(0);
        }

        if (isStalemated(playerToMove) && (!isEngineMove && !isCastlingMove)) {
            System.out.println("CONSOLE: Game over! Stalemate.");
            System.exit(0);
        }

        positionsSelected[0] = null;
        positionsSelected[1] = null;

        if ((playerToMove != playerStartingColor) && !isEngineMove) {
            makeEngineMove();
        }
    }

    // NOTE: there is coupling between the isSquareTargetedByColor and updateMoves methods; this causes error!
    public boolean isSquareTargetedByColor(int[] targetPosition, int color) {
        for (ChessPiece piece : pieceList) {
            if ((piece.color != color) || piece.isCaptured) {
                continue;
            }

            for (int[] possibleMove : piece.legalMoves) {
                if (Arrays.equals(possibleMove, targetPosition)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void updateLastMoved(ChessPiece movedPiece) {
        for (ChessPiece piece : pieceList) {
            piece.isLastMovedPiece = false;
        }

        movedPiece.isLastMovedPiece = true;
    }

    public void updateAllPieceMoves() {
        for (ChessPiece piece : pieceList) {
            if (!piece.pieceType.equals("king")) {
                moveGenerator.updateLegalMoves(piece);
            }
        }

        // Make sure to update king pieces last, they require all pieces to have updated moves to determine castling
        for (ChessPiece piece : pieceList) {
            if (piece.pieceType.equals("king")) {
                moveGenerator.updateLegalMoves(piece);
            }
        }
    }

    public void makeEngineMove() {
        ChessEngine engine = new ChessEngine(engineDepth);

        int[][] bestEngineMove = engine.getBestMove(this, engineDepth);

        positionsSelected[0] = Arrays.copyOf(bestEngineMove[0], bestEngineMove[0].length);
        positionsSelected[1] = Arrays.copyOf(bestEngineMove[1], bestEngineMove[1].length);
        applyMove();
    }

    public void initializeBoard(RenderBoard renderBoard) {
        pieceList = new ChessPiece[32];

        // Add black pieces to board
        pieceList[0]  = new ChessPiece("rook", BLACK, new int[] {0, 0}, 5);
        pieceList[1]  = new ChessPiece("knight", BLACK, new int[] {0, 1}, 3);
        pieceList[2]  = new ChessPiece("bishop", BLACK, new int[] {0, 2}, 3);
        pieceList[3]  = new ChessPiece("queen", BLACK, new int[] {0, 3}, 9);
        pieceList[4]  = new ChessPiece("king", BLACK, new int[] {0, 4}, 100);
        pieceList[5]  = new ChessPiece("bishop", BLACK, new int[] {0, 5}, 3);
        pieceList[6]  = new ChessPiece("knight", BLACK, new int[] {0, 6}, 3);
        pieceList[7]  = new ChessPiece("rook", BLACK, new int[] {0, 7}, 5);
        pieceList[8]  = new ChessPiece("pawn", BLACK, new int[] {1, 0}, 1);
        pieceList[9]  = new ChessPiece("pawn", BLACK, new int[] {1, 1}, 1);
        pieceList[10] = new ChessPiece("pawn", BLACK, new int[] {1, 2}, 1);
        pieceList[11] = new ChessPiece("pawn", BLACK, new int[] {1, 3}, 1);
        pieceList[12] = new ChessPiece("pawn", BLACK, new int[] {1, 4}, 1);
        pieceList[13] = new ChessPiece("pawn", BLACK, new int[] {1, 5}, 1);
        pieceList[14] = new ChessPiece("pawn", BLACK, new int[] {1, 6}, 1);
        pieceList[15] = new ChessPiece("pawn", BLACK, new int[] {1, 7}, 1);

        // Add white pieces to board
        pieceList[16] = new ChessPiece("rook", WHITE, new int[] {7, 0}, 5);
        pieceList[17] = new ChessPiece("knight", WHITE, new int[] {7, 1}, 3);
        pieceList[18] = new ChessPiece("bishop", WHITE, new int[] {7, 2}, 3);
        pieceList[19] = new ChessPiece("queen", WHITE, new int[] {7, 3}, 9);
        pieceList[20] = new ChessPiece("king", WHITE, new int[] {7, 4}, 100);
        pieceList[21] = new ChessPiece("bishop", WHITE, new int[] {7, 5}, 3);
        pieceList[22] = new ChessPiece("knight", WHITE, new int[] {7, 6}, 3);
        pieceList[23] = new ChessPiece("rook", WHITE, new int[] {7, 7}, 5);
        pieceList[24] = new ChessPiece("pawn", WHITE, new int[] {6, 0}, 1);
        pieceList[25] = new ChessPiece("pawn", WHITE, new int[] {6, 1}, 1);
        pieceList[26] = new ChessPiece("pawn", WHITE, new int[] {6, 2}, 1);
        pieceList[27] = new ChessPiece("pawn", WHITE, new int[] {6, 3}, 1);
        pieceList[28] = new ChessPiece("pawn", WHITE, new int[] {6, 4}, 1);
        pieceList[29] = new ChessPiece("pawn", WHITE, new int[] {6, 5}, 1);
        pieceList[30] = new ChessPiece("pawn", WHITE, new int[] {6, 6}, 1);
        pieceList[31] = new ChessPiece("pawn", WHITE, new int[] {6, 7}, 1);

        this.positionsSelected = new int[][] {null, null};
        playerToMove           = WHITE;

        // Create render board and render the initial board layout
        this.renderBoard  = renderBoard;
        this.isCastlingMove = false;
        this.renderBoard.renderPieces(pieceList);

        updateAllPieceMoves();

        if (playerToMove != playerStartingColor) {
            makeEngineMove();
        }
    }

    // NOTE: if using this version of the constructor, make sure to call initializeBoard() manually!
    public GameController(int engineDepth) {
        this.engineDepth   = engineDepth;
        this.moveGenerator = new GenerateMoves(this);
    }

    // Use this constructor if you want to start the game beyond the traditional starting point
    public GameController(ChessPiece[] pieceList, int playerToMove, boolean isCastlingMove, boolean isEngineMove) {
        this.positionsSelected = new int[][] {null, null};
        this.pieceList         = new ChessPiece[32];
        this.moveGenerator     = new GenerateMoves(this);
        this.playerToMove      = playerToMove;
        this.renderBoard       = null;
        this.isCastlingMove    = isCastlingMove;
        this.isEngineMove      = isEngineMove;

        for (int i = 0; i < 32; i++) {
            ChessPiece piece = pieceList[i];

            ChessPiece clonedPiece = piece.createCopy();
            this.pieceList[i]      = clonedPiece;
        }
    }
}
