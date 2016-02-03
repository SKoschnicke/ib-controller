// This file is part of the "IBController".
// Copyright (C) 2004 Steven M. Kearns (skearns23@yahoo.com )
// Copyright (C) 2004 - 2011 Richard L King (rlking@aultan.com)
// For conditions of distribution and use, see copyright notice in COPYING.txt

// IBController is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// IBController is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with IBController.  If not, see <http://www.gnu.org/licenses/>.

package ibcontroller;

import java.awt.Component;
import java.awt.Container;
import javax.swing.*;

class ConfigureTwsSettingsTask implements Runnable{
    
    final int mPortNumber;
    final Boolean mAllowExternalIps;
    final Boolean mReadOnlyApi;
    
    ConfigureTwsSettingsTask(int portNumber, Boolean allow, Boolean readOnlyApi) {
        mPortNumber = portNumber;
        mAllowExternalIps = allow;
        mReadOnlyApi = readOnlyApi;
    }

    @Override
    public void run() {
        try {
            final JDialog configDialog = TwsListener.getConfigDialog();    // blocks the thread until the config dialog is available
            
            GuiExecutor.instance().execute(new Runnable(){
                @Override
                public void run() {configure(configDialog, mPortNumber, mAllowExternalIps, mReadOnlyApi);}
            });

        } catch (Exception e){
            Utils.logError("" + e.getMessage());
        }
    }

    /**
     * Note that this thread handles two configruation
     * settings. Modularizing this into two threads would make the
     * code overly complex due to synchronization requirements (only
     * one thread may access the configuration dialog at a time).
     */
    private void configure(final JDialog configDialog, final int portNumber, final Boolean allowExternalIps, final Boolean readOnlyApi) {
        try {
            Utils.logToConsole("Performing configuration");
            
            if (!TwsListener.selectConfigSection(configDialog, new String[] {"API","Settings"}))
                // older versions of TWS don't have the Settings node below the API node
                TwsListener.selectConfigSection(configDialog, new String[] {"API"});

            Component comp = Utils.findComponent(configDialog, "Socket port");
            if (comp == null) throw new IBControllerException("could not find socket port component");

            JTextField tf = Utils.findTextField((Container)comp, 0);
            if (tf == null) throw new IBControllerException("could not find socket port field");
            
            if (portNumber != 0) {
                int currentPort = Integer.parseInt(tf.getText());
                if (currentPort == portNumber) {
                    Utils.logToConsole("TWS API socket port is already set to " + tf.getText());
                } else {
                    if (!IBController.isGateway()) {
                        JCheckBox cb = Utils.findCheckBox(configDialog, "Enable ActiveX and Socket Clients");
                        if (cb == null) throw new IBControllerException("could not find Enable ActiveX checkbox");
                        if (cb.isSelected()) TwsListener.setApiConfigChangeConfirmationExpected(true);
                    }
                    Utils.logToConsole("TWS API socket port was set to " + tf.getText());
                    tf.setText(new Integer(portNumber).toString());
                    Utils.logToConsole("TWS API socket port now set to " + tf.getText());
                }
            }

            if (allowExternalIps != null) {
                JCheckBox localhostOnly = Utils.findCheckBox(configDialog, "Allow connections from localhost only");
                if (localhostOnly != null) {
                    if (localhostOnly.isSelected() == !allowExternalIps) {
                        Utils.logToConsole("TWS API already " + (localhostOnly.isSelected() ? "not " : "") + "allowing external connections.");
                    } else {
                        localhostOnly.setSelected(!allowExternalIps);
                        Utils.logToConsole("Setting TWS API to " + (localhostOnly.isSelected() ? "not " : "") + "allow external connections.");
                    }
                } else {
                    throw new IBControllerException("could not find \"Allow connections from localhost only\" checkbox");
                }
            }

            if (readOnlyApi != null) {
                JCheckBox readOnlyApiCheckbox = Utils.findCheckBox(configDialog, "Read-Only API");
                if (readOnlyApiCheckbox != null) {
                    if (readOnlyApiCheckbox.isSelected() == readOnlyApi) {
                        Utils.logToConsole("TWS API is already " + (readOnlyApiCheckbox.isSelected() ? "read-only." : "read and write."));
                    } else {
                        readOnlyApiCheckbox.setSelected(readOnlyApi);
                        Utils.logToConsole("TWS API set to " + (readOnlyApiCheckbox.isSelected() ? "read-only." : "read and write."));
                    }
                } else {
                    throw new IBControllerException("could not find \"Read-Only API\" checkbox");
                }
            }


            Utils.clickButton(configDialog, "OK");

            configDialog.setVisible(false);
        } catch (IBControllerException e) {
            Utils.logError("" + e.getMessage());
        }
    }
}
