package com.xeiam.xchange.therock.service;

import java.math.BigInteger;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.HeaderParam;

import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.RestInvocation;

import com.xeiam.xchange.therock.TheRockAuthenticated;

public class TheRockDigest implements ParamsDigest {

  public static final String HMAC_SHA_512 = "HmacSHA512";

  private final Mac mac;

  public TheRockDigest(String secretKeyStr) {
    try {
      final SecretKey secretKey = new SecretKeySpec(secretKeyStr.getBytes(), HMAC_SHA_512);
      mac = Mac.getInstance(HMAC_SHA_512);
      mac.init(secretKey);
    } catch (Exception e) {
      throw new RuntimeException("Error initializing The Rock Signer", e);
    }
  }

  @Override
  public String digestParams(RestInvocation restInvocation) {
    final String nonce = restInvocation.getParamValue(HeaderParam.class, TheRockAuthenticated.X_TRT_NONCE).toString();
    mac.update(nonce.getBytes());
    mac.update(restInvocation.getInvocationUrl().getBytes());

    return String.format("%0128x", new BigInteger(1, mac.doFinal()));
  }
}