package tictactoe;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TicTacToe extends JFrame {
    public TicTacToe() {
        super("Tic Tac Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(0, 5, 5, 5));
        JLabel status = new JLabel();
        status.setName("LabelStatus");
        status.setFont(new Font("Arial", Font.BOLD, 12));
        JButton player1 = new PlayerButton("Human");
        player1.setName("ButtonPlayer1");
        JButton player2 = new PlayerButton("Human");
        player2.setName("ButtonPlayer2");
        JButton startReset = new JButton("Start");
        startReset.setName("ButtonStartReset");
        startReset.setBackground(Color.BLACK);
        startReset.setForeground(Color.WHITE);
        Board board = new Board(status, player1, player2, startReset);
        bottomPanel.add(status, BorderLayout.WEST);

        setupMenu(board, player1, player2);

        JPanel topPanel = new JPanel(new GridBagLayout());

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

        setVisible(true);
    }

    void setupMenu(Board board, JButton player1, JButton player2) {
        JMenuBar menuBar = new JMenuBar();
        JMenu game_menu = new JMenu("Game");
        game_menu.setName("MenuGame");

        JMenuItem human_v_human = new JMenuItem("Human vs Human");
        JMenuItem human_v_robot = new JMenuItem("Human vs Robot");
        JMenuItem robot_v_human = new JMenuItem("Robot vs Human");
        JMenuItem robot_v_robot = new JMenuItem("Robot vs Robot");
        JMenuItem exit_button = new JMenuItem("Exit");

        human_v_human.addActionListener(l -> {
            board.reset();
            player1.setText("Human");
            player2.setText("Human");
            board.startReset.doClick();
        });

        human_v_robot.addActionListener(l -> {
            board.reset();
            player1.setText("Human");
            player2.setText("Robot");
            board.startReset.doClick();
        });

        robot_v_human.addActionListener(l -> {
            board.reset();
            player1.setText("Robot");
            player2.setText("Human");
            board.startReset.doClick();
        });

        robot_v_robot.addActionListener(l -> {
            board.reset();
            player1.setText("Robot");
            player2.setText("Robot");
            board.startReset.doClick();
        });

        exit_button.addActionListener(l -> System.exit(0));

        human_v_human.setName("MenuHumanHuman");
        human_v_robot.setName("MenuHumanRobot");
        robot_v_human.setName("MenuRobotHuman");
        robot_v_robot.setName("MenuRobotRobot");
        exit_button.setName("MenuExit");

        game_menu.add(human_v_human);
        game_menu.add(human_v_robot);
        game_menu.add(robot_v_human);
        game_menu.add(robot_v_robot);
        game_menu.addSeparator();
        game_menu.add(exit_button);

        menuBar.add(game_menu);
        setJMenuBar(menuBar);
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
