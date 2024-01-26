model ExternalController

global {
}

experiment start {

	init {
		// we just open the html page to control gama-server in the default web browser
		try {
			write command("open " + project_path + "scripts/connector.html");
		}

		catch {
			string cmd <- "start \"\" " + project_path + "scripts/connector.html";
			write cmd;
			write command(cmd);
		}

		do die;
	}

}