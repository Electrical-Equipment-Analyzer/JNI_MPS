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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import tw.edu.sju.ee.eea.util.iepe.IEPEException;
import tw.edu.sju.ee.eea.util.iepe.IEPEInput;
import tw.edu.sju.ee.eea.util.iepe.io.QuantizationStream;
import tw.edu.sju.ee.eea.util.iepe.io.IepeInputStream;
import tw.edu.sju.ee.eea.jni.mps.MPS140801IEPE;

/**
 *
 * @author Leo
 */
public class TestStream {

    public static void main(String[] args) {
        try {
            IEPEInput iepe = new IEPEInput(new MPS140801IEPE(0, 128000), new int[]{1, 2}, 512);
            iepe.startIEPE();

//            VoltageInputStream vi = new VoltageInputStream(iepe.getIepeStreams(0));
            IepeInputStream vi_left = iepe.getIepeStreams(0);
            IepeInputStream vi_right = iepe.getIepeStreams(1);
//            for (int i = 0; i < 100; i++) {
//                System.out.println(vi.readVoltage());
//            }
//            QuantizationStream qs = new QuantizationStream(vi, 2);
//            for (int i = 0; i < 100; i++) {
//                byte[] buffer = new byte[2];
//                qs.read(buffer);
//                short aa = (short) ((buffer[0]) & 0xFF);
//                aa |= (buffer[1] << 8) & 0xFF00;
//                System.out.println(aa);
//            }

//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(TestStream.class.getName()).log(Level.SEVERE, null, ex);
//            }
            SourceDataLine audioOut = null;
//            Clip audioOut = null;
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
                        audioOut.open(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 128000, 16, 2, 4, 128000, currentFormat.isBigEndian()));
//                        audioOut.open(new AudioInputStream(qs, new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 128000, 8, 1, 1, 128000, currentFormat.isBigEndian()), 10240000));
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

//            for (int i = 0; i < 10240; i++) {
//                System.out.println(i + "\t" + qs.read());
//            }
            QuantizationStream qs_left = new QuantizationStream(vi_left, 16, 0.5);
            QuantizationStream qs_right = new QuantizationStream(vi_right, 16, 0.5);

            for (int i = 0; i < 1000000; i++) {
//                byte[] buffer = QuantizationStream.quantization(vi.readVoltage(), 16);
                byte[] left = qs_left.readQuantization();
                byte[] right = qs_right.readQuantization();
                byte[] buffer = new byte[left.length + right.length];
                System.arraycopy(left, 0, buffer, 0, left.length);
                System.arraycopy(right, 0, buffer, left.length, right.length);
//                int read = QuantizationStream.quantization(vi.readVoltage(), 16);
//                System.out.println("\t" + Arrays.toString(buffer));
                audioOut.write(buffer, 0, buffer.length);
            }
            System.out.println("Stop");
            audioOut.close();
            iepe.stopIEPE();

        } catch (IEPEException ex) {
            Logger.getLogger(TestStream.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestStream.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
