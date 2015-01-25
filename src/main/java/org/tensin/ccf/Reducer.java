package org.tensin.ccf;

/**
 * The Class Reducer.
 * Allows to log only if a given amount of errors have been encountered (in order to keep those error logs but to not dump every single error, which may cause a lot of useless logs (for example, for the reconnection mechanism)).
 *
 * @author Serge SIMON
 * @version $Revision: 1.00 $
 * @since 2 jan. 2012 13:18:03
 */
public final class Reducer {

    /**
     * Render.
     *
     * @param value
     *            the value
     * @return true, if successful
     */
    public static boolean render(final long value) {
        if (value < 0) {
            return false;
        }
        if (value == 0) {
            return true;
        }
        for (int seuil : SEUILS) {
            if (value == seuil) {
                return true;
            }
        }
        if ((value % LAST) == 0) {
            return true;
        }
        return false;
    }

    /** The Constant last. */
    private static final int LAST = 100;

    /** The Constant seuils. */
    private static final int[] SEUILS = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 25, 30, 45, 40, 45, 50, 60, 70, 80, 90, 100 };

    /**
     * Instantiates a new reducer.
     */
    private Reducer() {

    }
}