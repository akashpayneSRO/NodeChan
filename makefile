NODE_DEPENDS = lib/WaifUPnP.jar src/ChanPost.java src/ChanThread.java src/GUIAddPeer.java src/GUICreateNewThread.java src/GUIMain.java src/GUIRightClickMenu.java src/GUIThreadView.java src/IncomingThread.java src/NodeChan.java src/OutgoingThread.java src/Peer.java src/RequestedThreadSender.java
JAR_DEPENDS = NodeChan.jar manifest.mf lib/WaifUPnP.jar

NodeChan.jar: $(NODE_DEPENDS)
	javac -cp lib/WaifUPnP.jar src/*.java -d build

jar: $(JAR_DEPENDS)
	make
	jar cfm NodeChan.jar manifest.mf build/ lib/*

	# compress the distributable tar file
	tar cvzf NodeChan.tar.gz NodeChan.jar build/ lib/ 

clean:
	rm -v NodeChan.jar
	rm -v NodeChan.tar.gz
	# for safety, prompting the user if its okay to remove the build dir
	rm -rIv build

