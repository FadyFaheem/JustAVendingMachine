package com.fadyfaheem;

import com.pyramidacceptors.ptalk.api.PyramidAcceptor;
import com.pyramidacceptors.ptalk.api.PyramidDeviceException;

public class BillAcceptor {
    static PyramidAcceptor acceptor;

    public static void connect(com.pyramidacceptors.ptalk.api.event.PTalkEventListener listener) {
        try {
            acceptor = PyramidAcceptor.valueOfRS232();
            acceptor.connect();
            acceptor.addChangeListener(listener);
        } catch (PyramidDeviceException e) {
            e.printStackTrace();
        }
    }
}
