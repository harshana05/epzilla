@echo on
start rmiregistry
java -classpath %CD%\target\LCRRMIServer-0.0.1.jar -Djava.security.policy=%CD%\policy.txt -Djava.rmi.server.codebase=file:%CD%\target\LCRRMIServer-0.0.1.jar org.epzilla.leader.EpzillaNodeInitiator
pause
