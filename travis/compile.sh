#!/bin/bash

function mvn_install() {
	echo "Building " $1
	cd $1
	if mvn clean install; then
	   echo ok
	else
	   echo Something went wrong.
	   exit 1
	fi
	res=${PIPESTATUS[0]} 
	echo "return code $res"
	if [[ $res -ne 0 ]]; then
		exit $res
	fi
	cd -
}
function mvn_compile() {
	echo "Building " $1
	cd $1
	mvn clean compile
	res=$?
	if [[ $res -ne 0 ]]; then
		exit $res
	fi
	cd -
}

compile (){
	echo "Compile GAMA project"			
	mvn_install ummisco.gama.annotations
	mvn_install msi.gama.processor
	
	
	mvn_install msi.gama.parent
}

install (){
	echo "Install GAMA project"			
	mvn_install ummisco.gama.annotations
	mvn_install msi.gama.processor
	
	change=$(git log --pretty=format: --name-only --since="24 hour ago")
	
	if [[ ${change} == *"msi.gama.ext"* ]] || [[ $MSG == *"ci ext"* ]]; then
		mvn_install msi.gama.ext
		mvn_install ummisco.gama.feature.dependencies
	fi
	
	
	mvn_install msi.gama.parent
}

install1 (){
	echo "Install GAMA project"			
	
	
	change=$(git log --pretty=format: --name-only --since="24 hour ago")
	
	if [[ ${change} == *"ummisco.gama.annotations"* ]] || [[ $MSG == *"ci ummisco.gama.annotations"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		mvn_install ummisco.gama.annotations 		
		MSG+=" ci fullbuild "
	fi
	
	if [[ ${change} == *"msi.gama.processor"* ]] || [[ $MSG == *"ci msi.gama.processor"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
		mvn_install msi.gama.processor 		
		MSG+=" ci fullbuild "
	fi
	
		
	
	
	if [[ $MSG == *"ci fullbuild"* ]]; then		
		cd msi.gama.parent 
		mvn clean install
		cd -
		return 0
	fi
	
		
	
	
	
	if [[ ${change} == *"msi.gama.ext"* ]] || [[ $MSG == *"ci ext"* ]]; then
		mvn_install msi.gama.ext 
		mvn_install ummisco.gama.feature.dependencies 
	fi
	
	
	
	if [[ ${change} == *"msi.gama.core"* ]] || [[ $MSG == *"ci msi.gama.core"* ]]; then
		mvn_install msi.gama.core 
	fi
	
	

	
	if [[ ${change} == *"msi.gama.lang.gaml"* ]] || [[ $MSG == *"ci msi.gama.lang.gaml"* ]]; then
		mvn_install msi.gama.lang.gaml
	fi
	
	

	
	if [[ ${change} == *"msi.gama.documentation"* ]] || [[ $MSG == *"ci msi.gama.documentation"* ]]; then
		mvn_install msi.gama.documentation
	fi
	
	

	
	if [[ ${change} == *"msi.gama.application"* ]] || [[ $MSG == *"ci msi.gama.application"* ]]; then
		mvn_install msi.gama.application
	fi
	

	
	
	
	if [[ ${change} == *"ummisco.gama.ui.shared"* ]] || [[ $MSG == *"ci ummisco.gama.ui.shared"* ]]; then
		mvn_install ummisco.gama.ui.shared
	fi
	
	
	

	
	if [[ ${change} == *"ummisco.gama.ui.navigator"* ]] || [[ $MSG == *"ci ummisco.gama.ui.navigator"* ]]; then
		mvn_install ummisco.gama.ui.navigator
	fi
	
	
	
	if [[ ${change} == *"ummisco.gama.ui.modeling"* ]] || [[ $MSG == *"ci ummisco.gama.ui.modeling"* ]]; then
		mvn_install ummisco.gama.ui.modeling
	fi
	
	
	
	
	if [[ ${change} == *"ummisco.gama.ui.experiment"* ]] || [[ $MSG == *"ci ummisco.gama.ui.experiment"* ]]; then
		mvn_install ummisco.gama.ui.experiment
	fi
	
	
	
	if [[ ${change} == *"msi.gama.models"* ]] || [[ $MSG == *"ci msi.gama.models"* ]]; then
		mvn_install msi.gama.models
		
		mvn_install ummisco.gama.feature.models
	fi
	
	
	
	if [[ ${change} == *"irit.gaml.extensions.database"* ]] || [[ $MSG == *"ci irit.gaml.extensions.database"* ]]; then
		mvn_install irit.gaml.extensions.database
	fi
	
	
	
	if [[ ${change} == *"simtools.gaml.extensions.traffic"* ]] || [[ $MSG == *"ci simtools.gaml.extensions.traffic"* ]]; then
		mvn_install simtools.gaml.extensions.traffic
	fi
	
	
	
	if [[ ${change} == *"ummisco.gaml.extensions.maths"* ]] || [[ $MSG == *"ci ummisco.gaml.extensions.maths"* ]]; then
		mvn_install ummisco.gaml.extensions.maths
	fi
	
	
	
	
	if [[ ${change} == *"msi.gama.headless"* ]] || [[ $MSG == *"ci msi.gama.headless"* ]]; then
		mvn_install msi.gama.headless
	fi
	
	
	
	if [[ ${change} == *"ummisco.gaml.extensions.stats"* ]] || [[ $MSG == *"ci ummisco.gaml.extensions.stats"* ]]; then
		mvn_install ummisco.gaml.extensions.stats
		mvn_install ummisco.gama.feature.stats
	fi
	
	
	
	if [[ ${change} == *"msi.gama.core"* ]] || [[ ${change} == *"msi.gama.headless"* ]] || [[ ${change} == *"msi.gama.lang.gaml"* ]] || [[ ${change} == *"msi.gama.processor"* ]] || [[ ${change} == *"ummisco.gama.annotations"* ]] || [[ ${change} == *"ummisco.gama.annotations"* ]] || [[ $MSG == *"ci ummisco.gama.feature.core"* ]]; then
		mvn_install ummisco.gama.feature.core
	fi
	
	
	
	
	if [[ ${change} == *"msi.gaml.architecture.simplebdi"* ]] || [[ $MSG == *"ci msi.gaml.architecture.simplebdi"* ]]; then
		mvn_install msi.gaml.architecture.simplebdi
	fi
	
	
	
	
	
	if [[ ${change} == *"msi.gaml.extensions.fipa"* ]] || [[ $MSG == *"ci msi.gaml.extensions.fipa"* ]]; then
		mvn_install msi.gaml.extensions.fipa
	fi
	
	
	
	
	
	if [[ ${change} == *"simtools.gaml.extensions.physics"* ]] || [[ $MSG == *"ci simtools.gaml.extensions.physics"* ]]; then
		mvn_install simtools.gaml.extensions.physics
	fi
	
	
	
	
	if [[ ${change} == *"ummisco.gama.serialize"* ]] || [[ $MSG == *"ci ummisco.gama.serialize"* ]]; then
		mvn_install ummisco.gama.serialize
		mvn_install ummisco.gama.feature.serialize
	fi
	
	
	
	if [[ ${change} == *"ummisco.gama.network"* ]] || [[ $MSG == *"ci ummisco.gama.network"* ]]; then
		mvn_install ummisco.gama.network
		mvn_install ummisco.gama.feature.network
	fi
	

	
	
	if [[ ${change} == *"irit.gaml.extensions.database"* ]] || [[ ${change} == *"msi.gaml.extensions.fipa"* ]] || [[ ${change} == *"simtools.gaml.extensions.traffic"* ]] || [[ ${change} == *"msi.gaml.architecture.simplebdi"* ]] || [[ ${change} == *"ummisco.gaml.extensions.maths"* ]] || [[ ${change} == *"simtools.gaml.extensions.physics"* ]] || [[ ${change} == *"ummisco.gama.network"* ]] || [[ ${change} == *"ummisco.gama.serialize"* ]] || [[ $MSG == *"ci ummisco.gama.feature.core.extensions"* ]]; then
		mvn_install ummisco.gama.feature.core.extensions
	fi
	
	
	
	
	
	
	if [[ ${change} == *"msi.gama.application"* ]] || [[ ${change} == *"ummisco.gama.ui.shared"* ]] || [[ $MSG == *"ci ummisco.gama.feature.core.ui"* ]]; then
		mvn_install ummisco.gama.feature.core.ui
	fi
	
	
	
	
	if [[ ${change} == *"ummisco.gama.java2d"* ]] || [[ $MSG == *"ci ummisco.gama.java2d"* ]]; then
		mvn_install ummisco.gama.java2d
	fi
	
	
	
	
	
	if [[ ${change} == *"ummisco.gama.opengl"* ]] || [[ $MSG == *"ci ummisco.gama.opengl"* ]]; then
		mvn_install ummisco.gama.opengl
	fi
	
	
	
	
	
	
	
	if [[ ${change} == *"ummisco.gama.java2d"* ]] || [[ ${change} == *"ummisco.gama.ui.experiment"* ]] || [[ ${change} == *"ummisco.gama.opengl"* ]] || [[ $MSG == *"ci ummisco.gama.feature.experiment.ui"* ]]; then
		mvn_install ummisco.gama.feature.experiment.ui
	fi
	
	
	if [[ ${change} == *"ummisco.gama.ui.viewers"* ]] || [[ $MSG == *"ci ummisco.gama.ui.viewers"* ]]; then
		mvn_install ummisco.gama.ui.viewers
	fi
	
	
	
	
	
	
	if [[ ${change} == *"ummisco.gama.ui.modeling"* ]] || [[ ${change} == *"ummisco.gama.ui.navigator"* ]] || [[ ${change} == *"ummisco.gama.ui.viewers"* ]] || [[ $MSG == *"ci ummisco.gama.feature.modeling.ui"* ]]; then
		mvn_install ummisco.gama.feature.modeling.ui
	fi
	
	
	
	
	if [[ ${change} == *"simtools.graphanalysis.fr"* ]] || [[ $MSG == *"ci simtools.graphanalysis.fr"* ]]; then
		mvn_install simtools.graphanalysis.fr
		mvn_install simtools.graphlayout.feature
	fi
	
	
	
	
	
	
	
	
	if [[ ${change} == *"ummisco.gaml.extensions.sound"* ]] || [[ $MSG == *"ci ummisco.gaml.extensions.sound"* ]]; then
		mvn_install ummisco.gaml.extensions.sound
		mvn_install ummisco.gama.feature.audio
		
	fi
	
	
	
	

	cd msi.gama.parent 
	mvn clean install -f tiny_pom.xml 
	cd -
	
	
}


 
MESSAGE=$(git log -1 HEAD --pretty=format:%s)
echo $MESSAGE
if  [[ ${MESSAGE} == *"ci clean"* ]] || [[ $MSG == *"ci clean"* ]]; then
	MSG+=" ci fullbuild "
fi 
if [[ "$TRAVIS_EVENT_TYPE" == "cron" ]] || [[ $MSG == *"ci cron"* ]]; then 	
	install
else		
	install
fi
