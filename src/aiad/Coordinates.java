package aiad;

public class Coordinates {
    protected Integer x;
    protected Integer y;

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Double getDistance(Coordinates p2) {
        int tempX = Math.abs(getX()-p2.getX());
        int tempY = Math.abs(getY()-p2.getY());
        return Math.sqrt(Math.pow(tempX,2) + Math.pow(tempY,2));
    }

    public Coordinates(Integer x, Integer y){this.x = x; this.y = y;}
}