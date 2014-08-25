package com.lombardrisk.xbrl.render.gui;

import com.lombardrisk.xbrl.model.XbrlDataPointLight;
import com.lombardrisk.xbrl.model.XbrlTableLight;
import com.lombardrisk.xbrl.render.ejb.ExcelRenderService;
import com.lombardrisk.xbrl.render.ejb.XbrlModelService;
import com.lombardrisk.xbrl.render.ejb.XmlValidationBean;
import com.lombardrisk.xbrl.render.model.XbrlRenderRequest;
import com.lombardrisk.xbrl.render.util.FileUtils;
import com.lombardrisk.xbrl.render.util.ZipUtils;
import com.lombardrisk.xbrl.render.xml.XbrlInstanceParser;
import com.lombardrisk.xbrl.render.xml.XbrlParserToModel;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Created by Cesar on 17/06/2014.
 */
public class ExcelRendererGui extends JFrame {

    private static final Logger log = LoggerFactory.getLogger(ExcelRendererGui.class);

    private JFileChooser fileChooser;
    private JPanel panel1;
    private JButton uploadXbrlButton;
    private JLabel messageLabel;
    private File xbrlFile;

    private ExcelRenderService excelRenderService;

    private XbrlModelService xbrlModelService;

    private XmlValidationBean validationBean;

    public ExcelRendererGui(String title) throws HeadlessException {
        super(title);
        initComponents();
        setContentPane(panel1);
        uploadXbrlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadXbrlFile();
            }
        });
        SwingWorker<Void, Void> beanInitWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                excelRenderService = new ExcelRenderService();
                excelRenderService.init();
                xbrlModelService = new XbrlModelService();
                xbrlModelService.init();
                validationBean = new XmlValidationBean();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    uploadXbrlButton.setEnabled(true);
                    resetMessages();
                } catch (InterruptedException e) {
                    log.error("Error while initialising beans", e);
                    showMessageDialog(ExcelRendererGui.this, "Interrupted");
                } catch (ExecutionException e) {
                    log.error("Error while initialising beans", e);
                    showMessageDialog(ExcelRendererGui.this, "Error while initialising resources:\n" + getMessage(e));
                }
            }
        };
        messageLabel.setText("Loading resources");
        beanInitWorker.execute();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(350, 250));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("XBRL files", "xml", "xbrl", "zip", "gz");
        fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
        fileChooser.setFileFilter(filter);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
//        resetMessages();
        uploadXbrlButton.setEnabled(false);
        pack();
    }

    private void resetMessages() {
        messageLabel.setText("Upload your xbrl file");
    }

    private void uploadXbrlFile() {
        int returnVal = fileChooser.showOpenDialog(panel1);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            xbrlFile = fileChooser.getSelectedFile();
            messageLabel.setText("Processing xbrl file");
            ExcelRenderWorker excelRenderWorker = new ExcelRenderWorker();
            excelRenderWorker.execute();
            uploadXbrlButton.setEnabled(false);
        }
    }


    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ExcelRendererGui("Xbrl Renderer").setVisible(true);
            }
        });
    }

    private class ExcelRenderWorker extends SwingWorker<Workbook, Void> {

        @Override
        protected Workbook doInBackground() throws Exception {
            if (xbrlFile == null) {
                throw new IOException("No xbrlFile selected");
            }

            final InputStream xbrlContent = ZipUtils.getUncompressedInputStream(xbrlFile);

            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(xbrlContent, out);
            //first validation of the XBRL file
            final byte[] bytes = out.toByteArray();
            xbrlContent.close();
            out.close();

            final String xbrlFileName = xbrlFile.getName();

            log.info("Validating xbrl file {}", xbrlFileName);
            validationBean.validateXbrl(new BufferedInputStream(new ByteArrayInputStream(bytes)));
            log.info("Validation passed for xbrl file {}", xbrlFileName);

            log.info("Reading xbrl instance for xbrl file {}", xbrlFileName);
            final Reader xbrlReader = new InputStreamReader(new BufferedInputStream(new ByteArrayInputStream(bytes)));
            final XbrlParserToModel xbrlParserToModel = new XbrlParserToModel(xbrlModelService);
            XbrlInstanceParser xbrlInstanceParser = new XbrlInstanceParser(xbrlReader, xbrlParserToModel);
            xbrlInstanceParser.process();
            xbrlReader.close();
            log.info("Finish reading file");
            //Getting the light model
            final java.util.List<XbrlTableLight> tables = xbrlParserToModel.buildXbrlTableLightsAfterParsing();
            log.info("Finish building tables");
            final java.util.List<XbrlDataPointLight> dataPoints = xbrlParserToModel.buildXbrlDataPointLightsAfterParsing();
            log.info("Finish building data points");
            final Map<String, Boolean> isDataPointNumericMap = xbrlModelService.getIsDatapointNumericMap();
            final XbrlRenderRequest xbrlRenderRequest = new XbrlRenderRequest(tables, dataPoints, xbrlFileName);
            xbrlRenderRequest.putAllDataPointNumericType(isDataPointNumericMap);
            log.info("Finished reading xbrl instance for xbrl file {}", xbrlFileName);

            log.info("Preparing Excel file for xbrl file {}", xbrlFileName);
            final Workbook wb = excelRenderService.getExcelRenderedXbrl(xbrlRenderRequest);
            log.info("Finished preparing Excel file for xbrl file {}", xbrlFileName);
            return wb;
        }

        @Override
        protected void done() {
            try {
                Workbook wb = get();
                String xbrlFileName = FileUtils.getBaseFileName(xbrlFile.getName());
                final File outFile = new File(xbrlFileName + ".xlsx");
                OutputStream out = new FileOutputStream(outFile);
                wb.write(out);
                showMessageDialog(ExcelRendererGui.this, "Produced Excel file at " + outFile.getAbsolutePath());
            } catch (InterruptedException e) {
                showMessageDialog(ExcelRendererGui.this, "Interrupted");
            } catch (ExecutionException e) {
                log.error("Error while rendering xbrl", e);
                showMessageDialog(ExcelRendererGui.this, "Error while rendering xbrl:\n" + getMessage(e));
            } catch (IOException e) {
                log.error("Error while rendering xbrl", e);
                showMessageDialog(ExcelRendererGui.this, "Error while rendering xbrl:\n" + e.getMessage());
            }
            uploadXbrlButton.setEnabled(true);
            resetMessages();
        }
    }


    private String getMessage(ExecutionException e) {
        final Throwable cause = e.getCause();
        if (StringUtils.isEmpty(cause.getMessage())) {
            return cause.getClass().getName();
        } else {
            return cause.getMessage();
        }
    }
}
