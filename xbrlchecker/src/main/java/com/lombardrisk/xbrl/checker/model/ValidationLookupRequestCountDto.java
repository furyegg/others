package com.lombardrisk.xbrl.checker.model;

import com.lombardrisk.xbrl.checker.ejb.dao.ValidationLookupRequestDao;

/**
 * Created by Cesar on 10/05/2014.
 */
public class ValidationLookupRequestCountDto implements Comparable<ValidationLookupRequestCountDto> {
    private final String validationId;
    private final long count;

    public ValidationLookupRequestCountDto(String validationId, long count) {
        this.validationId = validationId;
        this.count = count;
    }

    public String getValidationId() {
        return validationId;
    }

    public long getCount() {
        return count;
    }

    @Override
    public int compareTo(ValidationLookupRequestCountDto o) {
        return (int) (count - o.count);
    }
}
