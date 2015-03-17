package org.tensin.ccf.model.history;

public enum CurrentCostHistoryItemType {

    DAY("daily"), MONTH("monthly"), HOUR("hourly");

    private final String topic;

    CurrentCostHistoryItemType(final String s) {
        topic = s;
    }

    public String topic() {
        return topic;
    }
}