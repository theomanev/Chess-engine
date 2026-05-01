package logic;

import java.util.ArrayList;
import java.util.Arrays;

import static logic.GameUtilitiesAndConstants.BLACK;

public class GenerateMoves {
    private GameController gameController;

    public void updateLegalMovesForCheck(ChessPiece pieceToCheck) {
        if (!gameController.isCastlingMove) {
            ArrayList<int[]> illegalMoves = new ArrayList<>();

            for (int[] legalMove : pieceToCheck.legalMoves) {
                ChessPiece[] pieceListCopy = new ChessPiece[32];
                int kingIndex = 0;

                // For every piece in current board
                for (int i = 0; i < 32; i++) {
                    ChessPiece pieceToCopy = gameController.pieceList[i];

                    // If piece is the king, keep note of its position
                    if ((pieceToCopy.color == pieceToCheck.color) && (pieceToCopy.pieceType == "king")) {
                        kingIndex = i;
                    }

                    // If not king, just do a normal copy
                    pieceListCopy[i] = pieceToCopy.createCopy();
                }

                // Create a new board where king is at the legalMove
                int oppositeColor = gameController.playerToMove ^ 1;
                GameController tempGameController = new GameController(pieceListCopy, gameController.playerToMove, true, true);
                ChessPiece updatedKing = tempGameController.pieceList[kingIndex];

                tempGameController.positionsSelected[0] = Arrays.copyOf(pieceToCheck.position, pieceToCheck.position.length);
                tempGameController.positionsSelected[1] = Arrays.copyOf(legalMove, legalMove.length);
                tempGameController.applyMove();

                // See if the king is attacked in the hypothetical scenario
                boolean isMoveValid = !(tempGameController.isSquareTargetedByColor(updatedKing.position, oppositeColor));

                // If the move is invalid, take the shown illegal move and remove it from the list of legal moves
                if (!isMoveValid) {
                    illegalMoves.add(legalMove);
                }
            }

            pieceToCheck.legalMoves.removeAll(illegalMoves);
        }
    }

    public void updateVerticalColumn(ChessPiece piece) {
        for (int i = 1; !(gameController.isOutsideBounds(new int[] {piece.position[0] + i, piece.position[1]})); i++) {
            ChessPiece pieceAtSquare = gameController.getPieceAtSquare(new int[] {piece.position[0] + i, piece.position[1]});
            if (pieceAtSquare == null) {
                piece.legalMoves.add(new int[] {piece.position[0] + i, piece.position[1]});
                continue;
            }

            if (pieceAtSquare.color != piece.color) {
                piece.legalMoves.add(new int[] {piece.position[0] + i, piece.position[1]});
            }

            break;
        }

        for (int i = -1; !(gameController.isOutsideBounds(new int[] {piece.position[0] + i, piece.position[1]})); i--) {
            ChessPiece pieceAtSquare = gameController.getPieceAtSquare(new int[] {piece.position[0] + i, piece.position[1]});
            if (pieceAtSquare == null) {
                piece.legalMoves.add(new int[] {piece.position[0] + i, piece.position[1]});
                continue;
            }

            if (pieceAtSquare.color != piece.color) {
                piece.legalMoves.add(new int[] {piece.position[0] + i, piece.position[1]});
            }

            break;
        }

        updateLegalMovesForCheck(piece);
    }

    public void updateHorizontalRow(ChessPiece piece) {
        for (int i = 1; !(gameController.isOutsideBounds(new int[] {piece.position[0], piece.position[1] + i})); i++) {
            ChessPiece pieceAtSquare = gameController.getPieceAtSquare(new int[] {piece.position[0], piece.position[1] + i});
            if (pieceAtSquare == null) {
                piece.legalMoves.add(new int[] {piece.position[0], piece.position[1] + i});
                continue;
            }

            if (pieceAtSquare.color != piece.color) {
                piece.legalMoves.add(new int[] {piece.position[0], piece.position[1] + i});
            }

            break;
        }

        for (int i = -1; !(gameController.isOutsideBounds(new int[] {piece.position[0], piece.position[1] + i})); i--) {
            ChessPiece pieceAtSquare = gameController.getPieceAtSquare(new int[] {piece.position[0], piece.position[1] + i});
            if (pieceAtSquare == null) {
                piece.legalMoves.add(new int[] {piece.position[0], piece.position[1] + i});
                continue;
            }

            if (pieceAtSquare.color != piece.color) {
                piece.legalMoves.add(new int[] {piece.position[0], piece.position[1] + i});
            }

            break;
        }

        updateLegalMovesForCheck(piece);
    }

