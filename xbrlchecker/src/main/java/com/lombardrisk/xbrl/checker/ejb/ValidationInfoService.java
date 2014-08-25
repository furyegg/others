package com.lombardrisk.xbrl.checker.ejb;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.healthmarketscience.jackcess.util.EntryIterableBuilder;
import com.healthmarketscience.jackcess.util.Joiner;
import com.lombardrisk.xbrl.checker.EbaConstants;
import com.lombardrisk.xbrl.checker.config.Config;
import com.lombardrisk.xbrl.checker.csv.CsvUtil;
import com.lombardrisk.xbrl.checker.csv.DeactivatedValidationCsvDescriptor;
import com.lombardrisk.xbrl.checker.model.DeactivatedValidation;
import com.lombardrisk.xbrl.checker.model.ReturnInfo;
import com.lombardrisk.xbrl.checker.model.ValidationInfo;
import com.lombardrisk.xbrl.checker.utils.RegexUtils;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.ejb.Singleton;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Cesar on 08/05/2014.
 */
@Singleton
@Startup
public class ValidationInfoService implements Serializable {
    private static final String DPM_PATH = System.getProperty("user.home") + File.separator + "DPM Database 2.1.0.accdb";
    private static final Logger log = LoggerFactory.getLogger(ValidationInfoService.class);

    private Database dpmDB;
    private Map<String, ValidationInfo> validationInfoMap;
    private Map<String, ReturnInfo> returnInfoMap;

    @PostConstruct
    public void init() throws IOException {
        final File dpmFile = new File(DPM_PATH);
        if (dpmFile == null) {
            throw new IOException("Could not load DPM database at location: " + dpmFile.getAbsolutePath());
        }
        dpmDB = DatabaseBuilder.open(dpmFile);
        loadReturnInformation();
        loadReturnInfo();
        updateDeactivatedValidations();
    }

    private void loadReturnInformation() throws IOException {
        validationInfoMap = new HashMap<>();

        final Table validationRule = dpmDB.getTable(EbaConstants.VALIDATION_RULE_TABLE);
        final Table expressionTable = dpmDB.getTable(EbaConstants.EXPRESSION_TABLE);
        final Joiner joiner = Joiner.create(validationRule.getForeignKeyIndex(expressionTable));

        for (Row validationRow : validationRule) {
            final Object scopeString = validationRow.get(EbaConstants.VALIDATION_RULE_TABLE_SCOPE);
            if (scopeString == null) {
                log.error("No scope");
                continue;
            }

            final EntryIterableBuilder expressionRows = joiner.findRows(validationRow);
            final Row expressionRow = expressionRows.iterator().next();
            final Object expression = expressionRow.get(EbaConstants.EXPRESSION_TABLE_FORMULA);
            final Object code = validationRow.get(EbaConstants.VALIDATION_RULE_TABLE_VALIDATION_CODE);
            if (expression == null) {
                log.error("No expression");
                continue;
            }
            final ValidationInfo validationInfo = new ValidationInfo(code.toString(), scopeString.toString(), expression.toString());
            validationInfoMap.put(validationInfo.getCode(), validationInfo);
        }
        log.debug("done");
    }

    private void loadReturnInfo() throws IOException {
        returnInfoMap = new HashMap<>();
        final Table tableVersionTable = dpmDB.getTable(EbaConstants.TABLE_VERSION_TABLE);
        for (Row tableVersionRow : tableVersionTable) {
            final String returnCode = tableVersionRow.get(EbaConstants.TABLE_VERSION_TABLE_CODE).toString();
            final String label = tableVersionRow.get(EbaConstants.TABLE_VERSION_TABLE_LABEL).toString();
            final ReturnInfo returnInfo = new ReturnInfo(returnCode, label);
            returnInfoMap.put(returnCode, returnInfo);
        }
        log.info("done");
    }

    private void updateDeactivatedValidations() throws IOException {
        final String deactivatedValidationPath = Config.INSTANCE.getString("deactivated.validations.location");
        final File deactivatedValidationFile = new File(deactivatedValidationPath);
        final List<DeactivatedValidation> deactivatedValidations = CsvUtil.parse(new FileReader(deactivatedValidationFile), new DeactivatedValidationCsvDescriptor(), true);
        for (DeactivatedValidation deactivatedValidation : deactivatedValidations) {
            ValidationInfo validationInfo = validationInfoMap.get(deactivatedValidation.getValidationId().toLowerCase());
            if(validationInfo != null){
                validationInfo.setDeactivated(true);
                log.info("Validation {} set as deactivated", validationInfo.getCode());
            }
        }

    }

    public ValidationInfo getValidationInfo(String validationCode) {
        log.info("getValidationInfo:" + validationCode);
        return validationInfoMap.get(validationCode);
    }

    public List<ReturnInfo> getReturnInfoList(ValidationInfo validationInfo) {
        final Set<ReturnInfo> returnInfoSet = new HashSet<>();
        final List<String> scopeTableCodes = RegexUtils.getTableCodes(validationInfo.getRawScope());
        final List<String> formulaTableCodes = RegexUtils.getTableCodes(validationInfo.getFormula());

        for (String taleCode : scopeTableCodes) {
            final ReturnInfo returnInfo = returnInfoMap.get(taleCode);
            if (returnInfo != null) {
                returnInfoSet.add(returnInfo);
            }
        }
        for (String taleCode : formulaTableCodes) {
            final ReturnInfo returnInfo = returnInfoMap.get(taleCode);
            if (returnInfo != null) {
                returnInfoSet.add(returnInfo);
            }
        }
        final List<ReturnInfo> result = new ArrayList<>(returnInfoSet) ;
        Collections.sort(result, new Comparator<ReturnInfo>() {
            @Override
            public int compare(ReturnInfo o1, ReturnInfo o2) {
                return o1.getCode().compareTo(o2.getCode());
            }
        });
        return result;
    }

}
