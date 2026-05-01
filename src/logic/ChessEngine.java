package logic;

import java.util.ArrayList;
import java.util.Arrays;

import static logic.GameUtilitiesAndConstants.BLACK;
import static logic.GameUtilitiesAndConstants.WHITE;

public class ChessEngine {
    public int depth;

    public int countLegalPositions(GameController gameController, int depth) {
        int color = gameController.playerToMove;
        // Count only leaf nodes
        int legalPositionCount = (depth == 0) ? 1 : 0;

        if ((depth > 0) && !((gameController.isCheckmated(color)) || (gameController.isStalemated(color)))) {
            ChessPiece[] pieceList = gameController.pieceList;
            for (ChessPiece piece : pieceList) {
                if ((piece.color != color) || (piece.isCaptured)) {
                    continue;
                }

                for (int[] legalMove : piece.legalMoves) {
                    ChessPiece[] pieceListCopy = new ChessPiece[32];

                    // For every piece in current board, copy piece and add to new board
                    for (int i = 0; i < 32; i++) {
                        ChessPiece pieceToCopy = pieceList[i];

                        pieceListCopy[i] = pieceToCopy.createCopy();
                    }

                    // Create a new board where piece is at the legalMove
                    GameController tempGameController = new GameController(pieceListCopy, color, false, true);

                    tempGameController.positionsSelected[0] = Arrays.copyOf(piece.position, piece.position.length);
                    tempGameController.positionsSelected[1] = Arrays.copyOf(legalMove, legalMove.length);
                    tempGameController.applyMove();


                    legalPositionCount += countLegalPositions(tempGameController, depth - 1);
                }
            }
        }

        return legalPositionCount;
    }

    int evaluateCurrentScore(GameController gameController) {
        int eval = 0;

        if (gameController.isCheckmated(0)) {
            return 1000;
        }

        if (gameController.isCheckmated(1)) {
            return -1000;
        }

        if (gameController.isStalemated(0) || gameController.isStalemated(1)) {
            return 0;
        }

        for (ChessPiece piece : gameController.pieceList) {
            if (piece.isCaptured) {
                continue;
            }

            int valueMultiplier = (piece.color == BLACK) ? -1 : 1;
            eval += piece.pieceValue * valueMultiplier;
        }

        return eval;
    }

    // Finds the strongest move for the current playerToMove using the maximin algorithm
    int[][] getBestMove(GameController gameController, int depth) {
        if (depth == 0) {
            return null;
        }

        int color = gameController.playerToMove;
        ArrayList<int[][]> bestMoves = new ArrayList<>();

        int valueMultiplier = (color == BLACK) ? -1 : 1;
        int currentEval = -2000 * valueMultiplier;

        int alpha = -2000;
        int beta = 2000;

        for (ChessPiece piece : gameController.pieceList) {
            if ((piece.color != color) || (piece.isCaptured)) {
                continue;
            }

            for (int[] legalMove : piece.legalMoves) {
                ChessPiece[] pieceListCopy = new ChessPiece[32];

                // For every piece in current board, copy piece and add to new board
                for (int i = 0; i < 32; i++) {
                    ChessPiece pieceToCopy = gameController.pieceList[i];

                    pieceListCopy[i] = pieceToCopy.createCopy();
                }

                // Create a new board where piece is at the legalMove
                GameController tempGameController = new GameController(pieceListCopy, color, false, true);

                tempGameController.positionsSelected[0] = Arrays.copyOf(piece.position, piece.position.length);
                tempGameController.positionsSelected[1] = Arrays.copyOf(legalMove, legalMove.length);
                tempGameController.applyMove();

                // Check the eval for the new board with new move for a given piece
                int moveResultingScore = leastFavourableEval(tempGameController, depth - 1, alpha, beta);
                boolean minimaxEvalCondition = (color == WHITE) ? (moveResultingScore > currentEval) : (moveResultingScore < currentEval);

                // Update alpha and beta parameters
                if (color == WHITE) {
                    alpha = Math.max(alpha, moveResultingScore);
                } else {
                    beta = Math.min(beta, moveResultingScore);
                }

                if (minimaxEvalCondition) {
                    int[][] bestMoveEntry = new int[][] {piece.position, legalMove};
                    currentEval = moveResultingScore;

                    bestMoves.clear();

                    bestMoves.add(bestMoveEntry);
                } else if (moveResultingScore == currentEval) {
                    int[][] bestMoveEntry = new int[][] {piece.position, legalMove};
                    currentEval = moveResultingScore;

                    bestMoves.add(bestMoveEntry);
                }
            }
        }

        // Randomly pick one of the best moves: technically you could add some more logic that filters pieces by alternate metrics, like
        // how weighted towards the center the pieces are...
        int randomIndex = (int) (Math.random() * bestMoves.size());
        return bestMoves.get(randomIndex);
    }

    // Note that this form of evaluation has a RELATIVE sign notation: positive is good for BOTH colors, depends on what the originalColor argument is
    int leastFavourableEval(GameController gameController, int depth, int alpha, int beta) {
        int color = gameController.playerToMove;
        if (depth == 0) {
            return evaluateCurrentScore(gameController);
        }

        // Count only leaf nodes; non-leaf nodes should be an unreasonably favourable value
        int valueMultiplier = (gameController.playerToMove == BLACK) ? -1 : 1;
        int currentEval = -2000 * valueMultiplier;

        for (ChessPiece piece : gameController.pieceList) {
            if ((piece.color != color) || (piece.isCaptured)) {
                continue;
            }

            for (int[] legalMove : piece.legalMoves) {
                ChessPiece[] pieceListCopy = new ChessPiece[32];

                // For every piece in current board, copy piece and add to new board
                for (int i = 0; i < 32; i++) {
                    ChessPiece pieceToCopy = gameController.pieceList[i];

                    pieceListCopy[i] = pieceToCopy.createCopy();
                }

                // Create a new board where piece is at the legalMove
                GameController tempGameController = new GameController(pieceListCopy, color, false, true);

                tempGameController.positionsSelected[0] = Arrays.copyOf(piece.position, piece.position.length);
                tempGameController.positionsSelected[1] = Arrays.copyOf(legalMove, legalMove.length);
                tempGameController.applyMove();

                if (color == WHITE) {
                    // Get most favourable move for white
                    currentEval = Math.max(currentEval, leastFavourableEval(tempGameController, depth - 1, alpha, beta));
                } else {
                    // Get most favourable move for black
                    currentEval = Math.min(currentEval, leastFavourableEval(tempGameController, depth - 1, alpha, beta));
                }

                // Update alpha and beta parameters
                if (color == WHITE) {
                    alpha = Math.max(alpha, currentEval);
                } else {
                    beta = Math.min(beta, currentEval);
                }

                if (beta <= alpha) break;
            }
        }

        return currentEval;
    }

    public ChessEngine(int depth) {
        this.depth = depth;
    }
}
