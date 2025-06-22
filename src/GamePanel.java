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
    private final int H_WIDTH = 100, H_HEIGHT = 50;
    private final int V_WIDTH = 60, V_HEIGHT = 120;

    public GamePanel() {
        JButton newGameButton = new JButton("Game Baru");
        JButton passButton = new JButton("Pass");
        
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
        this.add(newGameButton);
        this.add(passButton);

        newGameButton.addActionListener(e -> startNewGame());
        passButton.addActionListener(e -> handlePassTurn());

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseClick(e.getPoint());
            }
        });

        startNewGame();
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
        
        Timer enemyTimer = new Timer(1500, e -> {
            executeEnemyTurn();
            ((Timer)e.getSource()).stop();
        });
        enemyTimer.setRepeats(false);
        enemyTimer.start();
    }
    
    private void executeEnemyTurn() {
        if (isGameOver) return;
        System.out.println("Musuh sedang berpikir...");
        
        for (Domino d : new ArrayList<>(enemyHand)) {
            if (board.isEmpty() || d.getVal1() == currentRightEnd || d.getVal2() == currentRightEnd) {
                enemyHand.remove(d);
                board.addLast(d);
                gameMessage = "Musuh memainkan " + d + ". Giliran Anda.";
                enemyPassed = false;
                updateBoardEnds();
                endEnemyTurn();
                return;
            } else if (d.getVal1() == currentLeftEnd || d.getVal2() == currentLeftEnd) {
                enemyHand.remove(d);
                board.addFirst(d);
                gameMessage = "Musuh memainkan " + d + ". Giliran Anda.";
                enemyPassed = false;
                updateBoardEnds();
                endEnemyTurn();
                return;
            }
        }
        
        enemyPassed = true;
        gameMessage = "Musuh pass. Giliran Anda.";
        System.out.println("Musuh tidak punya kartu untuk dimainkan.");
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
                 finalMessage = "Game Macet! Anda Menang dengan skor " + playerScore + " vs " + enemyScore;
             } else if (enemyScore < playerScore) {
                 finalMessage = "Game Macet! Anda Kalah dengan skor " + playerScore + " vs " + enemyScore;
             } else {
                 finalMessage = "Game Macet! Hasil Seri dengan skor " + playerScore;
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
        for (Domino d : playerHand) {
            pipCounts[d.getVal1()]++;
            pipCounts[d.getVal2()]++;
        }
        for (Domino d : enemyHand) {
            pipCounts[d.getVal1()]++;
            pipCounts[d.getVal2()]++;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(13, 71, 161));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 16));

        // --- Gambar Komponen-komponen Game ---
        paintEnemyHand(g2d);
        paintBoard(g2d);
        paintPlayerHand(g2d);
        paintPipCountDisplay(g2d);
        paintGameMessage(g2d);
    }
    
    private void paintEnemyHand(Graphics2D g2d) {
        g2d.drawString("Tangan Musuh ("+ enemyHand.size() +" kartu)", 50, 40);
        int enemyX = 50;
        for (int i = 0; i < enemyHand.size(); i++) {
            drawCardBack(g2d, enemyX, 60);
            enemyX += 70;
        }
    }

    private void paintBoard(Graphics2D g2d) {
        g2d.drawString("Papan Permainan", 50, 180);
        if (!board.isEmpty()) {
            int totalWidth = 0;
            for (Domino d : board) totalWidth += (d.getVal1() == d.getVal2()) ? V_WIDTH : H_WIDTH;
            int currentX = getWidth() / 2 - totalWidth / 2;
            int prevEnd = -1;

            // Logika untuk menyimpan orientasi yang benar dari rantai
            Deque<Integer> leftChain = new LinkedList<>();
            Deque<Integer> rightChain = new LinkedList<>();
            Domino first = board.getFirst();
            leftChain.add(first.getVal1());
            rightChain.add(first.getVal2());
            if(board.size() > 1) {
                 Domino second = ((LinkedList<Domino>)board).get(1);
                 if (second.getVal1() != first.getVal2() && second.getVal2() != first.getVal2()) {
                      leftChain.clear();
                      rightChain.clear();
                      leftChain.add(first.getVal2());
                      rightChain.add(first.getVal1());
                 }
            }


            for(Domino d : board) {
                int valA = d.getVal1();
                int valB = d.getVal2();
                
                // Tentukan orientasi berdasarkan ujung rantai
                if(d != board.getFirst()) {
                     if (valA == rightChain.getLast() || valB == rightChain.getLast()) { // Menempel di kanan
                          if(valB == rightChain.getLast()) {int temp=valA; valA=valB; valB=temp;}
                          rightChain.removeLast();
                          rightChain.add(valA);
                          rightChain.add(valB);
                     } else { // Menempel di kiri
                          if(valA == leftChain.getFirst()) {int temp=valA; valA=valB; valB=temp;}
                          leftChain.removeFirst();
                          leftChain.addFirst(valA);
                          leftChain.addFirst(valB);
                     }
                }
                
                boolean isDouble = valA == valB;
                boolean isHorizontal = !isDouble;
                int currentWidth = isHorizontal ? H_WIDTH : V_WIDTH;
                int currentHeight = isHorizontal ? H_HEIGHT : V_HEIGHT;
                int yPos = 225 - currentHeight / 2;
                drawDomino(g2d, valA, valB, currentX, yPos, isHorizontal);
                
                if (d == board.getFirst()) leftEndRect = new Rectangle(currentX, yPos, currentWidth / 2, currentHeight);
                if (d == board.getLast()) rightEndRect = new Rectangle(currentX + currentWidth / 2, yPos, currentWidth / 2, currentHeight);
                currentX += currentWidth;
            }
        } else {
             leftEndRect = new Rectangle(getWidth()/2 - H_WIDTH, 200, H_WIDTH * 2, H_HEIGHT);
             rightEndRect = leftEndRect;
             if (selectedDomino != null) {
                 g2d.setColor(new Color(255, 255, 255, 70));
                 Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
                 g2d.setStroke(dashed);
                 g2d.drawRect(getWidth()/2 - H_WIDTH/2, 200, H_WIDTH, H_HEIGHT);
                 g2d.setFont(new Font("SansSerif", Font.ITALIC, 12));
                 g2d.drawString("Letakkan di sini", getWidth()/2 - 35, 230);
             }
        }
    }
    
    private void paintPlayerHand(Graphics2D g2d) {
        g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2d.setStroke(new BasicStroke());
        g2d.drawString("Tangan Anda ("+ playerHand.size() +" kartu)", 50, 520);
        playerHandRects.clear();
        int handX = 50;
        for (Domino d : playerHand) {
            Rectangle rect = new Rectangle(handX, 540, V_WIDTH, V_HEIGHT);
            playerHandRects.put(d, rect);
            drawDomino(g2d, d.getVal1(), d.getVal2(), handX, 540, false);
            if (d.equals(selectedDomino)) {
                g2d.setColor(new Color(255, 255, 0, 100));
                g2d.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 10,10);
            }
            handX += 70;
        }
    }

    private void paintPipCountDisplay(Graphics2D g2d) {
        calculatePipCounts();
        g2d.setStroke(new BasicStroke());
        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Jumlah Angka Tersisa (di Tangan):", 50, 680);
        
        int countX = 50;
        int countY = 695;
        for (int i = 0; i <= 6; i++) {
            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(countX, countY, 30, 30, 5, 5);
            drawMiniPips(g2d, i, countX, countY, 30, 30);
            
            g2d.setFont(new Font("SansSerif", Font.BOLD, 20));
            g2d.setColor(Color.WHITE);
            g2d.drawString(": " + pipCounts[i], countX + 32, countY + 23);
            countX += 90;
        }
    }
    
    private void paintGameMessage(Graphics2D g2d) {
        g2d.setFont(new Font("SansSerif", Font.BOLD, 20));
        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics();
        int msgWidth = fm.stringWidth(gameMessage);
        g2d.drawString(gameMessage, getWidth() / 2 - msgWidth / 2, 450);
    }

    private void drawCardBack(Graphics2D g2d, int x, int y) {
        g2d.setColor(new Color(200, 200, 220));
        g2d.fillRoundRect(x, y, V_WIDTH, 100, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(x, y, V_WIDTH, 100, 10, 10);
    }
    
    private void drawDomino(Graphics2D g2d, int val1, int val2, int x, int y, boolean horizontal) {
        int cardWidth = horizontal ? H_WIDTH : V_WIDTH;
        int cardHeight = horizontal ? H_HEIGHT : V_HEIGHT;
        g2d.setColor(Color.WHITE);
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

    private void drawMiniPips(Graphics2D g2d, int value, int x, int y, int width, int height) {
        g2d.setColor(new Color(229, 57, 53));
        int pipSize = 5;
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
        else if (value == 0) {}
    }
    private void drawPips(Graphics2D g2d, int value, int x, int y, int width, int height) {
        g2d.setColor(new Color(229, 57, 53));
        int pipSize = 10;
        int padding = 5;
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