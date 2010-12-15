package blokus;

/**
 *
 * @author vjsingh
 */
public enum MoveMessage {
    SUCCESS ("Success!"),
    OVERLAP ("You can't place a piece on top of an existing piece!"),
    ADJACENT ("Your new piece cannot be adjacent to another one of your pieces!"),
    NOCORNER ("Your piece must touch a corner of a piece you have already placed!");

    private final String _string;
    MoveMessage(String string) {
        _string = string;
    }

    public String getMessage() {
        return _string;
    }
}
