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
package tw.edu.sju.ee.eea.jni.eea.jni.mps_140801.iepe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import junit.framework.TestCase;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import tw.edu.sju.ee.eea.jni.mps.MPS140801IEPE;
import tw.edu.sju.ee.eea.jni.mps.MPSException;

/**
 *
 * @author Leo
 */
public class NewEmptyJUnitTest extends TestCase {

    public NewEmptyJUnitTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}
    public void test() throws IOException {
        SourceDataLine audioOut = null;
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info mixerInfo : mixerInfos) {
            if (!mixerInfo.getName().matches(".*HDMI.*")) {
                continue;
            }
            System.out.println(mixerInfo.getName());
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            Line.Info[] lineInfos = mixer.getSourceLineInfo();
            for (Line.Info lineInfo : lineInfos) {
                try {
                    audioOut = (SourceDataLine) mixer.getLine(lineInfo);
                    break;
                } catch (LineUnavailableException lue) {
                } catch (ClassCastException cce) {
                }
            }
            if (audioOut != null) {
                try {
                    AudioFormat currentFormat = audioOut.getFormat();
                    audioOut.open(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 128000, 8, 1, 1, 128000, currentFormat.isBigEndian()));
                    break;      //  Viable line found -- search no more!
                } catch (LineUnavailableException lue) {
                    audioOut = null;
                    continue;   //  Try another line or mixer
                }
            }
        }
        if (audioOut == null) {
            System.out.println("Unable Play Sounds");
            return;
        }

        audioOut.start();

        final byte[] sample = new byte[1024];
        final double inc = (Math.PI * 2) / 100; // 441 Hz
        double now = 0;
        short sineValue = 0;
//        while (true) {
//            for (int i = 0; i < 8; i++) {
//                sineValue = (short) (Math.sin(now) * 32767);
//                sample[0] = sample[2] = (byte) sineValue;
//                sample[1] = sample[3] = (byte) (sineValue >> 8);
//                now += inc;
//                sineValue = (short) (Math.sin(now) * 32767);
//                sample[4] = sample[6] = (byte) sineValue;
//                sample[5] = sample[7] = (byte) (sineValue >> 8);
//                now += inc;
//                for (int j = 0; j < 1; j++) {
////                    System.out.println(sineValue);
//                    audioOut.write(sample, 0, sample.length);
//                }
//            }
//            if (false) {
//                break;
//            }
//        }

        MPS140801IEPE mps = new MPS140801IEPE(0, 12800);
        try {
            mps.openDevice(0);
            mps.configure(128000);
            mps.start();
            double[][] dataBuffer = new double[8][sample.length];
            for (int tt = 0; tt < 10; tt++) {

                mps.dataIn(dataBuffer);
//                for (int i = 0; i < 8; i++) {
                    System.out.println(Arrays.toString(dataBuffer[1]));
//                }
//                System.out.println(tt);
                for (int i = 0; i < sample.length; i++) {
                    sample[i] = (byte) (dataBuffer[1][i] * 127);
                }
//                for (int i = 0; i < sample.length; i++) {
//                    System.out.println(sample[i]);
//                };
                audioOut.write(sample, 0, sample.length);
//        System.exit(0);
            }
            audioOut.flush();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(NewEmptyJUnitTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            mps.stop();
            mps.closeDevice();
        } catch (MPSException ex) {
            Logger.getLogger(NewEmptyJUnitTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
