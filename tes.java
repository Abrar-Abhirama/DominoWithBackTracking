public class tes {
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
