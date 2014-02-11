
//var shell = WScript.CreateObject("WScript.Shell");
var shell = new ActiveXObject("WScript.Shell");
   var fso, f, fc, s
   var keystore = "tomoKey.jks";
   var password = "614331";
	fso = new ActiveXObject ("Scripting.FileSystemObject");

   f = fso.GetFolder (".");

   files = new Enumerator (f.files);
   for (; !files.atEnd(); files.moveNext())
   {
       fi = files.item ();
		
       var fext = fso.GetExtensionName (fi).toUpperCase ();
       if (fext == "JAR")
       {
          s = fso.GetFileName (fi);
		  		shell.Run("jarsigner -keystore " + keystore + " -storepass " + password +" "+ s + " tomo");
          if (s.toLowerCase () != "runme.sql")
          {
          }
       }
   }

