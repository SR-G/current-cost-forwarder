package org.tensin.ccf;

/**
 * The Class MirrorException.
 */
public class CCFRuntimeException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5425682063963568465L;

    /**
     * Instantiates a new mirror exception.
     */
    public CCFRuntimeException() {
        super();
    }

    /**
     * Instantiates a new mirror exception.
     *
     * @param message
     *            the message
     */
    public CCFRuntimeException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new mirror exception.
     *
     * @param message
     *            the message
     * @param cause
     *            the cause
     */
    public CCFRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new mirror exception.
     *
     * @param cause
     *            the cause
     */
    public CCFRuntimeException(final Throwable cause) {
        super(cause);
    }
}