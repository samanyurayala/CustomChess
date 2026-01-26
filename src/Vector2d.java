public class Vector2d {
    public int x;
    public int y;

    public Vector2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2d negative() {
        return new Vector2d(-x, -y);
    }

    public Vector2d reverse() {
        return new Vector2d(y, x);
    }
}
