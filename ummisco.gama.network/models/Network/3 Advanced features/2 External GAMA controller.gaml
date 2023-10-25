model ExternalController

global {
}

experiment start {

	init {
		write project_path;
		string model_ <- project_path + "/3 Advanced features/1 Pong Teleportation (send agent).gaml";
		string exp_name <- 'start';
		string body_html <- '
  Host: <input type="text" id="host" value="localhost" /><br>
  Socket port: <input type="text" id="socket" value="1000" /><br>

  Model path: <input type="text" id="model"
    value="' + model_ + '" /><br>
  Experiment name: <input type="text" id="exp_name" value="' + exp_name + '" /><br>
';
		string header <- "" + text_file("../scripts/header.txt");
		string footer <- "" + text_file("../scripts/footer.txt");
		save header + " " + body_html + " " + footer to: "../scripts/controller.html" format: "text" header: false;
		try {
			write command("open " + project_path + "scripts/controller.html");
		}

		catch {
			string cmd <- "start \"\" " + project_path + "scripts/controller.html";
			write cmd;
			write command(cmd);
		}

		do die;
	}

}