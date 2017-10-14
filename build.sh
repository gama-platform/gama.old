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


cd msi.gama.core 
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -


cd msi.gama.lang.gaml
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -

cd msi.gama.documentation
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -


cd ummisco.gama.ui.shared
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -


cd ummisco.gama.ui.navigator
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -

cd ummisco.gama.ui.modeling
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -

cd ummisco.gama.ui.experiment
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -

cd msi.gama.application
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -

cd msi.gaml.extensions.fipa
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -

cd msi.gama.headless
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -

cd simtools.gaml.extensions.traffic
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -

cd simtools.gaml.extensions.physics
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -

cd irit.gaml.extensions.database
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -

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

cd msi.gaml.architecture.simplebdi
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -

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


cd ummisco.gama.opengl
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -


cd ummisco.gama.java2d
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -

cd ummisco.gama.ui.viewers
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -

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


cd ummisco.gaml.extensions.maths
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -

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

cd ummisco.gama.feature.core
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -

cd ummisco.gama.feature.core.extensions
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -


cd ummisco.gama.feature.core.ui
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -

cd ummisco.gama.feature.experiment.ui
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -



cd ummisco.gama.feature.modeling.ui
mvn clean install
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -



cd msi.gama.parent 
mvn clean install 
res=$?
if [[ $res -gt 0 ]]; then
	exit $res
fi
cd -
