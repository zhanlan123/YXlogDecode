package top.yinlingfeng.xlog.decode.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.net.URISyntaxException;

public class DecodeTest {

    @Test
    public void test() throws IOException, URISyntaxException {
        test_zip_1_0_5();
        test_zip_1_0_6();
        test_zip_1_0_7();
        test_zip_1_2_3();
        test_zip_1_2_4();
        test_zip_1_2_5();
        test_zip_1_2_6();
        test_zip_1_2_6_only_zip_compress();
        test_zstd_1_2_4();
    }

    @Test
    public void test_zip_1_0_5() throws IOException, URISyntaxException {
        String privateKey = "05cb6f67b111a49660d706b15875b1ffc840db68e3545bd2786f02ac9a1233ef";
        File inFile = new File(getClass().getClassLoader().getResource("Release_1680140145234_20230330_1.0.5.xlog").toURI());
        String inFilePath, outFilePath;
        inFilePath = inFile.getAbsolutePath();
        outFilePath = inFile.getAbsolutePath() + ".log";
        File outFile = new File(outFilePath);
        if (outFile.exists()) {
            boolean deleteResult = outFile.delete();
            System.out.println("deleteResult:" + deleteResult);
        }
        System.out.println("inFilePath:" + inFilePath);
        System.out.println("outFilePath:" + outFilePath);
        XLogFileDecode.parseFile(inFilePath, outFilePath, privateKey);
        Assertions.assertTrue(outFile.exists());
    }

    @Test
    public void test_zip_1_0_6() throws IOException, URISyntaxException {
        String privateKey = "05cb6f67b111a49660d706b15875b1ffc840db68e3545bd2786f02ac9a1233ef";
        File inFile = new File(getClass().getClassLoader().getResource("Release_1680140306482_20230330_1.0.6.xlog").toURI());
        String inFilePath, outFilePath;
        inFilePath = inFile.getAbsolutePath();
        outFilePath = inFile.getAbsolutePath() + ".log";
        File outFile = new File(outFilePath);
        if (outFile.exists()) {
            boolean deleteResult = outFile.delete();
            System.out.println("deleteResult:" + deleteResult);
        }
        System.out.println("inFilePath:" + inFilePath);
        System.out.println("outFilePath:" + outFilePath);
        XLogFileDecode.parseFile(inFilePath, outFilePath, privateKey);
        Assertions.assertTrue(outFile.exists());
    }

    @Test
    public void test_zip_1_0_7() throws IOException, URISyntaxException {
        String privateKey = "05cb6f67b111a49660d706b15875b1ffc840db68e3545bd2786f02ac9a1233ef";
        File inFile = new File(getClass().getClassLoader().getResource("Release_1680140676841_20230330_1.0.7.xlog").toURI());
        String inFilePath, outFilePath;
        inFilePath = inFile.getAbsolutePath();
        outFilePath = inFile.getAbsolutePath() + ".log";
        File outFile = new File(outFilePath);
        if (outFile.exists()) {
            boolean deleteResult = outFile.delete();
            System.out.println("deleteResult:" + deleteResult);
        }
        System.out.println("inFilePath:" + inFilePath);
        System.out.println("outFilePath:" + outFilePath);
        XLogFileDecode.parseFile(inFilePath, outFilePath, privateKey);
        Assertions.assertTrue(outFile.exists());
    }

    @Test
    public void test_zip_1_2_3() throws IOException, URISyntaxException {
        String privateKey = "05cb6f67b111a49660d706b15875b1ffc840db68e3545bd2786f02ac9a1233ef";
        File inFile = new File(getClass().getClassLoader().getResource("Release_1680140788774_20230330_1.2.3.xlog").toURI());
        String inFilePath, outFilePath;
        inFilePath = inFile.getAbsolutePath();
        outFilePath = inFile.getAbsolutePath() + ".log";
        File outFile = new File(outFilePath);
        if (outFile.exists()) {
            boolean deleteResult = outFile.delete();
            System.out.println("deleteResult:" + deleteResult);
        }
        System.out.println("inFilePath:" + inFilePath);
        System.out.println("outFilePath:" + outFilePath);
        XLogFileDecode.parseFile(inFilePath, outFilePath, privateKey);
        Assertions.assertTrue(outFile.exists());
    }

