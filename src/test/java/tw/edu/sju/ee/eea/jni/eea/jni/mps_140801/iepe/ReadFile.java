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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import tw.edu.sju.ee.eea.util.iepe.QuantizationStream;
import tw.edu.sju.ee.eea.util.iepe.VoltageInputStream;

/**
 *
 * @author Leo
 */
public class ReadFile {
    public static void main(String[] args) {
        FileInputStream fis = null;
        try {
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
                        audioOut.open(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000, 8, 1, 1, 16000, currentFormat.isBigEndian()));
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
            File file = new File("rec.iepe");
            file.setReadOnly();
            fis = new FileInputStream(file);
            VoltageInputStream vi = new VoltageInputStream(fis);
            QuantizationStream qs = new QuantizationStream(vi, 1);
            vi.skip(1500000);
            for (int i = 0; i < 100000000; i++) {
                
                byte[] buffer = new byte[2];
                qs.read(buffer);
//                byte[] buffer = QuantizationStream.quantization(vi.readVoltage(), 16);
//                byte[] left = QuantizationStream.quantization(vi_left.readVoltage(), 16);
//                byte[] right = QuantizationStream.quantization(vi_right.readVoltage(), 16);
//                byte[] buffer = new byte[left.length + right.length];
//                System.arraycopy(left, 0, buffer, 0, left.length);
//                System.arraycopy(right, 0, buffer, left.length, right.length);
//                int read = QuantizationStream.quantization(vi.readVoltage(), 16);
//                System.out.println("\t" + Arrays.toString(buffer));
                audioOut.write(buffer, 0, buffer.length);
            }
            System.out.println("Stop");
            audioOut.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