    public void updateDiagonalRows(ChessPiece piece) {
        for (int i = 1; !(gameController.isOutsideBounds(new int[] {piece.position[0] + i, piece.position[1] + i})); i++) {
            ChessPiece pieceAtSquare = gameController.getPieceAtSquare(new int[] {piece.position[0] + i, piece.position[1] + i});

            // If diagonal entry is an empty square
            if (pieceAtSquare == null) {
                piece.legalMoves.add(new int[] {piece.position[0] + i, piece.position[1] + i});
                continue;
            }

            // If diagonal entry is occupied and piece is of the opposite color
            if (pieceAtSquare.color != piece.color) {
                piece.legalMoves.add(new int[] {piece.position[0] + i, piece.position[1] + i});
            }

            break;
        }

        for (int i = -1; !(gameController.isOutsideBounds(new int[] {piece.position[0] + i, piece.position[1] + i})); i--) {
            ChessPiece pieceAtSquare = gameController.getPieceAtSquare(new int[] {piece.position[0] + i, piece.position[1] + i});

            // If diagonal entry is an empty square
            if (pieceAtSquare == null) {
                piece.legalMoves.add(new int[] {piece.position[0] + i, piece.position[1] + i});
                continue;
            }

            // If diagonal entry is occupied and piece is of the opposite color
            if (pieceAtSquare.color != piece.color) {
                piece.legalMoves.add(new int[] {piece.position[0] + i, piece.position[1] + i});
            }

            break;
        }

        for (int i = 1; !(gameController.isOutsideBounds(new int[] {piece.position[0] - i, piece.position[1] + i})); i++) {
            ChessPiece pieceAtSquare = gameController.getPieceAtSquare(new int[] {piece.position[0] - i, piece.position[1] + i});

            // If diagonal entry is an empty square
            if (pieceAtSquare == null) {
                piece.legalMoves.add(new int[] {piece.position[0] - i, piece.position[1] + i});
                continue;
            }

            // If diagonal entry is occupied and piece is of the opposite color
            if (pieceAtSquare.color != piece.color) {
                piece.legalMoves.add(new int[] {piece.position[0] - i, piece.position[1] + i});
            }

            break;
        }

        for (int i = -1; !(gameController.isOutsideBounds(new int[] {piece.position[0] - i, piece.position[1] + i})); i--) {
            ChessPiece pieceAtSquare = gameController.getPieceAtSquare(new int[] {piece.position[0] - i, piece.position[1] + i});

            // If diagonal entry is an empty square
            if (pieceAtSquare == null) {
                piece.legalMoves.add(new int[] {piece.position[0] - i, piece.position[1] + i});
                continue;
            }

            // If diagonal entry is occupied and piece is of the opposite color
            if (pieceAtSquare.color != piece.color) {
                piece.legalMoves.add(new int[] {piece.position[0] - i, piece.position[1] + i});
            }

            break;
        }

        updateLegalMovesForCheck(piece);
    }

    public void updateKingPosition(ChessPiece piece) {
        int[][] possiblePositions = {new int[] {piece.position[0] + 1, piece.position[1] + 1},
                                     new int[] {piece.position[0] - 1, piece.position[1] - 1},
                                     new int[] {piece.position[0] + 1, piece.position[1] - 1},
                                     new int[] {piece.position[0] - 1, piece.position[1] + 1},
                                     new int[] {piece.position[0] + 1, piece.position[1]},
                                     new int[] {piece.position[0] - 1, piece.position[1]},
                                     new int[] {piece.position[0],     piece.position[1] + 1},
                                     new int[] {piece.position[0],     piece.position[1] - 1},
                                     new int[] {piece.position[0],     piece.position[1] + 2},
                                     new int[] {piece.position[0],     piece.position[1] - 2}};

        for (int[] position : possiblePositions) {
            if (gameController.isOutsideBounds(position)) {
                continue;
            }

            ChessPiece pieceAtSquare = gameController.getPieceAtSquare(position);

            // If move is castling the king to the left or right...
            if (Math.abs(position[1] - piece.position[1]) == 2) {
                // If king has already moved
                if (piece.moveCount > 0) {
                    continue;
                }

                // Get whether the king is castling left or right (dir = -1 or 1, respectively)
                int dir = Integer.signum(position[1] - piece.position[1]);

                // Horizontal offset of rook from king depending on which way the king is castling
                int rookInc = (dir > 0) ? (3 * dir) : (4 * dir);

                ChessPiece pieceAtRookSquare = gameController.getPieceAtSquare(new int[] {piece.position[0], piece.position[1] + rookInc});

                if ((pieceAtRookSquare != null) && (pieceAtRookSquare.pieceType.equals("rook"))) {
                    if (pieceAtRookSquare.moveCount > 0) {
                        continue;
                    }

                    boolean isValidMove = true;

                    // If the king is currently in check
                    if (gameController.isSquareTargetedByColor(piece.position, (piece.color ^ 1))) {
                        isValidMove = false;
                    }

                    for (int i = dir; (Math.abs(i) < Math.abs(rookInc) && isValidMove); i += dir) {
                        int[] interimSquarePosition = new int[] {piece.position[0], piece.position[1] + i};

                        if (gameController.getPieceAtSquare(interimSquarePosition) != null) {
                            isValidMove = false;
                        }

                        if ((Math.abs(i) <= 2) && (gameController.isSquareTargetedByColor(interimSquarePosition, (piece.color ^ 1)))) {
                            isValidMove = false;
                        }
                    }

                    if (isValidMove) {
                        piece.legalMoves.add(position);
                    }
                }

                continue;
            }

            if (pieceAtSquare == null) {
                piece.legalMoves.add(position);
                continue;
            }

            if ((pieceAtSquare != null) && (pieceAtSquare.color != piece.color)) {
                piece.legalMoves.add(position);
            }
        }

        updateLegalMovesForCheck(piece);
    }

