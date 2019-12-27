package com.project.hashnote.encoders;

import javax.crypto.BadPaddingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;

public interface MessageEncoder {
    byte[] encode(byte[] message);
    byte[] decode(byte[] message) throws BadPaddingException;
    byte[] getPrivateKey();
    byte[] getInitVector();
}
