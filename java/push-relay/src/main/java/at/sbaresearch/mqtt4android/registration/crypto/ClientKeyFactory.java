package at.sbaresearch.mqtt4android.registration.crypto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.bc.BcX509v3CertificateBuilder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcECContentSignerBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ClientKeyFactory {

  private static final String SECURITY_PROVIDER = "BC";
  private static final String KEY_ALGORITHM = "EC";
  private static final String SIGN_ALGORITHM = "SHA3-512WITHECDSA";
  private static final int KEYSIZE = 384;

  private static final DefaultSignatureAlgorithmIdentifierFinder sigAlgFinder =
      new DefaultSignatureAlgorithmIdentifierFinder();
  private static final DefaultDigestAlgorithmIdentifierFinder digAlgFinder =
      new DefaultDigestAlgorithmIdentifierFinder();

  static {
    X500NameBuilder builder = new X500NameBuilder(RFC4519Style.INSTANCE);
    builder.addRDN(RFC4519Style.cn, "relay");
    ISSUER = builder.build();
  }
  private static final X500Name ISSUER;

  PrivateKey caKey;

  // TODO save and reload serial generator state
  AtomicInteger serialGenerator = new AtomicInteger(1);

  public ClientKeys createSignedKey(String clientId) throws Exception {
    KeyPair keypair = generateKeyPair();
    val subjectPubKey = toBCstructure(keypair.getPublic());
    val caAlg = toBCstructure(caKey);

    val cert = sign(caAlg, subjectPubKey, clientId);

    return new ClientKeys(keypair.getPrivate(), cert.toASN1Structure());
  }

  private KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
    // TODO do this with BC?
    val keyGen = KeyPairGenerator.getInstance(KEY_ALGORITHM, SECURITY_PROVIDER);
    keyGen.initialize(KEYSIZE, new SecureRandom());
    return keyGen.genKeyPair();
  }

  private AsymmetricKeyParameter toBCstructure(PublicKey key) throws IOException {
    return PublicKeyFactory.createKey(key.getEncoded());
  }

  private AsymmetricKeyParameter toBCstructure(PrivateKey key) throws IOException {
    return PrivateKeyFactory.createKey(key.getEncoded());

  }

  // TODO pubkey and privKey are of same type, can we change this?
  private X509CertificateHolder sign(AsymmetricKeyParameter caKey, AsymmetricKeyParameter pubKeyInfo,
      String clientId)
      throws Exception {

    // TODO does this work?

    AlgorithmIdentifier sigAlg = sigAlgFinder.find(SIGN_ALGORITHM);
    ContentSigner sigGen =
        new BcECContentSignerBuilder(sigAlg, digAlgFinder.find(sigAlg)).build(caKey);

    Date startDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
    Date endDate = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);
    X509v3CertificateBuilder certGen = new BcX509v3CertificateBuilder(
        ISSUER,
        BigInteger.valueOf(serialGenerator.incrementAndGet()),
        startDate, endDate,
        toX500Name(clientId), pubKeyInfo);

    return certGen.build(sigGen);
  }

  private X500Name toX500Name(String clientId) {
    X500NameBuilder builder = new X500NameBuilder(RFC4519Style.INSTANCE);
    builder.addRDN(RFC4519Style.cn, clientId);
    return builder.build();
  }

  @Getter
  @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
  public static class ClientKeys {
    byte[] encodedPrivateKey;
    byte[] encodedCert;

    private ClientKeys(PrivateKey privateKey, Certificate cert) throws IOException {
      this.encodedPrivateKey = privateKey.getEncoded();
      this.encodedCert = cert.getEncoded();
    }
  }
}