    public void updatePawnPosition(ChessPiece piece) {
        int i = (piece.color == BLACK) ? 1 : -1;

        int[][] possiblePositions = {new int[] {piece.position[0] + i,       piece.position[1]},
                                     new int[] {piece.position[0] + (2 * i), piece.position[1]},
                                     new int[] {piece.position[0] + i,       piece.position[1] + 1},
                                     new int[] {piece.position[0] + i,       piece.position[1] - 1}};

        for (int[] position : possiblePositions) {
            if (gameController.isOutsideBounds(position)) {
                continue;
            }

            ChessPiece pieceAtSquare = gameController.getPieceAtSquare(position);
            ChessPiece pieceBehindSquare = gameController.getPieceAtSquare(new int[] {position[0] - i, position[1]});

            // Move forwards two squares for first move
            if (Math.abs(position[0] - piece.position[0]) == 2) {
                if ((piece.moveCount) == 0 && (pieceAtSquare == null) && (pieceBehindSquare == null)) {
                    piece.legalMoves.add(position);
                    continue;
                }
            }

            // Regular move forwards at one square
            if ((Math.abs(position[0] - piece.position[0]) == 1) && (Math.abs(position[1] - piece.position[1]) == 0)) {
                if (pieceAtSquare == null) {
                    piece.legalMoves.add(position);
                    continue;
                }
            }

            // En passant or capture diagonally
            if (Math.abs(position[1] - piece.position[1]) == 1) {
                // If a diagonal capture (indicated by piece being present at diagonal position)...
                if ((pieceAtSquare != null) && (pieceAtSquare.color != piece.color)) {
                    piece.legalMoves.add(position);
                    continue;
                }

                // Get piece behind the diagonal position of pawn (containing a possible piece for en passant)
                ChessPiece pieceAtEnPassant  = gameController.getPieceAtSquare(new int[]{position[0] - i, position[1]});

                // If no piece is present on diagonal, but there is a pawn behind the diagonal with only one move, being a move two units forwards...
                if ((pieceAtSquare == null) && (pieceAtEnPassant != null) && (pieceAtEnPassant.color != piece.color)) {
                    if (pieceAtEnPassant.pieceType.equals("pawn") && (pieceAtEnPassant.moveCount == 1) && (pieceAtEnPassant.isLastMovedPiece)) {
                        if (Math.abs(pieceAtEnPassant.position[0] - pieceAtEnPassant.previousPosition[0]) == 2) {
                            piece.legalMoves.add(position);
                            continue;
                        }
                    }
                }
            }
        }

        updateLegalMovesForCheck(piece);
    }

    public void updateKnightPosition(ChessPiece piece) {
        int[][] possiblePositions = {new int[] {piece.position[0] + 2, piece.position[1] + 1},
                                     new int[] {piece.position[0] + 2, piece.position[1] - 1},
                                     new int[] {piece.position[0] - 2, piece.position[1] - 1},
                                     new int[] {piece.position[0] - 2, piece.position[1] + 1},
                                     new int[] {piece.position[0] + 1, piece.position[1] + 2},
                                     new int[] {piece.position[0] + 1, piece.position[1] - 2},
                                     new int[] {piece.position[0] - 1, piece.position[1] - 2},
                                     new int[] {piece.position[0] - 1, piece.position[1] + 2}};

        for (int[] position : possiblePositions) {
            if (gameController.isOutsideBounds(position)) {
                continue;
            }

            ChessPiece pieceAtSquare = gameController.getPieceAtSquare(position);
            if (pieceAtSquare == null) {
                piece.legalMoves.add(position);
                continue;
            }

            if (pieceAtSquare.color != piece.color) {
                piece.legalMoves.add(position);
            }
        }

        updateLegalMovesForCheck(piece);
    }

    public void updateLegalMoves(ChessPiece piece) {
        piece.legalMoves.clear();

        if (piece.isCaptured) {
            return;
        }

        switch (piece.pieceType) {
            case "rook":
                updateVerticalColumn(piece);
                updateHorizontalRow(piece);

                break;
            case "knight":
                updateKnightPosition(piece);

                break;
            case "bishop":
                updateDiagonalRows(piece);

                break;
            case "queen":
                updateVerticalColumn(piece);
                updateHorizontalRow(piece);
                updateDiagonalRows(piece);

                break;
            case "king":
                updateKingPosition(piece);
                break;
            case "pawn":
                updatePawnPosition(piece);
                break;
        }
    }

    public GenerateMoves(GameController gameController) {
        this.gameController = gameController;
    }
}
