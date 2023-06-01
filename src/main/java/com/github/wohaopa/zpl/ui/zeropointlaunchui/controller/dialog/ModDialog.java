package com.github.wohaopa.zpl.ui.zeropointlaunchui.controller.dialog;

import javafx.scene.control.Dialog;

public class ModDialog extends Dialog<String> {

    public ModDialog(String modString){

        this.setTitle("修改MOD版本");

        this.setHeaderText("选择一个版本，ZPL会自动下载相应的mod");
        this.setContentText("标准仓库：");

    }
}
