package tictactoe;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Board extends JPanel {
    final static String START_TEXT = "Game is not started";
    final static String INGAME_TEXT = "Game in progress";
    JLabel status;
    JButton entity1, entity2, startReset;
    Tile[][] tiles = new Tile[3][3];
    int[][] matrix = new int[3][3];
    private static boolean isX = true;
    private boolean isActive = false;

    Logger logger = Logger.getLogger("Board");

    Board(JLabel status, JButton entity1, JButton entity2, JButton startReset) {
        this.status = status;
        status.setText(START_TEXT);

        logger.setLevel(Level.OFF);

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
                    if (!tile.isEnabled() || !tile.getText().isBlank()) return;
                    if (isX) {
                        tile.setText("X");
                        matrix[tile.row][tile.col] = 1;
                    } else {
                        tile.setText("O");
                        matrix[tile.row][tile.col] = -1;
                    }
                    logger.info(tile.getName() + " pressed!");
                    isX = !isX;
                    updateStatus();
                    nextMove();
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
        isX = !isX;
        nextMove();
    }

    void nextMove() {

        if (!isAITurn()) return;

        int[] move = {-1, -1};
        int bestScore;

        if (isX) {
            bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (matrix[i][j] == 0) {
                        matrix[i][j] = 1;
                        int score = minimax2(false, 1);
                        matrix[i][j] = 0;
                        if (score > bestScore) {
                            bestScore = score;
                            move[0] = i;
                            move[1] = j;
                        }
                    }
                }
            }
        } else {
            bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (matrix[i][j] == 0) {
                        matrix[i][j] = -1;
                        int score = minimax2(true, 1);
                        matrix[i][j] = 0;
                        if (score < bestScore) {
                            bestScore = score;
                            move[0] = i;
                            move[1] = j;
                        }
                    }
                }
            }
        }

        update(move[0], move[1]);
//        pause(100);
        isX = !isX;
        nextMove();
    }

    void pause(int millis) {
        Timer timer = new Timer(millis, e -> {
            System.out.println("Hello");
        });
        timer.setRepeats(false);
        timer.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            System.out.println(ie.getMessage());
        }
    }

    int minimax2(boolean isX, int depth) {
        int multiplier = 10 - depth;
        int result = check();
        if (result != 0) return result * multiplier;

        int bestScore;

        if (isX) {
            bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (matrix[i][j] == 0) {
                        matrix[i][j] = 1;
                        int score = minimax2(false, depth + 1);
                        matrix[i][j] = 0;
                        if (score > bestScore) {
                            bestScore = score;
                        }
                    }
                }
            }
        } else {
            bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (matrix[i][j] == 0) {
                        matrix[i][j] = -1;
                        int score = minimax2(true, depth + 1);
                        matrix[i][j] = 0;
                        if (score < bestScore) {
                            bestScore = score;
                        }
                    }
                }
            }
        }

        return bestScore * multiplier;
    }

    boolean isAITurn() {
        return !isFilled() &&
                (isX && entity1.getText().equals("Robot") ||
                !isX && entity2.getText().equals("Robot"));
    }

    void update(int row, int col) {
        this.update(row, col, isX);
    }

    void update(int row, int col, boolean isX) {
        if (row + col >= 0) {
            matrix[row][col] = isX ? 1 : -1;
            tiles[row][col].setText(isX ? "X" : "O");
        }
//        isX = !isX;
        updateStatus();
    }

    void isEmpty(int row, int col) {
        if (row + col >= 0)
            matrix[row][col] = 0;
        isX = !isX;
    }

    void toggle(boolean enable) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tiles[i][j].toggle(enable);
            }
        }
        revalidate();
        repaint();
        isActive = enable;
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
        status.setText(START_TEXT); // TODO: may fail test case
//        firstMove();
    }

    void printMatrix() {
        StringBuilder rVal = new StringBuilder();
        for (int row = 0; row < 3; row++) {
            rVal.setLength(0);
            for (int col = 0; col < 3; col++) {
                rVal.append(matrix[row][col]).append("\t");
            }
            System.out.println(rVal.toString());
        }
        System.out.println("---------");
    }

    int check() {
        int diag = checkDiag();
        if (diag != 0) return checkDiag();

        for (int i = 0; i < 3; i++) {
            int rowRes = checkRow(i);
            int colRes = checkCol(i);
            int result = rowRes != 0 ? rowRes : colRes;
            if (result != 0)
                return result;
        }

        return 0;
    }

    void updateStatus() {
        int result = check();
        if (result != 0) {
            status.setText(String.format("%s wins", result == 1 ? "X" : "O"));
            toggle(false);
        }
        else if (isFilled()) {
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
        if (matrix[0][0] == matrix[1][1] && matrix[1][1] == matrix[2][2] ||
            matrix[0][2] == matrix[1][1] && matrix[1][1] == matrix[2][0]) {
            return matrix[1][1];
        }
        return 0;
    }

    boolean isFilled() {
        return Stream.of(matrix).flatMapToInt(IntStream::of).filter(i -> i != 0).count() == 9;
    }

    boolean isEmpty() {
        return Stream.of(matrix).flatMapToInt(IntStream::of).filter(i -> i == 0).count() == 9;
    }
}
