package cs.vt.edu.bdd.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class ScenarioEditor extends TextEditor {

	private ColorManager colorManager;

	public ScenarioEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new ScenarioConfiguration(colorManager));
		setDocumentProvider(new ScenarioDocumentProvider());
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
