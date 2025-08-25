package exceptions;

import static constants.Constants.ZERO_STREAM_ENTRY_ID;

public class InvalidEntryIdException extends RuntimeException {
    private final String lastEntryId;
    private final String invalidEntryId;

    public InvalidEntryIdException(String message, String invalidEntryId, String lastEntryId) {
        super(message);
        this.invalidEntryId = invalidEntryId;
        this.lastEntryId = lastEntryId;
    }

    public String getLastEntryId() {
        return lastEntryId;
    }

    public boolean isZeroEntryId() {
        return ZERO_STREAM_ENTRY_ID.equals(invalidEntryId);
    }
}
