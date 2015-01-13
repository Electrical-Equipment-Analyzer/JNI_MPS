/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.edu.sju.ee.eea.jni.mps;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import tw.edu.sju.ee.eea.jni.util.NativeUtils;
import tw.edu.sju.ee.eea.utils.io.tools.EEADevice;
import tw.edu.sju.ee.eea.utils.io.tools.EEAException;

/**
 *
 * @author Leo
 */
public class MPS140801 implements EEADevice {

    public static final int CHANNEL = 8;

    private int deviceNumber;
    private int sampleRate;

    static {
        try {
            NativeUtils.loadLibraryFromJar("jniMPS");
            loadLibrary(NativeUtils.temp("libs/MPS-140801x64").getAbsolutePath());
        } catch (IOException ex) {
            Logger.getLogger(MPS140801.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MPSException ex) {
            Logger.getLogger(MPS140801.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //**************************************************************************
    //Java Object
    private long nativeStruct;

    public MPS140801(int deviceNumber, int sampleRate) {
        construct();
        this.deviceNumber = deviceNumber;
        this.sampleRate = sampleRate;
    }

    @Override
    protected void finalize() throws Throwable {
        this.destruct();
        super.finalize();
    }

    //**************************************************************************
    //Native Object
    private static native void loadLibrary(String path) throws MPSException;

    private native void construct();

    private native void destruct();

    public native boolean isAlive();

    //**************************************************************************
    public native void openDevice(int deviceNumber) throws MPSException;

    public native int getDeviceId() throws MPSException;

    public native void configure(int sampleRate) throws MPSException;

    public native void dataIn(double[][] dataBuffer) throws MPSException;

    public native void start() throws MPSException;

    public native void stop() throws MPSException;

    public native void closeDevice() throws MPSException;

    //**************************************************************************
    @Override
    public void openDevice() throws EEAException {
        this.openDevice(deviceNumber);
    }

    @Override
    public void configure() throws EEAException {
        this.configure(sampleRate);
    }

    @Override
    public double[][] read(int length) throws EEAException {
        double[][] data = new double[MPS140801.CHANNEL][length];
        dataIn(data);
        return data;
    }

    @Override
    public int getChannelLength() throws EEAException {
        return MPS140801.CHANNEL;
    }

}
