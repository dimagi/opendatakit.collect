package org.odk.collect.android.jr.extensions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.javarosa.core.model.Action;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.core.model.condition.Recalculate;
import org.javarosa.core.model.data.AnswerDataFactory;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.instance.AbstractTreeElement;
import org.javarosa.core.model.instance.TreeReference;
import org.javarosa.core.util.externalizable.DeserializationException;
import org.javarosa.core.util.externalizable.ExtUtil;
import org.javarosa.core.util.externalizable.PrototypeFactory;

import android.os.AsyncTask;

public class PollSensorAction extends Action {
	private static String name = "pollsensor";
	private TreeReference target;

	public PollSensorAction() {
		super(name);
	}
	
	public PollSensorAction(TreeReference target) {
		super(name);
		this.target = target;
	}

	public void processAction(FormDef model, TreeReference contextRef) {
		AsyncTask<Object, Void, Void> poller = new AsyncTask<Object, Void, Void>() {
			@Override
			protected Void doInBackground(Object... params) {
				FormDef model = (FormDef) params[0];
				TreeReference contextRef = (TreeReference) params[1];
				
				String result = "42.36521590986622 -71.10284802016257 -4.0 5.0";
				if (PollSensorAction.this.target != null) {
					TreeReference qualifiedReference = contextRef == null ? target : target.contextualize(contextRef);
					EvaluationContext context = new EvaluationContext(model.getEvaluationContext(), qualifiedReference);
		
					AbstractTreeElement node = context.resolveReference(qualifiedReference);
					if(node == null) { throw new NullPointerException("Target of TreeReference " + qualifiedReference.toString(true) +" could not be resolved!"); }
					int dataType = node.getDataType();
					IAnswerData val = Recalculate.wrapData(result, dataType);
		
					model.setValue(val == null ? null: AnswerDataFactory.templateByDataType(dataType).cast(val.uncast()), qualifiedReference);
				}
				return null;
			}
		};
		Object[] taskParams = { model, contextRef };
		poller.execute(taskParams);
	}

	public void readExternal(DataInputStream in, PrototypeFactory pf) throws IOException, DeserializationException {
		target = (TreeReference)ExtUtil.read(in, TreeReference.class, pf);
	}

	public void writeExternal(DataOutputStream out) throws IOException {
		ExtUtil.write(out, target);
	}
}
