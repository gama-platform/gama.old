#!/bin/bash

compile (){
	echo "Compile GAMA project"			
	cd ummisco.gama.annotations
	mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
	cd -
	cd msi.gama.processor
	mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
	cd - 
	
	change=$(git log --pretty=format: --name-only --since="1 hour ago")
	
	if [[ ${change} == *"msi.gama.ext"* ]] || [[ $MSG == *"ci ext"* ]]; then
		cd msi.gama.ext 
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -			
		cd ummisco.gama.feature.dependencies 
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
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


install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi (){
	echo "install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi GAMA project"			
	
	
	change=$(git log --pretty=format: --name-only --since="1 hour ago")
	
	if [[ ${change} == *"ummisco.gama.annotations"* ]] || [[ $MSG == *"ci ummisco.gama.annotations"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd ummisco.gama.annotations 
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	if [[ ${change} == *"msi.gama.processor"* ]] || [[ $MSG == *"ci msi.gama.processor"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd msi.gama.processor 
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
		
	
	
	if [[ ${change} == *"msi.gama.ext"* ]] || [[ $MSG == *"ci ext"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd msi.gama.ext 
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
		cd ummisco.gama.feature.dependencies 
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	
	
	if [[ ${change} == *"msi.gama.core"* ]] || [[ $MSG == *"ci msi.gama.core"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd msi.gama.core 
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	

	
	if [[ ${change} == *"msi.gama.lang.gaml"* ]] || [[ $MSG == *"ci msi.gama.lang.gaml"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd msi.gama.lang.gaml
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	

	
	if [[ ${change} == *"msi.gama.documentation"* ]] || [[ $MSG == *"ci msi.gama.documentation"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd msi.gama.documentation
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	

	
	

	
	
	
	if [[ ${change} == *"ummisco.gama.ui.shared"* ]] || [[ $MSG == *"ci ummisco.gama.ui.shared"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd ummisco.gama.ui.shared
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	
	

	
	if [[ ${change} == *"ummisco.gama.ui.navigator"* ]] || [[ $MSG == *"ci ummisco.gama.ui.navigator"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd ummisco.gama.ui.navigator
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	
	
	if [[ ${change} == *"ummisco.gama.ui.modeling"* ]] || [[ $MSG == *"ci ummisco.gama.ui.modeling"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd ummisco.gama.ui.modeling
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	
	
	
	if [[ ${change} == *"ummisco.gama.ui.experiment"* ]] || [[ $MSG == *"ci ummisco.gama.ui.experiment"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd ummisco.gama.ui.experiment
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	
	if [[ ${change} == *"msi.gama.application"* ]] || [[ $MSG == *"ci msi.gama.application"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd msi.gama.application
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	
	
	
	if [[ ${change} == *"msi.gaml.extensions.fipa"* ]] || [[ $MSG == *"ci msi.gaml.extensions.fipa"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd msi.gaml.extensions.fipa
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	
	if [[ ${change} == *"msi.gama.headless"* ]] || [[ $MSG == *"ci msi.gama.headless"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd msi.gama.headless
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	
	
	if [[ ${change} == *"simtools.gaml.extensions.traffic"* ]] || [[ $MSG == *"ci simtools.gaml.extensions.traffic"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd simtools.gaml.extensions.traffic
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	
	if [[ ${change} == *"simtools.gaml.extensions.physics"* ]] || [[ $MSG == *"ci simtools.gaml.extensions.physics"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd simtools.gaml.extensions.physics
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	if [[ ${change} == *"irit.gaml.extensions.database"* ]] || [[ $MSG == *"ci irit.gaml.extensions.database"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd irit.gaml.extensions.database
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	
	if [[ ${change} == *"msi.gama.models"* ]] || [[ $MSG == *"ci msi.gama.models"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd msi.gama.models
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
		
		cd ummisco.gama.feature.models
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -		
		
	fi
	
	
	
	
	
	if [[ ${change} == *"msi.gaml.architecture.simplebdi"* ]] || [[ $MSG == *"ci msi.gaml.architecture.simplebdi"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd msi.gaml.architecture.simplebdi
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	
	if [[ ${change} == *"simtools.graphanalysis.fr"* ]] || [[ $MSG == *"ci simtools.graphanalysis.fr"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd simtools.graphanalysis.fr
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
		cd simtools.graphlayout.feature
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -		
	fi
	
	
	if [[ ${change} == *"ummisco.gama.opengl"* ]] || [[ $MSG == *"ci ummisco.gama.opengl"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd ummisco.gama.opengl
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	
	
	if [[ ${change} == *"ummisco.gama.java2d"* ]] || [[ $MSG == *"ci ummisco.gama.java2d"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd ummisco.gama.java2d
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	
	
	if [[ ${change} == *"ummisco.gama.ui.viewers"* ]] || [[ $MSG == *"ci ummisco.gama.ui.viewers"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd ummisco.gama.ui.viewers
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	
	
	
	if [[ ${change} == *"ummisco.gama.serialize"* ]] || [[ $MSG == *"ci ummisco.gama.serialize"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd ummisco.gama.serialize
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
		cd ummisco.gama.feature.serialize
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -		
	fi
	
	
	
	if [[ ${change} == *"ummisco.gama.network"* ]] || [[ $MSG == *"ci ummisco.gama.network"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd ummisco.gama.network
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
		cd ummisco.gama.feature.network
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -		
	fi
	
	
	if [[ ${change} == *"ummisco.gaml.extensions.maths"* ]] || [[ $MSG == *"ci ummisco.gaml.extensions.maths"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd ummisco.gaml.extensions.maths
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	
	
	
	if [[ ${change} == *"ummisco.gaml.extensions.sound"* ]] || [[ $MSG == *"ci ummisco.gaml.extensions.sound"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd ummisco.gaml.extensions.sound
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
		cd ummisco.gama.feature.audio
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
		
	fi
	
	
	
	
	if [[ ${change} == *"ummisco.gaml.extensions.stats"* ]] || [[ $MSG == *"ci ummisco.gaml.extensions.stats"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd ummisco.gaml.extensions.stats
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
		cd ummisco.gama.feature.stats
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -		
	fi
	
	
	
	
	
	if [[ ${change} == *"msi.gama.core"* ]] || [[ ${change} == *"msi.gama.headless"* ]] || [[ ${change} == *"msi.gama.lang.gaml"* ]] || [[ ${change} == *"msi.gama.processor"* ]] || [[ ${change} == *"ummisco.gama.annotations"* ]] || [[ ${change} == *"ummisco.gama.annotations"* ]] || [[ $MSG == *"ci ummisco.gama.feature.core"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd ummisco.gama.feature.core
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	
	
	
	
	if [[ ${change} == *"irit.gaml.extensions.database"* ]] || [[ ${change} == *"msi.gaml.extensions.fipa"* ]] || [[ ${change} == *"simtools.gaml.extensions.traffic"* ]] || [[ ${change} == *"msi.gaml.architecture.simplebdi"* ]] || [[ ${change} == *"ummisco.gaml.extensions.maths"* ]] || [[ ${change} == *"simtools.gaml.extensions.physics"* ]] || [[ ${change} == *"ummisco.gama.network"* ]] || [[ ${change} == *"ummisco.gama.serialize"* ]] || [[ $MSG == *"ci ummisco.gama.feature.core.extensions"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd ummisco.gama.feature.core.extensions
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	
	
	
	
	
	if [[ ${change} == *"msi.gama.application"* ]] || [[ ${change} == *"ummisco.gama.ui.shared"* ]] || [[ $MSG == *"ci ummisco.gama.feature.core.ui"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd ummisco.gama.feature.core.ui
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	
	
	
	
	
	if [[ ${change} == *"ummisco.gama.java2d"* ]] || [[ ${change} == *"ummisco.gama.ui.experiment"* ]] || [[ ${change} == *"ummisco.gama.opengl"* ]] || [[ $MSG == *"ci ummisco.gama.feature.experiment.ui"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd ummisco.gama.feature.experiment.ui
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	
	
	
	
	
	if [[ ${change} == *"ummisco.gama.ui.modeling"* ]] || [[ ${change} == *"ummisco.gama.ui.navigator"* ]] || [[ ${change} == *"ummisco.gama.ui.viewers"* ]] || [[ $MSG == *"ci ummisco.gama.feature.modeling.ui"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		cd ummisco.gama.feature.modeling.ui
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
		cd -
	fi
	
	
	
	
	

	
	
	cd msi.gama.parent
	
	if  [[ $MSG == *"ci debug"* ]]; then		
		mvn -e clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi -DskipTests
	else
		mvn clean install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi -DskipTests
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
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
else		
	install 
 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
fi
