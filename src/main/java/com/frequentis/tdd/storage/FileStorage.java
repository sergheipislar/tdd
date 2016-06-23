/*
 * COPYRIGHT: FREQUENTIS AG. All rights reserved.
 *            Registered with Commercial Court Vienna,
 *            reg.no. FN 72.115b.
 */
package com.frequentis.tdd.storage;

import java.io.IOException;

public interface FileStorage {
    boolean exists();

    void store(String name, byte[] bytes) throws IOException;
}
