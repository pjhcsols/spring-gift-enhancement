package gift.exception;

public class WishNotFoundException extends RuntimeException{
    public WishNotFoundException(String message) {
        super(message);
    }
}
