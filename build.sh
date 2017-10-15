 #!/bin/bash 

function mvn_install() {
	echo "Building"  $1
	cd $1
	mvn clean install 
	res=$?
	if [[ $res -gt 0 ]]; then
		exit $res
	fi
	cd -
}

mvn_install ummisco.gama.annotations

mvn_install msi.gama.processor

mvn_install msi.gama.ext
mvn_install ummisco.gama.feature.dependencies


mvn_install msi.gama.core


mvn_install msi.gama.lang.gam

mvn_install msi.gama.documentation


mvn_install ummisco.gama.ui.shared


mvn_install ummisco.gama.ui.navigator

mvn_install ummisco.gama.ui.modeling

mvn_install ummisco.gama.ui.experiment

mvn_install msi.gama.application

mvn_install msi.gaml.extensions.fipa

mvn_install msi.gama.headless

mvn_install simtools.gaml.extensions.traffic

mvn_install simtools.gaml.extensions.physics

mvn_install irit.gaml.extensions.database

mvn_install msi.gama.models

mvn_install ummisco.gama.feature.models

mvn_install msi.gaml.architecture.simplebdi

mvn_install simtools.graphanalysis.fr
mvn_install simtools.graphlayout.feature


mvn_install ummisco.gama.opengl


mvn_install ummisco.gama.java2d

mvn_install ummisco.gama.ui.viewers

mvn_install ummisco.gama.serialize
mvn_install ummisco.gama.feature.serialize

mvn_install ummisco.gama.network
mvn_install ummisco.gama.feature.network


mvn_install ummisco.gaml.extensions.maths

mvn_install ummisco.gaml.extensions.sound
mvn_install ummisco.gama.feature.audio

mvn_install ummisco.gaml.extensions.stats
mvn_install ummisco.gama.feature.stats

mvn_install ummisco.gama.feature.core

mvn_install ummisco.gama.feature.core.extensions


mvn_install ummisco.gama.feature.core.ui

mvn_install ummisco.gama.feature.experiment.ui



mvn_install ummisco.gama.feature.modeling.ui


mvn_install msi.gama.parent
