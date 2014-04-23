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
package tw.edu.sju.ee.eea.iepe;

import java.util.HashMap;
import java.util.Map;
import tw.edu.sju.ee.eea.jni.mps.MPSException;

/**
 *
 * @author Leo
 */
public interface IEPEDevice {

    public void openDevice() throws IEPEException;

    public void closeDevice() throws IEPEException;

    public int getDeviceId() throws IEPEException;

    public void configure() throws IEPEException;

    public double[][] read(int length) throws IEPEException;

    public void start() throws IEPEException;

    public void stop() throws IEPEException;

}
