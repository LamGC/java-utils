package net.lamgc.utils.event;

public class ExceptionThrowEvent implements EventObject {

    private Throwable exception;

    public ExceptionThrowEvent(Throwable exception){
        this.exception = exception;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }
}
