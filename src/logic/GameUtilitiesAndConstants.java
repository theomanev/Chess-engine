package logic;

public class GameUtilitiesAndConstants {
    // Game Settings
    public static int tileSize = 80;
    public static int playerStartingColor = (int) (Math.random() * 2);

    // Piece colors
    public static int BLACK = 0;
    public static int WHITE = 1;

    // Get true position of chess piece and flip the row position if starting player is black
    public static int[] getDisplayCoordinates(int[] coordinates) {
        if (playerStartingColor == BLACK) {
            return new int[] {7 - coordinates[0], 7 - coordinates[1]};
        } else {
            return coordinates;
        }
    }

    // Functionally, getting normal coordinates from displayCoordinates is identical to getDisplayCoordinates due to
    // displayPosition and position sharing the same conversion function
    public static int[] getCoordinatesFromDisplay(int[] displayPosition) {
        return getDisplayCoordinates(displayPosition);
    }

    // Avoid class instantiation
    private GameUtilitiesAndConstants() {}
}
