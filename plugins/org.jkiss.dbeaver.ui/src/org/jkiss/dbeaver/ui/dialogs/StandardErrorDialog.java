/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2017 Serge Rider (serge@jkiss.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jkiss.dbeaver.ui.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.ui.TextUtils;
import org.jkiss.dbeaver.ui.UIUtils;

/**
 * StandardErrorDialog
 */
public class StandardErrorDialog extends ErrorDialog {

    private static final String DIALOG_ID = "DBeaver.StandardErrorDialog";//$NON-NLS-1$
    private Text messageText;
    private boolean detailsVisible = false;

    public StandardErrorDialog(
        @NotNull Shell parentShell,
        @NotNull String dialogTitle,
        @Nullable String message,
        @NotNull IStatus status,
        int displayMask)
    {
        super(parentShell, dialogTitle, message, status, displayMask);
        setStatus(status);
        this.message = TextUtils.cutExtraLines(message == null ? status.getMessage()
                : JFaceResources.format("Reason", message, status.getMessage()), 20); //$NON-NLS-1$
    }

    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return UIUtils.getDialogSettings(DIALOG_ID);
    }

    protected Control createDialogArea(Composite parent) {
        return createMessageArea(parent);
    }

    protected Control createMessageArea(Composite composite) {
        // create composite
        // create image
        Image image = getImage();
        if (image != null) {
            imageLabel = new Label(composite, SWT.NULL);
            image.setBackground(imageLabel.getBackground());
            imageLabel.setImage(image);
            GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(imageLabel);
        }
        // create message
        if (message != null) {
            messageText = new Text(composite, SWT.READ_ONLY | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
            messageText.setText(message);
            GridData gd = new GridData(GridData.FILL_BOTH);
            gd.minimumWidth = IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH;
            gd.heightHint = UIUtils.getFontHeight(composite) * 10;
            gd.grabExcessVerticalSpace = true;
            gd.grabExcessHorizontalSpace = true;
            messageText.setLayoutData(gd);
        }
        return composite;
    }

    @Override
    public void create() {
        super.create();
        detailsVisible = getDialogBoundsSettings().getBoolean("showDetails");
        if (detailsVisible) {
            showDetailsArea();
        }
    }

    @Override
    protected List createDropDownList(Composite parent) {
        detailsVisible = true;
        List dropDownList = super.createDropDownList(parent);
        dropDownList.addDisposeListener(e -> {
            detailsVisible = false;
        });
        return dropDownList;
    }

    @Override
    protected void okPressed() {
        getDialogBoundsSettings().put("showDetails", detailsVisible);
        super.okPressed();
    }
}