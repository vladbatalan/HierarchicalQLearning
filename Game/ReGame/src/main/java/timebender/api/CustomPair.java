package timebender.api;

public class CustomPair {
    private final Integer id;
    private final String message;

    public CustomPair(Integer id, String message){
        this.id = id;
        this.message = message;
    }

    public Integer getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
