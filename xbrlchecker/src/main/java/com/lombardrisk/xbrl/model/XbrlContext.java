package com.lombardrisk.xbrl.model;

import java.util.Set;

/**
 * Created by Bartosz Jedrzejewski on 06/06/14.
 */
public class XbrlContext {

    Set<XbrlDimensionMember> xbrlDimensionMemberSet;

    public XbrlContext(Set<XbrlDimensionMember> xbrlDimensionMemberSet) {
        this.xbrlDimensionMemberSet = xbrlDimensionMemberSet;
    }

    public Set<XbrlDimensionMember> getXbrlDimensionMemberSet() {
        return xbrlDimensionMemberSet;
    }

    public void setXbrlDimensionMemberSet(Set<XbrlDimensionMember> xbrlDimensionMemberSet) {
        this.xbrlDimensionMemberSet = xbrlDimensionMemberSet;
    }
}
