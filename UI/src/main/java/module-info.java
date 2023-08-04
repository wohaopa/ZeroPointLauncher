module com.github.wohaopa.zplui {
  requires javafx.controls;
  requires com.jfoenix;
  requires java.desktop;
  requires java.base;
  requires ZeroPointLaunch.Core;
  requires cn.hutool.json;

  exports com.github.wohaopa.zplui;

  opens assets.css;
  opens assets.img;
}
