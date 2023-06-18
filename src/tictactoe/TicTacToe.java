package tictactoe;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class TicTacToe extends JFrame {
    public TicTacToe() {
        super("Tic Tac Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 350);
        setLocationRelativeTo(null);
        setVisible(true);
        setLayout(new BorderLayout());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(0, 5, 5, 5));
        JLabel status = new JLabel();
        status.setName("LabelStatus");
        status.setFont(new Font("Arial", Font.BOLD, 12));
        JButton player1 = new PlayerButton("Human");
        player1.setName("ButtonPlayer1");
        JButton player2 = new PlayerButton("Robot");
        player2.setName("ButtonPlayer2");
        JButton startReset = new JButton("Start");
        startReset.setName("ButtonStartReset");
        startReset.setBackground(Color.BLACK);
        startReset.setForeground(Color.WHITE);
        Board board = new Board(status, player1, player2, startReset);
        bottomPanel.add(status, BorderLayout.WEST);

        JPanel topPanel = new JPanel(new GridBagLayout());
//        topPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1.0;
        constraints.gridy = 0;

        constraints.gridx = 0;
        topPanel.add(player1, constraints);

        constraints.gridx = 1;
        topPanel.add(startReset, constraints);

        constraints.gridx = 2;
        topPanel.add(player2, constraints);
        topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        add(topPanel, BorderLayout.NORTH);
        add(board, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}

class PlayerButton extends JButton {
    PlayerButton(String label) {
        super(label);

        addActionListener(e -> {
            String newLabel = getText().equals("Human") ? "Robot" : "Human";
            setText(newLabel);
        });
    }
}