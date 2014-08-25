package com.lombardrisk.xbrl.render.util;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.model.UploadedFile;

import java.io.*;
import java.text.MessageFormat;
import java.util.zip.*;

/**
 * Created by Cesar on 09/06/2014.
 */
final public class ZipUtils {

    public static final String EXT = ".zip";
    private static final String BASE_DIR = "";
    private static final int BUFFER = 1024;
    public static final String GZIP = "gz";
    public static final String ZIP = "zip";
    public static final String XML = "xml";
    public static final String XBRL = "xbrl";

    private ZipUtils() {
    }

    public static void zip(File srcFile) throws IOException {
        String name = srcFile.getName();
        String basePath = srcFile.getParent();
        String destPath = basePath + name + EXT;
        zip(srcFile, destPath);
    }

    public static void zip(File srcFile, File destFile) throws IOException {

        CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(destFile), new CRC32());

        ZipOutputStream zos = new ZipOutputStream(cos);

        zip(srcFile, zos, BASE_DIR);

        zos.flush();
        zos.close();
    }

    public static void zip(File srcFile, String destPath) throws IOException {
        zip(srcFile, new File(destPath));
    }

    private static void zip(File srcFile, ZipOutputStream zos, String basePath) throws IOException {
        if (srcFile.isDirectory()) {
            zipDir(srcFile, zos, basePath);
        } else {
            zipFile(srcFile, zos, basePath);
        }
    }

    public static void zip(String srcPath) throws IOException {
        File srcFile = new File(srcPath);

        zip(srcFile);
    }

    public static void zip(String srcPath, String destPath) throws IOException {
        File srcFile = new File(srcPath);

        zip(srcFile, destPath);
    }

    private static void zipDir(File dir, ZipOutputStream zos, String basePath) throws IOException {

        File[] files = dir.listFiles();

        if (files.length < 1) {
            ZipEntry entry = new ZipEntry(basePath);

            zos.putNextEntry(entry);
            zos.closeEntry();
        }

        for (File file : files) {

            zip(file, zos, basePath);

        }
    }

    private static void zipFile(File file, ZipOutputStream zos, String dir) throws IOException {

        ZipEntry entry = new ZipEntry(dir + file.getName());

        zos.putNextEntry(entry);

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

        int count;
        byte data[] = new byte[BUFFER];
        while ((count = bis.read(data, 0, BUFFER)) != -1) {
            zos.write(data, 0, count);
        }
        bis.close();

        zos.closeEntry();
    }

    public static void unzip(String srcPath) throws IOException {
        File srcFile = new File(srcPath);

        unzip(srcFile);
    }

    public static void unzip(File srcFile) throws IOException {
        String basePath = srcFile.getParent();
        unzip(srcFile, basePath);
    }

    public static void unzip(File srcFile, File destFile) throws IOException {

        CheckedInputStream cis = new CheckedInputStream(new FileInputStream(srcFile), new CRC32());

        ZipInputStream zis = new ZipInputStream(cis);

        unzip(destFile, zis);

        zis.close();
    }

    public static void unzip(File srcFile, String destPath) throws IOException {
        unzip(srcFile, new File(destPath));

    }

    public static void unzip(String srcPath, String destPath) throws IOException {

        File srcFile = new File(srcPath);
        unzip(srcFile, destPath);
    }

    private static void unzip(File destFile, ZipInputStream zis) throws IOException {

        ZipEntry entry = null;
        while ((entry = zis.getNextEntry()) != null) {

            String dir = destFile.getPath() + File.separator + entry.getName();

            File dirFile = new File(dir);

            fileProber(dirFile);

            if (entry.isDirectory()) {
                dirFile.mkdirs();
            } else {
                unzipFile(dirFile, zis);
            }

            zis.closeEntry();
        }
    }

    private static void fileProber(File dirFile) {

        File parentFile = dirFile.getParentFile();
        if (!parentFile.exists()) {

            fileProber(parentFile);

            parentFile.mkdir();
        }

    }

    private static void unzipFile(File destFile, ZipInputStream zis) throws IOException {

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile));

        int count;
        byte data[] = new byte[BUFFER];
        while ((count = zis.read(data, 0, BUFFER)) != -1) {
            bos.write(data, 0, count);
        }

        bos.close();
    }

    /**
     * Compress to GZIP file
     */
    public static void compressToGzipFile(InputStream in, OutputStream out) throws IOException {
        GZIPOutputStream gzOut = new GZIPOutputStream(out);

        byte[] buffer = new byte[BUFFER];
        int len;
        while ((len = in.read(buffer)) != -1) {
            gzOut.write(buffer, 0, len);
        }
    }

    /**
     * Decompress GZIP file.
     *
     * @param in Must be instance of GZIPInputStream type.
     */
    public static void decompressGzipFile(InputStream in, OutputStream out) throws IOException {
        if (in instanceof GZIPInputStream) {
            byte[] buffer = new byte[BUFFER];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } else {
            throw new IOException("The input stream for GZ must be type GZIPInputStream.");
        }
    }

    /**
     * Decompress XBRL instance ZIP file, only used for zipped XBRL instance (only contains one entry).
     *
     * @param in Must be instance of ZipInputStream type.
     */
    public static void decompressXbrliZipFile(InputStream in, OutputStream out) throws IOException {
        if (in instanceof ZipInputStream) {
            ZipInputStream zin = (ZipInputStream) in;
            zin.getNextEntry();

            byte[] buffer = new byte[BUFFER];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            zin.closeEntry();
        } else {
            throw new IOException("The input stream for ZIP must be type ZipInputStream.");
        }
    }

    public static InputStream getUncompressedInputStream(File file) throws IOException {
        return getUncompressedInputStream(new FileInputStream(file), file.getName());
    }

    private static InputStream getUncompressedInputStream(InputStream in, String fileName) throws IOException {
        InputStream toReturn;
        if (in == null) {
            throw new IOException("Cannot un-compress null file");
        }
        final String extension = FilenameUtils.getExtension(fileName);
        if (extension.toLowerCase().equals(GZIP)) {
            toReturn = new GZIPInputStream(in);
        } else if (extension.toLowerCase().equals(ZIP)) {
            final ZipInputStream zipInputStream = new ZipInputStream(in);
            final ZipEntry entry = zipInputStream.getNextEntry();
            if (entry == null) {
                throw new IOException(MessageFormat.format("Zip archive {0} is empty", fileName));
            }
            toReturn = zipInputStream;
        } else if (extension.toLowerCase().equals(XML) || extension.toLowerCase().equals(XBRL)) {
            toReturn = in;
        } else {
            throw new IOException(MessageFormat.format("Extension {0} is not a valid compressed file", extension));
        }
        return new BufferedInputStream(checkForUtf8BOMAndDiscardIfAny(toReturn));
    }

    private static InputStream checkForUtf8BOMAndDiscardIfAny(InputStream inputStream) throws IOException {
        PushbackInputStream pushbackInputStream = new PushbackInputStream(new BufferedInputStream(inputStream), 3);
        byte[] bom = new byte[3];
        if (pushbackInputStream.read(bom) != -1) {
            if (!(bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF)) {
                pushbackInputStream.unread(bom);
            }
        }
        return pushbackInputStream;
    }

    public static InputStream getUncompressedInputStream(UploadedFile file) throws IOException {
        return getUncompressedInputStream(file.getInputstream(), file.getFileName());
    }
}
