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
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import tw.edu.sju.ee.eea.util.iepe.IEPEPlayer;

/**
 *
 * @author Leo
 */
public class PlaySystem {

    public static void main(String[] args) {
        FileInputStream fis = null;
        try {
            File file = new File("rec.iepe");
            file.setReadOnly();
            fis = new FileInputStream(file);
//            VoltageInputStream vi = new VoltageInputStream(fis);
            
            IEPEPlayer player = new IEPEPlayer(16000, 16, 1, 2, 16000);
            Thread playThread = new Thread(player);
            playThread.start();
            
            OutputStream out = player.getOutputStream();
//            fis.skip(1800000);
            for (int i = 0; i < 5000; i++) {
                byte[] buffer = new byte[1024];
                fis.read(buffer);
                out.write(buffer, 0, buffer.length);
            }
//            System.out.println("Pause");
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(PlaySystem.class.getName()).log(Level.SEVERE, null, ex);
//            }
            for (int i = 0; i < 5000; i++) {
                byte[] buffer = new byte[1024];
                fis.read(buffer);
                out.write(buffer, 0, buffer.length);
            }
            playThread.stop();
            
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PlaySystem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PlaySystem.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(PlaySystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
