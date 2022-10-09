package com.fadyfaheem;

import com.pyramidacceptors.ptalk.api.APIConstants;
import com.pyramidacceptors.ptalk.api.PyramidAcceptor;
import com.pyramidacceptors.ptalk.api.PyramidDeviceException;
import com.pyramidacceptors.ptalk.api.RS232Configuration;

public class BillAcceptor {
    static PyramidAcceptor acceptor;

    public static void connect(com.pyramidacceptors.ptalk.api.event.PTalkEventListener listener) {
        try {
            acceptor = PyramidAcceptor.valueOfRS232("/dev/ttyUSB_DEVICE1");
            acceptor.connect();
            acceptor.addChangeListener(listener);
        } catch (PyramidDeviceException e) {
            e.printStackTrace();
        }
    }
}
