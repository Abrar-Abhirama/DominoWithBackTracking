import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class GamePanel extends JPanel {

    private final List<Domino> initialHand;
    private Timer animationTimer;
    private int animationStep = 0;
    private List<Move> solutionMoves;

    private final Map<Move, PlacementInfo> layoutData;

    private static class PlacementInfo {
        Rectangle rect;
        boolean isHorizontal;
        int valA, valB;

        PlacementInfo(Rectangle r, boolean h, int vA, int vB) {
            this.rect = r;
            this.isHorizontal = h;
            this.valA = vA;
            this.valB = vB;
        }
    }

    public GamePanel() {
        // Menggunakan test case dari Anda
        initialHand = new ArrayList<>();
        initialHand.add(new Domino(6, 2));
        initialHand.add(new Domino(2, 1));
        initialHand.add(new Domino(6, 1));
        initialHand.add(new Domino(6, 3));
        initialHand.add(new Domino(6, 5));
        initialHand.add(new Domino(3, 1));
        initialHand.add(new Domino(5, 5));
        
        layoutData = new HashMap<>();

        JButton solveButton = new JButton("CARI SOLUSI & ANIMASIKAN!");
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
        this.add(solveButton);
        solveButton.addActionListener(e -> findAndAnimateSolution());
    }

    private void findAndAnimateSolution() {
        if (animationTimer != null && animationTimer.isRunning()) animationTimer.stop();

        System.out.println("Mencari solusi...");
        Domino startDomino = findAndRemoveHighestDouble(new ArrayList<>(initialHand));
        if (startDomino == null) startDomino = initialHand.get(0);

        List<Domino> handForSolving = new ArrayList<>(initialHand);
        handForSolving.remove(startDomino);
        Deque<Domino> boardForSolving = new LinkedList<>();
        boardForSolving.add(startDomino);
        
        solutionMoves = solve(handForSolving, boardForSolving, startDomino.getVal1(), startDomino.getVal2());
        
        if (solutionMoves != null) {
            System.out.println("SOLUSI DITEMUKAN! Menghitung layout...");
            solutionMoves.add(0, new Move(startDomino, "START"));
            
            calculateLayout(solutionMoves);

            animationStep = 0;
            animationTimer = new Timer(800, timerAction);
            animationTimer.start();
        } else {
            System.out.println("Tidak ada solusi yang ditemukan.");
            JOptionPane.showMessageDialog(this, "Tidak ada solusi yang ditemukan untuk set kartu ini.", "Gagal", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private final ActionListener timerAction = e -> {
        if (animationStep < solutionMoves.size()) {
            animationStep++;
            repaint();
        } else {
            animationTimer.stop();
            System.out.println("Animasi selesai.");
            JOptionPane.showMessageDialog(this, "Animasi Selesai!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        }
    };
    
    // ===================================================================
    //            METODE calculateLayout DENGAN PERBAIKAN FINAL
    // ===================================================================
    private void calculateLayout(List<Move> moves) {
        layoutData.clear();
        if (moves.isEmpty()) return;

        final int MARGIN = 60;
        final int H_WIDTH = 100, H_HEIGHT = 50;
        final int V_WIDTH = 60, V_HEIGHT = 120;

        // Inisialisasi kartu pertama
        Move firstMove = moves.get(0);
        Domino firstDomino = firstMove.getDomino();
        boolean firstIsHorizontal = !(firstDomino.getVal1() == firstDomino.getVal2());
        int firstWidth = firstIsHorizontal ? H_WIDTH : V_WIDTH;
        int firstHeight = firstIsHorizontal ? H_HEIGHT : V_HEIGHT;
        int firstX = getWidth() / 2 - firstWidth / 2;
        int firstY = 250 - firstHeight / 2;
        
        Rectangle leftEndRect = new Rectangle(firstX, firstY, firstWidth, firstHeight);
        Rectangle rightEndRect = new Rectangle(firstX, firstY, firstWidth, firstHeight);
        
        layoutData.put(firstMove, new PlacementInfo(new Rectangle(leftEndRect), firstIsHorizontal, firstDomino.getVal1(), firstDomino.getVal2()));
        
        int leftEndVal = firstDomino.getVal1();
        int rightEndVal = firstDomino.getVal2();
        
        String leftDir = "LEFT";
        String rightDir = "RIGHT";

        for (int i = 1; i < moves.size(); i++) {
            Move move = moves.get(i);
            Domino domino = move.getDomino();
            int valA = domino.getVal1(), valB = domino.getVal2();
            
            Rectangle newRect;
            boolean isTurner;
            boolean isHorizontal;

            if ("KANAN".equals(move.getPlacement())) {
                isTurner = (rightDir.equals("RIGHT") && rightEndRect.x + rightEndRect.width + H_WIDTH > getWidth() - MARGIN);
                isHorizontal = !isTurner;
                
                int currentWidth = isHorizontal ? H_WIDTH : V_WIDTH;
                int currentHeight = isHorizontal ? H_HEIGHT : V_HEIGHT;
                
                // --- LOGIKA ORIENTASI YANG DIPERBAIKI ---
                if (rightDir.equals("RIGHT")) { // Jika bergerak ke kanan, sisi kiri (valA) harus cocok
                    if (valB == rightEndVal) { int temp = valA; valA = valB; valB = temp; }
                } else { // Jika bergerak ke kiri (setelah wrap), sisi kanan (valB) harus cocok
                    if (valA == rightEndVal) { int temp = valA; valA = valB; valB = temp; }
                }
                rightEndVal = valB; // Ujung baru selalu sisi luar
                // -----------------------------------------

                int newX, newY;
                if (isTurner) {
                    newX = rightEndRect.x + rightEndRect.width / 2 - currentWidth / 2;
                    newY = rightEndRect.y + rightEndRect.height;
                    rightDir = "LEFT";
                } else {
                    newX = (rightDir.equals("RIGHT")) ? rightEndRect.x + rightEndRect.width : rightEndRect.x - currentWidth;
                    newY = rightEndRect.y + rightEndRect.height / 2 - currentHeight / 2;
                }
                newRect = new Rectangle(newX, newY, currentWidth, currentHeight);
                rightEndRect = newRect;

            } else { // KIRI
                isTurner = (leftDir.equals("LEFT") && leftEndRect.x - H_WIDTH < MARGIN);
                isHorizontal = !isTurner;
                
                int currentWidth = isHorizontal ? H_WIDTH : V_WIDTH;
                int currentHeight = isHorizontal ? H_HEIGHT : V_HEIGHT;
                
                // --- LOGIKA ORIENTASI YANG DIPERBAIKI ---
                if (leftDir.equals("LEFT")) { // Jika bergerak ke kiri, sisi kanan (valB) harus cocok
                    if (valA == leftEndVal) { int temp = valA; valA = valB; valB = temp; }
                } else { // Jika bergerak ke kanan (setelah wrap), sisi kiri (valA) harus cocok
                    if (valB == leftEndVal) { int temp = valA; valA = valB; valB = temp; }
                }
                leftEndVal = valA; // Ujung baru selalu sisi luar
                // -----------------------------------------

                int newX, newY;
                if(isTurner) {
                    newX = leftEndRect.x + leftEndRect.width / 2 - currentWidth / 2;
                    newY = leftEndRect.y - currentHeight;
                    leftDir = "RIGHT";
                } else {
                    newX = (leftDir.equals("LEFT")) ? leftEndRect.x - currentWidth : leftEndRect.x + leftEndRect.width;
                    newY = leftEndRect.y + leftEndRect.height / 2 - currentHeight / 2;
                }
                newRect = new Rectangle(newX, newY, currentWidth, currentHeight);
                leftEndRect = newRect;
            }
            layoutData.put(move, new PlacementInfo(newRect, isHorizontal, valA, valB));
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
        
        // ===================================================================
        //           LOGIKA BARU & LEBIH AMAN UNTUK MENGGAMBAR TANGAN
        // ===================================================================
        List<Domino> playedDominoes = new ArrayList<>();
        if (solutionMoves != null) {
            // Kumpulkan semua kartu yang sudah dimainkan sesuai langkah animasi
            for (int i = 0; i < animationStep; i++) {
                playedDominoes.add(solutionMoves.get(i).getDomino());
            }
        }
        g2d.drawString("Tangan Anda", 50, 520);
        int handX = 50;
        // Iterasi melalui tangan awal yang lengkap
        for (Domino domino : initialHand) {
            // Hanya gambar jika kartu ini BELUM dimainkan
            if (!playedDominoes.contains(domino)) {
                drawDomino(g2d, domino.getVal1(), domino.getVal2(), handX, 540, false);
                handX += 70;
            }
        }
        // ===================================================================

        // Gambar Papan Permainan (Logika ini sudah benar)
        g2d.drawString("Papan Permainan", 50, 40);
        if (solutionMoves != null && animationStep > 0) {
            for (int i = 0; i < animationStep; i++) {
                Move move = solutionMoves.get(i);
                PlacementInfo pInfo = layoutData.get(move);
                if (pInfo != null) {
                    drawDomino(g2d, pInfo.valA, pInfo.valB, pInfo.rect.x, pInfo.rect.y, pInfo.isHorizontal);
                }
            }
        }
    }
    
    private Domino findAndRemoveHighestDouble(List<Domino> hand) {
        Domino highestDouble = null;
        for (Domino d : hand) {
            if (d.getVal1() == d.getVal2()) {
                if (highestDouble == null || d.getVal1() > highestDouble.getVal1()) {
                    highestDouble = d;
                }
            }
        }
        return highestDouble;
    }
    
    private List<Move> solve(List<Domino> hand, Deque<Domino> board, int leftEnd, int rightEnd) {
        if (hand.isEmpty()) return new ArrayList<>();
        for (int i = 0; i < hand.size(); i++) {
            Domino domino = hand.get(i);
            List<Domino> nextHand = new ArrayList<>(hand);
            nextHand.remove(i);
            if (domino.getVal1() == rightEnd) {
                board.addLast(domino);
                List<Move> solution = solve(nextHand, board, leftEnd, domino.getVal2());
                board.removeLast();
                if (solution != null) { solution.add(0, new Move(domino, "KANAN")); return solution; }
            }
            if (domino.getVal1() != domino.getVal2() && domino.getVal2() == rightEnd) {
                board.addLast(domino);
                List<Move> solution = solve(nextHand, board, leftEnd, domino.getVal1());
                board.removeLast();
                if (solution != null) { solution.add(0, new Move(domino, "KANAN")); return solution; }
            }
            if (domino.getVal1() == leftEnd) {
                board.addFirst(domino);
                List<Move> solution = solve(nextHand, board, domino.getVal2(), rightEnd);
                board.removeFirst();
                if (solution != null) { solution.add(0, new Move(domino, "KIRI")); return solution; }
            }
            if (domino.getVal1() != domino.getVal2() && domino.getVal2() == leftEnd) {
                board.addFirst(domino);
                List<Move> solution = solve(nextHand, board, domino.getVal1(), rightEnd);
                board.removeFirst();
                if (solution != null) { solution.add(0, new Move(domino, "KIRI")); return solution; }
            }
        }
        return null;
    }

    private void drawDomino(Graphics2D g2d, int val1, int val2, int x, int y, boolean horizontal) {
        int cardWidth = horizontal ? 100 : 60;
        int cardHeight = horizontal ? 50 : 120;
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