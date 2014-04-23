/*
 * Copyright (C) 2014 Leo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package tw.edu.sju.ee.eea.io;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Leo
 */
public class QuantizationStream extends InputStream {

    private VoltageInputStream voltage;
    private byte[] buffer;
    private int index = Integer.MAX_VALUE;

    public QuantizationStream(VoltageInputStream voltage, int bytes) {
        this.voltage = voltage;
        this.buffer = new byte[bytes];
    }

    @Override
    public synchronized int read() throws IOException {
        if (this.index >= this.buffer.length) {
            conv();
        }
        return this.buffer[index++];
    }

    private void conv() throws IOException {
        long max = 0;
        for (int i = 0; i < this.buffer.length; i++) {
            max = (max << 8) | 0xFF;
        }
        max >>= 1;
        long data = (long) (voltage.readVoltage() * max);
        for (int i = 0; i < this.buffer.length; i++) {
            this.buffer[i] = (byte) (data >> (8 * i));
        }
        index = 0;
    }
    
    public static  byte[] quantization(double value, int sampleSizeInBits) {
        byte[] buffer =  new byte[sampleSizeInBits/8];
        long max = 0;
        for (int i = 0; i < buffer.length; i++) {
            max = (max << 8) | 0xFF;
        }
        max >>= 1;
        long data = (long) (value * max);
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte) (data >> (8 * i));
        }
        return buffer;
    }

}
