package ui;

import logic.ChessPiece;
import logic.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import static logic.GameUtilitiesAndConstants.tileSize;

public class RenderBoard {
    // Game Variables

    // GUI Variables
    private static JLabel[][] boardSquaresList = new JLabel[8][8];
    private static String workingDirectory = System.getProperty("user.dir") + File.separator + "src";
    private static JFrame frame;

    private GameController gameController;

    public void setupFrame() {
        // Create the GUI frame
        frame = new JFrame();

        // Set the GUI frame's size and functionality
        frame.setTitle("Chess Engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(tileSize * 8, tileSize * 8);
        frame.setResizable(false);

        // Set the GUI frame's icon image
        String applicationThumbnailPath = workingDirectory + File.separator + "resources" + File.separator + "king_black.png";
        frame.setIconImage(new ImageIcon(applicationThumbnailPath).getImage());

        // Set the GUI frame's layout
        frame.setLayout(new GridLayout(8, 8));

        // Set the GUI frame as visible
        frame.setVisible(true);
    }

    public void initializeCheckers() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JLabel boardSquare = new JLabel();

                // Set up board square layout settings
                boardSquare.setOpaque(true);
                boardSquare.setHorizontalAlignment(SwingConstants.CENTER);
                boardSquare.setVerticalAlignment(SwingConstants.CENTER);

                // Make checkered pattern on board by checking if column and row number of square is both even/odd, or otherwise
                // NOTE: checkers are swapped depending on whether our player is white or black
                if ((row + col) % 2 != 1) {
                    boardSquare.setBackground(Color.WHITE);
                } else {
                    boardSquare.setBackground(Color.darkGray);
                }

                // Copy variables to placed in MouseAdapter instance
                int classRow = row;
                int classCol = col;

                boardSquare.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        gameController.mouseClicked(new int[] {classRow, classCol});
                    }
                });

                boardSquaresList[row][col] = boardSquare;
                frame.add(boardSquare);
            }
        }
    }

    public void renderPieces(ChessPiece[] pieceList) {
        for (ChessPiece selectedPiece : pieceList) {
            if (selectedPiece.isCaptured) {
                continue;
            }

            String pieceColor = (selectedPiece.color == 0) ? "black" : "white";

            // Get address of .PNG for piece
            ImageIcon pieceImage = new ImageIcon(workingDirectory + File.separator + "resources" + File.separator + selectedPiece.pieceType + "_" + pieceColor + ".png");
            Image scaledImage = pieceImage.getImage().getScaledInstance(tileSize, tileSize, Image.SCALE_SMOOTH);
            pieceImage = new ImageIcon(scaledImage);

            // Add piece to board
            boardSquaresList[selectedPiece.displayPosition[0]][selectedPiece.displayPosition[1]].setIcon(pieceImage);
        }
    }

    public void clearBoardIcons() {
        for (JLabel[] boardRow : boardSquaresList) {
            for (JLabel boardSquare : boardRow) {
                boardSquare.setIcon(null);

            }
        }
    }

    public RenderBoard(GameController gameController) {
        this.gameController = gameController;
    }
}
