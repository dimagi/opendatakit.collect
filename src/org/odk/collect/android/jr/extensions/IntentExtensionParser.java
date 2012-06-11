/**
 * 
 */
package org.odk.collect.android.jr.extensions;

import java.util.Hashtable;

import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.instance.TreeReference;
import org.javarosa.model.xform.XPathReference;
import org.javarosa.xform.parse.IElementHandler;
import org.javarosa.xform.parse.XFormParser;
import org.kxml2.kdom.Element;

/**
 * @author ctsims
 *
 */
public class IntentExtensionParser implements IElementHandler {

	@Override
	public void handle(XFormParser p, Element e, Object parent) {
		if(!(parent instanceof FormDef)) {
			throw new RuntimeException("Intent extension improperly registered.");
		}
		FormDef form = (FormDef)parent;
		
		String id = e.getAttributeValue(null, "id");
		String className = e.getAttributeValue(null, "class");
		
		Hashtable<String, TreeReference> extras = new Hashtable<String, TreeReference>();
		for(int i = 0; i < e.getChildCount(); ++i) {
			if(e.getType(i) == Element.ELEMENT) {
				Element child = (Element)e.getChild(i);
				String key = child.getAttributeValue(null, "key");
				String ref = child.getAttributeValue(null, "ref");
				extras.put(key, (TreeReference)new XPathReference(ref).getReference());
			}
		}
		
		form.getExtension(AndroidXFormExtensions.class).registerIntent(id, new IntentCallout(className, extras));
	}

}
