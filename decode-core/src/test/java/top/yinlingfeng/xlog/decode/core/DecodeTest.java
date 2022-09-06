package top.yinlingfeng.xlog.decode.core;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class DecodeTest {

    @Test
    public void test() throws IOException {
        String privateKey = "920fa5a1780243747c5076f0931f13f6bf107456dd7034a986dd9720075fe863";
        String infile, outfile;
        infile = "C:\\Users\\yin\\Desktop\\Release_01525096_20220827.xlog";
        outfile = "C:\\Users\\yin\\Desktop\\Release_01525096_20220827.xlog.log";
        XLogFileDecode.ParseFile(infile, outfile, privateKey);
    }
}
