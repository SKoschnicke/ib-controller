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

import javax.swing.*;

public class ConfigureTwsAllowExternalIpsTask implements ConfigurationAction {

	final boolean mAllow;
	private JDialog configDialog;

	ConfigureTwsAllowExternalIpsTask(boolean allow) { mAllow = allow; }

	@Override
	public void run() {
		try {
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

			if (!Utils.selectConfigSection(configDialog, new String[] {"API","Settings"}))
				// older versions of TWS don't have the Settings node below the API node
				Utils.selectConfigSection(configDialog, new String[] {"API"});

			JCheckBox cb = SwingUtils.findCheckBox(configDialog, "Allow connections from localhost only");
			if (cb == null) throw new IBControllerException("could not find 'Allow connections from localhost only' checkbox");
			cb.setSelected(!allow);
		} catch (IBControllerException e) {
			Utils.logError("" + e.getMessage());
		}
	}


	@Override
	public void initialise(JDialog configDialog) {
		this.configDialog = configDialog;
	}

}
