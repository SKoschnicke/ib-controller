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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ArrayList;

class ConfigureTwsAllowExternalIpsTask implements Runnable{

    final boolean mAllow;

    ConfigureTwsAllowExternalIpsTask(boolean allow) {
	mAllow = allow;
    }

    @Override
    public void run() {
	try {
	    final JDialog configDialog = TwsListener.getConfigDialog();    // blocks the thread until the config dialog is available

	    GuiExecutor.instance().execute(new Runnable(){
		@Override
		public void run() {configure(configDialog, mAllow);}
	    });

	} catch (Exception e){
	    Utils.logError("" + e.getMessage());
	}
    }

    private void configure(final JDialog configDialog, final boolean allow) {
	try {
	    Utils.logToConsole("Performing external ips configuration");

	    if (!TwsListener.selectConfigSection(configDialog, new String[] {"API","Settings"}))
		// older versions of TWS don't have the Settings node below the API node
		TwsListener.selectConfigSection(configDialog, new String[] {"API"});

	    if (!Utils.setCheckBoxSelected(configDialog, "Allow connections from localhost only", !allow)) {
		throw new IBControllerException("could not find \"Allow connections from localhost only\" checkbox");
	    }

	    Utils.clickButton(configDialog, "OK");
	    configDialog.setVisible(false);
	} catch (IBControllerException e) {
	    Utils.logError("" + e.getMessage());
	}
    }
}
