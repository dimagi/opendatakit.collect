package org.odk.collect.android.jr.extensions;

import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.IDataReference;
import org.javarosa.core.model.instance.FormInstance;
import org.javarosa.core.model.instance.TreeReference;
import org.javarosa.model.xform.XPathReference;
import org.javarosa.xform.parse.IElementHandler;
import org.javarosa.xform.parse.XFormParser;
import org.kxml2.kdom.Element;

import android.content.Context;

public class PollSensorExtensionParser implements IElementHandler {
	Context context;
	
	public PollSensorExtensionParser(Context c) {
		this.context = c;
	}

	@Override
	public void handle(XFormParser p, Element e, Object parent) {
		String event = e.getAttributeValue(null, "event");
		FormDef form = (FormDef) parent;
		PollSensorAction action = new PollSensorAction();
		
		String ref = e.getAttributeValue(null, "ref");
		if (ref != null) {
			IDataReference dataRef = new XPathReference(ref);
			if (dataRef != null) {
				dataRef = XFormParser.getAbsRef(dataRef, TreeReference.rootRef());
			}
			TreeReference treeRef = FormInstance.unpackReference(dataRef);
			p.registerActionTarget(treeRef);
			action = new PollSensorAction(treeRef, this.context);
		}

		form.registerEventListener(event, action);
	}

}
