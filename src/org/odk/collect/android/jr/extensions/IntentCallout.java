/**
 * 
 */
package org.odk.collect.android.jr.extensions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.core.model.instance.AbstractTreeElement;
import org.javarosa.core.model.instance.TreeReference;
import org.javarosa.core.util.externalizable.DeserializationException;
import org.javarosa.core.util.externalizable.ExtUtil;
import org.javarosa.core.util.externalizable.ExtWrapMap;
import org.javarosa.core.util.externalizable.Externalizable;
import org.javarosa.core.util.externalizable.PrototypeFactory;

import android.content.Context;
import android.content.Intent;

/**
 * @author ctsims
 *
 */
public class IntentCallout implements Externalizable {
	private String className;
	private Hashtable<String, TreeReference> refs;
	
	public IntentCallout() {
		
	}
	
	public IntentCallout(String className,  Hashtable<String, TreeReference> refs) {
		this.className = className;
		this.refs = refs;
	}
	
	public Intent generate(EvaluationContext ec) {
		Intent i = new Intent(className);
		for(Enumeration<String> en = refs.keys() ; en.hasMoreElements() ;) {
			String key = en.nextElement();
			AbstractTreeElement e = ec.resolveReference(refs.get(key));
			if(e != null && e.getValue() != null) {
				i.putExtra(key, e.getValue().uncast().getString());
			}
		}
		return i;
	}

	@Override
	public void readExternal(DataInputStream in, PrototypeFactory pf) throws IOException, DeserializationException {
		className = ExtUtil.readString(in);
		refs = (Hashtable<String, TreeReference>)ExtUtil.read(in, new ExtWrapMap(String.class, TreeReference.class), pf);
	}

	@Override
	public void writeExternal(DataOutputStream out) throws IOException {
		ExtUtil.writeString(out, className);
		ExtUtil.write(out, new ExtWrapMap(refs));
	}

}
