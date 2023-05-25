module com.github.wohaopa.zpl.ui.zeropointlaunchui {
  requires javafx.controls;
  requires javafx.fxml;
  requires rxcontrols;

  opens com.github.wohaopa.zpl.ui.zeropointlaunchui to
      javafx.fxml;

  exports com.github.wohaopa.zpl.ui.zeropointlaunchui;
  exports com.github.wohaopa.zpl.ui.zeropointlaunchui.controller;

  opens com.github.wohaopa.zpl.ui.zeropointlaunchui.controller to
      javafx.fxml;
}
