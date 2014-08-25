package com.lombardrisk.xbrl.render.controller;

import com.lombardrisk.xbrl.render.ejb.UserTokenDao;
import com.lombardrisk.xbrl.render.ejb.XbrlRenderService;
import com.lombardrisk.xbrl.render.model.XbrlRenderListener;
import com.lombardrisk.xbrl.render.model.entities.UserToken;
import com.lombardrisk.xbrl.render.model.exception.UserTokenException;
import com.lombardrisk.xbrl.render.model.exception.XbrlRenderException;
import com.lombardrisk.xbrl.render.util.FileUtils;
import com.lombardrisk.xbrl.render.util.ZipUtils;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;

/**
 * Created by Cesar on 06/06/2014.
 */
@Named
@ViewAccessScoped
public class RenderBean implements Serializable, XbrlRenderListener {

    private static final Logger log = LoggerFactory.getLogger(RenderBean.class);
    public static final String EXCEL_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private int nCreate;

    private String token;

    private long remaining;

    private String message;

    @Inject
    private UserTokenDao userTokenDao;

    @Inject
    private XbrlRenderService xbrlRenderService;


    private StreamedContent excelFile;

    private UploadedFile xbrlFile;


    public void createUserToken() {
        UserToken userToken = null;
        try {
            userToken = userTokenDao.createUserToken(nCreate);
        } catch (UserTokenException e) {
            log.error("",e);
        }
        token = userToken.getToken();
    }

    public void findToken() {
        try {
            UserToken userToken = userTokenDao.getToken(token);
            remaining = userToken.getRemainingRequests();
            message = "";
        } catch (UserTokenException e) {
            message = "could not find token";
        }
    }

    public void consumeToken() {
        try {
            xbrlRenderService.test(token);
            UserToken userToken = userTokenDao.getToken(token);
            remaining = userToken.getRemainingRequests();
            message = "";
        } catch (UserTokenException | XbrlRenderException e) {
            message = e.getMessage();
        }
        throw new RuntimeException("aaa");
    }

    public void handleFileUpload(FileUploadEvent event) {
        xbrlFile = event.getFile();
        try {
            excelFile = null;
            InputStream in = ZipUtils.getUncompressedInputStream(xbrlFile);
            Workbook wb = xbrlRenderService.uploadXbrl(in, xbrlFile.getFileName(), this);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            out.close();
            InputStream excelIn = new ByteArrayInputStream(out.toByteArray());
            String xbrlFileName = FileUtils.getBaseFileName(xbrlFile.getFileName());
            excelFile = new DefaultStreamedContent(excelIn, EXCEL_MIME_TYPE, xbrlFileName + ".xlsx");
            message = "done";
        } catch (IOException | XbrlRenderException e) {
            log.error("", e);
            message = e.getMessage();
        }
    }

    public int getnCreate() {
        return nCreate;
    }

    public void setnCreate(int nCreate) {
        this.nCreate = nCreate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getRemaining() {
        return remaining;
    }

    public String getMessage() {
        return message;
    }

    public UploadedFile getXbrlFile() {
        return xbrlFile;
    }

    public void setXbrlFile(UploadedFile xbrlFile) {
        this.xbrlFile = xbrlFile;
    }

    public StreamedContent getExcelFile() {
        return excelFile;
    }

    @Override
    public void validatingInstance() {

    }

    @Override
    public void instanceValidated() {

    }

    @Override
    public void readingInstance() {

    }

    @Override
    public void instanceRead() {

    }

    @Override
    public void processingDataPoints(int processed, int total) {

    }

    @Override
    public void processedAllDataPoints() {

    }

    @Override
    public void rendering() {

    }

    @Override
    public void finishedRendering() {

    }
}
