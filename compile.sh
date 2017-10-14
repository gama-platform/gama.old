#!/bin/bash

compile (){
	echo "Compile GAMA project"			
	cd ummisco.gama.annotations
	mvn clean install
	cd -
	cd msi.gama.processor
	mvn clean install
	cd - 
	
	change=$(git log --pretty=format: --name-only --since="1 hour ago")
	
	if [[ ${change} == *"msi.gama.ext"* ]] || [[ $MSG == *"ci ext"* ]]; then
		cd msi.gama.ext 
		mvn clean install
		cd -			
		cd ummisco.gama.feature.dependencies 
		mvn clean install
		cd -
	fi
	
	
	cd msi.gama.parent
	
	if  [[ $MSG == *"ci debug"* ]]; then		
		mvn -e clean compile
	else
		mvn clean compile
	fi
		
	cd -
}


install (){
	echo "Install GAMA project"			
	
	
	change=$(git log --pretty=format: --name-only --since="1 hour ago")
	
	if [[ ${change} == *"ummisco.gama.annotations"* ]] || [[ $MSG == *"ci ummisco.gama.annotations"* ]]; then
		cd ummisco.gama.annotations 
		mvn clean install -T 8C
		cd -
	fi
	
	if [[ ${change} == *"msi.gama.processor"* ]] || [[ $MSG == *"ci msi.gama.processor"* ]]; then
		cd msi.gama.processor 
		mvn clean install -T 8C
		cd -
	fi
	
		
	
	
	if [[ ${change} == *"msi.gama.ext"* ]] || [[ $MSG == *"ci ext"* ]]; then
		cd msi.gama.ext 
		mvn clean install -T 8C
		cd -
		cd ummisco.gama.feature.dependencies 
		mvn clean install -T 8C
		cd -
	fi
	
	
	
	if [[ ${change} == *"msi.gama.core"* ]] || [[ $MSG == *"ci msi.gama.core"* ]]; then
		cd msi.gama.core 
		mvn clean install -T 8C
		cd -
	fi
	
	

	
	if [[ ${change} == *"msi.gama.lang.gaml"* ]] || [[ $MSG == *"ci msi.gama.lang.gaml"* ]]; then
		cd msi.gama.lang.gaml
		mvn clean install -T 8C
		cd -
	fi
	
	

	
	if [[ ${change} == *"msi.gama.documentation"* ]] || [[ $MSG == *"ci msi.gama.documentation"* ]]; then
		cd msi.gama.documentation
		mvn clean install -T 8C
		cd -
	fi
	
	

	
	
	if [[ ${change} == *"ummisco.gama.ui.modeling"* ]] || [[ $MSG == *"ci ummisco.gama.ui.modeling"* ]]; then
		cd ummisco.gama.ui.modeling
		mvn clean install -T 8C
		cd -
	fi
	
	

	
	
	
	if [[ ${change} == *"ummisco.gama.ui.shared"* ]] || [[ $MSG == *"ci ummisco.gama.ui.shared"* ]]; then
		cd ummisco.gama.ui.shared
		mvn clean install -T 8C
		cd -
	fi
	
	

	
	if [[ ${change} == *"ummisco.gama.ui.navigator"* ]] || [[ $MSG == *"ci ummisco.gama.ui.navigator"* ]]; then
		cd ummisco.gama.ui.navigator
		mvn clean install -T 8C
		cd -
	fi
	
	
	if [[ ${change} == *"ummisco.gama.ui.experiment"* ]] || [[ $MSG == *"ci ummisco.gama.ui.experiment"* ]]; then
		cd ummisco.gama.ui.experiment
		mvn clean install -T 8C
		cd -
	fi
	
	
	if [[ ${change} == *"msi.gama.application"* ]] || [[ $MSG == *"ci msi.gama.application"* ]]; then
		cd msi.gama.application
		mvn clean install -T 8C
		cd -
	fi
	
	
	
	
	if [[ ${change} == *"msi.gaml.extensions.fipa"* ]] || [[ $MSG == *"ci msi.gaml.extensions.fipa"* ]]; then
		cd msi.gaml.extensions.fipa
		mvn clean install -T 8C
		cd -
	fi
	
	
	if [[ ${change} == *"msi.gama.headless"* ]] || [[ $MSG == *"ci msi.gama.headless"* ]]; then
		cd msi.gama.headless
		mvn clean install -T 8C
		cd -
	fi
	
	
	
	if [[ ${change} == *"simtools.gaml.extensions.traffic"* ]] || [[ $MSG == *"ci simtools.gaml.extensions.traffic"* ]]; then
		cd simtools.gaml.extensions.traffic
		mvn clean install -T 8C
		cd -
	fi
	
	
	if [[ ${change} == *"simtools.gaml.extensions.physics"* ]] || [[ $MSG == *"ci simtools.gaml.extensions.physics"* ]]; then
		cd simtools.gaml.extensions.physics
		mvn clean install -T 8C
		cd -
	fi
	
	if [[ ${change} == *"irit.gaml.extensions.database"* ]] || [[ $MSG == *"ci irit.gaml.extensions.database"* ]]; then
		cd irit.gaml.extensions.database
		mvn clean install -T 8C
		cd -
	fi
	
	
	if [[ ${change} == *"msi.gama.models"* ]] || [[ $MSG == *"ci msi.gama.models"* ]]; then
		cd msi.gama.models
		mvn clean install -T 8C
		cd -
		
		cd ummisco.gama.feature.models
		mvn clean install -T 8C
		cd -		
		
	fi
	
	
	
	
	
	if [[ ${change} == *"msi.gaml.architecture.simplebdi"* ]] || [[ $MSG == *"ci msi.gaml.architecture.simplebdi"* ]]; then
		cd msi.gaml.architecture.simplebdi
		mvn clean install -T 8C
		cd -
	fi
	
	
	if [[ ${change} == *"simtools.graphanalysis.fr"* ]] || [[ $MSG == *"ci simtools.graphanalysis.fr"* ]]; then
		cd simtools.graphanalysis.fr
		mvn clean install -T 8C
		cd -
		cd simtools.graphlayout.feature
		mvn clean install -T 8C
		cd -		
	fi
	
	
	if [[ ${change} == *"ummisco.gama.opengl"* ]] || [[ $MSG == *"ci ummisco.gama.opengl"* ]]; then
		cd ummisco.gama.opengl
		mvn clean install -T 8C
		cd -
	fi
	
	
	
	if [[ ${change} == *"ummisco.gama.java2d"* ]] || [[ $MSG == *"ci ummisco.gama.java2d"* ]]; then
		cd ummisco.gama.java2d
		mvn clean install -T 8C
		cd -
	fi
	
	
	
	if [[ ${change} == *"ummisco.gama.ui.viewers"* ]] || [[ $MSG == *"ci ummisco.gama.ui.viewers"* ]]; then
		cd ummisco.gama.ui.viewers
		mvn clean install -T 8C
		cd -
	fi
	
	
	
	
	if [[ ${change} == *"ummisco.gama.serialize"* ]] || [[ $MSG == *"ci ummisco.gama.serialize"* ]]; then
		cd ummisco.gama.serialize
		mvn clean install -T 8C
		cd -
		cd ummisco.gama.feature.serialize
		mvn clean install -T 8C
		cd -		
	fi
	
	
	
	if [[ ${change} == *"ummisco.gama.network"* ]] || [[ $MSG == *"ci ummisco.gama.network"* ]]; then
		cd ummisco.gama.network
		mvn clean install -T 8C
		cd -
		cd ummisco.gama.feature.network
		mvn clean install -T 8C
		cd -		
	fi
	
	
	if [[ ${change} == *"ummisco.gaml.extensions.maths"* ]] || [[ $MSG == *"ci ummisco.gaml.extensions.maths"* ]]; then
		cd ummisco.gaml.extensions.maths
		mvn clean install -T 8C
		cd -
	fi
	
	
	
	
	if [[ ${change} == *"ummisco.gaml.extensions.sound"* ]] || [[ $MSG == *"ci ummisco.gaml.extensions.sound"* ]]; then
		cd ummisco.gaml.extensions.sound
		mvn clean install -T 8C
		cd -
		cd ummisco.gama.feature.audio
		mvn clean install -T 8C
		cd -
		
	fi
	
	
	
	
	if [[ ${change} == *"ummisco.gaml.extensions.stats"* ]] || [[ $MSG == *"ci ummisco.gaml.extensions.stats"* ]]; then
		cd ummisco.gaml.extensions.stats
		mvn clean install -T 8C
		cd -
		cd ummisco.gama.feature.stats
		mvn clean install -T 8C
		cd -		
	fi
	
	
	
	
	
	if [[ ${change} == *"msi.gama.core"* ]] || [[ ${change} == *"msi.gama.headless"* ]] || [[ ${change} == *"msi.gama.lang.gaml"* ]] || [[ ${change} == *"msi.gama.processor"* ]] || [[ ${change} == *"ummisco.gama.annotations"* ]] || [[ ${change} == *"ummisco.gama.annotations"* ]] || [[ $MSG == *"ci ummisco.gama.feature.core"* ]]; then
		cd ummisco.gama.feature.core
		mvn clean install -T 8C
		cd -
	fi
	
	
	
	
	
	if [[ ${change} == *"irit.gaml.extensions.database"* ]] || [[ ${change} == *"msi.gaml.extensions.fipa"* ]] || [[ ${change} == *"simtools.gaml.extensions.traffic"* ]] || [[ ${change} == *"msi.gaml.architecture.simplebdi"* ]] || [[ ${change} == *"ummisco.gaml.extensions.maths"* ]] || [[ ${change} == *"simtools.gaml.extensions.physics"* ]] || [[ ${change} == *"ummisco.gama.network"* ]] || [[ ${change} == *"ummisco.gama.serialize"* ]] || [[ $MSG == *"ci ummisco.gama.feature.core.extensions"* ]]; then
		cd ummisco.gama.feature.core.extensions
		mvn clean install -T 8C
		cd -
	fi
	
	
	
	
	
	
	if [[ ${change} == *"msi.gama.application"* ]] || [[ ${change} == *"ummisco.gama.ui.shared"* ]] || [[ $MSG == *"ci ummisco.gama.feature.core.ui"* ]]; then
		cd ummisco.gama.feature.core.ui
		mvn clean install -T 8C
		cd -
	fi
	
	
	
	
	
	
	if [[ ${change} == *"ummisco.gama.java2d"* ]] || [[ ${change} == *"ummisco.gama.ui.experiment"* ]] || [[ ${change} == *"ummisco.gama.opengl"* ]] || [[ $MSG == *"ci ummisco.gama.feature.experiment.ui"* ]]; then
		cd ummisco.gama.feature.experiment.ui
		mvn clean install -T 8C
		cd -
	fi
	
	
	
	
	
	
	if [[ ${change} == *"ummisco.gama.ui.modeling"* ]] || [[ ${change} == *"ummisco.gama.ui.navigator"* ]] || [[ ${change} == *"ummisco.gama.ui.viewers"* ]] || [[ $MSG == *"ci ummisco.gama.feature.modeling.ui"* ]]; then
		cd ummisco.gama.feature.modeling.ui
		mvn clean install -T 8C
		cd -
	fi
	
	
	
	
	

	
	
	cd msi.gama.parent
	
	if  [[ $MSG == *"ci debug"* ]]; then		
		mvn -e clean install -DskipTests -T 8C
	else
		mvn clean install -DskipTests -T 8C
	fi
		
	cd -
}



MESSAGE=$(git log -1 HEAD --pretty=format:%s)
echo $MESSAGE
if  [[ ${MESSAGE} == *"ci clean"* ]] || [[ $MSG == *"ci clean"* ]]; then
	MSG+=" ci ext "
fi 
if [[ "$TRAVIS_EVENT_TYPE" == "cron" ]] || [[ $MSG == *"ci cron"* ]]; then 	
	install
else		
	install
fi
