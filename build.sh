cd msi.gama.parent &&
mvn -U clean && mvn -X dependency:purge-local-repository  clean install &&
cd -

