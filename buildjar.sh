./build.sh
jar cfm NodeChan.jar manifest.mf build/ lib/*

# compress the distributable tar file
tar cvzf NodeChan.tar.gz NodeChan.jar build/ lib/ 
