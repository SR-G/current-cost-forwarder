package org.tensin.ccf;

/**
 * The Class MirrorException.
 */
public class CCFException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5425682063963568465L;

    /**
     * Instantiates a new mirror exception.
     */
    public CCFException() {
        super();
    }

    /**
     * Instantiates a new mirror exception.
     *
     * @param message
     *            the message
     */
    public CCFException(final String message) {
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
    public CCFException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new mirror exception.
     *
     * @param cause
     *            the cause
     */
    public CCFException(final Throwable cause) {
        super(cause);
    }
}