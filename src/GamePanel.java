import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

public class GamePanel extends JPanel {

    // --- State Permainan ---
    private List<Domino> playerHand;
    private List<Domino> enemyHand;
    private Deque<Domino> board;
    private Stack<Domino> boneyard;
    private final int[] pipCounts = new int[7];

    private boolean isPlayerTurn = true;
    private boolean isGameOver = false;
    private String gameMessage = "Selamat Datang!";
    private boolean playerPassed = false;
    private boolean enemyPassed = false;
    private int currentLeftEnd;
    private int currentRightEnd;

    // --- Variabel Interaksi ---
    private Domino selectedDomino = null;
    private final Map<Domino, Rectangle> playerHandRects = new HashMap<>();
    private Rectangle leftEndRect, rightEndRect;

    // --- Konstanta Visual ---
    private final int H_WIDTH = 80, H_HEIGHT = 40;
    private final int V_WIDTH = 45, V_HEIGHT = 90;
    private final Font MODERN_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private final Font MODERN_FONT_BOLD = new Font("SansSerif", Font.BOLD, 14);
    private final Color BACKGROUND_COLOR = new Color(28, 32, 38);
    private final Color FOREGROUND_COLOR = new Color(200, 200, 200);
    private final Color DOMINO_COLOR = new Color(240, 240, 240);
    private final Color PIP_COLOR = new Color(40, 40, 40);

