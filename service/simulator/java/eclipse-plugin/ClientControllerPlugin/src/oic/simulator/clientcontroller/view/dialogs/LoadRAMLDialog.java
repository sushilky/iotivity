/*
 * Copyright 2015 Samsung Electronics All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package oic.simulator.clientcontroller.view.dialogs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import org.oic.simulator.ILogger.Level;

import oic.simulator.clientcontroller.Activator;
import oic.simulator.clientcontroller.utils.Constants;
import oic.simulator.clientcontroller.utils.Utility;

/**
 * This dialog is used for loading the RAML file.
 */
public class LoadRAMLDialog extends TitleAreaDialog {

    private Text   locationTxt;
    private Button btnBrowse;
    private String configFilePath;

    public LoadRAMLDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public void create() {
        super.create();
        setTitle("Load Remote Resource Configuration");
        setMessage("Select the RAML Configuration file of the resource to enable Automation.");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite compLayout = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(compLayout, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(3, false);
        layout.verticalSpacing = 10;
        layout.marginTop = 10;
        container.setLayout(layout);

        Label loadRamlLbl = new Label(container, SWT.NONE);
        loadRamlLbl.setText("Load RAML File");
        GridData gd;
        gd = new GridData();
        gd.horizontalSpan = 3;
        loadRamlLbl.setLayoutData(gd);

        Label locationLbl = new Label(container, SWT.NONE);
        locationLbl.setText("Location:");

        locationTxt = new Text(container, SWT.BORDER);
        gd = new GridData();
        gd.minimumWidth = 300;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        locationTxt.setLayoutData(gd);

        btnBrowse = new Button(container, SWT.NONE);
        btnBrowse.setText("Browse");
        gd = new GridData();
        gd.widthHint = 80;
        gd.horizontalAlignment = SWT.FILL;
        btnBrowse.setLayoutData(gd);

        addUIListeners();

        return compLayout;
    }

    private void addUIListeners() {
        btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog fileDialog = new FileDialog(PlatformUI
                        .getWorkbench().getDisplay().getActiveShell(), SWT.NONE);
                fileDialog
                        .setFilterExtensions(Constants.BROWSE_RAML_FILTER_EXTENSIONS);
                String path = fileDialog.open();
                if (null == path) {
                    configFilePath = "";
                } else {
                    configFilePath = path;
                }
                locationTxt.setText(configFilePath);
            }
        });
    }

    public String getConfigFilePath() {
        return configFilePath;
    }

    @Override
    protected void okPressed() {
        configFilePath = locationTxt.getText();
        if (null == configFilePath) {
            return;
        }

        FileInputStream fileStream = null;
        try {
            fileStream = new FileInputStream(configFilePath);
        } catch (Exception e) {
            Activator
                .getDefault()
                .getLogManager()
                .log(Level.ERROR.ordinal(), new Date(),
                        Utility.getSimulatorErrorString(e, null));
            MessageDialog
                    .openError(getShell(), "Invalid File",
                            "File doesn't exist. Either the file path or file name is invalid.");
            return;
        } finally {
            if (null != fileStream) {
                try {
                    fileStream.close();
                } catch (IOException e) {
                    Activator
                            .getDefault()
                            .getLogManager()
                            .log(Level.ERROR.ordinal(),
                                    new Date(),
                                    "There is an error while closing the file stream.\n"
                                            + Utility.getSimulatorErrorString(
                                                    e, null));
                }
            }
        }
        close();
    }

    @Override
    public boolean isHelpAvailable() {
        return false;
    }
}
