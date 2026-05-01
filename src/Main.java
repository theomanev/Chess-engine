import logic.GameController;
import ui.RenderBoard;

import javax.swing.*;

public class Main {
    private static final int engineDepth = 3;

    public static void main(String[] args) {
        // To ensure updates to the board display occur while the Event Dispatch Thread (EDT) is ready, we must use .invokeLater() to avoid race conditions
        SwingUtilities.invokeLater(() -> {
            // Game controller
            GameController gameController = new GameController(engineDepth);

            // Render board
            RenderBoard boardRenderer = new RenderBoard(gameController);

            boardRenderer.setupFrame();
            boardRenderer.initializeCheckers();

            // Add pieces to board
            gameController.initializeBoard(boardRenderer);
        });
    }
}


