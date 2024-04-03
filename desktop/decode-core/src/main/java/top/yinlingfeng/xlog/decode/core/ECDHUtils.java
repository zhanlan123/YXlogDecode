package top.yinlingfeng.xlog.decode.core;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import javax.crypto.KeyAgreement;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

/**
  *
  * 用途：ECDH算法操作
  * 创建时间: 2024/4/3 10:13
  * @author  yinxueqin@rd.hgits.cn
  * @author  更新者：
  * 更新时间:
  * 更新说明：
  * @since   1.0
  *
  */
public class ECDHUtils {

    public static void init(){
        Security.addProvider(new BouncyCastleProvider());
    }

    public static PublicKey loadPublicKey(byte[] data) throws Exception {
        ECParameterSpec params = ECNamedCurveTable.getParameterSpec("secp256k1");
        ECPublicKeySpec pubKey = new ECPublicKeySpec(
                params.getCurve().decodePoint(data), params);
        KeyFactory kf = KeyFactory.getInstance("EC", "BC");
        return kf.generatePublic(pubKey);
    }

    public static PrivateKey loadPrivateKey(byte[] data) throws Exception {
        ECParameterSpec params = ECNamedCurveTable.getParameterSpec("secp256k1");
        ECPrivateKeySpec prvkey = new ECPrivateKeySpec(new BigInteger(1, data), params);
        KeyFactory kf = KeyFactory.getInstance("EC", "BC");
        return kf.generatePrivate(prvkey);
    }

    public static byte[] getECDHKey(byte[] pubkey, byte[] prvkey) throws Exception{
        KeyAgreement ka = KeyAgreement.getInstance("ECDH", "BC");
        PrivateKey prvk = loadPrivateKey(prvkey);
        PublicKey pubk = loadPublicKey(pubkey);
        ka.init(prvk);
        ka.doPhase(pubk, true);
        return ka.generateSecret();
    }

    /**
     * 生成密钥
     * @return 返回私钥和公钥：String[0]:私钥，String[1]:公钥
     * @throws NoSuchAlgorithmException 无算法异常
     * @throws NoSuchProviderException 无密钥算法提供者异常
     * @throws InvalidAlgorithmParameterException 算法无效异常
     */
    public static String[] genECDHKey() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        // 指定 secp256k1 曲线参数
        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");

        // 生成密钥对
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
        keyPairGenerator.initialize(spec);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // 获取私钥
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
        // 获取公钥
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();

        String[] genKey = new String[2];
        genKey[0] = privateKey.getD().toString(16);
        genKey[1] = publicKey.getQ().getRawXCoord().toString() + publicKey.getQ().getRawYCoord().toString();
        return genKey;
    }

    /**
     * 根据私钥推算公钥
     * @param privateKeyHexStr 私钥16进制字符串
     * @return 公钥16进制字符串
     * @throws Exception 异常
     */
    public static String derivePublicKey(String privateKeyHexStr) throws Exception {
        ECPrivateKey privateKey = (ECPrivateKey) loadPrivateKey(CommonUtils.hexStringToBytes(privateKeyHexStr));
        // 选择椭圆曲线参数
        ECNamedCurveParameterSpec curveParams = ECNamedCurveTable.getParameterSpec("secp256k1");
        ECParameterSpec params = new ECParameterSpec(
                curveParams.getCurve(),
                curveParams.getG(),
                curveParams.getN(),
                curveParams.getH()
        );
        ECPoint pointQ = curveParams.getG().multiply(privateKey.getD());
        ECPublicKeySpec pubSpec = new ECPublicKeySpec(pointQ, params);
        KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
        ECPublicKey dPublicKey = (ECPublicKey) keyFactory.generatePublic(pubSpec);
        return dPublicKey.getQ().getRawXCoord().toString() + dPublicKey.getQ().getRawYCoord().toString();
    }
}
