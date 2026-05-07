package net.lego.data.v2.enums;

public enum PhotoStatus {

    UPLOADED,
    PROCESSED,
    FAILED;

    public boolean canTransitionTo(PhotoStatus next) {
        if (this.equals(next)) {
            return true;
        }

        return switch (this) {
            case UPLOADED -> next == PROCESSED || next == FAILED;
            case FAILED -> next == UPLOADED || next == PROCESSED;
            case PROCESSED -> false;
        };
    }
}