#!/bin/bash


function mvn_deploy() {
	echo "Deploying " $1
	cd $1
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	res=$?
	if [[ $res -gt 0 ]]; then
		exit $res
	fi
	cd -
}

echo "Install GAMA project"			
	
	
change=$(git log --pretty=format: --name-only --since="1 day ago")

if [[ ${change} == *"ummisco.gama.annotations"* ]] || [[ $MSG == *"ci ummisco.gama.annotations"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	mvn_deploy ummisco.gama.annotations 		
	MSG+=" ci fullbuild "
fi

if [[ ${change} == *"msi.gama.processor"* ]] || [[ $MSG == *"ci msi.gama.processor"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	mvn_deploy msi.gama.processor 		
	MSG+=" ci fullbuild "
fi

	


if [[ $MSG == *"ci fullbuild"* ]]; then		
	cd msi.gama.parent 
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
	return 0
fi

	



if [[ ${change} == *"msi.gama.ext"* ]] || [[ $MSG == *"ci ext"* ]]; then
	mvn_deploy msi.gama.ext 
	mvn_deploy ummisco.gama.feature.dependencies 
fi



if [[ ${change} == *"msi.gama.core"* ]] || [[ $MSG == *"ci msi.gama.core"* ]]; then
	mvn_deploy msi.gama.core 
fi




if [[ ${change} == *"msi.gama.lang.gaml"* ]] || [[ $MSG == *"ci msi.gama.lang.gaml"* ]]; then
	mvn_deploy msi.gama.lang.gaml
fi




if [[ ${change} == *"msi.gama.documentation"* ]] || [[ $MSG == *"ci msi.gama.documentation"* ]]; then
	mvn_deploy msi.gama.documentation
fi




if [[ ${change} == *"msi.gama.application"* ]] || [[ $MSG == *"ci msi.gama.application"* ]]; then
	mvn_deploy msi.gama.application
fi





if [[ ${change} == *"ummisco.gama.ui.shared"* ]] || [[ $MSG == *"ci ummisco.gama.ui.shared"* ]]; then
	mvn_deploy ummisco.gama.ui.shared
fi





if [[ ${change} == *"ummisco.gama.ui.navigator"* ]] || [[ $MSG == *"ci ummisco.gama.ui.navigator"* ]]; then
	mvn_deploy ummisco.gama.ui.navigator
fi



if [[ ${change} == *"ummisco.gama.ui.modeling"* ]] || [[ $MSG == *"ci ummisco.gama.ui.modeling"* ]]; then
	mvn_deploy ummisco.gama.ui.modeling
fi




if [[ ${change} == *"ummisco.gama.ui.experiment"* ]] || [[ $MSG == *"ci ummisco.gama.ui.experiment"* ]]; then
	mvn_deploy ummisco.gama.ui.experiment
fi



if [[ ${change} == *"msi.gama.models"* ]] || [[ $MSG == *"ci msi.gama.models"* ]]; then
	mvn_deploy msi.gama.models
	
	mvn_deploy ummisco.gama.feature.models
fi



if [[ ${change} == *"irit.gaml.extensions.database"* ]] || [[ $MSG == *"ci irit.gaml.extensions.database"* ]]; then
	mvn_deploy irit.gaml.extensions.database
fi



if [[ ${change} == *"simtools.gaml.extensions.traffic"* ]] || [[ $MSG == *"ci simtools.gaml.extensions.traffic"* ]]; then
	mvn_deploy simtools.gaml.extensions.traffic
fi



if [[ ${change} == *"ummisco.gaml.extensions.maths"* ]] || [[ $MSG == *"ci ummisco.gaml.extensions.maths"* ]]; then
	mvn_deploy ummisco.gaml.extensions.maths
fi




if [[ ${change} == *"msi.gama.headless"* ]] || [[ $MSG == *"ci msi.gama.headless"* ]]; then
	mvn_deploy msi.gama.headless
fi



if [[ ${change} == *"ummisco.gaml.extensions.stats"* ]] || [[ $MSG == *"ci ummisco.gaml.extensions.stats"* ]]; then
	mvn_deploy ummisco.gaml.extensions.stats
	mvn_deploy ummisco.gama.feature.stats
fi



if [[ ${change} == *"msi.gama.core"* ]] || [[ ${change} == *"msi.gama.headless"* ]] || [[ ${change} == *"msi.gama.lang.gaml"* ]] || [[ ${change} == *"msi.gama.processor"* ]] || [[ ${change} == *"ummisco.gama.annotations"* ]] || [[ ${change} == *"ummisco.gama.annotations"* ]] || [[ $MSG == *"ci ummisco.gama.feature.core"* ]]; then
	mvn_deploy ummisco.gama.feature.core
fi




if [[ ${change} == *"msi.gaml.architecture.simplebdi"* ]] || [[ $MSG == *"ci msi.gaml.architecture.simplebdi"* ]]; then
	mvn_deploy msi.gaml.architecture.simplebdi
fi





if [[ ${change} == *"msi.gaml.extensions.fipa"* ]] || [[ $MSG == *"ci msi.gaml.extensions.fipa"* ]]; then
	mvn_deploy msi.gaml.extensions.fipa
fi





if [[ ${change} == *"simtools.gaml.extensions.physics"* ]] || [[ $MSG == *"ci simtools.gaml.extensions.physics"* ]]; then
	mvn_deploy simtools.gaml.extensions.physics
fi




if [[ ${change} == *"ummisco.gama.serialize"* ]] || [[ $MSG == *"ci ummisco.gama.serialize"* ]]; then
	mvn_deploy ummisco.gama.serialize
	mvn_deploy ummisco.gama.feature.serialize
fi



if [[ ${change} == *"ummisco.gama.network"* ]] || [[ $MSG == *"ci ummisco.gama.network"* ]]; then
	mvn_deploy ummisco.gama.network
	mvn_deploy ummisco.gama.feature.network
fi




if [[ ${change} == *"irit.gaml.extensions.database"* ]] || [[ ${change} == *"msi.gaml.extensions.fipa"* ]] || [[ ${change} == *"simtools.gaml.extensions.traffic"* ]] || [[ ${change} == *"msi.gaml.architecture.simplebdi"* ]] || [[ ${change} == *"ummisco.gaml.extensions.maths"* ]] || [[ ${change} == *"simtools.gaml.extensions.physics"* ]] || [[ ${change} == *"ummisco.gama.network"* ]] || [[ ${change} == *"ummisco.gama.serialize"* ]] || [[ $MSG == *"ci ummisco.gama.feature.core.extensions"* ]]; then
	mvn_deploy ummisco.gama.feature.core.extensions
fi






if [[ ${change} == *"msi.gama.application"* ]] || [[ ${change} == *"ummisco.gama.ui.shared"* ]] || [[ $MSG == *"ci ummisco.gama.feature.core.ui"* ]]; then
	mvn_deploy ummisco.gama.feature.core.ui
fi




if [[ ${change} == *"ummisco.gama.java2d"* ]] || [[ $MSG == *"ci ummisco.gama.java2d"* ]]; then
	mvn_deploy ummisco.gama.java2d
fi





if [[ ${change} == *"ummisco.gama.opengl"* ]] || [[ $MSG == *"ci ummisco.gama.opengl"* ]]; then
	mvn_deploy ummisco.gama.opengl
fi







if [[ ${change} == *"ummisco.gama.java2d"* ]] || [[ ${change} == *"ummisco.gama.ui.experiment"* ]] || [[ ${change} == *"ummisco.gama.opengl"* ]] || [[ $MSG == *"ci ummisco.gama.feature.experiment.ui"* ]]; then
	mvn_deploy ummisco.gama.feature.experiment.ui
fi


if [[ ${change} == *"ummisco.gama.ui.viewers"* ]] || [[ $MSG == *"ci ummisco.gama.ui.viewers"* ]]; then
	mvn_deploy ummisco.gama.ui.viewers
fi






if [[ ${change} == *"ummisco.gama.ui.modeling"* ]] || [[ ${change} == *"ummisco.gama.ui.navigator"* ]] || [[ ${change} == *"ummisco.gama.ui.viewers"* ]] || [[ $MSG == *"ci ummisco.gama.feature.modeling.ui"* ]]; then
	mvn_deploy ummisco.gama.feature.modeling.ui
fi




if [[ ${change} == *"simtools.graphanalysis.fr"* ]] || [[ $MSG == *"ci simtools.graphanalysis.fr"* ]]; then
	mvn_deploy simtools.graphanalysis.fr
	mvn_deploy simtools.graphlayout.feature
fi








if [[ ${change} == *"ummisco.gaml.extensions.sound"* ]] || [[ $MSG == *"ci ummisco.gaml.extensions.sound"* ]]; then
	mvn_deploy ummisco.gaml.extensions.sound
	mvn_deploy ummisco.gama.feature.audio
	
fi





cd msi.gama.parent 
mvn deploy -f tiny_pom.xml -DskipTests -T 8C -P p2Repo --settings ../settings.xml
cd -







