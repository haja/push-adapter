package at.sbaresearch.mqtt4android.registration.crypto;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static io.vavr.API.*;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class ClientKeyFactory {

  private static final String SECURITY_PROVIDER = "BC";
  private static final String KEY_ALGORITHM = "EC";
  private static final String SIGN_ALGORITHM = "SHA256withECDSA";
  private static final int KEYSIZE = 384;

  private static final DefaultSignatureAlgorithmIdentifierFinder sigAlgFinder =
      new DefaultSignatureAlgorithmIdentifierFinder();
  private static final DefaultDigestAlgorithmIdentifierFinder digAlgFinder =
      new DefaultDigestAlgorithmIdentifierFinder();

  AsymmetricKeyParameter caKey;
  X509CertificateHolder caCert;
  Option<File> keyPath;

  // TODO save and reload serial generator state
  AtomicInteger serialGenerator = new AtomicInteger(1);

  public ClientKeyFactory(PrivateKey caKey, java.security.cert.Certificate caCert,
      @Value("${ssl.debug.writeKeysPath}") String keyPath)
      throws IOException, CertificateEncodingException {
    this.caKey = toBCstructure(caKey);
    this.caCert = toBCstructure(caCert);

    if (!StringUtils.isEmpty(keyPath)) {
      this.keyPath = Some(new File(keyPath));
    } else {
      this.keyPath = None();
    }
  }

  public ClientKeys createSignedKey(String clientId) throws Exception {
    KeyPair keypair = generateKeyPair();
    val subjectPubKey = toBCstructure(keypair.getPublic());

    val cert = sign(subjectPubKey, clientId);

    debugWriteKeys(keypair, cert);

    return new ClientKeys(keypair.getPrivate(), cert.toASN1Structure());
  }

  private KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
    // TODO do this with BC?
    val keyGen = KeyPairGenerator.getInstance(KEY_ALGORITHM, SECURITY_PROVIDER);
    keyGen.initialize(KEYSIZE, new SecureRandom());
    return keyGen.genKeyPair();
  }

  private X509CertificateHolder sign(AsymmetricKeyParameter pubKeyInfo,
      String clientId)
      throws Exception {

    // TODO does this work?

    // TODO this can be done in ctor
    AlgorithmIdentifier sigAlg = sigAlgFinder.find(SIGN_ALGORITHM);
    ContentSigner sigGen =
        new BcECContentSignerBuilder(sigAlg, digAlgFinder.find(sigAlg)).build(caKey);

    Date startDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
    Date endDate = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);
    X509v3CertificateBuilder certGen = new BcX509v3CertificateBuilder(
        caCert,
        BigInteger.valueOf(serialGenerator.incrementAndGet()),
        startDate, endDate,
        toX500Name(clientId), pubKeyInfo);

    return certGen.build(sigGen);
  }

  /**
   * write out keys and certificates of clients to allow ssl testing/debugging (e.g. with openssl s_client).
   *
   * enable this with config parameter.
   */
  private void debugWriteKeys(KeyPair keypair, X509CertificateHolder cert) {
    keyPath.forEach(path -> {
      log.warn("writing key+certificate to filesystem at {}", path);
      val certPath = new File(path, "cert-" + serialGenerator.get());
      try(val fos = new FileOutputStream(certPath)) {
        val certificate = (X509Certificate) CertificateFactory.getInstance("X.509")
            .generateCertificate(new ByteArrayInputStream(cert.getEncoded()));
        fos.write(certificate.getEncoded());
      } catch (IOException | CertificateException e) {
        e.printStackTrace();
      }
      val keyPath = new File(path, "key-" + serialGenerator.get());
      try(val fos = new FileOutputStream(keyPath)) {
        fos.write(keypair.getPrivate().getEncoded());
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  private X500Name toX500Name(String clientId) {
    X500NameBuilder builder = new X500NameBuilder(RFC4519Style.INSTANCE);
    builder.addRDN(RFC4519Style.cn, clientId);
    return builder.build();
  }

  private AsymmetricKeyParameter toBCstructure(PublicKey key) throws IOException {
    return PublicKeyFactory.createKey(key.getEncoded());
  }

  private AsymmetricKeyParameter toBCstructure(PrivateKey key) throws IOException {
    return PrivateKeyFactory.createKey(key.getEncoded());
  }

  private X509CertificateHolder toBCstructure(java.security.cert.Certificate cert)
      throws CertificateEncodingException, IOException {
    return new X509CertificateHolder(cert.getEncoded());
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
