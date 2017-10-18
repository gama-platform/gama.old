 #!/bin/bash 

function mvn_install() {
	echo "Building"  $1
	cd $1
	mvn -q clean install -T 8C
	res=$?
	if [[ $res -gt 0 ]]; then
		exit $res
	fi
	cd -
}
projects=('ummisco.gama.annotations' 'msi.gama.processor' 'msi.gama.ext' 'ummisco.gama.feature.dependencies' 'msi.gama.core' 'msi.gama.lang.gaml' 'msi.gama.documentation' 'ummisco.gama.ui.shared' 'ummisco.gama.ui.navigator' 'ummisco.gama.ui.modeling'  'ummisco.gama.ui.experiment'  'msi.gama.application'  'msi.gaml.extensions.fipa'  'msi.gama.headless'  'simtools.gaml.extensions.traffic'  'simtools.gaml.extensions.physics'  'irit.gaml.extensions.database'  'msi.gama.models'  'ummisco.gama.feature.models'  'msi.gaml.architecture.simplebdi'  'simtools.graphanalysis.fr'  'simtools.graphlayout.feature'  'ummisco.gama.opengl'  'ummisco.gama.java2d'  'ummisco.gama.ui.viewers'  'ummisco.gama.serialize'  'ummisco.gama.feature.serialize' 'ummisco.gama.network' 'ummisco.gama.feature.network' 'ummisco.gaml.extensions.maths' 'ummisco.gaml.extensions.sound' 'ummisco.gama.feature.audio' 'ummisco.gaml.extensions.stats' 'ummisco.gama.feature.stats' 'ummisco.gama.feature.core' 'ummisco.gama.feature.core.extensions' 'ummisco.gama.feature.core.ui' 'ummisco.gama.feature.experiment.ui' 'ummisco.gama.feature.modeling.ui' 'msi.gama.parent');

for i in ${projects[@]}; do
	mvn_install $i
done