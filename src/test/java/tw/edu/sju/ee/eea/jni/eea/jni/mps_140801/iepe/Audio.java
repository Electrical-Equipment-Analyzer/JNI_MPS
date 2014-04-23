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
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import tw.edu.sju.ee.eea.io.QuantizationStream;
import tw.edu.sju.ee.eea.io.VoltageInputStream;

/**
 *
 * @author Leo
 */
public class Audio extends Thread {

    private SourceDataLine audioOut = null;
    private PipedInputStream pipeIn;
    private PipedOutputStream pipeOut;

    public Audio() throws IOException {
        this.pipeIn = new PipedInputStream(1024);
        this.pipeOut = new PipedOutputStream(pipeIn);
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
                    audioOut.open(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 1, 2, 16000, currentFormat.isBigEndian()));
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
    }

    public void startPlay() {
        super.start();
        audioOut.start();
    }

    public void stopPlay() {
        super.stop();
        audioOut.stop();
    }

    public PipedOutputStream getPipeOut() {
        return pipeOut;
    }

    @Override
    public void run() {
        VoltageInputStream vi = new VoltageInputStream(pipeIn);
        while (true) {
            try {
                byte[] buffer = QuantizationStream.quantization(vi.readVoltage(), 16);
                audioOut.write(buffer, 0, buffer.length);
            } catch (IOException ex) {
            }
        }
    }

}