    public GamePanel() {
        setBackground(BACKGROUND_COLOR);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPanel.setOpaque(false);

        JButton newGameButton = createStyledButton("Game Baru");
        JButton passButton = createStyledButton("Pass");

        buttonPanel.add(newGameButton);
        buttonPanel.add(passButton);

        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.NORTH);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseClick(e.getPoint());
            }
        });

        newGameButton.addActionListener(e -> startNewGame());
        passButton.addActionListener(e -> handlePassTurn());

        startNewGame();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(60, 63, 70));
        button.setForeground(FOREGROUND_COLOR);
        button.setFont(MODERN_FONT_BOLD);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return button;
    }

    private void startNewGame() {
        List<Domino> fullDeck = new ArrayList<>();
        for (int i = 0; i <= 6; i++) {
            for (int j = i; j <= 6; j++) {
                fullDeck.add(new Domino(i, j));
            }
        }
        Collections.shuffle(fullDeck);

        playerHand = new ArrayList<>();
        enemyHand = new ArrayList<>();
        board = new LinkedList<>();
        boneyard = new Stack<>();
        boneyard.addAll(fullDeck);

        for (int i = 0; i < 7; i++) {
            if (!boneyard.isEmpty()) playerHand.add(boneyard.pop());
            if (!boneyard.isEmpty()) enemyHand.add(boneyard.pop());
        }
        
        selectedDomino = null;
        isPlayerTurn = true;
        isGameOver = false;
        playerPassed = false;
        enemyPassed = false;
        updateBoardEnds();
        gameMessage = "Game baru dimulai. Giliran Anda.";
        repaint();
    }

    private void handleMouseClick(Point point) {
        if (!isPlayerTurn || isGameOver) return;
        if (selectedDomino != null) {
            boolean played = false;
            if (rightEndRect != null && rightEndRect.contains(point)) {
                played = tryPlayDomino(selectedDomino, "KANAN");
            } else if (leftEndRect != null && leftEndRect.contains(point)) {
                played = tryPlayDomino(selectedDomino, "KIRI");
            }
            if (!played) gameMessage = "Pilihan dibatalkan.";
            selectedDomino = null;
            repaint();
            return;
        }
        for (Map.Entry<Domino, Rectangle> entry : playerHandRects.entrySet()) {
            if (entry.getValue().contains(point)) {
                selectedDomino = entry.getKey();
                gameMessage = "Anda memilih " + selectedDomino + ". Klik ujung papan untuk meletakkan.";
                repaint();
                return;
            }
        }
    }

    private void handlePassTurn() {
        if (!isPlayerTurn || isGameOver) return;
        gameMessage = "Anda pass. Giliran Musuh.";
        playerPassed = true;
        endTurn();
    }
    
    private boolean tryPlayDomino(Domino domino, String placement) {
        boolean isValid = false;
        if (board.isEmpty()) {
            isValid = true;
        } else {
            int valA = domino.getVal1();
            int valB = domino.getVal2();
            if ("KANAN".equals(placement) && (valA == currentRightEnd || valB == currentRightEnd)) {
                isValid = true;
            } else if ("KIRI".equals(placement) && (valA == currentLeftEnd || valB == currentLeftEnd)) {
                isValid = true;
            }
        }

        if (isValid) {
            playerHand.remove(domino);
            if (board.isEmpty() || "KANAN".equals(placement)) {
                board.addLast(domino);
            } else {
                board.addFirst(domino);
            }
            playerPassed = false;
            updateBoardEnds();
            endTurn();
            return true;
        }
        gameMessage = "Langkah tidak valid!";
        return false;
    }

    private void updateBoardEnds() {
        if (board.isEmpty()) {
            currentLeftEnd = -1;
            currentRightEnd = -1;
            return;
        }

        Domino firstDomino = board.getFirst();
        Domino lastDomino = board.getLast();

        if (board.size() == 1) {
            currentLeftEnd = firstDomino.getVal1();
            currentRightEnd = firstDomino.getVal2();
            return;
        }

        Domino secondDomino = ((LinkedList<Domino>) board).get(1);
        Domino secondLastDomino = ((LinkedList<Domino>) board).get(board.size() - 2);

        if (firstDomino.getVal1() == secondDomino.getVal1() || firstDomino.getVal1() == secondDomino.getVal2()) {
            currentLeftEnd = firstDomino.getVal2();
        } else {
            currentLeftEnd = firstDomino.getVal1();
        }

        if (lastDomino.getVal2() == secondLastDomino.getVal1() || lastDomino.getVal2() == secondLastDomino.getVal2()) {
            currentRightEnd = lastDomino.getVal1();
        } else {
            currentRightEnd = lastDomino.getVal2();
        }
    }

    private void endTurn() {
        isPlayerTurn = false;
        selectedDomino = null;
        repaint();

        if (playerHand.isEmpty()) {
            gameOver("Selamat! Anda Menang!");
            return;
        }
        
        Timer enemyTimer = new Timer(1000, e -> {
            executeEnemyTurn();
            ((Timer)e.getSource()).stop();
        });
        enemyTimer.setRepeats(false);
        enemyTimer.start();
    }
    
    // ===================================================================
    //                  KEMBALI KE AI SEDERHANA & STABIL
    // ===================================================================
    private void executeEnemyTurn() {
        if (isGameOver) return;
        gameMessage = "Giliran Musuh...";
        repaint();
        System.out.println("Musuh mencari langkah...");

        // Cari langkah valid pertama yang bisa dimainkan
        for (Domino d : new ArrayList<>(enemyHand)) {
            if (board.isEmpty() || d.getVal1() == currentRightEnd || d.getVal2() == currentRightEnd) {
                System.out.println("AI memainkan: " + d + " di kanan.");
                enemyHand.remove(d);
                board.addLast(d);
                gameMessage = "Musuh memainkan " + d + ". Giliran Anda.";
                enemyPassed = false;
                updateBoardEnds();
                endEnemyTurn();
                return; // Keluar setelah menemukan langkah
            } else if (d.getVal1() == currentLeftEnd || d.getVal2() == currentLeftEnd) {
                System.out.println("AI memainkan: " + d + " di kiri.");
                enemyHand.remove(d);
                board.addFirst(d);
                gameMessage = "Musuh memainkan " + d + ". Giliran Anda.";
                enemyPassed = false;
                updateBoardEnds();
                endEnemyTurn();
                return; // Keluar setelah menemukan langkah
            }
        }

        // Jika loop selesai dan tidak ada langkah, musuh pass
        enemyPassed = true;
        gameMessage = "Musuh pass. Giliran Anda.";
        System.out.println("Musuh tidak punya langkah dan pass.");
        endEnemyTurn();
    }
    
    private void endEnemyTurn() {
        repaint();
        if (enemyHand.isEmpty()) {
            gameOver("Sayang sekali, Anda Kalah!");
            return;
        }
        if (playerPassed && enemyPassed) {
             int playerScore = calculateHandScore(playerHand);
             int enemyScore = calculateHandScore(enemyHand);
             String finalMessage;
             if (playerScore < enemyScore) {
                 finalMessage = "Game Macet! Anda Menang (skor " + playerScore + " vs " + enemyScore + ")";
             } else if (enemyScore < playerScore) {
                 finalMessage = "Game Macet! Anda Kalah (skor " + playerScore + " vs " + enemyScore + ")";
             } else {
                 finalMessage = "Game Macet! Hasil Seri (skor " + playerScore + ")";
             }
             gameOver(finalMessage);
             return;
        }
        isPlayerTurn = true;
    }

    private int calculateHandScore(List<Domino> hand) {
        int score = 0;
        for (Domino d : hand) {
            score += d.getVal1() + d.getVal2();
        }
        return score;
    }

    private void gameOver(String message) {
        gameMessage = message;
        isGameOver = true;
        System.out.println("Game Selesai: " + message);
        repaint();
    }
    
    private void calculatePipCounts() {
        Arrays.fill(pipCounts, 0);
        for (Domino d : playerHand) { pipCounts[d.getVal1()]++; pipCounts[d.getVal2()]++; }
        for (Domino d : enemyHand) { pipCounts[d.getVal1()]++; pipCounts[d.getVal2()]++; }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        paintEnemyHand(g2d);
        paintBoard(g2d);
        paintPlayerHand(g2d);
        paintPipCountDisplay(g2d);
        paintGameMessage(g2d);
    }
    
    private void paintEnemyHand(Graphics2D g2d) {
        g2d.setFont(MODERN_FONT_BOLD);
        g2d.setColor(FOREGROUND_COLOR);
        g2d.drawString("Musuh ("+ enemyHand.size() +" kartu)", 20, 60);
        int enemyX = 20;
        for (int i = 0; i < enemyHand.size(); i++) {
            drawCardBack(g2d, enemyX, 75);
            enemyX += V_WIDTH + 5;
        }
    }

    private void paintBoard(Graphics2D g2d) {
        g2d.setFont(MODERN_FONT_BOLD);
        g2d.setColor(FOREGROUND_COLOR);
        g2d.drawString("Papan Permainan", 20, 180);
        
        int yPos = 225;

        if (!board.isEmpty()) {
            int totalWidth = 0;
            for (Domino d : board) totalWidth += (d.getVal1() == d.getVal2()) ? V_WIDTH : H_WIDTH;
            
            int currentX = getWidth() / 2 - totalWidth / 2;
            int prevEnd = -1;
            
            if(board.size() > 1) {
                Domino first = board.getFirst();
                Domino second = ((LinkedList<Domino>)board).get(1);
                if(first.getVal1() == second.getVal1() || first.getVal1() == second.getVal2()){
                    prevEnd = first.getVal1();
                } else {
                    prevEnd = first.getVal2();
                }
            } else if (board.size() == 1) {
                prevEnd = board.getFirst().getVal1();
            }

            for(Domino d : board) {
                int valA = d.getVal1();
                int valB = d.getVal2();
                if(d != board.getFirst() && valB == prevEnd) {
                    int temp = valA; valA = valB; valB = temp;
                }

                boolean isDouble = valA == valB;
                boolean isHorizontal = !isDouble;
                int currentWidth = isHorizontal ? H_WIDTH : V_WIDTH;
                int currentHeight = isHorizontal ? H_HEIGHT : V_HEIGHT;
                int currentY = yPos - currentHeight / 2;
                drawDomino(g2d, valA, valB, currentX, currentY, isHorizontal);
                
                if (d == board.getFirst()) leftEndRect = new Rectangle(currentX, currentY, currentWidth / 2, currentHeight);
                if (d == board.getLast()) rightEndRect = new Rectangle(currentX + currentWidth / 2, currentY, currentWidth / 2, currentHeight);
                currentX += currentWidth + 2;
                prevEnd = valB;
            }
        } else {
             leftEndRect = new Rectangle(getWidth()/2 - H_WIDTH, yPos - H_HEIGHT/2, H_WIDTH * 2, H_HEIGHT);
             rightEndRect = leftEndRect;

             if (selectedDomino != null) {
                 g2d.setColor(new Color(255, 255, 255, 70));
                 Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
                 g2d.setStroke(dashed);
                 g2d.drawRect(getWidth()/2 - H_WIDTH/2, yPos - H_HEIGHT/2, H_WIDTH, H_HEIGHT);
                 g2d.setFont(new Font("SansSerif", Font.ITALIC, 12));
                 g2d.setColor(FOREGROUND_COLOR);
                 g2d.drawString("Letakkan di sini", getWidth()/2 - 35, yPos + 5);
             }
        }
    }
    
    private void paintPlayerHand(Graphics2D g2d) {
        int handY = getHeight() - V_HEIGHT - 40; 
        int titleY = handY - 15;

        g2d.setFont(MODERN_FONT_BOLD);
        g2d.setColor(FOREGROUND_COLOR);
        g2d.setStroke(new BasicStroke());
        g2d.drawString("Tangan Anda ("+ playerHand.size() +" kartu)", 20, titleY);
        playerHandRects.clear();
        int handX = 20;
        for (Domino d : playerHand) {
            Rectangle rect = new Rectangle(handX, handY, V_WIDTH, V_HEIGHT);
            playerHandRects.put(d, rect);
            drawDomino(g2d, d.getVal1(), d.getVal2(), handX, handY, false);
            if (d.equals(selectedDomino)) {
                g2d.setColor(new Color(255, 255, 102, 120));
                g2d.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 10,10);
            }
            handX += V_WIDTH + 5;
        }
    }

    private void paintPipCountDisplay(Graphics2D g2d) {
        calculatePipCounts();
        int pipCountX = getWidth() - 120;
        int pipCountY = 60;
        
        g2d.setFont(MODERN_FONT_BOLD);
        g2d.setColor(FOREGROUND_COLOR);
        g2d.drawString("Sisa Angka", pipCountX, pipCountY);
        pipCountY += 15;
        
        g2d.setFont(MODERN_FONT);
        for (int i = 0; i <= 6; i++) {
            g2d.setColor(DOMINO_COLOR);
            g2d.fillRoundRect(pipCountX, pipCountY, 20, 20, 5, 5);
            drawMiniPips(g2d, i, pipCountX, pipCountY, 20, 20);
            
            g2d.setColor(FOREGROUND_COLOR);
            g2d.drawString(": " + pipCounts[i], pipCountX + 25, pipCountY + 15);
            pipCountY += 25;
        }
    }
    
    private void paintGameMessage(Graphics2D g2d) {
        g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2d.setColor(FOREGROUND_COLOR);
        FontMetrics fm = g2d.getFontMetrics();
        int msgWidth = fm.stringWidth(gameMessage);
        g2d.drawString(gameMessage, getWidth() / 2 - msgWidth / 2, getHeight() - 20);
    }

    private void drawCardBack(Graphics2D g2d, int x, int y) {
        g2d.setColor(new Color(70, 73, 80));
        g2d.fillRoundRect(x, y, V_WIDTH, V_HEIGHT, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(x, y, V_WIDTH, V_HEIGHT, 10, 10);
    }
    
    private void drawDomino(Graphics2D g2d, int val1, int val2, int x, int y, boolean horizontal) {
        int cardWidth = horizontal ? H_WIDTH : V_WIDTH;
        int cardHeight = horizontal ? H_HEIGHT : V_HEIGHT;
        g2d.setColor(DOMINO_COLOR);
        g2d.fillRoundRect(x, y, cardWidth, cardHeight, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(x, y, cardWidth, cardHeight, 10, 10);
        if (horizontal) {
            g2d.drawLine(x + cardWidth / 2, y, x + cardWidth / 2, y + cardHeight);
            drawPips(g2d, val1, x, y, cardWidth / 2, cardHeight);
            drawPips(g2d, val2, x + cardWidth / 2, y, cardWidth / 2, cardHeight);
        } else {
            g2d.drawLine(x, y + cardHeight / 2, x + cardWidth, y + cardHeight / 2);
            drawPips(g2d, val1, x, y, cardWidth, cardHeight / 2);
            drawPips(g2d, val2, x, y + cardHeight / 2, cardWidth, cardHeight / 2);
        }
    }

    private void drawPips(Graphics2D g2d, int value, int x, int y, int width, int height) {
        g2d.setColor(PIP_COLOR);
        int pipSize = 8;
        int padding = 4;
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int left = x + padding;
        int right = x + width - padding - pipSize;
        int top = y + padding;
        int bottom = y + height - padding - pipSize;
        if (value == 1 || value == 3 || value == 5) g2d.fillOval(centerX - pipSize / 2, centerY - pipSize / 2, pipSize, pipSize);
        if (value >= 2) { g2d.fillOval(left, top, pipSize, pipSize); g2d.fillOval(right, bottom, pipSize, pipSize); }
        if (value >= 4) { g2d.fillOval(right, top, pipSize, pipSize); g2d.fillOval(left, bottom, pipSize, pipSize); }
        if (value == 6) { g2d.fillOval(left, centerY - pipSize / 2, pipSize, pipSize); g2d.fillOval(right, centerY - pipSize / 2, pipSize, pipSize); }
    }
    
    private void drawMiniPips(Graphics2D g2d, int value, int x, int y, int width, int height) {
        g2d.setColor(PIP_COLOR);
        int pipSize = 4;
        int padding = 3;
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int left = x + padding;
        int right = x + width - padding - pipSize;
        int top = y + padding;
        int bottom = y + height - padding - pipSize;
        if (value == 1 || value == 3 || value == 5) g2d.fillOval(centerX - pipSize / 2, centerY - pipSize / 2, pipSize, pipSize);
        if (value >= 2) { g2d.fillOval(left, top, pipSize, pipSize); g2d.fillOval(right, bottom, pipSize, pipSize); }
        if (value >= 4) { g2d.fillOval(right, top, pipSize, pipSize); g2d.fillOval(left, bottom, pipSize, pipSize); }
        if (value == 6) { g2d.fillOval(left, centerY - pipSize / 2, pipSize, pipSize); g2d.fillOval(right, centerY - pipSize / 2, pipSize, pipSize); }
    }
}