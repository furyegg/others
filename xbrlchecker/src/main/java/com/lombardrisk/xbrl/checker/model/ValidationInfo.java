package com.lombardrisk.xbrl.checker.model;

/**
 * Created by Cesar on 08/05/2014.
 */
public class ValidationInfo {
    private final String code;
    private final String rawScope;
    private final String formula;
    private boolean deactivated;

    public ValidationInfo(String code, String rawScope, String formula) {
        this.code = code;
        this.rawScope = rawScope;
        this.formula = formula;
    }

    public String getCode() {
        return code;
    }

    public String getRawScope() {
        return rawScope;
    }

    public String getFormula() {
        return formula;
    }

    public String toString(){
        String str= "code " + code + " rowScope " + rawScope + " formula " +formula;
        return str;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }
}
