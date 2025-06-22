public class Move {
    private Domino domino;
    private String placement; // "START", "KIRI", atau "KANAN"

    public Move(Domino domino, String placement) {
        this.domino = domino;
        this.placement = placement;
    }
    
    public Domino getDomino() {
        return domino;
    }
    
    public String getPlacement() {
        return placement;
    }

    @Override
    public String toString() {
        return "Mainkan " + domino + " di " + placement;
    }
}