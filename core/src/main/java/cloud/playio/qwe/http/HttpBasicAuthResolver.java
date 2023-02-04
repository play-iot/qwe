package cloud.playio.qwe.http;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import cloud.playio.qwe.auth.credential.BasicCredential;

import lombok.NonNull;

public class HttpBasicAuthResolver extends HttpCredentialAddressResolver<BasicCredential> {

    @Override
    public @NonNull String resolveHeader(@NonNull BasicCredential credential) {
        final byte[] combine = (credential.getUser() + ":" + credential.getPassword()).getBytes(StandardCharsets.UTF_8);
        return credential.getHeaderAuthType() + " " + Base64.getEncoder().encodeToString(combine);
    }

    protected String computeUrlCredential(@NonNull BasicCredential credential) {
        return credential.getUser() + ":" + credential.getPassword() + "@";
    }

}
