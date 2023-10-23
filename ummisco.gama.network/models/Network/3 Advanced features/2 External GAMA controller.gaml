model ExternalController

global { 
}

 


experiment start
{ 
	init
	{ 
		write project_path;
		write command("open "+project_path+"/scripts/controller.html");
		do die;
	}
}