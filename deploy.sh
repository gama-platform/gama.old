#!/bin/bash


change=$(git log --pretty=format: --name-only --since="1 hour ago")



if [[ ${change} == *"ummisco.gama.annotations"* ]] || [[ $MSG == *"ci ummisco.gama.annotations"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd ummisco.gama.annotations 
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi

if [[ ${change} == *"msi.gama.processor"* ]] || [[ $MSG == *"ci msi.gama.processor"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd msi.gama.processor 
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi
















if [[ ${change} == *"msi.gama.ext"* ]] || [[ $MSG == *"ci ext"* ]]; then
	cd msi.gama.ext 
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -	
	cd ummisco.gama.feature.dependencies 
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi


	
	
	
if [[ ${change} == *"msi.gama.core"* ]] || [[ $MSG == *"ci msi.gama.core"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd msi.gama.core 
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi




if [[ ${change} == *"msi.gama.lang.gaml"* ]] || [[ $MSG == *"ci msi.gama.lang.gaml"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd msi.gama.lang.gaml
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi




if [[ ${change} == *"msi.gama.documentation"* ]] || [[ $MSG == *"ci msi.gama.documentation"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd msi.gama.documentation
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi






if [[ ${change} == *"ummisco.gama.ui.shared"* ]] || [[ $MSG == *"ci ummisco.gama.ui.shared"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd ummisco.gama.ui.shared
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi




if [[ ${change} == *"ummisco.gama.ui.navigator"* ]] || [[ $MSG == *"ci ummisco.gama.ui.navigator"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd ummisco.gama.ui.navigator
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi





if [[ ${change} == *"ummisco.gama.ui.modeling"* ]] || [[ $MSG == *"ci ummisco.gama.ui.modeling"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd ummisco.gama.ui.modeling
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi





if [[ ${change} == *"ummisco.gama.ui.experiment"* ]] || [[ $MSG == *"ci ummisco.gama.ui.experiment"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd ummisco.gama.ui.experiment
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi


if [[ ${change} == *"msi.gama.application"* ]] || [[ $MSG == *"ci msi.gama.application"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd msi.gama.application
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi




if [[ ${change} == *"msi.gaml.extensions.fipa"* ]] || [[ $MSG == *"ci msi.gaml.extensions.fipa"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd msi.gaml.extensions.fipa
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi


if [[ ${change} == *"msi.gama.headless"* ]] || [[ $MSG == *"ci msi.gama.headless"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd msi.gama.headless
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi



if [[ ${change} == *"simtools.gaml.extensions.traffic"* ]] || [[ $MSG == *"ci simtools.gaml.extensions.traffic"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd simtools.gaml.extensions.traffic
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi


if [[ ${change} == *"simtools.gaml.extensions.physics"* ]] || [[ $MSG == *"ci simtools.gaml.extensions.physics"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd simtools.gaml.extensions.physics
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi

if [[ ${change} == *"irit.gaml.extensions.database"* ]] || [[ $MSG == *"ci irit.gaml.extensions.database"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd irit.gaml.extensions.database
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi


if [[ ${change} == *"msi.gama.models"* ]] || [[ $MSG == *"ci msi.gama.models"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd msi.gama.models
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
	
	cd ummisco.gama.feature.models
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -		
	
fi





if [[ ${change} == *"msi.gaml.architecture.simplebdi"* ]] || [[ $MSG == *"ci msi.gaml.architecture.simplebdi"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd msi.gaml.architecture.simplebdi
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi


if [[ ${change} == *"simtools.graphanalysis.fr"* ]] || [[ $MSG == *"ci simtools.graphanalysis.fr"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd simtools.graphanalysis.fr
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
	cd simtools.graphlayout.feature
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -		
fi


if [[ ${change} == *"ummisco.gama.opengl"* ]] || [[ $MSG == *"ci ummisco.gama.opengl"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd ummisco.gama.opengl
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi



if [[ ${change} == *"ummisco.gama.java2d"* ]] || [[ $MSG == *"ci ummisco.gama.java2d"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd ummisco.gama.java2d
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi



if [[ ${change} == *"ummisco.gama.ui.viewers"* ]] || [[ $MSG == *"ci ummisco.gama.ui.viewers"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd ummisco.gama.ui.viewers
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi




if [[ ${change} == *"ummisco.gama.serialize"* ]] || [[ $MSG == *"ci ummisco.gama.serialize"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd ummisco.gama.serialize
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
	cd ummisco.gama.feature.serialize
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -		
fi



if [[ ${change} == *"ummisco.gama.network"* ]] || [[ $MSG == *"ci ummisco.gama.network"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd ummisco.gama.network
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
	cd ummisco.gama.feature.network
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -		
fi


if [[ ${change} == *"ummisco.gaml.extensions.maths"* ]] || [[ $MSG == *"ci ummisco.gaml.extensions.maths"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd ummisco.gaml.extensions.maths
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi




if [[ ${change} == *"ummisco.gaml.extensions.sound"* ]] || [[ $MSG == *"ci ummisco.gaml.extensions.sound"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd ummisco.gaml.extensions.sound
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
	cd ummisco.gama.feature.audio
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
	
fi




if [[ ${change} == *"ummisco.gaml.extensions.stats"* ]] || [[ $MSG == *"ci ummisco.gaml.extensions.stats"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd ummisco.gaml.extensions.stats
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
	cd ummisco.gama.feature.stats
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -		
fi





if [[ ${change} == *"msi.gama.core"* ]] || [[ ${change} == *"msi.gama.headless"* ]] || [[ ${change} == *"msi.gama.lang.gaml"* ]] || [[ ${change} == *"msi.gama.processor"* ]] || [[ ${change} == *"ummisco.gama.annotations"* ]] || [[ ${change} == *"ummisco.gama.annotations"* ]] || [[ $MSG == *"ci ummisco.gama.feature.core"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd ummisco.gama.feature.core
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi





if [[ ${change} == *"irit.gaml.extensions.database"* ]] || [[ ${change} == *"msi.gaml.extensions.fipa"* ]] || [[ ${change} == *"simtools.gaml.extensions.traffic"* ]] || [[ ${change} == *"msi.gaml.architecture.simplebdi"* ]] || [[ ${change} == *"ummisco.gaml.extensions.maths"* ]] || [[ ${change} == *"simtools.gaml.extensions.physics"* ]] || [[ ${change} == *"ummisco.gama.network"* ]] || [[ ${change} == *"ummisco.gama.serialize"* ]] || [[ $MSG == *"ci ummisco.gama.feature.core.extensions"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd ummisco.gama.feature.core.extensions
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi






if [[ ${change} == *"msi.gama.application"* ]] || [[ ${change} == *"ummisco.gama.ui.shared"* ]] || [[ $MSG == *"ci ummisco.gama.feature.core.ui"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd ummisco.gama.feature.core.ui
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi






if [[ ${change} == *"ummisco.gama.java2d"* ]] || [[ ${change} == *"ummisco.gama.ui.experiment"* ]] || [[ ${change} == *"ummisco.gama.opengl"* ]] || [[ $MSG == *"ci ummisco.gama.feature.experiment.ui"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd ummisco.gama.feature.experiment.ui
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi






if [[ ${change} == *"ummisco.gama.ui.modeling"* ]] || [[ ${change} == *"ummisco.gama.ui.navigator"* ]] || [[ ${change} == *"ummisco.gama.ui.viewers"* ]] || [[ $MSG == *"ci ummisco.gama.feature.modeling.ui"* ]] || [[ $MSG == *"ci fullbuild"* ]]; then
	cd ummisco.gama.feature.modeling.ui
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi
	
	
	
	

















cd msi.gama.parent
mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
cd -


