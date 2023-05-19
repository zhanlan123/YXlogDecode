package top.yinlingfeng.xlog.decode.yxlogdecodedemo;

import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import top.yinlingfeng.xlog.decode.core.XLogFileDecode;
import top.yinlingfeng.xlog.decode.yxlogdecodedemo.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.btnTestZip.setOnClickListener(v -> {
            testZip_1_2_6();
        });
        binding.btnTestZstd.setOnClickListener(v -> {
            testZstd_1_2_4();
        });
    }

    private void testZip_1_2_6() {
        ThreadPoolUtils.execute(() -> {
            try {
                InputStream inputStream = getResources().getAssets().open("Release_1680141337138_20230330_1.2.6.xlog");
                File externalFileDir = getExternalFilesDir("XLog");
                if (externalFileDir != null) {
                    String logPath = externalFileDir.getAbsolutePath();
                    FileOutputStream fileOutputStream = new FileOutputStream(logPath + "/Release_1680141337138_20230330_1.2.6.xlog");
                    byte[] buffer = new byte[2048];
                    while (inputStream.read(buffer) != -1) {
                        fileOutputStream.write(buffer);
                    }
                    inputStream.close();
                    fileOutputStream.close();
                    Log.i(TAG, "复制文件完成");
                    String logFilePath = logPath + "/Release_1680141337138_20230330_1.2.6.xlog";
                    String outFilePath = logPath + "/Release_1680141337138_20230330_1.2.6.xlog.log";
                    String privateKey = "05cb6f67b111a49660d706b15875b1ffc840db68e3545bd2786f02ac9a1233ef";
                    XLogFileDecode.parseFile(logFilePath, outFilePath, privateKey);
                    Log.i(TAG, "解密文件完成");
                }
            } catch (IOException e) {
                Log.e(TAG, "异常信息:" + e.getStackTrace());
            }

        });

    }

    private void testZstd_1_2_4() {
        ThreadPoolUtils.execute(() -> {
            try {
                InputStream inputStream = getResources().getAssets().open("Release_1680767342752_20230406_1.2.4_zstd.xlog");
                File externalFileDir = getExternalFilesDir("XLog");
                if (externalFileDir != null) {
                    String logPath = externalFileDir.getAbsolutePath();
                    FileOutputStream fileOutputStream = new FileOutputStream(logPath + "/Release_1680767342752_20230406_1.2.4_zstd.xlog");
                    byte[] buffer = new byte[2048];
                    while (inputStream.read(buffer) != -1) {
                        fileOutputStream.write(buffer);
                    }
                    inputStream.close();
                    fileOutputStream.close();
                    Log.i(TAG, "复制文件完成");
                    String logFilePath = logPath + "/Release_1680767342752_20230406_1.2.4_zstd.xlog";
                    String outFilePath = logPath + "/Release_1680767342752_20230406_1.2.4_zstd.xlog.log";
                    String privateKey = "05cb6f67b111a49660d706b15875b1ffc840db68e3545bd2786f02ac9a1233ef";
                    XLogFileDecode.parseFile(logFilePath, outFilePath, privateKey);
                    Log.i(TAG, "解密文件完成");
                }
            } catch (IOException e) {
                Log.e(TAG, "异常信息:" + e.getStackTrace());
            }

        });

    }
}