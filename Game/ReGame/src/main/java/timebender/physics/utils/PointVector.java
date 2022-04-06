package timebender.physics.utils;

/**
 * Class responsible for representing a 2D vector.
 *
 * @author Batalan Vlad
 * @since 1.0
 */

public class PointVector {
    private float x;
    private float y;

    public PointVector() {
        x = 0;
        y = 0;
    }

    public PointVector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public PointVector(PointVector v) {
        this.x = v.getX();
        this.y = v.getY();
    }



    public PointVector add(PointVector v) {
        PointVector result = new PointVector();
        result.x = this.x + v.x;
        result.y = this.y + v.y;
        return result;
    }

    public PointVector sub(PointVector v) {
        PointVector result = new PointVector();
        result.x = this.x - v.x;
        result.y = this.y - v.y;
        return result;
    }

    public float abs() {
        float result;
        result = (float) Math.sqrt(this.x * this.x + this.y * this.y);
        return result;
    }

    public float distanceTo(PointVector pct) {
        return (float) Math.sqrt((x - pct.getX()) * (x - pct.getX()) + (y - pct.getY()) * (y - pct.getY()));
    }

    public PointVector scalarMultiply(float a) {
        return new PointVector(this.getX() * a, this.getY() * a);
    }

    public PointVector setInMapBounds(int width, int height, PointVector bounds) {
        PointVector result = new PointVector(this);
        if (this.getX() < 0) result.setX(0);
        if (this.getY() < 0) result.setY(0);
        if (this.getX() + width > bounds.getX())
            result.setX((bounds.getX() - width - 2));
        if (this.getY() + height > bounds.getY())
            result.setY(bounds.getY() - height - 2);
        return result;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Function responsible for creating a copy of PointVector.
     *
     * @return A clone of the object.
     * @throws CloneNotSupportedException If clonning is not supported
     */
    @Override
    public PointVector clone() throws CloneNotSupportedException {
        return (PointVector) super.clone();
    }

    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof PointVector)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        PointVector c = (PointVector) o;

        return c.x == x && c.y == y;
    }
}
