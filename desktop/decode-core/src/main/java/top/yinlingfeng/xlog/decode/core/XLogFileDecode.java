package top.yinlingfeng.xlog.decode.core;

import org.apache.commons.lang3.exception.ExceptionUtils;
import top.yinlingfeng.xlog.decode.core.log.LogUtil;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

class RetData{
    int startPos;
    int lastSeq;
    public RetData(int startPos, int lastSeq){
        this.startPos = startPos;
        this.lastSeq = lastSeq;
    }
}

public class XLogFileDecode {

    private static final String TAG = "XlogFileDecoder";

    public static byte MAGIC_NO_COMPRESS_START = 0x03;
    public static byte MAGIC_NO_COMPRESS_START1 = 0x06;
    public static byte MAGIC_NO_COMPRESS_NO_CRYPT_START = 0x08;
    public static byte MAGIC_COMPRESS_START = 0x04;
    public static byte MAGIC_COMPRESS_START1 = 0x05;
    public static byte MAGIC_COMPRESS_START2 = 0x07;
    public static byte MAGIC_COMPRESS_NO_CRYPT_START = 0x09;

    public static byte MAGIC_SYNC_ZSTD_START = 0x0A;
    public static byte MAGIC_SYNC_NO_CRYPT_ZSTD_START = 0x0B;
    public static byte MAGIC_ASYNC_ZSTD_START = 0x0C;
    public static byte MAGIC_ASYNC_NO_CRYPT_ZSTD_START = 0x0D;

    public static byte MAGIC_END = 0x00;

