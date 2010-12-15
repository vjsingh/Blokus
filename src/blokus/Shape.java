package blokus;

/**
 * @author vjsingh
 */
//For pentomino names, see
//http://www.cimt.plymouth.ac.uk/resources/puzzles/pentoes/pentoint.htm
public enum Shape {
    oneSquare(1),
    domino(2),
    smallL(3),
    threeLine(3),
    fourLine(4),
    fourL(4),
    fourSquare(4),
    zigZag(5),
    T(5),
    fiveJ(5),
    fiveT(5),
    fiveL(5),
    fiveZigZag(5),
    Z(5),
    fiveLine(5),
    P(5),
    W(5),
    U(5),
    F(5),
    cross(5),
    Y(5);

    private final int _numOfSquares;

    Shape(int numOfSquares) {
        _numOfSquares = numOfSquares;
    }

    public int getnumOfSquares() {
        return _numOfSquares;
    }

}