    @Test
    public void test_zip_1_2_4() throws IOException, URISyntaxException {
        String privateKey = "05cb6f67b111a49660d706b15875b1ffc840db68e3545bd2786f02ac9a1233ef";
        File inFile = new File(getClass().getClassLoader().getResource("Release_1680140991265_20230330_1.2.4.xlog").toURI());
        String inFilePath, outFilePath;
        inFilePath = inFile.getAbsolutePath();
        outFilePath = inFile.getAbsolutePath() + ".log";
        File outFile = new File(outFilePath);
        if (outFile.exists()) {
            boolean deleteResult = outFile.delete();
            System.out.println("deleteResult:" + deleteResult);
        }
        System.out.println("inFilePath:" + inFilePath);
        System.out.println("outFilePath:" + outFilePath);
        XLogFileDecode.parseFile(inFilePath, outFilePath, privateKey);
        Assertions.assertTrue(outFile.exists());
    }

    @Test
    public void test_zip_1_2_5() throws IOException, URISyntaxException {
        String privateKey = "05cb6f67b111a49660d706b15875b1ffc840db68e3545bd2786f02ac9a1233ef";
        File inFile = new File(getClass().getClassLoader().getResource("Release_1680141201353_20230330_1.2.5.xlog").toURI());
        String inFilePath, outFilePath;
        inFilePath = inFile.getAbsolutePath();
        outFilePath = inFile.getAbsolutePath() + ".log";
        File outFile = new File(outFilePath);
        if (outFile.exists()) {
            boolean deleteResult = outFile.delete();
            System.out.println("deleteResult:" + deleteResult);
        }
        System.out.println("inFilePath:" + inFilePath);
        System.out.println("outFilePath:" + outFilePath);
        XLogFileDecode.parseFile(inFilePath, outFilePath, privateKey);
        Assertions.assertTrue(outFile.exists());
    }

    @Test
    public void test_zip_1_2_6() throws IOException, URISyntaxException {
        String privateKey = "05cb6f67b111a49660d706b15875b1ffc840db68e3545bd2786f02ac9a1233ef";
        File inFile = new File(getClass().getClassLoader().getResource("Release_1680141337138_20230330_1.2.6.xlog").toURI());
        String inFilePath, outFilePath;
        inFilePath = inFile.getAbsolutePath();
        outFilePath = inFile.getAbsolutePath() + ".log";
        File outFile = new File(outFilePath);
        if (outFile.exists()) {
            boolean deleteResult = outFile.delete();
            System.out.println("deleteResult:" + deleteResult);
        }
        System.out.println("inFilePath:" + inFilePath);
        System.out.println("outFilePath:" + outFilePath);
        XLogFileDecode.parseFile(inFilePath, outFilePath, privateKey);
        Assertions.assertTrue(outFile.exists());
    }

    @Test
    public void test_zip_1_2_6_only_zip_compress() throws IOException, URISyntaxException {
        String privateKey = "05cb6f67b111a49660d706b15875b1ffc840db68e3545bd2786f02ac9a1233ef";
        File inFile = new File(getClass().getClassLoader().getResource("Release_1682159319133_20230422_1_2_6_only_zip_compress.xlog").toURI());
        String inFilePath, outFilePath;
        inFilePath = inFile.getAbsolutePath();
        outFilePath = inFile.getAbsolutePath() + ".log";
        File outFile = new File(outFilePath);
        if (outFile.exists()) {
            boolean deleteResult = outFile.delete();
            System.out.println("deleteResult:" + deleteResult);
        }
        System.out.println("inFilePath:" + inFilePath);
        System.out.println("outFilePath:" + outFilePath);
        XLogFileDecode.parseFile(inFilePath, outFilePath, privateKey);
        Assertions.assertTrue(outFile.exists());
    }

    @Test
    public void test_zstd_1_2_4() throws IOException, URISyntaxException {
        String privateKey = "05cb6f67b111a49660d706b15875b1ffc840db68e3545bd2786f02ac9a1233ef";
        File inFile = new File(getClass().getClassLoader().getResource("Release_1680767342752_20230406_1.2.4_zstd.xlog").toURI());
        String inFilePath, outFilePath;
        inFilePath = inFile.getAbsolutePath();
        outFilePath = inFile.getAbsolutePath() + ".log";
        File outFile = new File(outFilePath);
        if (outFile.exists()) {
            boolean deleteResult = outFile.delete();
            System.out.println("deleteResult:" + deleteResult);
        }
        System.out.println("inFilePath:" + inFilePath);
        System.out.println("outFilePath:" + outFilePath);
        XLogFileDecode.parseFile(inFilePath, outFilePath, privateKey);
        Assertions.assertTrue(outFile.exists());
    }

}
