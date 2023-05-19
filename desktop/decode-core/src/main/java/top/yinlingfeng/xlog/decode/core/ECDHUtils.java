package top.yinlingfeng.xlog.decode.core;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import javax.crypto.KeyAgreement;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

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
        byte[] secret = ka.generateSecret();
        return secret;
    }
}
