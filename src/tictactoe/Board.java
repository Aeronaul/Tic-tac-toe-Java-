package tictactoe;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Board extends JPanel {
    final static String START_TEXT = "Game is not started";
    final static String INGAME_TEXT = "Game in progress";
    final static int AI_DELAY = 300;
    final static int AUTO_DELAY = 500;
    
    private static boolean isX = true;
    private boolean clickable = true;
    
    JLabel status;
    JButton entity1, entity2, startReset;
    Tile[][] tiles = new Tile[3][3];
    int[][] matrix = new int[3][3];
    Logger logger = Logger.getLogger("Board");


    Board(JLabel status, JButton entity1, JButton entity2, JButton startReset) {
        this.status = status;
        status.setText(START_TEXT);

        logger.setLevel(Level.OFF); // change to INFO when debugging

        this.entity1 = entity1;
        this.entity2 = entity2;
        this.startReset = startReset;

        startReset.addActionListener(e -> {
            startReset.removeActionListener(startReset.getActionListeners()[0]);
            toggle(true);
            status.setText(Board.INGAME_TEXT);
            firstMove();
            startReset.setText("Reset");
            startReset.addActionListener(f -> reset());
        });

        setLayout(new GridLayout(3, 3, 5, 5));
        setBorder(new EmptyBorder(5, 5, 5, 5));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tiles[i][j] = new Tile(i, j);
                Tile tile = tiles[i][j];
                add(tile);
                String name = String.valueOf((char) (j + 65)) + (3 - i);
                tile.setName("Button".concat(name));
                tile.addActionListener(e -> {
                    clickable = !isAITurn();
                    if (!tile.isEnabled() || !tile.getText().isBlank() || !clickable) return;
                    clickable = false;
                    update(tile.row, tile.col);
                    logger.info(String.format("%s pressed!", tile.getName()));
                    new Thread(() -> nextMove(AI_DELAY)).start(); // to add delay

                });
            }
        }
    }

    void firstMove() {
        entity1.setEnabled(false);
        entity2.setEnabled(false);

        if (!isAITurn()) return;

        Random random = new Random();
        int row = random.nextInt(0, 3);
        int col = random.nextInt(0, 3);
        update(row, col);
        new Thread(() -> nextMove(2, AUTO_DELAY)).start(); // to add delay
    }

    void nextMove(int delay) {
        nextMove(1, delay);
    }

    void nextMove(int depth, int delay) {

        if (!isAITurn()) return;

        int[] move = {-1, -1};
        int bestScore;

        pause(delay);

        if (isX) {
            bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (matrix[i][j] == 0) {
                        matrix[i][j] = 1;
                        if (logger.getLevel() == Level.INFO) printMatrix();
                        int score = minimax(false, depth);
                        logger.info(String.format("Overall Score (X): %d\n", score));
                        matrix[i][j] = 0;
                        if (score > bestScore) {
                            bestScore = score;
                            move[0] = i;
                            move[1] = j;
                        }
                    }
                }
            }
            if (bestScore == Integer.MIN_VALUE) bestScore = 0;
        } else {
            bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (matrix[i][j] == 0) {
                        matrix[i][j] = -1;
                        if (logger.getLevel() == Level.INFO) printMatrix();
                        int score = minimax(true, depth);
                        logger.info(String.format("Overall Score (X): %d\n", score));
                        matrix[i][j] = 0;
                        if (score < bestScore) {
                            bestScore = score;
                            move[0] = i;
                            move[1] = j;
                        }
                    }
                }
            }
            if (bestScore == Integer.MAX_VALUE) bestScore = 0;
        }

        logger.info(String.format("Best score is %d at (%d,%d)\n", bestScore, move[0], move[1]));
        update(move[0], move[1]);
        clickable = true;
        new Thread(() -> nextMove(delay)).start(); // to add delay
    }

    void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            System.out.println(ie.getMessage());
        }
    }

    int minimax(boolean isX, int depth) {
        int result = check();
        int multiplier = 10 - depth;
        if (result != 0) {
            return result * multiplier;
        }

        int bestScore;
        if (isX) {
            bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (matrix[i][j] == 0) {
                        matrix[i][j] = 1;
                        int score = minimax(false, depth + 1);
                        matrix[i][j] = 0;
                        if (score > bestScore)
                            bestScore = score;
                    }
                }
            }
            if (bestScore == Integer.MIN_VALUE) bestScore = 0;
        } else {
            bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (matrix[i][j] == 0) {
                        matrix[i][j] = -1;
                        int score = minimax(true, depth + 1);
                        matrix[i][j] = 0;
                        if (score < bestScore)
                            bestScore = score;
                    }
                }
            }
            if (bestScore == Integer.MAX_VALUE) bestScore = 0;
        }
        return bestScore;
    }

    boolean isAITurn() {
        return !isFilled() &&
                (isX && entity1.getText().equals("Robot") ||
                        !isX && entity2.getText().equals("Robot"));
    }

    void update(int row, int col) {
        if (!isBoardEnabled()) return;
        this.update(row, col, isX);
        isX = !isX;
    }

    void update(int row, int col, boolean isX) {
        if (row + col >= 0) {
            matrix[row][col] = isX ? 1 : -1;
            tiles[row][col].setText(isX ? "X" : "O");
        }
        updateStatus();
    }

    void toggle(boolean enable) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tiles[i][j].toggle(enable);
            }
        }
        revalidate();
        repaint();
    }

    void reset() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                tiles[row][col].setText(" ");
                matrix[row][col] = 0;
            }
        }
        entity1.setEnabled(true);
        entity2.setEnabled(true);
        toggle(false);

        startReset.removeActionListener(startReset.getActionListeners()[0]);
        startReset.setText("Start");
        startReset.addActionListener(e -> {
            startReset.removeActionListener(startReset.getActionListeners()[0]);
            toggle(true);
            status.setText(Board.INGAME_TEXT);
            firstMove();
            startReset.setText("Reset");
            startReset.addActionListener(f -> reset());
        });

        isX = true;
        status.setText(START_TEXT);
        clickable = true;
    }

    void printMatrix() {
        StringBuilder rVal = new StringBuilder();
        for (int row = 0; row < 3; row++) {
            rVal.append("\n");
            for (int col = 0; col < 3; col++) {
                rVal.append(matrix[row][col]).append("\t");
            }
        }
        logger.info(String.format("%s\n-----------", rVal));
    }

    int check() {
        int diag = checkDiag();
        if (diag != 0) return diag;
        int result = 0;

        for (int i = 0; i < 3; i++) {
            int rowRes = checkRow(i);
            int colRes = checkCol(i);
            result = rowRes != 0 ? rowRes : colRes;
            if (result != 0)
                break;
        }

        return result;
    }

    void updateStatus() {
        int result = check();
        if (result != 0) {
            status.setText(String.format("%s wins", result == 1 ? "X" : "O"));
            toggle(false);
        } else if (isFilled()) {
            status.setText("Draw");
            toggle(false);
        }
    }

    private int checkRow(int row) {
        if (matrix[row][0] == matrix[row][1] && matrix[row][1] == matrix[row][2])
            return matrix[row][1];
        else return 0;
    }

    private int checkCol(int col) {
        if (matrix[0][col] == matrix[1][col] && matrix[1][col] == matrix[2][col])
            return matrix[1][col];
        else return 0;
    }

    private int checkDiag() {
        if ((matrix[0][0] == matrix[1][1] && matrix[1][1] == matrix[2][2]) ||
                (matrix[0][2] == matrix[1][1] && matrix[1][1] == matrix[2][0])) {
            return matrix[1][1];
        }
        return 0;
    }

    boolean isFilled() {
        return Stream.of(matrix).flatMapToInt(IntStream::of).filter(i -> i != 0).count() == 9;
    }

    boolean isBoardEnabled() {
        return tiles[0][0].isEnabled();
    }
}
