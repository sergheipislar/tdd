/*
 * COPYRIGHT: FREQUENTIS AG. All rights reserved.
 *            Registered with Commercial Court Vienna,
 *            reg.no. FN 72.115b.
 */
package com.frequentis.tdd.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileSystemStorage implements FileStorage{
    private final String filePath;

    @Autowired
    public FileSystemStorage(final @Value("${com.frequentis.tdd.filePath}") String filePath){
        this.filePath = filePath;
    }

    @Override
    public boolean exists() {
        return Files.exists(Paths.get(filePath));
    }

    @Override
    public void store(final String name, final byte[] bytes) throws IOException {
        Files.write(Paths.get(filePath, name), bytes);
    }
}
