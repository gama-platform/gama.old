 #!/bin/bash 

function mvn_install() {
	echo "Building"  $1
	cd $1
	mvn clean install -o
	res=$?
	if [[ $res -gt 0 ]]; then
		exit $res
	fi
	cd -
}
projects=('ummisco.gama.annotations' 'msi.gama.processor' 'msi.gama.ext'
'msi.gama.core'
'msi.gama.lang.gaml'
'msi.gama.documentation'
'msi.gama.application'
'ummisco.gama.ui.shared'
'ummisco.gama.ui.navigator'
'ummisco.gama.ui.modeling'
'ummisco.gama.ui.experiment'
'msi.gama.models'
'ummisco.gama.feature.models'
'ummisco.gama.feature.dependencies'
'irit.gaml.extensions.database'
'simtools.gaml.extensions.traffic'
'ummisco.gaml.extensions.maths'
'msi.gama.headless'
'ummisco.gaml.extensions.stats'
'ummisco.gama.feature.core'
'msi.gaml.architecture.simplebdi'
'msi.gaml.extensions.fipa'
'simtools.gaml.extensions.physics'
'ummisco.gama.serialize'
'ummisco.gama.network'
'ummisco.gama.feature.core.extensions'
'ummisco.gama.feature.core.ui'
'ummisco.gama.java2d'
'ummisco.gama.opengl'
'ummisco.gama.feature.experiment.ui'
'ummisco.gama.ui.viewers'
'ummisco.gama.feature.modeling.ui'
'ummisco.gama.feature.serialize'
'ummisco.gama.feature.network'
'simtools.graphanalysis.fr'
'simtools.graphlayout.feature'
'ummisco.gaml.extensions.sound'
'ummisco.gama.feature.audio'
'ummisco.gama.feature.stats'
'ummisco.gama.product' 'msi.gama.parent' );

for i in ${projects[@]}; do
	mvn_install $i
done