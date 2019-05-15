/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.jraft.util;

import java.nio.ByteBuffer;

/**
 * A byte buffer collector that will expand automatically.
 *
 * @author dennis
 */
public final class ByteBufferCollector {

    private ByteBuffer buffer;

    public int capacity() {
        return this.buffer != null ? this.buffer.capacity() : 0;
    }

    public void expandIfNecessary() {
        if (!hasRemaining()) {
            getBuffer(Utils.RAFT_DATA_BUF_SIZE);
        }
    }

    public void expandAtMost(final int atMostBytes) {
        if (this.buffer == null) {
            this.buffer = Utils.allocate(atMostBytes);
        } else {
            this.buffer = Utils.expandByteBufferAtMost(this.buffer, atMostBytes);
        }
    }

    public boolean hasRemaining() {
        return this.buffer != null && this.buffer.hasRemaining();
    }

    private ByteBufferCollector(final int size) {
        if (size > 0) {
            this.buffer = Utils.allocate(size);
        }
    }

    public static ByteBufferCollector allocate(final int size) {
        return new ByteBufferCollector(size);
    }

    public static ByteBufferCollector allocate() {
        return new ByteBufferCollector(Utils.RAFT_DATA_BUF_SIZE);
    }

    private ByteBuffer getBuffer(final int expectSize) {
        if (this.buffer == null) {
            this.buffer = Utils.allocate(expectSize);
        } else if (this.buffer.remaining() < expectSize) {
            this.buffer = Utils.expandByteBufferAtLeast(this.buffer, expectSize);
        }
        return this.buffer;
    }

    public void put(final ByteBuffer buf) {
        getBuffer(buf.remaining()).put(buf);
    }

    public void put(final byte[] bs) {
        getBuffer(bs.length).put(bs);
    }

    public void setBuffer(final ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public ByteBuffer getBuffer() {
        return this.buffer;
    }
}