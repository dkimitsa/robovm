/*
 * Copyright 2016 Justin Shapcott.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.compiler.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author dkimitsa
 * Output stream that can be observed (monitored)
 */
public class ObservableOutputStream extends FilterOutputStream {
    public interface Observer {
        void observeWrite(byte[] b, int off, int len);
    }

    private final byte[] oneByteBuffer = new byte[1];
    private final Observer observer;

    public ObservableOutputStream(OutputStream out, Observer observer) {
        super(out);
        this.observer = observer;
    }

    @Override
    public void write(int b) throws IOException {
        oneByteBuffer[0] = (byte) (b & 0xFF);
        observer.observeWrite(oneByteBuffer, 0, 1);

        super.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (len > 0)
            observer.observeWrite(b, off, len);

        super.write(b, off, len);
    }
}
