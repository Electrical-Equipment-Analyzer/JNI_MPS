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
package tw.edu.sju.ee.eea.jni.eea.jni.mps140801;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Exceptions;
import tw.edu.sju.ee.eea.jni.mps.MPS140801;
import tw.edu.sju.ee.eea.jni.mps.MPSException;
import tw.edu.sju.ee.eea.utils.io.tools.EEAException;
import tw.edu.sju.ee.eea.utils.io.tools.EEAInput;

/**
 *
 * @author Leo
 */
public class Re {

    public static void main(String[] args) {
        MPS140801 mps = new MPS140801(0, 12800);
        try {
            mps.openDevice(0);
            System.out.println(mps.getDeviceId());
            mps.configure(16000);
            mps.start();
            try {
                for (int i = 0; i < 1; i++) {
                    double[][] read = mps.read(10);
                    for (int j = 0; j < read.length; j++) {
                        System.out.println(Arrays.toString(read[j]));
                    }
                }
            } catch (EEAException ex) {
                Exceptions.printStackTrace(ex);
            }
            mps.stop();
            mps.closeDevice();
        } catch (MPSException ex) {
            Logger.getLogger(NewEmptyJUnitTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