    private static boolean IsGoodLogBuffer(byte[] _buffer, int _offset, int count) {
        if (_offset == _buffer.length) {
            return true;
        }

        int crypt_key_len;
        byte magic_start = _buffer[_offset];
        if (MAGIC_NO_COMPRESS_START == magic_start ||
                MAGIC_COMPRESS_START == magic_start ||
                MAGIC_COMPRESS_START1 == magic_start){
            crypt_key_len = 4;
        } else if (MAGIC_COMPRESS_START2 == magic_start ||
                MAGIC_NO_COMPRESS_START1 == magic_start ||
                MAGIC_NO_COMPRESS_NO_CRYPT_START == magic_start ||
                MAGIC_COMPRESS_NO_CRYPT_START == magic_start ||
                MAGIC_SYNC_ZSTD_START == magic_start ||
                MAGIC_SYNC_NO_CRYPT_ZSTD_START == magic_start ||
                MAGIC_ASYNC_ZSTD_START == magic_start ||
                MAGIC_ASYNC_NO_CRYPT_ZSTD_START == magic_start){
            crypt_key_len = 64;
        } else {
            LogUtil.i(TAG, String.format("_buffer[%d]:%d != MAGIC_NUM_START", _offset, _buffer[_offset]));
            return false;
        }

        int headerLen = 1 + 2 + 1 + 1 + 4 + crypt_key_len;

        if (_offset + headerLen + 1 + 1 > _buffer.length) {
            LogUtil.i(TAG, String.format("offset:%d > len(buffer):%d", _offset, _buffer.length));
            return false;
        }
        int length = ByteBuffer.wrap(_buffer, _offset + headerLen - 4 - crypt_key_len, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
        long llength = length&0xffffffffL;
        if (_offset + headerLen + llength + 1 > _buffer.length){
            LogUtil.i(TAG, String.format("log length:%d, end pos %d > len(buffer):%d",llength, _offset + headerLen + llength + 1, _buffer.length));
            return false;
        }
        if(MAGIC_END != _buffer[_offset + headerLen + length]){
            LogUtil.i(TAG, String.format("log length:%d, buffer[%d]:%d != MAGIC_END", length, _offset + headerLen + length, _buffer[_offset + headerLen + length]));
            return false;
        }

        if (1 >= count) {
            return true;
        } else {
            return IsGoodLogBuffer(_buffer, _offset + headerLen + length + 1, count - 1);
        }
    }

    private static int GetLogStartPos(byte[] _buffer, int _count){
        int offset = 0;
        while (true) {
            if (offset >= _buffer.length) {
                break;
            }

            if (MAGIC_NO_COMPRESS_START==_buffer[offset] ||
                    MAGIC_NO_COMPRESS_START1==_buffer[offset] ||
                    MAGIC_COMPRESS_START==_buffer[offset] ||
                    MAGIC_COMPRESS_START1==_buffer[offset] ||
                    MAGIC_COMPRESS_START2==_buffer[offset] ||
                    MAGIC_COMPRESS_NO_CRYPT_START==_buffer[offset] ||
                    MAGIC_NO_COMPRESS_NO_CRYPT_START==_buffer[offset] ||
                    MAGIC_SYNC_ZSTD_START == _buffer[offset] ||
                    MAGIC_SYNC_NO_CRYPT_ZSTD_START == _buffer[offset] ||
                    MAGIC_ASYNC_ZSTD_START == _buffer[offset] ||
                    MAGIC_ASYNC_NO_CRYPT_ZSTD_START == _buffer[offset]){
                if(IsGoodLogBuffer(_buffer, offset, _count)) {
                    return offset;
                }
            }

            offset += 1;
        }

        return  -1;
    }

    private static RetData DecodeBuffer(byte[] _buffer, int _offset, int lastseq, StringBuilder _outbuffer, String privateKey){
        RetData retData = new RetData(_offset, lastseq);
        if (_offset >= _buffer.length) {
            return new RetData(-1, lastseq);
        }

        boolean ret = IsGoodLogBuffer(_buffer, _offset, 1);
        byte[] tmpbuffer = new byte[_buffer.length - _offset];
        if (!ret) {
            System.arraycopy(_buffer, _offset,  tmpbuffer,0, tmpbuffer.length);
            int fixpos = GetLogStartPos(tmpbuffer, 1);
            if (-1 == fixpos) {
                return new RetData(-1, lastseq);
            } else {
                LogUtil.i(TAG, String.format("[F]decode_log_file.py decode error len=%d, result:%s \n", fixpos, ret));
                _outbuffer.append(String.format("[F]decode_log_file.py decode error len=%d, result:%s \n", fixpos, ret));
                _offset += fixpos;
            }
        }
        int magic_start = _buffer[_offset];
        int crypt_key_len;
        if (MAGIC_NO_COMPRESS_START==magic_start ||
                MAGIC_COMPRESS_START==magic_start ||
                MAGIC_COMPRESS_START1==magic_start){
            crypt_key_len = 4;
        } else if (MAGIC_COMPRESS_START2==magic_start ||
                MAGIC_NO_COMPRESS_START1==magic_start ||
                MAGIC_NO_COMPRESS_NO_CRYPT_START==magic_start ||
                MAGIC_COMPRESS_NO_CRYPT_START==magic_start ||
                MAGIC_SYNC_ZSTD_START == magic_start ||
                MAGIC_SYNC_NO_CRYPT_ZSTD_START == magic_start ||
                MAGIC_ASYNC_ZSTD_START == magic_start ||
                MAGIC_ASYNC_NO_CRYPT_ZSTD_START == magic_start){

            crypt_key_len = 64;
        } else {
            LogUtil.i(TAG, String.format("n DecodeBuffer _buffer[%d]:%d != MAGIC_NUM_START", _offset, magic_start));
            _outbuffer.append("in DecodeBuffer _buffer[%d]:%d != MAGIC_NUM_START", _offset, magic_start);
            return new RetData(-1, lastseq);
        }

        int headerLen = 1 + 2 + 1 + 1 + 4 + crypt_key_len;
        int length = ByteBuffer.wrap(_buffer, _offset + headerLen - 4 - crypt_key_len, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

        tmpbuffer = new byte[length];

        int seq = (ByteBuffer.wrap(_buffer, _offset + headerLen-4-crypt_key_len-2-2, 2).order(ByteOrder.LITTLE_ENDIAN).getShort())&0x0FFFF;
        char begin_hour = (char)ByteBuffer.wrap(_buffer, _offset+headerLen-4-crypt_key_len-1-1, 1).order(ByteOrder.LITTLE_ENDIAN).get();
        char end_hour = (char)ByteBuffer.wrap(_buffer, _offset+headerLen-4-crypt_key_len-1, 1).order(ByteOrder.LITTLE_ENDIAN).get();

        if (seq != 0 && seq != 1 && lastseq != 0 && seq != (lastseq+1)){
            LogUtil.i(TAG, String.format("[F]decode_log_file.py log seq:%d-%d is missing\n", lastseq+1, seq-1));
            _outbuffer.append(String.format("[F]decode_log_file.py log seq:%d-%d is missing\n", lastseq+1, seq-1));
        }

        if (seq != 0){
            retData.lastSeq = seq;
        }

        System.arraycopy(_buffer, _offset+headerLen, tmpbuffer,0, tmpbuffer.length);

        try{
            if (MAGIC_NO_COMPRESS_START1 == _buffer[_offset] ||  MAGIC_SYNC_ZSTD_START==_buffer[_offset]){
                LogUtil.i(TAG, "不是压缩开始位置一");
            } else if (MAGIC_COMPRESS_START2 == _buffer[_offset] || MAGIC_ASYNC_ZSTD_START==_buffer[_offset]) {
                if (privateKey != null && privateKey.length() > 0) {
                    byte[] byte_pubkey_x = new byte[32];
                    byte[] byte_pubkey_y = new byte[32];
                    ByteBuffer.wrap(_buffer, _offset+headerLen-crypt_key_len, crypt_key_len>>1).order(ByteOrder.LITTLE_ENDIAN).get(byte_pubkey_x);
                    ByteBuffer.wrap(_buffer, _offset+headerLen-(crypt_key_len>>1), crypt_key_len>>1).order(ByteOrder.LITTLE_ENDIAN).get(byte_pubkey_y);

                    String pubkey_x = CommonUtils.bytesToHexString(byte_pubkey_x);
                    String pubkey_y = CommonUtils.bytesToHexString(byte_pubkey_y);
                    String pubkey = String.format("04%s%s", pubkey_x, pubkey_y);

                    byte[] tea_key = ECDHUtils.getECDHKey(CommonUtils.hexStringToByteArray(pubkey), CommonUtils.hexStringToBytes(privateKey));


                    tmpbuffer = CommonUtils.tea_decrypt(tmpbuffer, tea_key);
                }
                if (MAGIC_COMPRESS_START2==_buffer[_offset]) {
                    tmpbuffer = CommonUtils.decompress(tmpbuffer);
                } else {
                    tmpbuffer = CommonUtils.zstdDecompress(tmpbuffer);
                }
            } else if(MAGIC_ASYNC_NO_CRYPT_ZSTD_START==_buffer[_offset]) {
                tmpbuffer = CommonUtils.zstdDecompress(tmpbuffer);
            } else if(MAGIC_COMPRESS_START == _buffer[_offset] ||
                MAGIC_COMPRESS_NO_CRYPT_START == _buffer[_offset]) {
                tmpbuffer = CommonUtils.decompress(tmpbuffer);
            } else if(MAGIC_COMPRESS_START1 == _buffer[_offset]) {
                LogUtil.i(TAG, "压缩开始位置一");
            } else {
                LogUtil.i(TAG, "未知位置");
            }
        }catch (Exception e){
            LogUtil.e("异常信息：" + ExceptionUtils.getStackTrace(e));
            _outbuffer.append(String.format("[F]decode_log_file.py decompress err, %s\n", e.toString()));
            retData.startPos = _offset + headerLen + length + 1;
            return retData;
        }

        if (tmpbuffer != null) {
            _outbuffer.append(new String(tmpbuffer, StandardCharsets.UTF_8));
        }

        retData.startPos = _offset + headerLen + length + 1;
        return retData;
    }

    public static void parseFile(String _file, String _outfile, String privateKey) throws IOException{
        if (_file == null || _file.length() == 0) {
            LogUtil.ei(TAG, "解密文件不能为空");
            return;
        }
        if (_outfile == null || _outfile.length() == 0) {
            _outfile = _file + ".log";
        }
        if (privateKey == null || privateKey.length() == 0) {
            LogUtil.ei(TAG, "解密私钥为空，只解压缩");
            privateKey = "";
        }

        FileInputStream fis = null;
        DataInputStream dis = null;

        OutputStream os = null;
        OutputStreamWriter writer = null;
        BufferedWriter bw = null;
        try {
             //创建输入流
            fis = new FileInputStream(_file);
            dis = new DataInputStream(fis);

            byte[] _buffer = new byte[dis.available()];

            dis.readFully(_buffer);

            int startpos = GetLogStartPos(_buffer, 2);

            if (-1 == startpos){
                return;
            }

            os = new FileOutputStream(_outfile);
            writer = new OutputStreamWriter(os);
            bw = new BufferedWriter(writer);

            RetData retData = new RetData(startpos, 0);
            while (true) {
                StringBuilder outBuffer = new StringBuilder();
                retData = DecodeBuffer(_buffer, retData.startPos, retData.lastSeq, outBuffer, privateKey);
                if (-1 == retData.startPos) {
                    break;
                }
                if (!outBuffer.isEmpty()) {
                    bw.write(outBuffer.toString());
                    bw.flush();
                }
            }
        } catch (IOException e) {
            LogUtil.e("异常信息：" + ExceptionUtils.getStackTrace(e));
            throw new IOException(e);
        }
        finally{
            try {
                if (fis!=null) {
                    fis.close();
                }
                if (dis!=null) {
                    dis.close();
                }
                if (os!=null){
                    os.close();
                }
                if (writer!=null){
                    writer.close();
                }
                if (bw!=null){
                    bw.close();
                }
            } catch (IOException e) {
                LogUtil.e("异常信息：" + ExceptionUtils.getStackTrace(e));
            }
        }
    }
}
