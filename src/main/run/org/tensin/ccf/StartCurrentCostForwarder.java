package org.tensin.ccf;

/**
 * The Class StartCurrentCostForwarder.
 */
public class StartCurrentCostForwarder {

    /**
     * The main method.
     *
     * @param args
     *            the arguments
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        // CurrentCostForwarder.main(new String[] { "--help" });
        CurrentCostForwarder.main(new String[] { "-d", "/dev/ttyUSB0", "--debug", "--broker-url", "tcp://192.168.8.40:1883", "--broker-topic",
                "metrics/currentcost" });
    }
}