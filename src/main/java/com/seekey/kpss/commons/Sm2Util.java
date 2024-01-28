package com.seekey.kpss.commons;

import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * SM2加解密工具类
 */
@Slf4j
public class Sm2Util {

    static {
        // 加入BouncyCastleProvider支持
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * SM2加密
     *
     * @param publicKeyStr 公钥
     * @param data         待加密数据
     * @return 加密后的数据
     */
    public static String encrypt(String publicKeyStr, String data) {
        PublicKey publicKey = null;

        try {
            byte[] keyBytes = Base64.decodeBase64(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            publicKey = keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            log.warn("SM2字符串公钥转换异常：" + e.getMessage());
        }

        ECPublicKeyParameters ecPublicKeyParameters = getEcPublicKeyParameters(publicKey);

        SM2Engine sm2Engine = new SM2Engine();
        sm2Engine.init(true, new ParametersWithRandom(ecPublicKeyParameters, new SecureRandom()));
        byte[] arrayOfBytes = null;

        try {
            byte[] in = data.getBytes(StandardCharsets.UTF_8);
            arrayOfBytes = sm2Engine.processBlock(in, 0, in.length);
        } catch (Exception e) {
            log.warn("SM2加密时出现异常:" + e.getMessage());
        }

        return Base64.encodeBase64String(arrayOfBytes);
    }

    private static ECPublicKeyParameters getEcPublicKeyParameters(PublicKey publicKey) {
        ECPublicKeyParameters ecPublicKeyParameters = null;
        if (publicKey instanceof BCECPublicKey) {
            BCECPublicKey bcecPublicKey = (BCECPublicKey) publicKey;
            ECParameterSpec ecParameterSpec = bcecPublicKey.getParameters();
            ECDomainParameters ecDomainParameters = new ECDomainParameters(ecParameterSpec.getCurve(), ecParameterSpec.getG(), ecParameterSpec.getN());
            ecPublicKeyParameters = new ECPublicKeyParameters(bcecPublicKey.getQ(), ecDomainParameters);
        }
        return ecPublicKeyParameters;
    }

    /**
     * SM2解密
     *
     * @param privateKeyStr 私钥
     * @param cipherData    待解密数据
     * @return 解密后的数据
     */
    public static String decrypt(String privateKeyStr, String cipherData) {
        SM2Engine sm2Engine = getSm2Engine(privateKeyStr);
        String result = null;

        try {
            byte[] in = Base64.decodeBase64(cipherData);
            byte[] arrayOfBytes = new byte[0];
            if (sm2Engine != null) {
                arrayOfBytes = sm2Engine.processBlock(in, 0, in.length);
            }
            result = new String(arrayOfBytes, StandardCharsets.UTF_8);
        } catch (Exception var12) {
            log.warn("SM2解密时出现异常");
        }

        return result;
    }

    /**
     * 生成随机SM2密钥对
     *
     * @return 密钥对
     */
    public static KeyPair generateSm2KeyPair() {
        return SecureUtil.generateKeyPair("sm2");
    }

    public static void main(String[] args) {
        // String appKey = RandomUtil.randomString(32);
        // System.out.println(appKey);
        // Digester digester = SecureUtil.md5();
        // System.out.println(Base64.encodeBase64String(digester.digest(appKey)));

        String appKey = "加密信息~";
        System.out.println("sourceCode: " + appKey);
        KeyPair keyPair = generateSm2KeyPair();
        String privateKey = Base64.encodeBase64String(keyPair.getPrivate().getEncoded());
        String publicKey = Base64.encodeBase64String(keyPair.getPublic().getEncoded());
        System.out.println("privateKey: " + privateKey);
        System.out.println("publicKey: " + publicKey);
        String encrypt = encrypt(publicKey, appKey);
        System.out.println("encryptCode: " + encrypt);
        System.out.println("decryptCode: " + decrypt(privateKey, encrypt));

    }

    private static SM2Engine getSm2Engine(String privateKeyStr) {
        PrivateKey privateKey = null;

        try {
            byte[] keyBytes = Base64.decodeBase64(privateKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            log.warn("SM2字符串私钥转换异常：" + e.getMessage());
        }

        if (privateKey != null) {
            return getSm2Engine((BCECPrivateKey) privateKey);
        }

        return null;
    }

    private static SM2Engine getSm2Engine(BCECPrivateKey privateKey) {
        ECParameterSpec ecParameterSpec = privateKey.getParameters();
        ECDomainParameters ecDomainParameters = new ECDomainParameters(ecParameterSpec.getCurve(), ecParameterSpec.getG(), ecParameterSpec.getN());
        ECPrivateKeyParameters ecPrivateKeyParameters = new ECPrivateKeyParameters(privateKey.getD(), ecDomainParameters);
        SM2Engine sm2Engine = new SM2Engine();
        sm2Engine.init(false, ecPrivateKeyParameters);
        return sm2Engine;
    }

}
