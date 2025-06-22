import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class DominoSolverGUI extends JFrame {

    public DominoSolverGUI() {
        setTitle("Domino Backtracking Solver");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Perubahan di bagian ini ---
        GamePanel gamePanel = new GamePanel();
        add(gamePanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DominoSolverGUI gameWindow = new DominoSolverGUI();
                gameWindow.setVisible(true);
            }
        });
    }
}