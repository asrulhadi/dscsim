
I. How to create a jar file for the app.

1.) in the dscsim directory execute the following command.
 
	"jar cfmv dscapp.jar ./META-INF/MANIFEST.MF -C bin . -C . etc -C . data"
	
	this will work if the Manifest.mf file in META-INF looks as follows:
	
	Manifest-Version: 1.0
	Created-By: 1.5.0_04 (Sun Microsystems Inc.)
	Main-Class: app.DscApp
	Class-Path: j3dcore.jar j3dutils.jar vecmath.jar

2.) No longer 3d app. Make sure that the j3d.dll is in the jre/bin directory of JAVA_HOME.
 
3.) to run the app execute:
	"java -jar dscapp.jar" 

4.)  to sign the jar:
jarsigner DscApp.jar mykey

5.) to generate the key.
keytool -genkey

To implement direct peer-to-peer communication between applets you'll 
have to sign your applet and have the user grant permission to the
applet to open sockets to any host.

Here's an excellent site with step-by-step instructions on how to 
write and deploy a signed applet.

http://download.baltimore.com/keytools/docs/v51/pro/j-docs/html/SampleCodes/sampleApplet/codesign/sa/sa_step0.html

//adding jar to the applet.
Add that jar file to the ARCHIVE= attribute of the &lt;APPLET tag and the browser will download it to get classes it needs.
They're either separated by a , or a ;


//sample html
<html>
  <head>
    <title></title>
  </head>
  <body>    
      <applet archive=dscapp.jar code=main.java.net.sourceforge.dscsim.controller.SingleDscApplet.class width=1536 height=460>
				<param name=mmsi value="211001602">
				<param name=iacMethod value="UDP">					
        alt="Your browser understands the &lt;APPLET&gt; tag but isn't running the applet, for some reason."
        Your browser is completely ignoring the &lt;APPLET&gt; tag!
     </applet>
</html>

Creating Mac OS executable.
/Developer/Application/Java Tools/Jar Bundler

See:http://java.sun.com/developer/technicalArticles/JavaLP/JavaToMac3/


the app file (dscsim.app) should be created in the dscsim root directory. The info.plist file found in ./dscsim.app/Contents should look as follows:
Adding a non jar file, as in "." to the classpath may have to be done manually.
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist SYSTEM "file://localhost/System/Library/DTDs/PropertyList.dtd">
<plist version="0.9">
<dict>
	<key>CFBundleName</key>
	<string>dscsim</string>
	<key>CFBundleVersion</key>
	<string>10.2</string>
	<key>CFBundleAllowMixedLocalizations</key>
	<string>true</string>
	<key>CFBundleExecutable</key>
	<string>JavaApplicationStub</string>
	<key>CFBundleDevelopmentRegion</key>
	<string>English</string>
	<key>CFBundlePackageType</key>
	<string>APPL</string>
	<key>CFBundleSignature</key>
	<string>????</string>
	<key>CFBundleInfoDictionaryVersion</key>
	<string>6.0</string>
	<key>CFBundleIconFile</key>
	<string>GenericJavaApp.icns</string>
	<key>Java</key>
	<dict>
		<key>VMOptions</key>
		<string>-Xms125m -Xmx512m</string>
		<key>MainClass</key>
		<string>net.sourceforge.dscsim.controller.SingleDscApplet</string>
		<key>JVMVersion</key>
		<string>1.4+</string>
		<key>ClassPath</key>
			<array>
			<string>/Users/william/sourceforge/dscsim/lib/tritonus_share.jar</string>
			<string>/Users/william/sourceforge/dscsim/lib/dscapp.jar</string>
			<string>/Users/william/sourceforge/dscsim/lib/jdom.jar</string>
			<string>/Users/william/sourceforge/dscsim/lib/incubator-activemq-4.0.1.jar</string>
			<string>/Users/william/sourceforge/dscsim/lib/jspeex.jar</string>
			<string>/Users/william/sourceforge/dscsim</string>
			</array>
		<key>Properties</key>
		<dict>
			<key>apple.laf.useScreenMenuBar</key>
			<string>true</string>
			<key>com.apple.macos.useScreenMenuBar</key>
			<string>true</string>
			<key>com.apple.hwaccel</key>
			<string>true</string>
		</dict>
	</dict>
</dict>
</plist>





