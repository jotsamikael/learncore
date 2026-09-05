package com.dodibo.learncore.elearningcore.common;

import com.dodibo.learncore.exception.OperationNotPermittedException;

public final class DisplayOrderHelper {

    private DisplayOrderHelper() {
    }

    public static int resolveOrderForCreate(Integer requestedOrder, long currentCount) {
        if (requestedOrder == null) {
            return (int) currentCount + 1;
        }
        validateOrderForCreate(requestedOrder, currentCount);
        return requestedOrder;
    }

    public static void validateOrderForCreate(int displayOrder, long currentCount) {
        int maxAllowed = (int) currentCount + 1;
        if (displayOrder < 1 || displayOrder > maxAllowed) {
            throw new OperationNotPermittedException(
                    "displayOrder must be between 1 and " + maxAllowed);
        }
    }

    public static int requireOrderForUpdate(Integer displayOrder, long currentCount) {
        if (displayOrder == null) {
            throw new OperationNotPermittedException("displayOrder is required");
        }
        validateOrderForUpdate(displayOrder, currentCount);
        return displayOrder;
    }

    public static void validateOrderForUpdate(int displayOrder, long currentCount) {
        int maxAllowed = (int) currentCount;
        if (displayOrder < 1 || displayOrder > maxAllowed) {
            throw new OperationNotPermittedException(
                    "displayOrder must be between 1 and " + maxAllowed);
        }
    }
}
