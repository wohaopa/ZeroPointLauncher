package com.github.wohaopa.zpl.ui.zplui.scene;

import com.github.wohaopa.zpl.ui.zplui.Instances;
import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class InstanceView extends BaseMyScene {
    public InstanceView() {
        super(() -> {
            var rootPane = new StackPane();

            var mainPane = new HBox();

            var treeView = new JFXTreeView<String>();
            {

                var rootTreeItem = new TreeItem<>("实例继承图");
                {


                    Map<String, TreeItem> cache = new HashMap<>();
                    Instances.instancesProperty().get().forEach(o -> {
                        if (o instanceof String obj) {
                            cache.put(obj, new TreeItem<>(obj));
                        }
                    });
                    cache.forEach((s, o) -> {
//                        if (o.toString().endsWith("."))
                        rootTreeItem.getChildren().add(o);
                    });
                }

                treeView.setRoot(rootTreeItem);
                treeView.setShowRoot(true);

                var dialog = new JFXDialog();
                {
                    var layout = new JFXDialogLayout();
                    layout.getStyleClass().add("dialog-layout");

                    var heading = new Label("新建实例");
                    heading.getStyleClass().add("dialog-title");
                    layout.setHeading(heading);


                    var tabPane = new TabPane();
                    {
                        var zplTab = new Tab();
                        {
                            zplTab.setText("导出包安装");
                            zplTab.setClosable(false);
                            var content = new VBox();

                            var validator = new RequiredFieldValidator();
                            validator.setMessage("必填！");

                            var textField1 = new JFXTextField();
                            textField1.setPromptText("实例名");
                            textField1.getStyleClass().add("zpl-text-field");

                            textField1.getValidators().add(validator);
                            textField1.focusedProperty().addListener((o, oldVal, newVal) -> {
                                if (!newVal) {
                                    textField1.validate();
                                }
                            });


                            var textField2 = new JFXTextField();
                            textField2.setPromptText("安装包路径");
                            textField2.getStyleClass().add("zpl-text-field");

                            textField2.getValidators().add(validator);
                            textField2.focusedProperty().addListener((o, oldVal, newVal) -> {
                                if (!newVal) {
                                    textField2.validate();
                                }
                            });

                            var btn = new JFXButton();
                            btn.setText("安装");
                            btn.getStyleClass().add("zpl-button");


                            content.setAlignment(Pos.CENTER);
                            content.setSpacing(25);
                            content.getChildren().addAll(textField1, textField2, btn);

                            textField1.widthProperty().addListener((observable, oldValue, newValue) -> {
                                if (newValue != null) btn.setPrefWidth(newValue.doubleValue());
                            });

                            content.setPadding(new Insets(20, 0, 20, 0));

                            zplTab.setContent(content);
                        }
                        var onlineTab = new Tab();
                        {
                            onlineTab.setText("在线安装");
                            onlineTab.setClosable(false);
                            var content = new VBox();

                            var validator = new RequiredFieldValidator();
                            validator.setMessage("必填！");

                            var textField1 = new JFXTextField();
                            textField1.setPromptText("实例名");
                            textField1.getStyleClass().add("zpl-text-field");


                            textField1.getValidators().add(validator);
                            textField1.focusedProperty().addListener((o, oldVal, newVal) -> {
                                if (!newVal) {
                                    textField1.validate();
                                }
                            });


                            var jfxComboBox = new JFXComboBox<>();
                            jfxComboBox.setPromptText("远程版本");
                            jfxComboBox.getStyleClass().add("zpl-combo-box");

                            jfxComboBox.getValidators().add(validator);
                            jfxComboBox.focusedProperty().addListener((o, oldVal, newVal) -> {
                                if (!newVal) {
                                    jfxComboBox.validate();
                                }
                            });
                            var btn = new JFXButton();
                            btn.setText("安装");
                            btn.getStyleClass().add("zpl-button");

                            textField1.widthProperty().addListener((observable, oldValue, newValue) -> {
                                if (newValue != null) {
                                    jfxComboBox.setPrefWidth(newValue.doubleValue());
                                    btn.setPrefWidth(newValue.doubleValue());
                                }
                            });

                            content.setAlignment(Pos.CENTER);
                            content.setSpacing(25);
                            content.getChildren().addAll(textField1, jfxComboBox, btn);

                            content.setPadding(new Insets(20, 0, 20, 0));
                            onlineTab.setContent(content);

                        }
                        var gtnhtab = new Tab();
                        {
                            gtnhtab.setText("GTNH压缩包");
                            gtnhtab.setClosable(false);
                            var content0 = new ScrollPane();

                            var content = new VBox();

                            var validator = new RequiredFieldValidator();
                            validator.setMessage("必填！");

                            var textField1 = new JFXTextField();
                            textField1.setPromptText("实例名");
                            textField1.getStyleClass().add("zpl-text-field");
                            textField1.getValidators().add(validator);
                            textField1.focusedProperty().addListener((o, oldVal, newVal) -> {
                                if (!newVal) {
                                    textField1.validate();
                                }
                            });

                            var textField2 = new JFXTextField();
                            textField2.setPromptText("版本");
                            textField2.getStyleClass().add("zpl-text-field");
                            textField2.getValidators().add(validator);
                            textField2.focusedProperty().addListener((o, oldVal, newVal) -> {
                                if (!newVal) {
                                    textField2.validate();
                                }
                            });


                            var jfxComboBox = new JFXComboBox<>();
                            jfxComboBox.setPromptText("安装包路径");
                            jfxComboBox.getStyleClass().add("zpl-combo-box");

                            jfxComboBox.getValidators().add(validator);
                            jfxComboBox.focusedProperty().addListener((o, oldVal, newVal) -> {
                                if (!newVal) {
                                    jfxComboBox.validate();
                                }
                            });
                            var btn = new JFXButton();
                            btn.setText("安装");
                            btn.getStyleClass().add("zpl-button");

                            textField1.widthProperty().addListener((observable, oldValue, newValue) -> {
                                if (newValue != null) {
                                    jfxComboBox.setPrefWidth(newValue.doubleValue());
                                    btn.setPrefWidth(newValue.doubleValue());
                                }
                            });

                            content.setAlignment(Pos.CENTER);
                            content.setSpacing(25);
                            content.getChildren().addAll(textField1,textField2, jfxComboBox, btn);

                            content.setPadding(new Insets(20, 0, 20, 0));

                            content0.widthProperty().addListener((observable, oldValue, newValue) -> {
                                if(newValue!=null)content.setPrefWidth(newValue.doubleValue()*0.95);
                            });

                            content0.getStyleClass().add("zpl-content");
                            content0.setContent(content);

                            gtnhtab.setContent(content0);

                        }

                        tabPane.getTabs().addAll(zplTab, onlineTab, gtnhtab);
                        tabPane.getSelectionModel().selectFirst();

                    }

                    layout.setBody(tabPane);

                    dialog.setContent(layout);
                }


                var menu = new ContextMenu();
                {

                    var newInstance = new MenuItem("新建实例");
                    newInstance.setOnAction(event -> {
                        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
                        dialog.show(rootPane);
                    });
                    menu.getItems().addAll(newInstance);
                }
                treeView.setContextMenu(menu);
            }

            var gridPane = new GridPane();
            {
                gridPane.getStyleClass().add("instance-info-grid");

                ColumnConstraints column1 = new ColumnConstraints(70, 70, 100);
                ColumnConstraints column2 = new ColumnConstraints(50, 150, 300);
                column1.setHgrow(Priority.ALWAYS);
                gridPane.getColumnConstraints().addAll(column1, column2);


                gridPane.add(new Label("实例名："), 0, 0);
                gridPane.add(new Label("版本："), 0, 1);
                gridPane.add(new Label("继承："), 0, 2);
                gridPane.add(new Label("启动器："), 0, 3);
                gridPane.add(new Label("分享器："), 0, 4);
                gridPane.add(new Label("自更新："), 0, 5);


                gridPane.add(textFileWrapper("测试用例"), 1, 0);
                gridPane.add(textFileWrapper("测试用例版本"), 1, 1);
                gridPane.add(textFileWrapper("null"), 1, 2);
                gridPane.add(textFileWrapper("默认"), 1, 3);
                gridPane.add(textFileWrapper("默认"), 1, 4);
                gridPane.add(textFileWrapper("False"), 1, 5);

            }
            var controlView = new VBox();
            {
                controlView.getStyleClass().add("control-view");
                var openInstanceDir = new JFXButton("打开实例目录");
                var openRuntimeDir = new JFXButton("打开运行目录");
                var openModsManager = new JFXButton("Mods管理");
                {
                    var modsManager = new JFXDialog();
                    {
                        var layout = new JFXDialogLayout();
                        layout.getStyleClass().add("dialog-layout");

                        var heading = new Label("Mods管理");
                        heading.getStyleClass().add("dialog-title");
                        layout.setHeading(heading);

                        var body = new JFXTreeView<>();
                        {
                        }

                        modsManager.setContent(layout);
                    }

                    openModsManager.setOnAction(event -> {
                        modsManager.setTransitionType(JFXDialog.DialogTransition.CENTER);
                        modsManager.show(rootPane);
                    });
                }

                var openMapManager = new JFXButton("映射管理");
                var refreshInstance = new JFXButton("刷新实例");
                var newSubInstance = new JFXButton("新建子实例");
                var updateVersion = new JFXButton("版本更新");
                var extractInstance = new JFXButton("导出实例");

                controlView.getChildren().addAll(openInstanceDir, openRuntimeDir, openModsManager, openMapManager, refreshInstance, newSubInstance, updateVersion, extractInstance);
            }

            mainPane.widthProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) return;
                var size = newValue.doubleValue() * 0.333;
                treeView.setPrefWidth(size);
                gridPane.setPrefWidth(size);
                controlView.setPrefWidth(size);
            });
            mainPane.getChildren().addAll(treeView, gridPane, controlView);

            rootPane.getChildren().add(mainPane);
            return rootPane;
        });
    }

    private static Node textFileWrapper(String s) {
        var textField = new JFXTextField();
        textField.setText(s);
//        textField.textProperty().bindBidirectional();

        return textField;

    }

    @Override
    public Parent getIcon() {
        var img = new Label("实例");
        img.setTextFill(Color.WHITE);
        return img;
    }
}
