/*
 * Copyright (c) 2017 Schibsted Media Group. All rights reserved.
 */
package com.schibsted.security.strongbox.archaius;

import com.netflix.config.ConfigurationManager;
import com.schibsted.security.strongbox.sdk.testing.SecretEntryMock;
import com.schibsted.security.strongbox.sdk.types.*;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * @author zamzterz
 */
public class InMemoryPlaintextSecretDerivedTest extends ArchaiusTestBase {
    @Test
    public void testSecretIsDecryptedBeforeBeingPassedToDecoder() {
        String secretValue = "test_secret_value";
        long version = 1;
        RawSecretEntry rawSecret = new RawSecretEntry(secretIdentifier,
                version,
                State.ENABLED,
                Optional.empty(),
                Optional.empty(),
                new SecretValue(secretValue, SecretType.OPAQUE).asByteArray());
        SecretEntry secretEntry = new SecretEntryMock.Builder().secretValue(secretValue).build();
        when(mockSecretsGroup.decrypt(rawSecret, secretIdentifier, version)).thenReturn(secretEntry);

        InMemoryPlaintextSecretDerived<Integer> p = new InMemoryPlaintextSecretDerived<>(mockSecretsGroup,
                secretIdentifier,
                String::length);
        ConfigurationManager.getConfigInstance().setProperty(secretIdentifier.name, rawSecret.toJsonBlob());
        assertEquals(p.getValue().intValue(), secretValue.length());
    }
}
