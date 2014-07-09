/**
 * Handler for <pollsensor> tags, which get processed by PollSensorActions.
 * @author jschweers
 */
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

	/**
	 * Handle pollsensor node, creating a new PollSensor action with the 
	 * current context and the node that sensor data will be written to.
	 * @param p Parser
	 * @param e pollsensor Element
	 * @param parent FormDef for the form being parsed
	 */
	@Override
	public void handle(XFormParser p, Element e, Object parent) {
		String event = e.getAttributeValue(null, "event");
		FormDef form = (FormDef) parent;
		PollSensorAction action;
		
		String ref = e.getAttributeValue(null, "ref");
		if (ref != null) {
			TreeReference targetRef = getRef(ref);
			TreeReference auditRef = getRef(e.getAttributeValue(null, "time"));
			p.registerActionTarget(targetRef);
			p.registerActionTarget(auditRef);
			action = new PollSensorAction(this.context, targetRef, auditRef);
		}
		else {
			action = new PollSensorAction(this.context);
		}

		form.registerEventListener(event, action);
	}
	
	private TreeReference getRef(String attribute) {
		IDataReference dataRef = new XPathReference(attribute);
		if (dataRef != null) {
			dataRef = XFormParser.getAbsRef(dataRef, TreeReference.rootRef());
		}
		TreeReference treeRef = FormInstance.unpackReference(dataRef);
		return treeRef;
	}

}
