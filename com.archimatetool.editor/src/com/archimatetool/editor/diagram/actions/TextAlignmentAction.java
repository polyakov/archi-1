/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.editor.diagram.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.RetargetAction;

import com.archimatetool.editor.diagram.commands.TextAlignmentCommand;
import com.archimatetool.editor.ui.IArchimateImages;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IFontAttribute;
import com.archimatetool.model.ILockable;



/**
 * Text Alignment Action
 * 
 * @author Phillip Beauvoir
 */
public class TextAlignmentAction extends SelectionAction {
    
    public static final String ACTION_LEFT_ID = "TextAlignmentActionLeft"; //$NON-NLS-1$
    public static final String ACTION_CENTER_ID = "TextAlignmentActionCenter"; //$NON-NLS-1$
    public static final String ACTION_RIGHT_ID = "TextAlignmentActionRight"; //$NON-NLS-1$
    
    public static final String ACTION_LEFT_TEXT = Messages.TextAlignmentAction_0;
    public static final String ACTION_CENTER_TEXT = Messages.TextAlignmentAction_1;
    public static final String ACTION_RIGHT_TEXT = Messages.TextAlignmentAction_2;
    
    public static final String ACTION_IDS[] = {
        ACTION_LEFT_ID,
        ACTION_CENTER_ID,
        ACTION_RIGHT_ID
    };
    
    public static final String ACTION_TEXTS[] = {
        ACTION_LEFT_TEXT,
        ACTION_CENTER_TEXT,
        ACTION_RIGHT_TEXT
    };

    public static List<RetargetAction> createRetargetActions() {
        List<RetargetAction> list = new ArrayList<RetargetAction>();
        
        for(int i = 0; i < ACTION_IDS.length; i++) {
            list.add(new RetargetAction(ACTION_IDS[i], ACTION_TEXTS[i], IAction.AS_RADIO_BUTTON));
        }
     
        return list;
    }
    
    public static List<TextAlignmentAction> createActions(IWorkbenchPart part) {
        List<TextAlignmentAction> list = new ArrayList<TextAlignmentAction>();
        
        list.add(new TextAlignmentAction(part, IFontAttribute.TEXT_ALIGNMENT_LEFT, ACTION_LEFT_ID, ACTION_LEFT_TEXT,
                IArchimateImages.ImageFactory.getImageDescriptor(IArchimateImages.ICON_ALIGN_TEXT_LEFT)));
        list.add(new TextAlignmentAction(part, IFontAttribute.TEXT_ALIGNMENT_CENTER, ACTION_CENTER_ID, ACTION_CENTER_TEXT,
                IArchimateImages.ImageFactory.getImageDescriptor(IArchimateImages.ICON_ALIGN_TEXT_CENTER)));
        list.add(new TextAlignmentAction(part, IFontAttribute.TEXT_ALIGNMENT_RIGHT, ACTION_RIGHT_ID, ACTION_RIGHT_TEXT,
                IArchimateImages.ImageFactory.getImageDescriptor(IArchimateImages.ICON_ALIGN_TEXT_RIGHT)));
     
        return list;
    }
    
    private int fAlignment;
    
    public TextAlignmentAction(IWorkbenchPart part, int alignment, String id, String text, ImageDescriptor desc) {
        super(part, AS_RADIO_BUTTON);
        fAlignment = alignment;
        setId(id);
        setText(text);
    }

    @Override
    protected boolean calculateEnabled() {
        setChecked(false);
        
        List<?> selected = getSelectedObjects();
        
        IFontAttribute model = (IFontAttribute)getFirstValidSelectedModelObject(selected);

        if(model != null && selected.size() == 1) {
            setChecked(model.getTextAlignment() == fAlignment);
        }
        
        return model != null;
    }

    private Object getFirstValidSelectedModelObject(List<?> selection) {
        for(Object object : getSelectedObjects()) {
            if(object instanceof EditPart) {
                Object model = ((EditPart)object).getModel();
                if(shouldModify(model)) {
                    return model;
                }
            }
        }
        
        return null;
    }
    
    @Override
    public void run() {
        List<?> selection = getSelectedObjects();
        
        Object model = getFirstValidSelectedModelObject(selection);
        if(model != null) {
            if(shouldModify(model)) {
                execute(createCommand(selection));
            }
        }
    }
    
    private Command createCommand(List<?> selection) {
        CompoundCommand result = new CompoundCommand(Messages.TextAlignmentAction_3);
        
        for(Object object : selection) {
            if(object instanceof EditPart) {
                Object model = ((EditPart)object).getModel();
                if(shouldModify(model)) {
                    Command cmd = new TextAlignmentCommand((IFontAttribute)model, fAlignment);
                    if(cmd.canExecute()) {
                        result.add(cmd);
                    }
                }
            }
        }

        return result.unwrap();
    }
    
    private boolean shouldModify(Object model) {
        if(model instanceof ILockable && ((ILockable)model).isLocked()) {
            return false;
        }
        
        return (model instanceof IFontAttribute) && (model instanceof IDiagramModelObject) &&
                (((IDiagramModelObject)model).shouldExposeFeature(IArchimatePackage.Literals.FONT_ATTRIBUTE__TEXT_ALIGNMENT));
    }

}
