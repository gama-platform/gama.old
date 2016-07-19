cd msi.gama.parent &&
mvn install:install-file -Dfile=file://home/travis/build/gama-platform/gama/msi.gama.processor/processor/plugins/msi.gama.processor-1.4.0.jar -DgroupId=msi.gama.processor -Dversion=1.4.0 -Dpackaging=jar && mvn -X clean install &&
cd -

