KeyTool IUI - standalone
version #2.4.1


**** NEW ****
Support for Android's APK files.
  ==> sign APK files:
      from the tree, please select "Keytool IUI | Sign file with private key | Embedded signature | JAR"
      ==> the respective "Sign JAR file with private key entry" frame shows up on the right
          ==> click on the "APK" radio button", located at top right of the frame

  ==> verify signed APK files:
      from the tree, please select "Keytool IUI | Verify signed file | Embedded signature | JAR"
      ==> the respective "Verify signed JAR file" frame shows up on the right
          ==> click on the "APK" radio button", located at top right of the frame


**** REQUIREMENTS ****
JRE (Java Runtime Environment 1.6), or higher


**** IMPORTANT NOTICE: TO DO FIRST ****
Please edit the batch file (described below) and modify J2RE path, prior to launch the appli.

**** USAGE ****
open up a DOS (Windows) or a SHELL (Unix, Linux, Mac OS X), go to the directory that contains this readme file, next:

case 1) default Java is 1.6+
  . to launch "KeyTool IUI", type in:
      java -jar rc15ktl.jar


case 2) default Java not 1.6+:
  First edit the batch file, modify J2RE path, and save it prior to launch the appli.
  Then:
  . Windows:
       type in "run_ktl.bat"
  . Unix-Linux, Mac OS X:
      type in "./run_ktl.sh"


**** CONTENTS ****
contains:
. JAR:
  . rc15ktl.jar    ==> KeyTool IUI main application
  . rc15shr.jar    ==> YellowCat shared library
  . bcmail138.jar  ==> BouncyCastle SMIME/CMS
  . bcprov138.jar  ==> BouncyCastle provider
  . bctsp138.jar   ==> BouncyCastle TSP
  . jmf211ecor.jar ==> SUN's JMF core
  . jmf211eplg.jar ==> SUN's JMF plugin
  . jh2004jh.jar   ==> SUN's JavaHelp
  . rc15ktl_hs.jar ==> KeyTool IUI's Help
  . rc15shr_rs.jar ==> YellowCat shared resources
  . rc15ktl_rs.jar ==> KeyTool IUI main resources

. batch:
  . run_ktl.bat ==> DOS batch for KeyTool IUI
  . run_ktl.sh  ==> SHELL batch for KeyTool IUI


---
contact: yellowcat1@free.fr

Enjoy ...
