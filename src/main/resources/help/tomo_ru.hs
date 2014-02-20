  <?xml version='1.0' encoding='ISO-8859-1' ?>
  <!DOCTYPE helpset  PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN" "../dtd/helpset_2_0.dtd">
  <helpset version="1.0">
    <!-- title -->
    <title>Помощь!! ! HELP</title>
 
    <!-- maps -->
    <maps>
      <homeID>top</homeID>
      <mapref location="Map.jhm"/>
    </maps>
 
    <!-- views -->
    <view>
      <name>TOC</name>
      <label>Содержание</label>
      <type>javax.help.TOCView</type>
      <data>toc.xml</data>
    </view>


	
    <presentation default="true" displayviewimages="false">
      <name>Главное окно</name>
      <size width="700" height="400" />
      <location x="200" y="200" />
      <title>Помощь</title>
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
      <name>другое</name>
      <size width="400" height="400" />
      <location x="200" y="200" />
      <title>Помощь</title>
    </presentation>
  </helpset>