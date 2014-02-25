  <?xml version='1.0' encoding='ISO-8859-1' ?>
  <!DOCTYPE helpset  PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN" "../dtd/helpset_2_0.dtd">
  <helpset version="1.0">
    <!-- title -->
    <title>Index</title>
 
    <!-- maps -->
    <maps>
      <homeID>top</homeID>
      <mapref location="Map_en.jhm"/>
    </maps>
 
    <!-- views -->
    <view>
      <name>TOC</name>
      <label>Index</label>
      <type>javax.help.TOCView</type>
      <data>toc_en.xml</data>
    </view>


	
    <presentation default="true" displayviewimages="false">
      <name>Window</name>
      <size width="900" height="400" />
      <location x="200" y="200" />
      <title>Help</title>
      <image>toplevelfolder</image>
      <toolbar>
        <helpaction>javax.help.BackAction</helpaction>
        <helpaction>javax.help.ForwardAction</helpaction>
        <helpaction>javax.help.SeparatorAction</helpaction>
        <helpaction>javax.help.SeparatorAction</helpaction>
        <helpaction>javax.help.PrintAction</helpaction>
        <helpaction>javax.help.PrintSetupAction</helpaction>
      </toolbar>
    </presentation>
    <presentation>
      <name>other</name>
      <size width="600" height="400" />
      <location x="200" y="200" />
      <title>Помощь</title>
    </presentation>
  </helpset>