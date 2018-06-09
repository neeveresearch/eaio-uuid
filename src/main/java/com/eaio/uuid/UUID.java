/**
 * Copyright 2016 Neeve Research, LLC
 *
 * This product includes software developed at Neeve Research, LLC
 * (http://www.neeveresearch.com/) as well as software licenced to
 * Neeve Research, LLC under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * Neeve Research licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.eaio.uuid;

import java.io.*;

import com.eaio.util.lang.Hex;

/**
 * Creates UUIDs according to the DCE Universal Token Identifier specification.
 * <p>
 * All you need to know:
 * <pre>
 * UUID u = new UUID();
 * </pre>
 *
 * @see <a href="http://www.opengroup.org/onlinepubs/9629399/apdxa.htm">
 * http://www.opengroup.org/onlinepubs/9629399/apdxa.htm
 * </a>
 * @see <a href="http://www.uddi.org/pubs/draft-leach-uuids-guids-01.txt">
 * http://www.uddi.org/pubs/draft-leach-uuids-guids-01.txt
 * </a>
 * @see <a href="http://johannburkard.de/software/uuid/">UUID</a>
 * @author <a href="mailto:jb@eaio.de">Johann Burkard</a>
 * @version $Id: UUID.java 4688 2012-03-09 14:49:49Z johann $
 */
public class UUID implements Comparable<UUID>, Cloneable {

    /**
     * Hasn't ever changed between versions.
     */
    static final long serialVersionUID = 7435962790062944603L;

    /**
     * The time field of the UUID.
     */
    public long time;

    /**
     * The clock sequence and node field of the UUID.
     */
    public long clockSeqAndNode;

    /**
     * Constructor for UUID. Constructs a new, unique UUID.
     *
     * @see UUIDGen#newTime()
     * @see UUIDGen#getClockSeqAndNode()
     */
    public UUID() {
        this(UUIDGen.newTime(), UUIDGen.getClockSeqAndNode());
    }

    /**
     * Constructor for UUID. Constructs a UUID from two <code>long</code> values.
     *
     * @param time the upper 64 bits
     * @param clockSeqAndNode the lower 64 bits
     */
    public UUID(long time, long clockSeqAndNode) {
        this.time = time;
        this.clockSeqAndNode = clockSeqAndNode;
    }

    /**
     * Copy constructor for UUID. Values of the given UUID are copied.
     *
     * @param u the UUID, may not be <code>null</code>
     */
    public UUID(UUID u) {
        this(u.time, u.clockSeqAndNode);
    }

    /**
     * Parses a textual representation of a UUID.
     * <p>
     * No validation is performed. If the {@link CharSequence} is shorter than 36 characters,
     * {@link ArrayIndexOutOfBoundsException}s will be thrown.
     *
     * @param s the {@link CharSequence}, may not be <code>null</code>
     */
    public UUID(CharSequence s) {
        this(Hex.parseLong(s.subSequence(0, 18)), Hex.parseLong(s.subSequence(
                                                                              19, 36)));
    }

    /**
     * Compares this UUID to another Object. Throws a {@link ClassCastException} if
     * the other Object is not an instance of the UUID class. Returns a value
     * smaller than zero if the other UUID is "larger" than this UUID and a value
     * larger than zero if the other UUID is "smaller" than this UUID.
     *
     * @param t the other UUID, may not be <code>null</code>
     * @return a value &lt; 0, 0 or a value &gt; 0
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(UUID t) {
        if (this == t) {
            return 0;
        }
        if (time > t.time) {
            return 1;
        }
        if (time < t.time) {
            return -1;
        }
        if (clockSeqAndNode > t.clockSeqAndNode) {
            return 1;
        }
        if (clockSeqAndNode < t.clockSeqAndNode) {
            return -1;
        }
        return 0;
    }

    /**
     * Returns this UUID as a String.
     *
     * @return a String, never <code>null</code>
     * @see java.lang.Object#toString()
     * @see #toAppendable(Appendable)
     */
    @Override
    public final String toString() {
        return toAppendable(null).toString();
    }

    /**
     * Appends a String representation of this to the given {@link StringBuffer} or
     * creates a new one if none is given.
     *
     * @param in the StringBuffer to append to, may be <code>null</code>
     * @return a StringBuffer, never <code>null</code>
     * @see #toAppendable(Appendable)
     */
    public StringBuffer toStringBuffer(StringBuffer in) {
        StringBuffer out = in;
        if (out == null) {
            out = new StringBuffer(36);
        }
        else {
            out.ensureCapacity(out.length() + 36);
        }
        return (StringBuffer)toAppendable(out);
    }

    /**
     * Appends a String representation of this object to the given {@link Appendable} object.
     * <p>
     * For reasons I'll probably never understand, Sun has decided to have a number of I/O classes implement
     * Appendable which forced them to destroy an otherwise nice and simple interface with {@link IOException}s.
     * <p>
     * I decided to ignore any possible IOExceptions in this method.
     *
     * @param a the Appendable object, may be <code>null</code>
     * @return an Appendable object, defaults to a {@link StringBuilder} if <code>a</code> is <code>null</code>
     */
    public Appendable toAppendable(Appendable a) {
        Appendable out = a;
        if (out == null) {
            out = new StringBuilder(36);
        }
        try {
            Hex.append(out, (int)(time >> 32)).append('-');
            Hex.append(out, (short)(time >> 16)).append('-');
            Hex.append(out, (short)time).append('-');
            Hex.append(out, (short)(clockSeqAndNode >> 48)).append('-');
            Hex.append(out, clockSeqAndNode, 12);
        }
        catch (IOException ex) {
            // What were they thinking?
        }
        return out;
    }

    /**
     * Returns a hash code of this UUID. The hash code is calculated by XOR'ing the
     * upper 32 bits of the time and clockSeqAndNode fields and the lower 32 bits of
     * the time and clockSeqAndNode fields.
     *
     * @return an <code>int</code> representing the hash code
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (int)((time >> 32) ^ time ^ (clockSeqAndNode >> 32) ^ clockSeqAndNode);
    }

    /**
     * Clones this UUID.
     *
     * @return a new UUID with identical values, never <code>null</code>
     */
    @Override
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException ex) {
            // One of Sun's most epic fails.
            return null;
        }
    }

    /**
     * Returns the time field of the UUID (upper 64 bits).
     *
     * @return the time field
     */
    public final long getTime() {
        return time;
    }

    /**
     * Returns the clock and node field of the UUID (lower 64 bits).
     *
     * @return the clockSeqAndNode field
     */
    public final long getClockSeqAndNode() {
        return clockSeqAndNode;
    }

    /**
     * Compares two Objects for equality.
     *
     * @see java.lang.Object#equals(Object)
     * @param obj the Object to compare this UUID with, may be <code>null</code>
     * @return <code>true</code> if the other Object is equal to this UUID,
     * <code>false</code> if not
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UUID)) {
            return false;
        }
        return compareTo((UUID)obj) == 0;
    }

    /**
     * Returns the nil UUID (a UUID whose values are both set to zero).
     * <p>
     * Starting with version 2.0, this method does return a new UUID instance every
     * time it is called. Earlier versions returned one instance. This has now been
     * changed because this UUID has public, non-final instance fields. Returning a
     * new instance is therefore more safe.
     *
     * @return a nil UUID, never <code>null</code>
     */
    public static UUID nilUUID() {
        return new UUID(0, 0);
    }

}
