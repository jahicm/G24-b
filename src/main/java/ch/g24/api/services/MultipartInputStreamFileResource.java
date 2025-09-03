package ch.g24.api.services;

import org.springframework.core.io.InputStreamResource;
import java.io.IOException;
import java.io.InputStream;

/**
 * Helper class to wrap a MultipartFile's InputStream as a Resource
 * for RestTemplate multipart upload.
 */
public class MultipartInputStreamFileResource extends InputStreamResource {

    private final String filename;

    public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
        super(inputStream);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public long contentLength() throws IOException {
        return -1; // we don't know the content length in advance
    }
}

